package xyz.ufactions.customcrates.file.crate;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.CrateSettings;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.item.ItemStackFileIO;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.*;
import java.util.logging.Level;

@RequiredArgsConstructor
class CrateFileReaderImpl implements CrateFileReader {

    private final CustomCrates plugin;
    private final FileConfiguration configuration;

    /*
    We're caching the identifier for this reader because it is used on multiple occasions to debug and warn, we don't
    want to repeatedly fetch the identifier from config everytime it is called.
     */

    private String identifier;

    @Override
    public String readIdentifier() {
        if (this.identifier != null) return this.identifier;
        String identifier = this.configuration.getString("Crate.identifier");
        if (identifier != null) identifier = identifier.replaceAll(" ", "_");
        return this.identifier = identifier;
    }

    // *** Utilities ***

    private Optional<ItemStackBuilder> readItemStack(String path) {
        debug(String.format("Attempting to read item from given path '%s'", path));
        if (!configuration.isConfigurationSection(path)) {
            warn(String.format("Path '%s' is not a configuration section.", path));
            return Optional.empty();
        }
        ConfigurationSection section = configuration.getConfigurationSection(path);
        ItemStackBuilder item = ItemStackFileIO.create(this.plugin, section).read();
        return Optional.ofNullable(item);
    }

    private void debug(Object obj) {
        this.plugin.debug(String.format("(%s) %s", readIdentifier(), obj));
    }

    private void warn(Object obj) {
        warn(obj, null);
    }

    private void warn(Object obj, Exception exception) {
        String identifier = readIdentifier();
        String msg = "(" + identifier + ") " + obj + ". Please enable debugging in your config.yml before submitting a support ticket via Discord.";
        if (exception == null)
            this.plugin.getLogger().warning(msg);
        else
            this.plugin.getLogger().log(Level.WARNING, msg, exception);
    }

    // *** Reading ***

    @Override
    public Material readBlock() {
        if (!configuration.isString("Crate.block")) {
            debug("Could not find configured block. Defaulting to chest");
            return Material.CHEST;
        }
        try {
            Material material = Material.valueOf(configuration.getString("Crate.block"));
            if (!material.isBlock())
                debug("'" + material.name() + "' is not a block.");
            return material;
        } catch (EnumConstantNotPresentException e) {
            debug("Configured block was not found. Defaulting to chest");
            return Material.CHEST;
        }
    }

    @Override
    public Optional<String> readDisplay() {
        if (configuration.isString("Crate.display"))
            return Optional.ofNullable(configuration.getString("Crate.display"));
        debug("Configured display was not found.");
        return Optional.empty();
    }

    @Override
    public OptionalLong readSpinTime() {
        if (configuration.isLong("Crate.spin time") || configuration.isInt("Crate.spin time"))
            return OptionalLong.of(configuration.getLong("Crate.spin time"));
        debug("Configured spin time was not found.");
        return OptionalLong.empty();
    }

    @Override
    public Optional<Sound> readOpenSound() {
        if (configuration.isString("Crate.open sound"))
            return Optional.ofNullable(plugin.getSoundFactory().parse(configuration.getString("Crate.open sound")));
        debug("Configured open sound was not found.");
        return Optional.empty();
    }

    @Override
    public Optional<Sound> readSpinSound() {
        if (configuration.isString("Crate.spin sound"))
            return Optional.ofNullable(plugin.getSoundFactory().parse(configuration.getString("Crate.spin sound")));
        debug("Configured spin sound was not found.");
        return Optional.empty();
    }

    @Override
    public Optional<Sound> readWinSound() {
        if (configuration.isString("Crate.win sound"))
            return Optional.ofNullable(plugin.getSoundFactory().parse(configuration.getString("Crate.win sound")));
        debug("Configured win sound was not found.");
        return Optional.empty();
    }

    @Override
    public List<String> readCommands() {
        return configuration.getStringList("Crate.open-commands");
    }

    @Override
    public ItemStackBuilder readKey() {
        return readItemStack("Key").orElse(null);
    }

    @Override
    public Optional<ItemStackBuilder> readPouch() {
        return readItemStack("pouch");
    }

