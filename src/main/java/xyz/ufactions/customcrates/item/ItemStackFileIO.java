package xyz.ufactions.customcrates.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import xyz.ufactions.customcrates.CustomCrates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemStackFileIO {

    /**
     * Get an instance of {@link ItemStackFileIO} to read and write {@link ItemStackBuilder}.
     * <p>
     * An instance of {@link CustomCrates} is required to provide debugging to console in-case
     * of an error.
     * <p>
     * {@link ConfigurationSection} is the section of the configuration you want to read with key
     * "item base64".
     *
     * @param plugin  The plugin
     * @param section The section
     * @return A new {@link ItemStackFileIO} for reading/writing.
     */
    public static ItemStackFileIO create(CustomCrates plugin, ConfigurationSection section) {
        return new ItemStackFileIO(plugin, section);
    }

    private final CustomCrates plugin;
    private final ConfigurationSection section;

    /**
     * Read an ItemStack from {@link ConfigurationSection} with key "item base64".
     * If the ConfigurationSection does not contain the key then {@code null} is returned,
     * otherwise the read {@link ItemStackBuilder} is returned
     *
     * @return {@link ItemStackBuilder}
     */
    @Nullable
    public ItemStackBuilder read() {
        String encodedItem = section.getString("item base64");
        if (encodedItem == null) {
            plugin.getLogger().log(Level.WARNING, String.format("ItemStackFileIO could not find '%s' in section '%s'.",
                    "item base64",
                    section.getCurrentPath()));
            return null;
        }
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(encodedItem));
             BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(byteInputStream)) {
            return ItemStackBuilder.from((ItemStack) bukkitInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, String.format("ItemStackFileIO failed to read from section '%s' found '%s'.",
                    section.getCurrentPath(),
                    encodedItem), e);
            return null;
        }
    }

    /**
     * Write an {@link ItemStackBuilder} to a {@link ConfigurationSection} with the key "item base64".
     *
     * @param builder The builder
     */
    public void write(ItemStackBuilder builder) {
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(byteOutputStream)) {
            bukkitOutputStream.writeObject(builder.build());

            String encodedItem = Base64Coder.encodeLines(byteOutputStream.toByteArray());
            section.set("item base64", encodedItem);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, String.format("ItemStackFileIO failed to write to section '%s'.",
                    section.getCurrentPath()), e);
        }
    }
}