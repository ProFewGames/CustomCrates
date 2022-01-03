package xyz.ufactions.customcrates.item;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import xyz.ufactions.customcrates.item.exception.ItemReadException;

import java.util.*;

public final class ItemStackConfiguration {

    public static ItemStackConfiguration of(FileConfiguration configuration, ConfigurationSection section) {
        return new ItemStackConfiguration(configuration, section);
    }

    private final FileConfiguration config;
    private final ConfigurationSection section;

    private ItemStackConfiguration(FileConfiguration configuration, ConfigurationSection section) {
        Validate.notNull(configuration, "configuration");
        Validate.notNull(section, "section");

        this.config = configuration;
        this.section = section;
    }

    public Material readMaterial() throws ItemReadException {
        String name = section.isString("item") ? section.getString("item") : section.getString("material");
        if (name == null)
            throw new ItemReadException("Cannot read material from configuration section '" + section.getCurrentPath() + "'");
        try {
            return Material.getMaterial(name.toUpperCase());
        } catch (EnumConstantNotPresentException e) {
            throw new ItemReadException("Cannot find material type '" + name + "'.", e);
        }
    }

    public OptionalInt readDurability() {
        if (section.isInt("durability")) return OptionalInt.of(section.getInt("durability"));
        if (section.isInt("data")) return OptionalInt.of(section.getInt("data"));
        return OptionalInt.empty();
    }

    public OptionalInt readAmount() {
        return OptionalInt.of(section.getInt("amount"));
    }

    public Optional<Boolean> readGlow() {
        if (section.isBoolean("glow")) return Optional.of(section.getBoolean("glow"));
        return Optional.empty();
    }

    public Optional<String> readName() {
        if (section.isString("name")) return Optional.ofNullable(section.getString("name"));
        if (section.isString("display")) return Optional.ofNullable(section.getString("display"));
        return Optional.empty();
    }

    public Optional<Map<Enchantment, Integer>> readEnchantments() {
        if (section.isConfigurationSection("enchantments") || section.isConfigurationSection("enchants")) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            ConfigurationSection section = this.section.getConfigurationSection("enchantments") != null ? this.section.getConfigurationSection("enchantments") : this.section.getConfigurationSection("enchants");
            for (String key : section.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(section.getString(key + ".name"));
                if (enchantment == null) continue;
                int level = section.getInt(key + ".level", 1);
                enchantments.put(enchantment, level);
            }
            return Optional.of(enchantments);
        }
        return Optional.empty();
    }

    public Optional<List<String>> readLore() {
        return Optional.of(section.getStringList("lore"));
    }

    public Optional<UUID> readOwner() {
        if (section.isString("owner")) {
            try {
                return Optional.of(UUID.fromString(section.getString("owner")));
            } catch (IllegalArgumentException e) {
                // TODO Log
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public Optional<Color> readColor() {
        if (section.isColor("color")) return Optional.of(section.getColor("color"));
        return Optional.empty();
    }

    public Optional<Map<ItemFlag, Boolean>> readFlags() {
        if (section.isConfigurationSection("flags")) {
            Map<ItemFlag, Boolean> flags = new HashMap<>();
            ConfigurationSection section = this.section.getConfigurationSection("flags");
            for (String key : section.getKeys(false)) {
                ItemFlag flag;
                try {
                    flag = ItemFlag.valueOf(section.getString(key + ".name"));
                } catch (EnumConstantNotPresentException e) {
                    // TODO LOG
                    continue;
                }
                boolean value = section.getBoolean(key + ".value", true);
                flags.put(flag, value);
            }
            return Optional.of(flags);
        }
        return Optional.empty();
    }

    public Optional<Boolean> readUnbreakable() {
        if (section.isBoolean("unbreakable")) return Optional.of(section.getBoolean("unbreakable"));
        return Optional.empty();
    }

    public ItemStackBuilder read() {
        Material material;
        try {
            material = readMaterial();
        } catch (ItemReadException e) {
            e.printStackTrace();
            return null;
        }
        ItemStackBuilder builder = ItemStackBuilder.of(material);

        readDurability().ifPresent(builder::durability);
        readAmount().ifPresent(builder::amount);

        Optional<Boolean> glow = readGlow();
        if (glow.isPresent() && glow.get()) builder.glow();

        readName().ifPresent(builder::name);
        readEnchantments().ifPresent(builder::enchant);
        readLore().ifPresent(builder::lore);
        readOwner().ifPresent(builder::owner);
        readColor().ifPresent(builder::color);
        readFlags().ifPresent(builder::flag);
        readUnbreakable().ifPresent(builder::unbreakable);

        return builder;
    }
}