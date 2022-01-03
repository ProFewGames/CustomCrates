package xyz.ufactions.customcrates.crates;

import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.List;

public class Prize {

    private final String configurationSection; // The section configured to this prize

    private final ItemStackBuilder builder;
    private final List<String> commands;
    private final double chance;

    public Prize(ItemStackBuilder builder, double chance, String configurationSection, List<String> commands) {
        builder.applyMeta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) return;

            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                line = line.replaceAll("%chance%", String.valueOf(chance));
                lore.set(i, line);
            }

            meta.setLore(lore);
        });
        this.builder = builder;
        this.commands = commands;
        this.chance = chance;
        this.configurationSection = configurationSection;
    }

    public ItemStack getDisplayItem() {
        return builder.build();
    }

    public double getChance() {
        return chance;
    }

    public String getConfigurationSection() {
        return configurationSection;
    }

    public List<String> getCommands() {
        return commands;
    }
}