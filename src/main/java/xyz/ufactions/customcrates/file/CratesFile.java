package xyz.ufactions.customcrates.file;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.CrateSettings;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.FileHandler;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.WeightedList;
import xyz.ufactions.customcrates.spin.Spin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CratesFile {

    private final HashMap<String, Crate> crates;
    private final CustomCrates plugin;
    private File directory;

    public CratesFile(CustomCrates plugin) {
        this.plugin = plugin;
        this.crates = new HashMap<>();
        reload();
    }

    private File locateFile(String identifier) {
        for (File file : directory.listFiles()) { // TODO : Check empty directory | Directory Null -> MkDir Def File
            try {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                if (configuration.getString("Crate.identifier").equalsIgnoreCase(identifier)) {
                    return file;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to read information from file: " + file.getName());
                if (plugin.getConfigurationFile().debugging())
                    e.printStackTrace();
            }
        }
        return null;
    }

    public void createPrize(Crate crate, Prize prize) {
        File file = locateFile(crate.getSettings().getIdentifier());
        if (file != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            int index = config.getInt("prize index", 0);
            config.set("Prizes." + index + ".chance", prize.getChance());
            config.set("Prizes." + index + ".display.item", prize.getDisplayItem().getType().name());
//            config.set("Prizes." + index + ".display.glow", prize.isGlowing());
            config.set("Prizes." + index + ".display.amount", prize.getDisplayItem().getAmount());
            config.set("Prizes." + index + ".display.name", prize.getDisplayItem().getItemMeta().getDisplayName());
            config.set("Prizes." + index + ".display.lore", prize.getDisplayItem().getItemMeta().getLore());
            config.set("Prizes." + index + ".commands", prize.getCommands());
            config.set("prize index", ++index);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning(plugin.getLanguage().errorFileSaving());
                if (plugin.getConfigurationFile().debugging())
                    e.printStackTrace();
            }
        }
    }

    public void setPrizeGlow(Crate crate, Prize prize, boolean glowing) {
        File file = locateFile(crate.getSettings().getIdentifier());
        if (file != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(prize.getConfigurationSection() + ".display.glow", glowing);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning(plugin.getLanguage().errorFileSaving());
                if (plugin.getConfigurationFile().debugging())
                    e.printStackTrace();
            }
        }
    }

    public void changeCrateDisplayName(String identifier, String displayName) {
        File file = locateFile(identifier);
        if (file != null) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("Crate.display", displayName);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning(plugin.getLanguage().errorFileSaving());
                if (plugin.getConfigurationFile().debugging())
                    e.printStackTrace();
            }
        }
    }

    public boolean deleteCrate(String identifier) {
        File file = locateFile(identifier);
        if (file != null) {
            return file.delete();
        } else {
            return false;
        }
    }

    public List<Crate> getCrates() {
        if (crates.isEmpty()) {
            for (File file : directory.listFiles()) {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                getCrate(configuration);
            }
        }
        return new ArrayList<>(crates.values()); // TODO Optimize -> Defeats the whole purpose of only loading once.=
    }

    public Crate getCrate(FileConfiguration configuration) {
        Validate.notNull(configuration, "File Configuration is null");

        // Load Crate
        plugin.debug("Loading Crate Preset...");
        String identifier = configuration.getString("Crate.identifier");
        plugin.debug("Identifier=" + identifier);
        if (identifier == null || identifier.isEmpty()) {
            plugin.getLogger().warning("Identifier for configuration \"" + configuration.getCurrentPath() + "\" is null" +
                    " or empty. '" + identifier + "'");
            return null;
        }
        identifier = identifier.replaceAll(" ", "_");

        if (crates.containsKey(identifier)) {
            plugin.debug("Loaded from cache.");
            return crates.get(identifier);
        }

        String display = configuration.getString("Crate.display", "Unable to fetch display from config");
        long spinTime = configuration.getLong("Crate.spin time", 2500);

        Material block = materialFromConfiguration(configuration, "Crate.block");
        if (block == null || !block.isBlock()) {
            plugin.getLogger().warning("'" + configuration.getString("Crate.block", "NaN") + "' is null or" +
                    "is not a block. \"" + identifier + "\"");
            if (block == null) block = Material.CHEST;
        }

        // Load Sounds
        Sound openingSound = parseSound(configuration.getString("Crate.open sound", ""), identifier, "CHEST_OPEN", "BLOCK_CHEST_OPEN");
        Sound spinSound = parseSound(configuration.getString("Crate.spin sound", ""), identifier, "NOTE_PLING", "BLOCK_NOTE_PLING");
        Sound winSound = parseSound(configuration.getString("Crate.win sound", ""), identifier, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP");
        List<String> openCommands = configuration.getStringList("Crate.open-commands");

        plugin.debug("Loading Key...");
        // Load Key
        ItemBuilder keyBuilder = itemFromConfiguration(configuration, "Key");
        if (keyBuilder == null) keyBuilder = new ItemBuilder(Material.AIR);

        plugin.debug("Loading Pouch...");
        // Load Pouch
        ItemBuilder pouchBuilder = itemFromConfiguration(configuration, "pouch");
        ItemStack pouch = pouchBuilder == null ? new ItemStack(Material.AIR) : pouchBuilder.build();

        // Load Spin
        Spin.SpinType spinType = Spin.SpinType.CSGO;
        try {
            spinType = Spin.SpinType.valueOf(configuration.getString("Crate.spin"));
        } catch (EnumConstantNotPresentException e) {
            plugin.getLogger().warning("Could not load spin type for \"" + identifier + "\". '" + configuration.getString("Crate.spin", "NaN") + "'");
            if (plugin.debugging())
                e.printStackTrace();
        }

        plugin.debug("Loading Prizes...");
        // Load Prizes
        WeightedList<Prize> prizes = new WeightedList<>();
        for (String key : configuration.getConfigurationSection("Prizes").getKeys(false)) {
            String path = "Prizes." + key;
            ItemBuilder prizeBuilder = itemFromConfiguration(configuration, path + ".display");
            if (prizeBuilder == null) {
                prizeBuilder = new ItemBuilder(Material.AIR);
            }
            double chance = configuration.getDouble(path + ".chance");
            List<String> commands = configuration.getStringList(path + ".commands");
            prizes.add(new Prize(prizeBuilder, chance, path, commands), chance);
        }

        plugin.debug("Loading Hologram...");
        // Load Hologram
        List<String> holographicLines = configuration.getStringList("hologram.lines");
        HashMap<String, ItemBuilder> holographicItemMap = new HashMap<>();
        if (!holographicLines.isEmpty()) { // Redundant check most of the times but if holograms aren't configured we aren't going to load the rest
            if (configuration.contains("hologram.items")) {
                for (String key : configuration.getConfigurationSection("hologram.items").getKeys(false)) {
                    String path = "hologram.items." + key;

                    String itemIdentifier = configuration.getString(path + ".identifier");
                    ItemBuilder builder = itemFromConfiguration(configuration, path);
                    holographicItemMap.put(itemIdentifier, builder);
                }
            }
        }
        // Construction
        CrateSettings settings = new CrateSettings(identifier, display, openCommands, block, spinType, spinTime, openingSound,
                spinSound, winSound, keyBuilder, pouchBuilder, prizes, holographicItemMap, holographicLines);
        Crate crate = new Crate(settings);
        plugin.debug("Crate Construction Complete.");
        return crates.put(identifier, crate);
    }

    public void reload() {
        this.directory = new File(plugin.getDataFolder(), "crates");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (directory.listFiles().length == 0) {
            new FileHandler(plugin, "crates/default.yml", directory, "default.yml") {
            };
        }
        this.crates.clear();
    }

    // Private Methods

    private ItemBuilder itemFromConfiguration(FileConfiguration config, String path) {
        Material material = materialFromConfiguration(config, path + ".item");
        if (material == null) materialFromConfiguration(config, path + ".material");
        if (material == null) return new ItemBuilder(Material.AIR);

        int data = config.getInt(path + ".data", 0);
        int amount = config.getInt(path + ".amount", 1);

        ItemBuilder builder = new ItemBuilder(material, amount, data);

        boolean glow = config.getBoolean(path + ".glow", false);
        builder.glow(glow);

        String name = config.getString(path + ".name");
        if (name != null)
            builder.name(name);

        List<String> lore = config.getStringList(path + ".lore");
        builder.lore(lore);
        return builder;
    }

    private Material materialFromConfiguration(FileConfiguration config, String path) {
        if (!config.contains(path))
            return null;
        try {
            return Material.getMaterial(config.getString(path).toUpperCase());
        } catch (EnumConstantNotPresentException e) {
            return null;
        }
    }

    private Sound parseSound(String configuredName, String identifier, String... soundNames) {
        try {
            return Sound.valueOf(configuredName);
        } catch (IllegalArgumentException | EnumConstantNotPresentException e) {
            Sound sound = null;
            for (String name : soundNames) {
                try {
                    sound = sound == null ? Sound.valueOf(name) : sound;
                } catch (IllegalArgumentException | EnumConstantNotPresentException ignored) {
                }
            }
            if (sound == null)
                if (plugin.getConfigurationFile().debugging())
                    plugin.getLogger().info("Could not load closing sound for crate: \"" + identifier + "\". Defaulting to: " + sound.name() + ".");
            return sound;
        }
    }
}