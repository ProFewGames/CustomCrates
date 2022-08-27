package xyz.ufactions.customcrates.gui.preview;

import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.List;
import java.util.stream.Collectors;

public interface PreviewGUI {

    Crate getCrate();

    default List<ItemStackBuilder> getPrizeItemBuilders() {
        return getCrate().getSettings()
                .getPrizes()
                .getEntries()
                .stream()
                .map(entry -> entry.getObject().getItemBuilder()).collect(Collectors.toList());
    }

    default String getTitle() {
        return getCrate().getSettings().getDisplay();
    }
}