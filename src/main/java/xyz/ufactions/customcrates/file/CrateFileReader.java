package xyz.ufactions.customcrates.file;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.CrateSettings;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.item.ItemStackFileReader;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.*;
import java.util.logging.Level;

public final class CrateFileReader {

    private final CustomCrates plugin;
    private final FileConfiguration configuration;

    public CrateFileReader(CustomCrates plugin, FileConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    /**
     * @return The crate unique identifier
     */
    public String readIdentifier() {
        String identifier = configuration.getString("Crate.identifier");
        if (identifier != null) identifier = cleanIdentifier(identifier);
        return identifier;
    }

    /**
     * @return The crate block item
     */
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

    /**
     * @return Crate inventory title if present
     */
    public Optional<String> readDisplay() {
        if (configuration.isString("Crate.display"))
            return Optional.ofNullable(configuration.getString("Crate.display"));
        debug("Configured display was not found.");
        return Optional.empty();
    }

    /**
     * @return Crate inventory spin time if present
     */
    public OptionalLong readSpinTime() {
        if (configuration.isLong("Crate.spin time")) return OptionalLong.of(configuration.getLong("Crate.spin time"));
        debug("Configured spin time was not found.");
        return OptionalLong.empty();
    }

    /**
     * @return Sound to play when crate is opened
     */
    public Optional<Sound> readOpeningSound() {
        if (configuration.isString("Crate.open sound"))
            return Optional.ofNullable(plugin.getSoundManager().parse(configuration.getString("Crate.open sound")));
        debug("Configured open sound was not found.");
        return Optional.empty();
    }

    /**
     * @return Sound to play while crate is spinning
     */
    public Optional<Sound> readSpinSound() {
        if (configuration.isString("Crate.spin sound"))
            return Optional.ofNullable(plugin.getSoundManager().parse(configuration.getString("Crate.spin sound")));
        debug("Configured spin sound was not found.");
        return Optional.empty();
    }

    /**
     * @return Sound to play when a prize is won
     */
    public Optional<Sound> readWinSound() {
        if (configuration.isString("Crate.win sound"))
            return Optional.ofNullable(plugin.getSoundManager().parse(configuration.getString("Crate.win sound")));
        debug("Configured win sound was not found.");
        return Optional.empty();
    }

    /**
     * @return Commands to be run when crate is opened
     */
    public List<String> readCommands() {
        return configuration.getStringList("Crate.open-commands");
    }

    /**
     * @return Crate key item
     */
    public ItemStackBuilder readKey() {
        return readItemStack("Key").orElse(null);
    }

    /**
     * @return Crate pouch item
     */
    public Optional<ItemStackBuilder> readPouch() {
        return readItemStack("pouch");
    }

    /**
     * @return Crate spin type if present
     */
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

    /**
     * @return All configured prizes
     */
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
                ItemStackBuilder builder = readItemStack(section.getCurrentPath() + ".display").orElseGet(() -> ItemStackBuilder.of(Material.AIR));
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

    /**
     * @return Holographic Lines
     */
    public List<String> readHologramLines() {
        return configuration.getStringList("hologram.lines");
    }

    /**
     * @return Holographic Items
     */
    public Map<String, ItemStackBuilder> readHologramItems() {
        Map<String, ItemStackBuilder> map = new HashMap<>();
        if (configuration.isConfigurationSection("hologram.items")) {
            for (String key : configuration.getConfigurationSection("hologram.items").getKeys(false)) {
                debug("Mapping hologram item " + key);
                if (!configuration.isConfigurationSection("hologram.items." + key)) continue;
                ConfigurationSection section = configuration.getConfigurationSection("hologram.items." + key);
                String identifier = section.getString("identifier");
                ItemStackBuilder builder = readItemStack(section.getCurrentPath()).orElse(ItemStackBuilder.of(Material.AIR));
                map.put(identifier, builder);
                debug("Hologram Item Mapped.");
            }
        }
        return map;
    }

    public Crate read() {
        String identifier = readIdentifier();
        if (identifier == null || identifier.isEmpty()) {
            warn("Identifier is null or is empty '" + identifier + "'");
            return null;
        }
        ItemStackBuilder key = readKey();
        if (key == null) {
            warn("Could not load key from configuration.");
            return null;
        }

        String display = readDisplay().orElse(identifier);
        List<String> commands = readCommands();
        Material block = readBlock();
        Spin.SpinType spinType = readSpinType().orElse(Spin.SpinType.CSGO);
        long spinTime = readSpinTime().orElse(2500);
        Sound openingSound = readOpeningSound().orElseGet(() ->
                this.plugin.getSoundManager().parseOrDefault(
                        "BLOCK_CHEST_OPEN",
                        "CHEST_OPEN"));
        Sound spinSound = readSpinSound().orElseGet(() ->
                this.plugin.getSoundManager().parseOrDefault(
                        "BLOCK_NOTE_BLOCK_PLING",
                        "NOTE_PLING"));
        Sound winSound = readWinSound().orElseGet(() ->
                this.plugin.getSoundManager().parseOrDefault(
                        "ENTITY_PLAYER_LEVELUP",
                        "LEVEL_UP"));
        ItemStackBuilder pouch = readPouch().orElse(ItemStackBuilder.of(Material.AIR));
        RandomizableList<Prize> prizes = readPrizes();
        List<String> hologramLines = readHologramLines();
        Map<String, ItemStackBuilder> hologramItems = readHologramItems();

        CrateSettings settings = new CrateSettings(identifier,
                display,
                commands,
                block,
                spinType,
                spinTime,
                openingSound,
                spinSound,
                winSound,
                key,
                pouch,
                prizes,
                hologramItems,
                hologramLines);
        return new Crate(plugin, settings);
    }

    private Optional<ItemStackBuilder> readItemStack(String path) {
        debug("Attempting to read itemstack from given path '" + path + "'");
        if (configuration.isConfigurationSection(path)) {
            ItemStackFileReader itemStackConfiguration = ItemStackFileReader.of(configuration.getConfigurationSection(path));
            ItemStackBuilder builder = itemStackConfiguration.read();
            debug("Item read: " + builder);
            return Optional.ofNullable(builder);
        }
        debug("Configured itemstack was not found.");
        return Optional.empty();
    }

    private void warn(Object o) {
        warn(o, null);
    }

    private void warn(Object o, Exception e) {
        String identifier = readIdentifier();
        String msg = "(" + identifier + ") " + o + ". Please enable debugging in the config.yml before submitting a support ticket via discord.";
        if (e == null)
            this.plugin.getLogger().warning(msg);
        else
            this.plugin.getLogger().log(Level.WARNING, msg, e);
    }

    private void debug(Object o) {
        this.plugin.debug("(" + configuration.getName() + ") " + o);
    }

    private String cleanIdentifier(String identifier) {
        return identifier.replaceAll(" ", "_");
    }
}