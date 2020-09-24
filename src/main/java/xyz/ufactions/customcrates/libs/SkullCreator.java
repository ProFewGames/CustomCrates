package xyz.ufactions.customcrates.libs;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullCreator {

    private static final ReflectionUtils.RefClass ClassGameProfile = ReflectionUtils.getRefClass("com.mojang.authlib.GameProfile");
    private static final ReflectionUtils.RefClass ClassPropertyMap = ReflectionUtils.getRefClass("com.mojang.authlib.properties.PropertyMap");
    private static final ReflectionUtils.RefClass ClassProperty = ReflectionUtils.getRefClass("com.mojang.authlib.properties.Property");
    private static final ReflectionUtils.RefClass ClassSkullMeta = ReflectionUtils.getRefClass("org.bukkit.inventory.meta.SkullMeta");

    private static final ReflectionUtils.RefConstructor ConstructorGameProfile = ClassGameProfile.getConstructor(UUID.class, String.class);
    private static final ReflectionUtils.RefConstructor ConstructorProperty = ClassProperty.getConstructor(String.class, String.class);

    private static final ReflectionUtils.RefField FieldProfile = ClassSkullMeta.getField("profile");

    //    private static final ReflectionUtils.RefMethod MethodSetProfile = ClassGameProfile.getMethod("setProfile", ClassGameProfile.getRealClass());
    private static final ReflectionUtils.RefMethod MethodGetProperties = ClassGameProfile.getMethod("getProperties");
    private static final ReflectionUtils.RefMethod MethodPut = ClassPropertyMap.getMethod("put", String.class, ClassProperty.getRealClass());

    @Deprecated
    public static ItemStack fromName(String name) {
        Validate.notNull(name, "name");

        ItemStack item = baseSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(name);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack fromUUID(UUID uuid) {
        Validate.notNull(uuid, "uuid");

        ItemStack item = baseSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack fromBase64(String base64) {
        Validate.notNull(base64, "base64");

        ItemStack item = baseSkull();
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        FieldProfile.of(meta).set(makeProfile(base64));
//        MethodSetProfile.of(meta).call(makeProfile(base64));

        item.setItemMeta(meta);

        return item;
    }

    private static Object makeProfile(String base64) {
        // random uuid based on the base64 string
        UUID uuid = new UUID(base64.substring(base64.length() - 20).hashCode(), base64.substring(base64.length() - 10).hashCode());

        Object profile = ConstructorGameProfile.create(uuid, "aaaaa");
        Object propertiesMap = MethodGetProperties.of(profile).call();
        Object property = ConstructorProperty.create("textures", base64);
        MethodPut.of(propertiesMap).call("textures", property);

        return profile;
    }

    private static ItemStack baseSkull() {
        if (VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_13)) {
            return new ItemStack(Material.getMaterial("SKULL_ITEM"));
        } else {
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }
}