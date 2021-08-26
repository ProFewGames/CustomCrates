package xyz.ufactions.customcrates.gui.internal;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.button.Button;
import xyz.ufactions.customcrates.libs.Callback;

import java.util.*;
import java.util.function.Predicate;

public final class GUIBuilder {

    public static GUIBuilder instance(CustomCrates plugin, String name, GUI.GUIFiller filler) {
        return new GUIBuilder(plugin, name, filler);
    }

    private final CustomCrates plugin;
    private final String name;
    private final GUI.GUIFiller filler;

    private int size = -1;
    private ChatColor color;

    private final Map<GUI.GUIAction, Callback<Player>> actions = new HashMap<>();
    private final List<Button> buttons = new ArrayList<>();
    private Predicate<Player> canClose;
    private Predicate<Player> canOpen;

    private GUIBuilder(CustomCrates plugin, String name, GUI.GUIFiller filler) {
        this.plugin = plugin;
        this.name = name;
        this.filler = filler;
    }

    public GUIBuilder size(int size) {
        this.size = size;
        return this;
    }

    public GUIBuilder color(ChatColor color) {
        this.color = color;
        return this;
    }

    public GUIBuilder addButton(Button... button) {
        this.buttons.addAll(Arrays.asList(button));
        return this;
    }

    public GUIBuilder onActionPerformed(GUI.GUIAction action, Callback<Player> player) {
        this.actions.put(action, player);
        return this;
    }

    public GUIBuilder canOpen(Predicate<Player> predicate) {
        this.canOpen = predicate;
        return this;
    }

    public GUIBuilder canClose(Predicate<Player> predicate) {
        this.canClose = predicate;
        return this;
    }

    public GUI build() {
        GUI gui = new GUI(plugin, name, size, filler) {

            @Override
            public boolean canClose(Player player) {
                if (canClose == null) return super.canClose(player);
                return canClose.test(player);
            }

            @Override
            public boolean canOpenInventory(Player player) {
                if (canOpen == null) return super.canOpenInventory(player);
                return canOpen.test(player);
            }

            @Override
            public void onActionPerformed(GUIAction action, Player player) {
                Callback<Player> callback = actions.get(action);
                if (callback != null) callback.run(player);
            }
        };
        if (color != null) gui.setPaneColor(color);
        gui.addButton(buttons.toArray(new Button[0]));
        return gui;
    }
}