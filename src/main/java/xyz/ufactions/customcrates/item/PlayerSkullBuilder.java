package xyz.ufactions.customcrates.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSkullBuilder {


    // some reflection stuff to be used when setting a skull's profile
    private static Field blockProfileField;
    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

    private PlayerSkullBuilder() {
    }

    /**
     * Creates a player skull
     */
    public static ItemStack createSkull() {
        try {
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
        }
    }

    /**
     * Creates a player skull item with the skin based on a player's uuid
     *
     * @param uuid The player's UUID.
     * @return The head of the player.
     */
    public static ItemStack itemFromUUID(UUID uuid) {
        return itemWithUUID(createSkull(), uuid);
    }

    /**
     * Create a player skull item with the skin at a Mojang URL.
     *
     * @param url The Mojang URL.
     * @return The head of the player.
     */
    public static ItemStack itemFromURL(String url) {
        return itemWithURL(createSkull(), url);
    }

    /**
     * Create a player skull with the skin based on a base64 string.
     *
     * @param base64 The Mojang URL.
     * @return The head of the player.
     */
    public static ItemStack itemFromBase64(String base64) {
        return itemWithBase64(createSkull(), base64);
    }

    /**
     * Modifies a skull to use the skin of the player with a given UUID.
     *
     * @param item The item to apply the name to. Must be a player skull.
     * @param uuid The player's UUID.
     * @return The head of the player.
     */
    public static ItemStack itemWithUUID(ItemStack item, UUID uuid) {
        Validate.notNull(item, "item");
        Validate.notNull(uuid, "uuid");

        return transform(item, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid)));
    }

    /**
     * Modifies a skull to use the skin at the given Mojang URL.
     *
     * @param item The item to apply the skin to. Must be a player skull.
     * @param url  The URL of the Mojang skin.
     * @return The head associated with the URL.
     */
    public static ItemStack itemWithURL(ItemStack item, String url) {
        Validate.notNull(item, "item");
        Validate.notNull(url, "url");

        return itemWithBase64(item, urlToBase64(url));
    }

    /**
     * Modifies a skull to use the skin based on the given base64 string.
     *
     * @param item   The ItemStack to put the base64 onto. Must be a player skull.
     * @param base64 The base64 string containing the texture.
     * @return The head with a custom texture.
     */
    public static ItemStack itemWithBase64(ItemStack item, String base64) {
        Validate.notNull(item, "item");
        Validate.notNull(base64, "base64");

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        mutateItemMeta(meta, base64);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Sets the block to a skull with the given UUID.
     *
     * @param block The block to set.
     * @param uuid  The player to set it to.
     */
    public static void blockWithUUID(Block block, UUID uuid) {
        Validate.notNull(block, "block");
        Validate.notNull(uuid, "uuid");

        setToSkull(block);
        Skull state = (Skull) block.getState();
        state.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        state.update(false, false);
    }

    /**
     * Sets the block to a skull with the skin found at the provided mojang URL.
     *
     * @param block The block to set.
     * @param url   The mojang URL to set it to use.
     */
    public static void blockWithURL(Block block, String url) {
        Validate.notNull(block, "block");
        Validate.notNull(url, "url");

        blockWithBase64(block, urlToBase64(url));
    }

    /**
     * Sets the block to a skull with the skin for the base64 string.
     *
     * @param block  The block to set.
     * @param base64 The base64 to set it to use.
     */
    public static void blockWithBase64(Block block, String base64) {
        Validate.notNull(block, "block");
        Validate.notNull(base64, "base64");

        setToSkull(block);
        Skull state = (Skull) block.getState();
        mutateBlockState(state, base64);
        state.update(false, false);
    }

    private static void setToSkull(Block block) {
        try {
            block.setType(Material.valueOf("PLAYER_HEAD"), false);
        } catch (IllegalArgumentException e) {
            block.setType(Material.valueOf("SKULL"), false);
            Skull state = (Skull) block.getState();
            state.setSkullType(SkullType.PLAYER);
            state.update(false, false);
        }
    }

    private static String urlToBase64(String url) {
        URL actualURL;
        try {
            actualURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualURL.toString() + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    private static GameProfile makeProfile(String base64) {
        //random uuid based on the base64 string
        UUID uuid = new UUID(
                base64.substring(base64.length() - 20).hashCode(),
                base64.substring(base64.length() - 10).hashCode());
        GameProfile profile = new GameProfile(uuid, "Player");
        profile.getProperties().put("textures", new Property("textures", base64));
        return profile;
    }

    private static void mutateBlockState(Skull block, String base64) {
        try {
            if (blockProfileField == null) {
                blockProfileField = block.getClass().getDeclaredField("profile");
                blockProfileField.setAccessible(true);
            }
            blockProfileField.set(block, makeProfile(base64));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void mutateItemMeta(SkullMeta meta, String base64) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("profile");
                metaSetProfileMethod.setAccessible(true);
            }
            metaSetProfileMethod.invoke(meta, makeProfile(base64));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }
                metaProfileField.set(meta, makeProfile(base64));
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static ItemStack transform(ItemStack item, Consumer<SkullMeta> meta) {
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        meta.accept(skullMeta);
        item.setItemMeta(skullMeta);

        return item;
    }
}