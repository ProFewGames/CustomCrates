package xyz.ufactions.customcrates.gui.preview;

import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.List;

public class RegularPreviewGUI extends GUI implements PreviewGUI {

    private final Crate crate;

    public RegularPreviewGUI(CustomCrates plugin, Crate crate, Player player) {
        super(plugin, player, crate.getSettings().getPrizes().size());

        this.crate = crate;

        setTitle(getTitle());
        List<ItemStackBuilder> builders = getPrizeItemBuilders();
        for (int index = 0; index < builders.size(); index++) {
            setItem(index, builders.get(index).build(event -> {
            }));
        }
    }

    @Override
    public Crate getCrate() {
        return this.crate;
    }
}
