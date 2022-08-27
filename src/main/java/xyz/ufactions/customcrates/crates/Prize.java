package xyz.ufactions.customcrates.crates;

import lombok.Getter;
import lombok.Setter;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.List;

@Setter
@Getter
public class Prize {

    private final String configurationSection; // The section configured to this prize

    /**
     * The {@link ItemStackBuilder} of this prize.
     */
    private ItemStackBuilder itemBuilder;

    /**
     * If {@code true}, when the player wins this prize it will also give them this
     * prize's {@link ItemStackBuilder}. Otherwise, will not give the item.
     */
    private boolean giveItem;

    /**
     * The commands to be executed as {@link org.bukkit.command.ConsoleCommandSender}
     */
    private List<String> commands;

    /**
     * The chance to get this prize.
     */
    private double chance;

    public Prize(ItemStackBuilder itemBuilder, double chance, boolean giveItem, String configurationSection, List<String> commands) {
        itemBuilder.applyMeta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) return;

            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                line = line.replaceAll("%chance%", String.valueOf(chance));
                lore.set(i, line);
            }

            meta.setLore(lore);
        });
        this.itemBuilder = itemBuilder;
        this.giveItem = giveItem;
        this.commands = commands;
        this.chance = chance;
        this.configurationSection = configurationSection;
    }

    @Override
    public String toString() {
        return "Prize{" +
                "configurationSection='" + configurationSection + '\'' +
                ", itemBuilder=" + itemBuilder +
                ", giveItem=" + giveItem +
                ", commands=" + commands +
                ", chance=" + chance +
                '}';
    }
}