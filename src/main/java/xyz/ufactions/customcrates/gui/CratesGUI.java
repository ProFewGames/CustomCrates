package xyz.ufactions.customcrates.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.crate.CrateEditorGUI;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.Slot;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;

import java.util.List;

public class CratesGUI extends GUI {

    private final EditorGUI gui;

    private boolean fallback = true;

    public CratesGUI(CustomCrates plugin, EditorGUI gui, Player player) {
        super(plugin, player, 54);

        this.gui = gui;

        setTitle("&3&lCrates Editor");

        setItem(49, ItemStackBuilder.of(Material.BARRIER)
                .name("&b&lCreate Crate")
                .lore("&7&o* Click to create crate *")
                .glow()
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_CREATE_CRATE_QUESTION))
                            .repeatIfFailed(true)
                            .stripColor(true)
                            .inputPredicate(input -> {
                                input = input.replaceAll(" ", "_");
                                boolean created = plugin.getCratesManager().createCrate(input);
                                if (created) {
                                    plugin.reload();
                                    player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_CREATE_CRATE_RESPONSE)));
                                    open();
                                }
                                return created;
                            })
                            .build();
                    this.fallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));
    }

    @Override
    protected void onOpen() {
        this.fallback = true;
        for (Slot slot : getSlots()) {
            if (slot.getSlot() == 49) continue;
            slot.clear();
        }
        List<Crate> crates = plugin.getCratesManager().getCrates();
        for (int index = 0; index < crates.size(); index++) {
            Crate crate = crates.get(index);
            setItem(index, ItemStackBuilder.of(crate.getSettings().getBlock())
                    .name(crate.getSettings().getDisplay())
                    .lore("", "&7&o* Click to modify *")
                    .build(event -> {
                        this.fallback = false;
                        new CrateEditorGUI(plugin, this, crate, player).open();
                    }));
        }
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        if (this.fallback) this.gui.open();
    }
}