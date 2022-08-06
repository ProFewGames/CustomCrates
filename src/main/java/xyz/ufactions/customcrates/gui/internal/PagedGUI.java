package xyz.ufactions.customcrates.gui.internal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.item.Item;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class PagedGUI extends GUI {

    private final GUI fallbackGUI;

    protected boolean openFallback = true;

    private List<Item<InventoryClickEvent>> items;
    private String search;
    private int page = 0;

    public PagedGUI(String title, CustomCrates plugin, GUI fallbackGUI, Player player) {
        super(plugin, player, 54);

        this.fallbackGUI = fallbackGUI;
        setTitle(title);

        setItem(47, ItemStackBuilder.of(Material.BOOK)
                .name("&b&lPrevious Page")
                .lore("&7&o* Click to view the previous page *")
                .glow()
                .build(event -> {
                    if (this.page == 0) return;
                    this.page -= 1;
                    seatItems();
                }));

        setItem(49, ItemStackBuilder.of(Material.PAPER)
                .name("&b&lQuick Search")
                .lore("&7&o* Click to search *")
                .glow()
                .build(event -> {
                    Question question = Question.create("Enter Lookup") // TODO: 8/5/2022 Language
                            .stripColor(true)
                            .inputPredicate(input -> {
                                this.search = input;
                                this.page = 0;
                                this.items.clear();
                                this.items.addAll(getSearchItems(input));

                                open();
                                return true;
                            }).build();
                    this.openFallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));

        setItem(51, ItemStackBuilder.of(Material.BOOK)
                .name("&b&lNext Page")
                .lore("&7&o* Click to view the next page *")
                .build(event -> {
                    if ((this.page + 1) * 45 > this.items.size()) return;
                    this.page += 1;
                    seatItems();
                }));
    }

    @Override
    protected final void onOpen() {
        this.openFallback = true;

        if (this.items == null) setDefaultItems();

        seatSearch();
        seatItems();
    }

    @Override
    protected final void handleClose(InventoryCloseEvent event) {
        if (this.openFallback) this.fallbackGUI.open();
    }

    private void setDefaultItems() {
        if (this.items == null)
            this.items = new ArrayList<>();
        this.items.clear();
        this.items.addAll(getItems());
    }

    private void seatSearch() {
        if (this.search == null) {
            getSlot(45).clear();
            getSlot(53).clear();
            return;
        }
        setItem(45, ItemStackBuilder.of(Material.EMERALD)
                .name("&b&lCurrent Search:")
                .lore("&f&l" + this.search)
                .build(event -> {
                }));
        setItem(53, ItemStackBuilder.of(Material.BARRIER)
                .name("&b&lClear Search")
                .lore("&7&o* Click to clear search *")
                .build(event -> {
                    this.search = null;
                    this.page = 0;
                    setDefaultItems();
                    seatSearch();
                    seatItems();
                }));
    }

    private void seatItems() {
        for (int slot = 0; slot < 45; slot++)
            getSlot(slot).clear();

        List<Item<InventoryClickEvent>> items = this.items.subList(page * 45, Math.min(45 + (page * 45), this.items.size()));
        for (int index = 0; index < items.size(); index++) {
            setItem(index, items.get(index));
        }
    }

    protected abstract List<Item<InventoryClickEvent>> getItems();

    protected abstract List<Item<InventoryClickEvent>> getSearchItems(String search);
}