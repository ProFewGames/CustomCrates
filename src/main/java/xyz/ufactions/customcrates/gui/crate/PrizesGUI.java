package xyz.ufactions.customcrates.gui.crate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.PrizeEditorGUI;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.Slot;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.RandomizableList;

import java.util.Arrays;

public class PrizesGUI extends GUI {

    private final GUI gui;
    private final Crate crate;

    private boolean fallback = true;

    public PrizesGUI(CustomCrates plugin, Crate crate, GUI gui, Player player) {
        super(plugin, player, 54);

        this.gui = gui;
        this.crate = crate;

        setTitle(crate.getSettings().getDisplay() + "&3&l's Prizes");

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
                                Prize prize = new Prize(builder,
                                        50d,
                                        "Prizes." + input.toLowerCase(),
                                        Arrays.asList("[bc] &e&l%player% &b&lhas won &e&l1&b&lx diamond", "minecraft:give %player% diamond 1"));
                                plugin.getCratesManager().createPrize(this.crate, prize);
                                open();
                                return true;
                            }).build();
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

        for (Slot slot : getSlots())
            if (slot.getSlot() != 49)
                slot.clear();

        int slot = 0;
        for (RandomizableList<Prize>.Entry entry : this.crate.getSettings().getPrizes()) {
            Prize prize = entry.getObject();
            setItem(slot++, prize.getItemBuilder().clone()
                    .lore("", "&7&o* Click to edit *")
                    .build(event -> {
                        this.fallback = false;
                        new PrizeEditorGUI(plugin, crate, prize, this, player).open();
                    }));
        }
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        if (this.fallback) this.gui.open();
    }
}