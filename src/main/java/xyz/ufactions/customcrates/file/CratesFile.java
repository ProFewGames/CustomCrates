package xyz.ufactions.customcrates.file;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.crates.PhysicalCrate;
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

    private final HashMap<String, ICrate> crates;
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

    public void createPrize(ICrate crate, Prize prize) {
        File file = locateFile(crate.getIdentifier());
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

    public void setPrizeGlow(ICrate crate, Prize prize, boolean glowing) {
        File file = locateFile(crate.getIdentifier());
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

    public List<ICrate> getCrates() {
        if (crates.isEmpty()) {
            for (File file : directory.listFiles()) {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                getCrate(configuration);
            }
        }
        return new ArrayList<>(crates.values()); // TODO Optimize -> Defeats the whole purpose of only loading once.
/*        List<ICrate> crates = new ArrayList<>();
        for (File file : directory.listFiles()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                // CRATE
                String identifier = config.getString("Crate.identifier");
                if (identifier == null) {
                    plugin.getLogger().warning("No identifier for crate '" + file.getName() + "'.");
                    continue;
                }
                String display = config.getString("Crate.display", identifier);
                Material block = materialFromConfiguration(config, "Crate.block");
                if (block == null) block = Material.CHEST;
                long spinTime = config.getLong("Crate.spin time");
                List<String> openCommands = config.getStringList("Crate.open-commands");
                Spin.SpinType spinType;

                try {
                    spinType = Spin.SpinType.valueOf(config.getString("Crate.spin").toUpperCase());
                } catch (EnumConstantNotPresentException e) {
                    plugin.getLogger().warning(plugin.getLanguage().spinTypeNotFound(identifier));
                    spinType = Spin.SpinType.ROULETTE;
                }

                // Sound
                Sound openingSound = parseSound(config.getString("Crate.open sound", ""), identifier, "CHEST_OPEN", "BLOCK_CHEST_OPEN");
                Sound spinSound = parseSound(config.getString("Crate.spin sound", ""), identifier, "NOTE_PLING", "BLOCK_NOTE_PLING");
                Sound winSound = parseSound(config.getString("Crate.win sound", ""), identifier, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP");

                // Hologram
                List<String> hologram = new ArrayList<>();
                Map<String, ItemStack> holographicItemsMap = new HashMap<>();
                if (config.contains("hologram.lines")) {
                    hologram = config.getStringList("hologram.lines");
                }
                if (config.contains("hologram.items")) {
                    if (config.isConfigurationSection("hologram.items")) {
                        for (String key : config.getConfigurationSection("hologram.items").getKeys(false)) {
                            String path = "hologram.items." + key;
                            try {
                                String item_identifier = config.getString(path + ".identifier");
                                ItemBuilder item = itemFromConfiguration(config, path);
                                if (item != null)
                                    holographicItemsMap.put(item_identifier, item.build());
                            } catch (Exception e) {
                                plugin.getLogger().warning("Failed to load holographic item: " + path);
                                if (plugin.getConfigurationFile().debugging()) e.printStackTrace();
                            }
                        }
                    }
                }

                // Pouches
                ItemBuilder pouchBuilder = itemFromConfiguration(config, "pouch");
                ItemStack pouch;
                if (pouchBuilder == null)
                    pouch = new ItemStack(Material.AIR);
                else
                    pouch = pouchBuilder.build();

                // KEY
                Material keyMaterial = Material.getMaterial(config.getString("Key.item").toUpperCase());
                int keyData = config.getInt("Key.data");
                ItemBuilder keyBuilder = new ItemBuilder(keyMaterial, keyData);
                keyBuilder.glow(config.getBoolean("Key.glow", true));
                if (config.contains("Key.name"))
                    keyBuilder.name(config.getString("Key.name"));
                if (config.contains("Key.lore"))
                    keyBuilder.lore(config.getStringList("Key.lore"));

                // PRIZES
                List<Prize> prizes = new ArrayList<>();

                for (String key : config.getConfigurationSection("Prizes").getKeys(false)) {
                    String path = "Prizes." + key;
                    try {
                        Material displayPrize = Material.getMaterial(config.getString(path + ".display.item").toUpperCase());
                        int displayData = config.getInt(path + ".display.data", 0);
                        ItemBuilder builder = new ItemBuilder(displayPrize, displayData);
                        boolean glow = config.getBoolean(path + ".display.glow", false);
                        builder.glow(glow);
                        builder.amount(config.getInt(path + ".display.amount", 1));
                        if (config.contains(path + ".display.name"))
                            builder.name(config.getString(path + ".display.name"));
                        if (config.contains(path + ".display.lore"))
                            builder.lore(config.getStringList(path + ".display.lore"));

                        double chance = config.getDouble(path + ".chance", 50);
                        List<String> commands = config.getStringList(path + ".commands");
                        prizes.add(new Prize(builder, glow, chance, path, commands));
                    } catch (Exception e) {
                        plugin.getLogger().warning(plugin.getLanguage().failedLoadPrize(path, identifier));
                        if (plugin.getConfigurationFile().debugging()) e.printStackTrace();
                    }
                }

                crates.add(new PhysicalCrate(identifier, display, openingSound, spinSound, winSound, spinType, spinTime, block, keyBuilder, prizes, openCommands, hologram, holographicItemsMap, pouch));
            } catch (Exception e) {
                plugin.getLogger().warning(plugin.getLanguage().failedLoadCrate(file.getName()));
                if (plugin.getConfigurationFile().debugging())
                    e.printStackTrace();
            }
        }
        return crates;*/
    }

    public ICrate getCrate(FileConfiguration configuration) {
        Validate.notNull(configuration, "File Configuration is null");

        // Load Crate
        String identifier = configuration.getString("Crate.identifier");
        if (identifier == null || identifier.isEmpty()) {
            plugin.getLogger().warning("Identifier for configuration \"" + configuration.getCurrentPath() + "\" is null" +
                    " or empty. '" + identifier + "'");
            return null;
        }
        identifier = identifier.replaceAll(" ", "_");

        if (crates.containsKey(identifier)) {
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

        // Load Key
        ItemBuilder keyBuilder = itemFromConfiguration(configuration, "Key");
        if (keyBuilder == null) keyBuilder = new ItemBuilder(Material.AIR);

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

        // Load Hologram
        List<String> holographicLines = configuration.getStringList("hologram.lines");
        HashMap<String, ItemStack> holographicItemMap = new HashMap<>();
        if (!holographicLines.isEmpty()) { // Redundant check most of the times but if holograms aren't configured we aren't going to load the rest
            if (configuration.contains("hologram.items")) {
                for (String key : configuration.getConfigurationSection("hologram.items").getKeys(false)) {
                    String path = "hologram.items." + key;

                    String itemIdentifier = configuration.getString(path + ".identifier");
                    ItemBuilder builder = itemFromConfiguration(configuration, path);
                    ItemStack item = builder == null ? new ItemStack(Material.AIR) : builder.build();
                    holographicItemMap.put(itemIdentifier, item);
                }
            }
        }
        // Construction
        ICrate crate = new PhysicalCrate(identifier, display, openingSound, spinSound, winSound, spinType, spinTime, block,
                keyBuilder, prizes, openCommands, holographicLines, holographicItemMap, pouch);
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
        if (material == null) return null;

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
        return Material.getMaterial(config.getString(path).toUpperCase());
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