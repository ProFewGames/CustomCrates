package xyz.ufactions.customcrates.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.ufactions.enchantmentlib.EnchantmentLib;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemStackFileWriter {

    public static ItemStackFileWriter of(ItemStackBuilder builder, ConfigurationSection section) {
        return new ItemStackFileWriter(builder, section);
    }

    private final ItemStackBuilder builder;
    private final ConfigurationSection section;

    public void write() {
        builder.apply(item -> {
            section.set(getPath("material", "item"), item.getType().name());
            section.set(getPath("data", "durability"), item.getDurability());
            section.set("amount", item.getAmount());
            section.set("glow", item.containsEnchantment(EnchantmentLib.getGlowEnchantment()));

            if (item.getType() == PlayerSkullBuilder.createSkull().getType()) {
                UUID owner = PlayerSkullBuilder.getSkullOwner(item);
                if (owner != null)
                    section.set("owner", owner.toString());
                else
                    section.set("owner", PlayerSkullBuilder.getSkullBase64(item));
            }

            String enchantmentPath = getPath("enchantments", "enchants");
            section.set(enchantmentPath, null);
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                if (entry.getKey() == EnchantmentLib.getGlowEnchantment()) continue;
                section.set(enchantmentPath + "." + entry.getKey().getName() + ".name", entry.getKey().getName());
                section.set(enchantmentPath + "." + entry.getKey().getName() + ".level", entry.getValue());
            }
        });
        builder.applyMeta(meta -> {
            if (meta.hasDisplayName())
                section.set(getPath("display", "name"), meta.getDisplayName());
            if (meta.hasLore())
                section.set("lore", meta.getLore());
            if (meta instanceof LeatherArmorMeta)
                section.set("color", ((LeatherArmorMeta) meta).getColor());
            section.set("flags", null);
            for (ItemFlag flag : meta.getItemFlags()) {
                section.set("flags." + flag.name() + ".name", flag.name());
                section.set("flags." + flag.name() + ".value", true);
            }
            section.set("unbreakable", meta.isUnbreakable());
            if (meta.hasCustomModelData())
                section.set("model", meta.getCustomModelData());
        });
    }

    private String getPath(String... availablePaths) {
        for (String availablePath : availablePaths)
            if (section.contains(availablePath))
                return availablePath;
        return availablePaths[0];
    }
}