package xyz.ufactions.customcrates.file;

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
import xyz.ufactions.customcrates.spin.Spin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CratesFile {

    private final CustomCrates plugin;
    private File directory;

    public CratesFile(CustomCrates plugin) {
        this.plugin = plugin;
        reload();
    }

    private File locateFile(String identifier) {
        for (File file : directory.listFiles()) {
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
            int index = config.getInt("saving index", 0);
            config.set("Prizes." + index + ".chance", prize.getChance());
            config.set("Prizes." + index + ".display.item", prize.getDisplayItem().getType().name());
            config.set("Prizes." + index + ".display.glow", prize.isGlowing());
            config.set("Prizes." + index + ".display.amount", prize.getDisplayItem().getAmount());
            config.set("Prizes." + index + ".display.name", prize.getDisplayItem().getItemMeta().getDisplayName());
            config.set("Prizes." + index + ".display.lore", prize.getDisplayItem().getItemMeta().getLore());
            config.set("Prizes." + index + ".commands", prize.getCommands());
            config.set("saving index", ++index);
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
        List<ICrate> crates = new ArrayList<>();
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
                Material block;
                Spin.SpinType spinType;
                long spinTime = config.getLong("Crate.spin time");
                List<String> openCommands;
                if (config.contains("Crate.open-commands")) {
                    openCommands = config.getStringList("Crate.open-commands");
                } else {
                    openCommands = new ArrayList<>();
                }

                // Sound
                Sound openingSound = parseSound(config.getString("Crate.open sound", ""), identifier, "CHEST_OPEN", "BLOCK_CHEST_OPEN");
                Sound spinSound = parseSound(config.getString("Crate.spin sound", ""), identifier, "NOTE_PLING", "BLOCK_NOTE_PLING");
                Sound winSound = parseSound(config.getString("Crate.win sound", ""), identifier, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP");

                try {
                    spinType = Spin.SpinType.valueOf(config.getString("Crate.spin").toUpperCase());
                } catch (EnumConstantNotPresentException e) {
                    plugin.getLogger().warning(plugin.getLanguage().spinTypeNotFound(identifier));
                    spinType = Spin.SpinType.ROULETTE;
                }

                try {
                    block = Material.getMaterial(config.getString("Crate.block").toUpperCase());
                } catch (Exception e) {
                    plugin.getLogger().warning(plugin.getLanguage().crateBlockNotFound(identifier));
                    block = Material.CHEST;
                }

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
                                Material material = Material.valueOf(config.getString(path + ".item"));
                                boolean glow = config.getBoolean(path + ".glow", false);
                                int data = config.getInt(path + ".data");
                                ItemStack item = new ItemBuilder(material, data).glow(glow).build();
                                holographicItemsMap.put(item_identifier, item);
                            } catch (Exception e) {
                                plugin.getLogger().warning("Failed to load holographic item: " + path);
                                if (plugin.getConfigurationFile().debugging()) e.printStackTrace();
                            }
                        }
                    }
                }

                // Pouches
                ItemStack pouch = null;
                if (config.contains("pouch")) {
                    try {
                        Material material = Material.valueOf(config.getString("pouch.material"));
                        boolean glow = config.getBoolean("pouch.glow", false);
                        String name = config.getString("pouch.name", "");
                        List<String> lore;
                        if (config.contains("pouch.lore")) {
                            lore = config.getStringList("pouch.lore");
                        } else {
                            lore = new ArrayList<>();
                        }
                        int data = config.getInt("pouch.data");

                        pouch = new ItemBuilder(material, data).glow(glow).name(name).lore(lore).build();
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to load pouch for crate: " + identifier);
                    }
                } else {
                    pouch = new ItemStack(Material.AIR);
                }

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
        return crates;
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
    }

    private Sound parseSound(String configuredName, String identifier, String... soundNames) {
        try {
            return Sound.valueOf(configuredName);
        } catch (IllegalArgumentException | EnumConstantNotPresentException e) {
            Sound sound = null;
            for (String name : soundNames) {
                try {
                    sound = Sound.valueOf(name);
                } catch (IllegalArgumentException | EnumConstantNotPresentException ignored) {
                }
            }
            assert sound != null;
            if (plugin.getConfigurationFile().debugging())
                plugin.getLogger().info("Could not load closing sound for crate: \"" + identifier + "\". Defaulting to: " + sound.name() + ".");
            return sound;
        }
    }
}