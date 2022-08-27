package xyz.ufactions.customcrates.file;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackFileWriter;
import xyz.ufactions.customcrates.libs.RandomizableList;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CrateFileWriter {

    private final CustomCrates plugin;

    private final Crate crate;

    private final File file;
    private final FileConfiguration config;

    public CrateFileWriter(CustomCrates plugin, Crate crate) {
        this.plugin = plugin;

        this.crate = crate;

        if (!plugin.getCratesFile().crateExist(crate.getSettings().getIdentifier()))
            plugin.getCratesFile().createCrate(crate.getSettings().getIdentifier());
        this.file = plugin.getCratesFile().getFileByCrate(crate);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void writeBlock() {
        config.set("Crate.block", crate.getSettings().getBlock().name());
        debug("Block written");
    }

    public void writeDisplay() {
        config.set("Crate.display", crate.getSettings().getDisplay());
        debug("Display written");
    }

    public void writeSpinTime() {
        config.set("Crate.spin time", crate.getSettings().getSpinTime());
        debug("Spin time written");
    }

    public void writeOpenSound() {
        config.set("Crate.open sound", crate.getSettings().getOpeningSound().name());
        debug("Opening sound written");
    }

    public void writeSpinSound() {
        config.set("Crate.spin sound", crate.getSettings().getSpinSound().name());
        debug("Spin sound written");
    }

    public void writeWinSound() {
        config.set("Crate.win sound", crate.getSettings().getWinSound().name());
        debug("Win sound written");
    }

    public void writeOpenCommands() {
        config.set("Crate.open-commands", crate.getSettings().getOpenCommands());
        debug("Open commands written");
    }

    public void writeKey() {
        ItemStackFileWriter.of(crate.getSettings().getKey(), getConfigurationSection("Key")).write();
        debug("Key written");
    }

    public void writePouch() {
        ItemStackFileWriter.of(crate.getSettings().getPouch(), getConfigurationSection("pouch")).write();
        debug("Pouch written");
    }

    public void writeSpinType() {
        config.set("Crate.spin", crate.getSettings().getSpinType().name());
        debug("Spin written");
    }

    public void writePrizes() {
        ConfigurationSection section;
        for (RandomizableList<Prize>.Entry entry : crate.getSettings().getPrizes()) {
            ItemStackFileWriter.of(
                            entry.getObject().getItemBuilder(),
                            getConfigurationSection(entry.getObject().getConfigurationSection() + ".display"))
                    .write();
            section = getConfigurationSection(entry.getObject().getConfigurationSection());
            section.set("chance", entry.getObject().getChance());
            section.set("commands", entry.getObject().getCommands());
            section.set("give item", entry.getObject().isGiveItem());
        }
        debug(crate.getSettings().getPrizes().size() + " prizes written");
    }

    public void writeHologramLines() {
        config.set("hologram.lines", crate.getSettings().getHolographicLines());
        debug("Hologram lines written");
    }

    public void writeHologramItems() {
        debug("Hologram items not yet implemented.");
    }

    public void writeAll() {
        writeBlock();
        writeDisplay();
        writeSpinTime();
        writeOpenSound();
        writeSpinSound();
        writeWinSound();
        writeOpenCommands();
        writeKey();
        writePouch();
        writeSpinType();
        writePrizes();
        writeHologramItems();
        writeHologramLines();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            warn(e);
        }
    }

    private ConfigurationSection getConfigurationSection(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) section = config.createSection(path);
        return section;
    }

    private void warn(Exception exception) {
        String msg = "(" + crate.getSettings().getIdentifier() + ") Failed to save crate file. Please enable debugging in the config.yml before submitting a support ticket via discord.";
        if (exception == null)
            this.plugin.getLogger().warning(msg);
        else
            this.plugin.getLogger().log(Level.WARNING, msg, exception);
    }

    private void debug(String string) {
        this.plugin.debug("(" + config.getName() + ") " + string);
    }
}