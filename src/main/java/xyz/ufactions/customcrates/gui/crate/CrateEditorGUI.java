package xyz.ufactions.customcrates.gui.crate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.CrateFileWriter;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.CommandsGUI;
import xyz.ufactions.customcrates.gui.CratesGUI;
import xyz.ufactions.customcrates.gui.DeleteConfirmationGUI;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.item.ItemGUI;
import xyz.ufactions.customcrates.gui.item.MaterialGUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.universal.UniversalMaterial;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CrateEditorGUI extends GUI {

    private final Crate crate;
    private final CratesGUI gui;

    private boolean fallback = true;

    public CrateEditorGUI(CustomCrates plugin, CratesGUI gui, Crate crate, Player player) {
        super(plugin, player, 54);

        this.gui = gui;
        this.crate = crate;

        setTitle(crate.getSettings().getDisplay() + "&3&l Editor");

        setItem(40, ItemStackBuilder.of(Material.BARRIER)
                .name("&c&lDelete")
                .lore("&4&lWARNING THIS ACTION IS IRREVERSIBLE",
                        "",
                        "&7&o* Click to delete crate *")
                .build(event -> {
                    this.fallback = false;
                    new DeleteConfirmationGUI(response -> {
                        if (response == DeleteConfirmationGUI.DeletionResponse.DECLINED)
                            open();
                        else {
                            plugin.getCratesManager().deleteCrate(crate);
                            plugin.reload();
                            this.gui.open();
                        }
                    }, plugin, player).open();
                }));
    }

    @Override
    protected void onOpen() {
        this.fallback = true;

        setItem(11, ItemStackBuilder.of(crate.getSettings().getBlock())
                .hideAttributes()
                .name("&b&lChange Crate Block")
                .lore("",
                        "&e&lCurrent Block:",
                        "&f&l" + F.capitalizeFirstLetter(crate.getSettings().getBlock().name()),
                        "",
                        "&7&o* Click to change *")
                .build(event -> {
                    this.fallback = false;
                    new MaterialGUI(material -> {
                        crate.getSettings().setBlock(material);
                        CrateFileWriter writer = new CrateFileWriter(plugin, crate);
                        writer.writeBlock();
                        writer.save();
                        for (Location location : plugin.getLocationsFile().getLocations(crate))
                            location.getBlock().setType(material, false);
                    }, Arrays.stream(Material.values()).filter(Material::isBlock).collect(Collectors.toList()), plugin, this, player).open();
                }));
        setItem(20, ItemStackBuilder.of(UniversalMaterial.SIGN.get())
                .name("&b&lChange Display")
                .lore("",
                        "&e&lCurrent Display:",
                        "&f&l" + crate.getSettings().getDisplay(),
                        "",
                        "&7&o* Click to change *")
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_CRATE_DISPLAY_QUESTION))
                            .stripColor(false)
                            .inputPredicate(input -> {
                                crate.getSettings().setDisplay(input);
                                CrateFileWriter writer = new CrateFileWriter(plugin, crate);
                                writer.writeDisplay();
                                writer.save();
                                new CrateEditorGUI(plugin, gui, crate, player).open();
                                return true;
                            }).build();
                    this.fallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));
        setItem(29, ItemStackBuilder.of(crate.getSettings().getSpinType().getDisplayItem())
                .name("&b&lSelect Spin Type")
                .lore("",
                        "&e&lCurrent Spin:",
                        "&f&l" + F.capitalizeFirstLetter(crate.getSettings().getSpinType().name()),
                        "",
                        "&7&o* Click to change *")
                .build(event -> {
                    this.fallback = false;
                    new SpinTypeGUI(plugin, this, crate, player).open();
                }));

        setItem(15, ItemStackBuilder.of(UniversalMaterial.COMMAND_BLOCK.get())
                .glow()
                .name("&b&lEdit Open Commands")
                .lore("", "&e&lCurrent Commands:")
                .lore(crate.getSettings().getOpenCommands().stream().map(command -> "&f" + command).collect(Collectors.toList()))
                .lore("", "&7&o* Click to edit *")
                .build(event -> {
                    this.fallback = false;
                    new CommandsGUI(commands -> {
                        crate.getSettings().setOpenCommands(commands);
                        CrateFileWriter writer = new CrateFileWriter(plugin, crate);
                        writer.writeOpenCommands();
                        writer.save();
                    }, () -> crate.getSettings().getOpenCommands(), plugin, this, player).open();
                }));

        setItem(24, ItemStackBuilder.of(Material.TRIPWIRE_HOOK)
                .name("&b&lEdit Key")
                .lore("", "&7&o* Click to edit *")
                .glow()
                .build(event -> {
                    this.fallback = false;
                    new ItemGUI(builder -> {
                        crate.getSettings().setKey(builder);
                        CrateFileWriter writer = new CrateFileWriter(plugin, crate);
                        writer.writeKey();
                        writer.save();
                    }, plugin, crate.getSettings().getKey(), this, player).open();
                }));

        setItem(33, ItemStackBuilder.of(Material.JUKEBOX)
                .name("&b&lEdit Prizes")
                .lore("", "&7&o* Click to edit prizes *")
                .build(event -> {
                    this.fallback = false;
                    new PrizeGUI(plugin, crate, this, player).open();
                }));
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        if (this.fallback) this.gui.open();
    }
}