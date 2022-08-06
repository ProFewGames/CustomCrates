package xyz.ufactions.customcrates.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.Slot;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.universal.UniversalMaterial;

import java.util.List;
import java.util.function.Consumer;

public class LoreGUI extends GUI {

    private final Consumer<ItemStackBuilder> consumer;
    private final ItemStackBuilder builder;
    private final GUI gui;

    private boolean fallback = true;

    public LoreGUI(Consumer<ItemStackBuilder> consumer, ItemStackBuilder builder, GUI gui, CustomCrates plugin, Player player) {
        super(plugin, player);

        this.consumer = consumer;
        this.builder = builder;
        this.gui = gui;

        setTitle("&3&lLore Editor");

        List<String> lore = builder.getLore();
        setInventorySize(lore.size() + 2);

        getSlot(0).apply(ItemStackBuilder.of(UniversalMaterial.SIGN.get())
                .name("&b&lAdd Line")
                .lore("&7&o* Click to add line *")
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_ADD_LORE_QUESTION))
                            .stripColor(false)
                            .inputPredicate(input -> {
                                if (ChatColor.stripColor(input).equals("\" \""))
                                    this.builder.lore(" ");
                                else
                                    this.builder.lore(input);
                                open();
                                return true;
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
            if (slot.getSlot() == 0) continue;
            slot.clear();
        }

        seatLines();

        List<String> lore = builder.getLore();
        if (!lore.isEmpty())
            getSlot(1).apply(ItemStackBuilder.of(Material.BARRIER)
                    .name("&4&lClear Lore")
                    .lore("&7&o* Click to clear lore *")
                    .build(event -> {
                        this.builder.clearLore();
                        open();
                    }));
    }

    private void seatLines() {
        for (Slot slot : getSlots())
            if (slot.getSlot() >= 2)
                slot.clear();
        List<String> lore = builder.getLore();
        for (int index = 0; index < lore.size(); index++) {
            final int finalIndex = index;
            getSlot(index + 2).apply(ItemStackBuilder.of(Material.PAPER)
                    .name(lore.get(index))
                    .lore(
                            "",
                            "&7&o* &ePress Q &7&oto remove this line *",
                            "&7&o* &eLeft Click &7&oto move line up *",
                            "&7&o* &eRight Click &7&oto move line down *"
                    )
                    .build(event -> {
                        if (event.getClick() == ClickType.DROP) {
                            List<String> newLore = builder.getLore();
                            newLore.remove(finalIndex);
                            builder.clearLore();
                            builder.lore(newLore);
                        } else {
                            List<String> newLore;
                            if (event.isLeftClick())
                                newLore = sortLoreIndex(finalIndex, finalIndex - 1);
                            else
                                newLore = sortLoreIndex(finalIndex, finalIndex + 1);
                            if (newLore == null) return;
                            builder.clearLore();
                            builder.lore(newLore);
                        }
                        seatLines();
                    }));
        }
    }

    private List<String> sortLoreIndex(int oldIndex, int newIndex) {
        if (oldIndex < 0) return null;
        if (newIndex < 0) return null;
        List<String> lore = builder.getLore();
        if (oldIndex >= lore.size()) return null;
        if (newIndex >= lore.size()) return null;
        String line = lore.remove(oldIndex);
        lore.add(newIndex, line);
        return lore;
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        if (!this.fallback) return;
        this.consumer.accept(this.builder);
        this.gui.open();
    }
}