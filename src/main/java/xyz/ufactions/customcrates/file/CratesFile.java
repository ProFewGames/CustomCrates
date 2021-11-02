package xyz.ufactions.customcrates.file;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.CrateSettings;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.FileHandler;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CratesFile {

    private final HashMap<String, Crate> crates;
    private final CustomCrates plugin;
    private File directory;

    public CratesFile(CustomCrates plugin) {
        this.plugin = plugin;
        this.crates = new HashMap<>();
        reload();
    }

    // Creators/Setters

    public Crate createCrate(String identifier) {
        identifier = cleanIdentifier(identifier);
        Crate crate = getCrateByIdentifier(identifier);
        if (crate != null) return crate;
        File file = new File(directory, identifier + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create crate file. Please submit the following error when making a ticket.");
            e.printStackTrace();
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String path;

        // Creating Crate
        path = "Crate";
        config.set(path + ".identifier", identifier);
        config.set(path + ".display", "&3&l" + identifier + " Crate");
        config.set(path + ".open-commands", Collections.singletonList(
                "broadcast &e%player% &bis opening &e%crate%&b."
        ));
        config.set(path + ".block", Material.CHEST.name());
        config.set(path + ".spin", Spin.SpinType.CSGO.name());
        config.set(path + ".spin time", 2500);

        // Creating Key
        path = "Key";
        config.set(path + ".name", "&3&l" + identifier + " Key");
        config.set(path + ".item", Material.TRIPWIRE_HOOK.name());
        config.set(path + ".glow", true);
        config.set(path + ".lore", Arrays.asList(
                "&7Use this at the nearest",
                "&7Crate to open!"
        ));

        // Creating Pouch
        path = "pouch";
        config.set(path + ".item", Material.ENDER_CHEST.name());
        config.set(path + ".glow", false);
        config.set(path + ".name", "&3&l" + identifier + " Pouch");
        config.set(path + ".lore", Collections.singletonList(
                "&7Click on this item to open pouch"
        ));

        // Creating Hologram
        path = "hologram.items.key";
        config.set(path + ".identifier", "item_key");
        config.set(path + ".item", Material.TRIPWIRE_HOOK.name());
        config.set(path + ".glow", true);
        path = "hologram";
        config.set(path + ".lines", Arrays.asList(
                /*"{item_key}",*/
                "{crate_display}",
                "&7(Right Click to Open)",
                "&7(Left Click to Preview)"
        ));

        // Create Prizes

        ItemBuilder builder = new ItemBuilder(Material.DIAMOND)
                .glow(true)
                .name("&b&lDIAMONDS!!")
                .lore(Collections.singletonList(
                        "&aChance: &e%chance%"
                ));
        Prize prize = new Prize(builder, 50, "Prizes.diamond", Arrays.asList(
                "broadcast &e%player% &ahas won &e2x &b&lDIAMONDS!",
                "give %player% diamond 2"
        ));
        savePrize(config, prize);

        builder = new ItemBuilder(Material.GOLD_INGOT)
                .glow(false)
                .name("&6&lGold")
                .lore(Collections.singletonList(
                        "&aChance: &e%chance%"
                ));
        prize = new Prize(builder, 50, "Prizes.gold", Collections.singletonList(
                "give %player% gold_ingot 1"
        ));
        savePrize(config, prize);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save new crate file. Please submit the following error when making a ticket:");
            e.printStackTrace();
        }

        return getCrateByFile(file);
    }

    public boolean createPrize(Crate crate, Prize prize) {
        File file = getFileByCrate(crate);
        if (file == null) {
            plugin.getLogger().warning("Failed to fetch crate file for creating prize.");
            return false;
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        savePrize(configuration, prize);
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save configuration file. Please submit the following error when making a ticket:");
            e.printStackTrace();
        }
        return true;
    }

    private void savePrize(FileConfiguration config, Prize prize) {
        config.set(prize.getConfigurationSection() + ".chance", prize.getChance());
        config.set(prize.getConfigurationSection() + ".display.item", prize.getDisplayItem().getType().name());
        config.set(prize.getConfigurationSection() + ".display.data", prize.getDisplayItem().getData().getData());
        config.set(prize.getConfigurationSection() + ".display.glow", false); // Not implemented yet
        config.set(prize.getConfigurationSection() + ".display.amount", prize.getDisplayItem().getAmount());
        config.set(prize.getConfigurationSection() + ".display.name", prize.getDisplayItem().getItemMeta().getDisplayName());
        config.set(prize.getConfigurationSection() + ".display.lore", prize.getDisplayItem().getItemMeta().getLore());
        List<String> enchantments = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : prize.getDisplayItem().getEnchantments().entrySet()) {
            enchantments.add(entry.getKey().toString() + ":" + entry.getValue());
        }
        config.set(prize.getConfigurationSection() + ".display.enchantments", enchantments);
        config.set(prize.getConfigurationSection() + ".commands", prize.getCommands());
    }

    public boolean crateExist(String identifier) {
        return getCrateByIdentifier(identifier) != null;
    }

    // Getters

    public List<Crate> getCrates() {
        if (crates.isEmpty()) {
            for (File file : directory.listFiles()) {
                getCrateByFile(file);
            }
        }
        return new ArrayList<>(crates.values());
    }

    public Crate getCrateByIdentifier(String identifier) {
        identifier = cleanIdentifier(identifier);
        if (crates.containsKey(identifier)) return crates.get(identifier);
        for (File file : directory.listFiles()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (configuration.contains("Crate.identifier")) {
                if (configuration.getString("Crate.identifier").equalsIgnoreCase(identifier)) {
                    return getCrateByFile(file);
                }
            }
        }
        return null;
    }

    private File getFileByCrate(Crate crate) {
        String identifier = cleanIdentifier(crate.getSettings().getIdentifier());
        for (File file : directory.listFiles()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (!configuration.contains("Crate.identifier")) continue;
            if (configuration.getString("Crate.identifier").equalsIgnoreCase(identifier)) {
                return file;
            }
        }
        return null;
    }

    public Crate getCrateByFile(File file) {
        Validate.notNull(file, "File is null");

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        // Load Crate
        plugin.debug("Loading Crate Preset...");
        String identifier = configuration.getString("Crate.identifier");
        plugin.debug("Identifier=" + identifier);
        if (identifier == null || identifier.isEmpty()) {
            plugin.getLogger().warning("Identifier for configuration \"" + configuration.getCurrentPath() + "\" is null" +
                    " or empty. '" + identifier + "'");
            return null;
        }
        identifier = cleanIdentifier(identifier);

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
        RandomizableList<Prize> prizes = new RandomizableList<>();
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

        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (String serializedEnchantment : config.getStringList(path + ".enchantments")) {
            if (!serializedEnchantment.contains(":")) {
                plugin.getLogger().warning("Enchantment format incorrect '" + path + "'. Must be in format {ENCHANTMENT NAME}:{LEVEL}");
                continue;
            }
            String[] array = serializedEnchantment.split(":");
            Enchantment enchantment = Enchantment.getByName(array[0]);
            if (enchantment == null) {
                plugin.getLogger().warning("Cannot find enchantment '" + array[0] + "' from '" + path + "'");
                continue;
            }
            int level;
            try {
                level = Integer.parseInt(array[1]);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Cannot parse enchantment level '" + array[1] + "' from '" + path + "'");
                continue;
            }
            enchantments.put(enchantment, level);
        }
        builder.enchant(enchantments);

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

    private String cleanIdentifier(String identifier) {
        return identifier.replaceAll(" ", "_");
    }
}