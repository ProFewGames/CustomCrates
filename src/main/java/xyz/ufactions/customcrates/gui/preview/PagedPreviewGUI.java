package xyz.ufactions.customcrates.gui.preview;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.gui.internal.PagedGUI;
import xyz.ufactions.customcrates.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public class PagedPreviewGUI extends PagedGUI implements PreviewGUI {

    private final Crate crate;

    public PagedPreviewGUI(CustomCrates plugin, Crate crate, Player player) {
        super(crate.getSettings().getDisplay(), plugin, null, player);

        this.crate = crate;

        getSlot(49).clear();
    }

    @Override
    public Crate getCrate() {
        return crate;
    }

    @Override
    protected List<Item<InventoryClickEvent>> getItems() {
        return getPrizeItemBuilders().stream()
                .map(builder -> builder.build(event -> {
                })).collect(Collectors.toList());
    }

    @Override
    protected List<Item<InventoryClickEvent>> getSearchItems(String search) {
        return getItems();
    }
}