    @Override
    public Optional<Spin.SpinType> readSpinType() {
        if (configuration.isString("Crate.spin")) {
            try {
                Spin.SpinType type = Spin.SpinType.valueOf(configuration.getString("Crate.spin"));
                return Optional.of(type);
            } catch (IllegalArgumentException e) {
                warn("Could not find configured spin type");
                return Optional.empty();
            }
        }
        debug("Configured spin type was not found");
        return Optional.empty();
    }

    @Override
    public RandomizableList<Prize> readPrizes() {
        RandomizableList<Prize> prizes = new RandomizableList<>();
        if (!configuration.isConfigurationSection("Prizes")) {
            debug("Prizes is not configuration section.");
            return prizes;
        }
        for (String key : configuration.getConfigurationSection("Prizes").getKeys(false)) {
            debug("Loading prize " + key);
            try {
                ConfigurationSection section = configuration.getConfigurationSection("Prizes." + key);
                ItemStackBuilder builder = readItemStack(section.getCurrentPath()).orElseGet(() -> ItemStackBuilder.of(Material.AIR));
                double chance = section.getDouble("chance");
                List<String> commands = section.getStringList("commands");
                boolean giveItem = section.getBoolean("give item", false);
                prizes.add(new Prize(builder, chance, giveItem, section.getCurrentPath(), commands), chance);
            } catch (Exception e) {
                warn("Failed to read configured prize '" + key + "'.", e);
            }
        }
        return prizes;
    }

    @Override
    public List<String> readHologramLines() {
        return configuration.getStringList("hologram.lines");
    }

    @Override
    public OptionalDouble readHologramOffset() {
        if (configuration.isDouble("hologram.offset") || configuration.isInt("hologram.offset"))
            return OptionalDouble.of(configuration.getDouble("hologram.offset"));
        return OptionalDouble.empty();
    }

    @Override
    public Map<String, ItemStackBuilder> readHologramItems() {
        debug("Hologram Items Not Yet Implemented");
        return new HashMap<>();
    }

    @Override
    public Crate readAll() {
        String identifier = readIdentifier();
        if (identifier == null || identifier.isEmpty()) {
            warn(String.format("Identifier is null or is empty '%s'", identifier));
            return null;
        }
        ItemStackBuilder key = readKey();
        if (key == null) {
            warn("Could not read key from configuration.");
            return null;
        }

        // Properties
        String display = readDisplay().orElse(identifier);
        List<String> commands = readCommands();
        Material block = readBlock();
        Spin.SpinType spinType = readSpinType().orElse(Spin.SpinType.CSGO);
        long spinTime = readSpinTime().orElse(2_500 /* 2.5 Seconds */);

        RandomizableList<Prize> prizes = readPrizes();

        // Pouch
        ItemStackBuilder pouch = readPouch().orElse(ItemStackBuilder.of(Material.AIR));

        // Sound
        Sound openSound = readOpenSound().orElseGet(() ->
                this.plugin.getSoundFactory().parseOrDefault(
                        "BLOCK_CHEST_OPEN",
                        "CHEST_OPEN"));
        Sound spinSound = readSpinSound().orElseGet(() ->
                this.plugin.getSoundFactory().parseOrDefault(
                        "BLOCK_NOTE_BLOCK_PLING",
                        "NOTE_PLING"));
        Sound winSound = readWinSound().orElseGet(() ->
                this.plugin.getSoundFactory().parseOrDefault(
                        "ENTITY_PLAYER_LEVELUP",
                        "LEVEL_UP"));

        // Holograms
        List<String> hologramLines = readHologramLines();
        double hologramOffset = readHologramOffset().orElse(-1.4);
        Map<String, ItemStackBuilder> hologramItems = readHologramItems();

        CrateSettings settings = new CrateSettings(identifier,
                display,
                commands,
                block,
                spinType,
                spinTime,
                openSound,
                spinSound,
                winSound,
                key,
                pouch,
                prizes,
                hologramItems,
                hologramOffset,
                hologramLines);
        return new Crate(this.plugin, settings);
    }
}
