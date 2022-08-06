package xyz.ufactions.customcrates.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.CrateFileWriter;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.item.ItemGUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.universal.UniversalMaterial;

import java.util.stream.Collectors;

public class PrizeEditorGUI extends ItemGUI {

    private final Prize prize;
    private final Crate crate;

    public PrizeEditorGUI(CustomCrates plugin, Crate crate, Prize prize, GUI fallbackGUI, Player player) {
        super(builder -> {
            CrateFileWriter writer = new CrateFileWriter(plugin, crate);
            writer.writePrizes();
            writer.save();
        }, plugin, prize.getItemBuilder(), fallbackGUI, player);

        this.prize = prize;
        this.crate = crate;
    }

    private void saveCrate() {
        CrateFileWriter writer = new CrateFileWriter(this.plugin, this.crate);
        writer.writePrizes();
        writer.save();
    }

    @Override
    protected void onOpen() {
        super.onOpen();

        setItem(47, ItemStackBuilder.of(UniversalMaterial.COMMAND_BLOCK.get())
                .name("&b&lEdit Commands")
                .lore("", "&e&lCommands:")
                .lore(this.prize.getCommands().stream().map(command -> "&f&l" + command).collect(Collectors.toList()))
                .lore("", "&7&o* Click to edit *")
                .build(event -> {
                    this.fallback = false;
                    new CommandsGUI(commands -> {
                        prize.setCommands(commands);
                        saveCrate();
                    }, prize::getCommands, plugin, this, player).open();
                }));

        setItem(49, ItemStackBuilder.of(Material.DIAMOND)
                .name("&b&lEdit Chance")
                .lore("", "&e&lChance:", "&f&l" + this.prize.getChance(), "", "&7&o* Click to edit *")
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_PRIZE_CHANCE_QUESTION))
                            .stripColor(true)
                            .repeatIfFailed(true)
                            .inputPredicate(input -> {
                                double chance;
                                try {
                                    chance = Double.parseDouble(input);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_PRIZE_CHANCE_UNKNOWN_FORMAT)));
                                    return false;
                                }
                                prize.setChance(chance);
                                saveCrate();
                                open();
                                return true;
                            }).build();
                    this.fallback = false;
                    player.closeInventory();
                    this.plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));

        setItem(51, ItemStackBuilder.of(Material.BARRIER)
                .glow()
                .name("&4&lDELETE")
                .lore("&c&lWARNING THIS ACTION IS IRREVERSIBLE", "", "&7&o* Click to delete *")
                .build(event -> {
                    this.fallback = false;
                    new DeleteConfirmationGUI(response -> {
                        if (response == DeleteConfirmationGUI.DeletionResponse.DECLINED)
                            open();
                        else if (response == DeleteConfirmationGUI.DeletionResponse.ACCEPTED) {
                            this.plugin.getCratesManager().deletePrize(this.crate, this.prize);
                            this.fallbackGUI.open();
                        }
                    }, plugin, player).open();
                }));
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        super.handleClose(event);

        if (this.fallback)
            this.plugin.reload();
    }
}