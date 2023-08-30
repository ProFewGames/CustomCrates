package xyz.ufactions.customcrates.gui.crate;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.crate.CrateFileWriter;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.spin.Spin;

public class SpinTypeGUI extends GUI {

    private final CrateEditorGUI gui;

    public SpinTypeGUI(CustomCrates plugin, CrateEditorGUI gui, Crate crate, Player player) {
        super(plugin, player, Spin.SpinType.values().length);

        this.gui = gui;

        setTitle("&3&lSelect Spin Type...");

        Spin.SpinType[] types = Spin.SpinType.values();
        for (int i = 0; i < types.length; i++) {
            Spin.SpinType type = types[i];
            setItem(i, ItemStackBuilder.of(type.getDisplayItem())
                    .name("&b&l" + F.capitalizeFirstLetter(type.name()))
                    .lore("&7&o* Click to apply spin to crate *")
                    .build(event -> {
                        crate.getSettings().setSpinType(type);
                        CrateFileWriter writer = CrateFileWriter.create(plugin,crate);
                        writer.writeSpinType();
                        writer.save();
                        player.closeInventory();
                    }));
        }
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        this.gui.open();
    }
}