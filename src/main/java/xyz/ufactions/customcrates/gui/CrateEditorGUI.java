package xyz.ufactions.customcrates.gui;

import org.bukkit.ChatColor;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.gui.buttons.CrateDisplayChangeButton;
import xyz.ufactions.customcrates.gui.buttons.DeleteButton;
import xyz.ufactions.customcrates.gui.buttons.EditPrizesButton;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.libs.UtilMath;

public class CrateEditorGUI extends GUI<CustomCrates> {

    public CrateEditorGUI(CustomCrates plugin, Crate crate) {
        super(plugin, "Editor for: " + crate.getSettings().getDisplay(), 27, GUIFiller.PANE);

        setPaneColor(ChatColor.values()[UtilMath.random.nextInt(ChatColor.values().length)]);

        addButton(new DeleteButton(plugin, crate));
        addButton(new EditPrizesButton(plugin, crate));
        addButton(new CrateDisplayChangeButton(plugin, crate));
    }
}