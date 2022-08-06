package xyz.ufactions.customcrates.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

public class EditorGUI extends GUI {

    public EditorGUI(CustomCrates plugin, Player player) {
        super(plugin, player, 27);

        setTitle("&3&lEditor");

        setItem(11, ItemStackBuilder.of(Material.BARRIER)
                .name("&b&lCrate Editor")
                .lore("", "&7&o* Click to open crate editor *")
                .build(event ->
                        new CratesGUI(plugin, this, player).open()
                ));

        setItem(15, ItemStackBuilder.of(Material.BARRIER)
                .name("&b&lLanguage Editor")
                .lore("", "&7&o* Click to open language editor *")
                .build(event ->
                        new LanguageGUI(plugin, this, player).open()
                ));
    }
}