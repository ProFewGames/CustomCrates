package xyz.ufactions.customcrates.file.crate;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackFileIO;
import xyz.ufactions.customcrates.libs.RandomizableList;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

class CrateFileWriterImpl implements CrateFileWriter {

    private final CustomCrates plugin;

    private final Crate crate;

    private final File file;
    private final FileConfiguration config;

    CrateFileWriterImpl(CustomCrates plugin, Crate crate) {
        this.plugin = plugin;
        this.crate = crate;

        File file = plugin.getCrateFileHandler().getFileByCrate(crate);
        if (file == null)
            file = new File(((CrateFileHandlerImpl) plugin.getCrateFileHandler()).getCratesDirectory(),
                    crate.getSettings().getIdentifier() + ".yml");
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void writeIdentifier() {
        config.set("Crate.identifier", crate.getSettings().getIdentifier());
        debug("Identifier Written");
    }

    @Override
    public void writeBlock() {
        config.set("Crate.block", crate.getSettings().getBlock().name());
        debug("Block written");
    }

    @Override
    public void writeDisplay() {
        config.set("Crate.display", crate.getSettings().getDisplay());
        debug("Display written");
    }

    @Override
    public void writeSpinTime() {
        config.set("Crate.spin time", crate.getSettings().getSpinTime());
        debug("Spin time written");
    }

    @Override
    public void writeOpenSound() {
        Sound openingSound = crate.getSettings().getOpeningSound();
        if (openingSound == null) {
            debug("No set open sound, skipping...");
            return;
        }
        config.set("Crate.open sound", openingSound.name());
        debug("Opening sound written");
    }

    @Override
    public void writeSpinSound() {
        Sound spinSound = crate.getSettings().getSpinSound();
        if (spinSound == null) {
            debug("No set spin sound, skipping...");
            return;
        }
        config.set("Crate.spin sound", spinSound.name());
        debug("Spin sound written");
    }

    @Override
    public void writeWinSound() {
        Sound winSound = crate.getSettings().getWinSound();
        if (winSound == null) {
            debug("No set win sound, skipping...");
            return;
        }
        config.set("Crate.win sound", winSound.name());
        debug("Win sound written");
    }

    @Override
    public void writeOpenCommands() {
        config.set("Crate.open-commands", crate.getSettings().getOpenCommands());
        debug("Open commands written");
    }

    @Override
    public void writeKey() {
        ConfigurationSection section = config.getConfigurationSection("Key");
        if (section == null)
            section = config.createSection("Key");
        ItemStackFileIO.create(plugin, section).write(crate.getSettings().getKey());
        debug("Key written");
    }

    @Override
    public void writePouch() {
        ConfigurationSection section = config.getConfigurationSection("pouch");
        if (section == null)
            section = config.createSection("pouch");
        ItemStackFileIO.create(plugin, section)
                .write(crate.getSettings().getPouch());
        debug("Pouch written");
    }

    @Override
    public void writeSpinType() {
        config.set("Crate.spin", crate.getSettings().getSpinType().name());
        debug("Spin written");
    }

    @Override
    public void writePrize(Prize prize) {
        ConfigurationSection section = config.getConfigurationSection(prize.getConfigurationSection());
        if (section == null)
            section = config.createSection(prize.getConfigurationSection());
        section.set("chance", prize.getChance());
        section.set("commands", prize.getCommands());
        section.set("give item", prize.isGiveItem());
        ItemStackFileIO.create(plugin, section).write(prize.getItemBuilder());
        debug("Prize written");
    }

    @Override
    public void writePrizes() {
        for (RandomizableList<Prize>.Entry entry : crate.getSettings().getPrizes())
            writePrize(entry.getObject());
        debug(crate.getSettings().getPrizes().size() + " prizes written");
    }

    @Override
    public void writeHologramOffset() {
        config.set("hologram.offset", crate.getSettings().getHologramOffset());
        debug("Hologram offset written");
    }

    @Override
    public void writeHologramLines() {
        config.set("hologram.lines", crate.getSettings().getHolographicLines());
        debug("Hologram lines written");
    }

    @Override
    public void writeHologramItems() {
        debug("Hologram items not yet implemented.");
    }

    @Override
    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,
                    String.format("(%s) Failed to save crate file. Please submit the following error when creating a ticket:",
                            this.crate.getSettings().getIdentifier()),
                    e);
        }
    }

    private void debug(String string) {
        this.plugin.debug("(" + this.config.getName() + ") " + string);
    }
}
