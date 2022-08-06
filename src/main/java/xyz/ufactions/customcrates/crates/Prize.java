package xyz.ufactions.customcrates.crates;

import lombok.Getter;
import lombok.Setter;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.List;

@Setter
@Getter
public class Prize {

    private final String configurationSection; // The section configured to this prize

    private ItemStackBuilder itemBuilder;
    private List<String> commands;
    private double chance;

    public Prize(ItemStackBuilder itemBuilder, double chance, String configurationSection, List<String> commands) {
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
        this.commands = commands;
        this.chance = chance;
        this.configurationSection = configurationSection;
    }
}