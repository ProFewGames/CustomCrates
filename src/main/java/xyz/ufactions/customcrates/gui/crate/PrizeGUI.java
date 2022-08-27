package xyz.ufactions.customcrates.gui.crate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.PrizeEditorGUI;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.PagedGUI;
import xyz.ufactions.customcrates.item.Item;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.RandomizableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrizeGUI extends PagedGUI {

    private final Crate crate;

    public PrizeGUI(CustomCrates plugin, Crate crate, GUI fallbackGUI, Player player) {
        super(crate.getSettings().getDisplay() + "&3&l's Prizes", plugin, fallbackGUI, player);

        this.crate = crate;

        setItem(49, ItemStackBuilder.of(Material.BARRIER)
                .glow()
                .name("&b&lCreate Prize")
                .lore("&7&o* Click to create prize *")
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_PRIZE_NAME_QUESTION))
                            .stripColor(true)
                            .repeatIfFailed(true)
                            .inputPredicate(input -> {
                                for (RandomizableList<Prize>.Entry entry : crate.getSettings().getPrizes()) {
                                    if (entry.getObject().getConfigurationSection().toLowerCase().endsWith(input.toLowerCase())) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_PRIZE_EXISTS)));
                                        return false;
                                    }
                                }
                                ItemStackBuilder builder = ItemStackBuilder.of(Material.DIAMOND)
                                        .name("&b&l" + input.toUpperCase())
                                        .lore("&9Chance: &e%chance%")
                                        .glow();
                                List<String> commands = new ArrayList<>();
                                commands.add("[bc] &e&l%player% &b&lhas won &e&l1&b&lx diamond");
                                commands.add("minecraft:give %player% diamond 1");
                                Prize prize = new Prize(builder,
                                        50d,
                                        false,
                                        "Prizes." + input.toLowerCase(),
                                        commands);
                                plugin.getCratesManager().createPrize(this.crate, prize);
                                setDefaultItems();
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
    }

    private Item<InventoryClickEvent> itemFromPrize(Prize prize) {
        return prize.getItemBuilder().clone()
                .lore("", "&7&o* Click to edit *")
                .build(event -> {
                    this.openFallback = false;
                    new PrizeEditorGUI(this.plugin, this.crate, prize, this, this.player).open();
                });
    }

    @Override
    protected List<Item<InventoryClickEvent>> getItems() {
        return this.crate.getSettings()
                .getPrizes()
                .getEntries()
                .stream()
                .map(entry -> itemFromPrize(entry.getObject()))
                .collect(Collectors.toList());
    }

    @Override
    protected List<Item<InventoryClickEvent>> getSearchItems(String search) {
        return getItems(); // Method will not be used for this GUI
    }
}