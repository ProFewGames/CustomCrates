package xyz.ufactions.customcrates.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.ColorLib;
import xyz.ufactions.customcrates.libs.F;

import java.util.function.Consumer;

public class FlagsGUI extends GUI {

    private static int[] slots = new int[]{
            10, 12, 14, 16, 20, 22, 24
    };

    private final Consumer<ItemStackBuilder> consumer;
    private final ItemStackBuilder builder;
    private final GUI gui;

    public FlagsGUI(Consumer<ItemStackBuilder> consumer, CustomCrates plugin, ItemStackBuilder builder, GUI gui, Player player) {
        super(plugin, player, 36);

        this.consumer = consumer;
        this.builder = builder;
        this.gui = gui;

        setTitle("&3&lFlag GUI");

        seatItems();
    }

    private void seatItems() {
        ItemFlag[] flags = ItemFlag.values();
        for (int i = 0; i < flags.length; i++) {
            ItemFlag flag = flags[i];
            setItem(slots[i], ColorLib.banner(ChatColor.RED)
                    .name("&b&l" + F.capitalizeFirstLetter(flag.name()))
                    .lore(
                            "",
                            "&e&lHas Flag:",
                            builder.hasFlag(flag) ? "&a&ltrue" : "&c&lfalse",
                            "",
                            "&7&o* Click to " + F.ar(builder.hasFlag(flag)) + " &7&oflag *"
                    )
                    .build(event -> {
                        if (builder.hasFlag(flag))
                            builder.unflag(flag);
                        else
                            builder.flag(flag);
                        seatItems();
                    }));
        }
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        this.consumer.accept(this.builder);
        this.gui.open();
    }
}