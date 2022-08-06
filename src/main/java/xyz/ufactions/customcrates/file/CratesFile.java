package xyz.ufactions.customcrates.file;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.item.ItemStackFileWriter;
import xyz.ufactions.customcrates.libs.FileHandler;
import xyz.ufactions.customcrates.spin.Spin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CratesFile {

    private final CustomCrates plugin;
    private File directory;

    public CratesFile(CustomCrates plugin) throws NotDirectoryException {
        this.plugin = plugin;
        reload();
    }

    // Creators/Setters

    public Crate createCrate(String identifier) {
        identifier = cleanIdentifier(identifier);
        Crate crate = getCrateByIdentifier(identifier);
        if (crate != null) return crate;
        File file = new File(directory, identifier + ".yml");
        try {
            if (file.createNewFile())
                if (plugin.debugging())
                    plugin.getLogger().info("Created new crate file for '" + identifier + "'");
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
                "[bc]&e%player% &bis opening &e%crate%&b."
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

        ItemStackBuilder builder = ItemStackBuilder.of(Material.DIAMOND)
                .glow()
                .name("&b&lDIAMONDS!!")
                .lore("&aChance: &e%chance%");
        Prize prize = new Prize(builder, 50, "Prizes.diamond", Arrays.asList(
                "[msg]&aYou have won &e2x &b&lDIAMONDS!",
                "give %player% diamond 2"
        ));
        savePrize(config, prize);

        builder = ItemStackBuilder.of(Material.GOLD_INGOT)
                .glow()
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

    public boolean deleteCrate(Crate crate) {
        return Objects.requireNonNull(getFileByCrate(crate)).delete();
    }

    private void savePrize(FileConfiguration config, Prize prize) {
        config.set(prize.getConfigurationSection() + ".chance", prize.getChance());
        ConfigurationSection section = config.getConfigurationSection(prize.getConfigurationSection() + ".display");
        if (section == null)
            section = config.createSection(prize.getConfigurationSection() + ".display");
        ItemStackFileWriter writer = ItemStackFileWriter.of(prize.getItemBuilder(), section);
        writer.write();
        config.set(prize.getConfigurationSection() + ".commands", prize.getCommands());
    }

    public void deletePrize(Crate crate, Prize prize) {
        File file = getFileByCrate(crate);
        if (file == null) return;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(prize.getConfigurationSection(), null);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save configuration file. Please submit the following error when making a ticket:");
            e.printStackTrace();
        }
    }

    public boolean crateExist(String identifier) {
        return getCrateByIdentifier(identifier) != null;
    }

    // Getters

    public List<Crate> getCrates() {
        List<Crate> crates = new ArrayList<>();
        List<File> files = getFiles();
        if (files == null) return crates;
        for (File file : files) {
            Crate crate = getCrateByFile(file);
            if (crate != null)
                crates.add(crate);
        }
        return crates;
    }

    public Crate getCrateByFile(File file) {
        Validate.notNull(file, "file");

        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            this.plugin.getLogger().warning("Failed to read file '" + file.getAbsolutePath() + "'. Is the formatting correct?");
            e.printStackTrace();
            return null;
        }

        this.plugin.debug("Reading data from file '" + file.getAbsolutePath() + "'");
        CrateFileReader readable = new CrateFileReader(plugin, config);
        return readable.read();
    }

    public Crate getCrateByIdentifier(String identifier) {
        identifier = cleanIdentifier(identifier);

        for (Map.Entry<File, FileConfiguration> entry : getConfigurations().entrySet()) {
            CrateFileReader readable = new CrateFileReader(plugin, entry.getValue());
            if (readable.readIdentifier().equalsIgnoreCase(identifier)) return getCrateByFile(entry.getKey());
        }

        return null;
    }

    public void reload() throws NotDirectoryException {
        this.directory = new File(plugin.getDataFolder(), "crates");

        if (!this.directory.exists())
            if (this.directory.mkdirs())
                this.plugin.debug("Created crates directory");

        if (!Files.isDirectory(this.directory.toPath()))
            throw new NotDirectoryException(this.directory.getAbsolutePath());

        try (Stream<Path> entries = Files.list(this.directory.toPath())) {
            if (!entries.findFirst().isPresent()) {
                this.plugin.debug("Could not find any crates in directory. Loading default...");
                new FileHandler(plugin, "default.yml", directory, "default.yml") {
                };
            }
        } catch (IOException e) {
            this.plugin.getLogger().warning("Failed to read crates directory path '" + this.directory.getAbsolutePath() + "'");
            e.printStackTrace();
        }
    }

    @Nullable
    public File getFileByCrate(Crate crate) {
        String identifier = cleanIdentifier(crate.getSettings().getIdentifier());
        for (Map.Entry<File, FileConfiguration> entry : getConfigurations().entrySet()) {
            CrateFileReader readable = new CrateFileReader(plugin, entry.getValue());
            if (readable.readIdentifier().equalsIgnoreCase(identifier)) return entry.getKey();
        }
        return null;
    }

    // Private Methods

    private Map<File, FileConfiguration> getConfigurations() {
        Map<File, FileConfiguration> configurations = new HashMap<>();
        List<File> files = getFiles();
        if (files == null) return configurations;
        for (File file : files) {
            try {
                FileConfiguration config = new YamlConfiguration();
                config.load(file);
                configurations.put(file, config);
            } catch (IOException | InvalidConfigurationException e) {
                this.plugin.getLogger().warning("Failed to read file '" + file.getAbsolutePath() + "'. Is the YAML formatting correct?");
            }
        }
        return configurations;
    }

    private List<File> getFiles() {
        try (Stream<Path> stream = Files.list(this.directory.toPath())) {
            return stream.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            this.plugin.getLogger().warning("Failed to read crates directory path '" + this.directory.getAbsolutePath() + "'.");
            e.printStackTrace();
            return null;
        }
    }

    private String cleanIdentifier(String identifier) {
        return identifier.replaceAll(" ", "_");
    }
}