package xyz.ufactions.customcrates.crates;

import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.libs.ItemBuilder;

import java.util.List;

public class Prize {

    private final String configurationSection; // The section configured to this prize

    private final ItemBuilder builder;
    private final List<String> commands;
    private boolean glowing;
    private final double chance;

    public Prize(ItemBuilder builder, boolean glow, double chance, String configurationSection, List<String> commands) {
        List<String> lore = builder.getLore();

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            line = line.replaceAll("%chance%", String.valueOf(chance));
            lore.set(i, line);
        }

        builder.lore(lore);

        this.glowing = glow;
        this.builder = builder;
        this.commands = commands;
        this.chance = chance;
        this.configurationSection = configurationSection;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        builder.glow(glowing);
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