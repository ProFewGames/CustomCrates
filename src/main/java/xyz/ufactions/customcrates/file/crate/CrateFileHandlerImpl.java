package xyz.ufactions.customcrates.file.crate;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.ApiStatus;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.CrateSettings;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrateFileHandlerImpl implements CrateFileHandler {

    private static final String TICKET_WARN = "Please submit the following error when creating a ticket:";

    private final CustomCrates plugin;
    private File directory;

    public CrateFileHandlerImpl(CustomCrates plugin) {
        this.plugin = plugin;
    }

    @ApiStatus.Internal
    public Map<File, FileConfiguration> getConfigurations() {
        Map<File, FileConfiguration> configurations = new HashMap<>();
        List<File> files = getFiles();
        if (files == null) return configurations;
        for (File file : files) {
            try {
                FileConfiguration config = new YamlConfiguration();
                config.load(file);
                configurations.put(file, config);
            } catch (IOException | InvalidConfigurationException e) {
                this.plugin.getLogger().log(Level.SEVERE,
                        String.format("Failed to read file '%s'. Is the YAML formatting correct? " + TICKET_WARN,
                                file.getAbsolutePath()),
                        e);
            }
        }
        return configurations;
    }

    private List<File> getFiles() {
        try (Stream<Path> stream = Files.list(this.directory.toPath())) {
            return stream.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,
                    String.format("Failed to read crates directory path '%s' " + TICKET_WARN,
                            this.directory.getAbsolutePath()),
                    e);
            return Collections.emptyList();
        }
    }

    private String cleanIdentifier(String identifier) {
        return identifier.replaceAll(" ", "_");
    }

    private void applyToFile(Consumer<YamlConfiguration> consumer, File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        consumer.accept(config);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING,
                    "Failed to save configuration file. " + TICKET_WARN,
                    e);
        }
    }

    protected File getCratesDirectory() {
        return this.directory;
    }

    @Override
    public void reload() throws NotDirectoryException {
        this.directory = new File(this.plugin.getDataFolder(), "crates");

        if (!this.directory.exists())
            if (this.directory.mkdirs())
                this.plugin.debug("Crates directory created.");

        if (!Files.isDirectory(this.directory.toPath()))
            throw new NotDirectoryException(this.directory.getAbsolutePath());

        try (Stream<Path> entries = Files.list(this.directory.toPath())) {
            if (entries.findFirst().isPresent()) return;
            this.plugin.debug("Could not find any crates in parent directory. Loading default...");
            createCrate("default");
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,
                    String.format("Failed to read crates from directory path '%s'", this.directory.getAbsolutePath()),
                    e);
        }
    }

    @Override
    public void createPrize(Crate crate, Prize prize) {
        CrateFileWriter writer = CrateFileWriter.create(plugin, crate);
        writer.writePrize(prize);
        writer.save();
    }

    @Override
    public void deletePrize(Crate crate, Prize prize) {
        File file = getFileByCrate(crate);
        if (file == null) {
            this.plugin.getLogger().warning(String.format("Failed to get crate file for '%s' when creating a prize.",
                    crate.getSettings().getIdentifier()));
            return;
        }
        applyToFile(config -> config.set(prize.getConfigurationSection(), null), file);
    }

    @Override
    public boolean doesCrateExist(String identifier) {
        // TODO Rework - With the current method we're fetching the whole crate from file, this can be improved upon.
        return getCrateByIdentifier(identifier) != null;
    }

    @Override
    public Crate createCrate(String identifier) {
        identifier = cleanIdentifier(identifier);

        Crate crate = getCrateByIdentifier(identifier);
        if (crate != null) return crate;

        ItemStackBuilder pouchItem = ItemStackBuilder.of(Material.ENDER_CHEST)
                .name("&3&l" + identifier + " Pouch")
                .glow()
                .lore("&7Click on this item to open.");

        ItemStackBuilder keyItem = ItemStackBuilder.of(Material.TRIPWIRE_HOOK)
                .name("&3&l" + identifier + " Key")
                .glow()
                .lore("&7Use this at the nearest", "&7Crate to open!");

        RandomizableList<Prize> prizes = new RandomizableList<>();
        prizes.add(new Prize(ItemStackBuilder.of(Material.GOLD_INGOT)
                        .name("&6&lGold")
                        .glow()
                        .lore("&aChance: &e%chance%"),
                        50,
                        false,
                        "Prizes.gold",
                        Collections.singletonList("minecraft:give %player% gold_ingot 1")),
                50);
        prizes.add(new Prize(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                        .name("&4&lSword of Zeus")
                        .glow()
                        .lore("&b&lOnly the best of the best can", "&b&lcan use this sword!")
                        .enchant(Enchantment.DAMAGE_ALL, 3),
                        50,
                        true,
                        "Prizes.sword",
                        Collections.singletonList("[bc]&e&l%player% &b&lhas won the zeus sword!")),
                50);

        CrateSettings settings = new CrateSettings(identifier,
                "&3&l" + identifier + " Crate",
                Collections.singletonList("[bc]&e%player% &bis opening &e%crate%&b."),
                Material.CHEST,
                Spin.SpinType.CSGO,
                2_500,
                null,
                null,
                null,
                keyItem,
                pouchItem,
                prizes,
                new HashMap<>(),
                -1.4,
                Arrays.asList(
                        "{crate_display}",
                        "&7(Right Click to Open)",
                        "&7(Left Click to Preview)")
        );
        crate = new Crate(plugin, settings);

        CrateFileWriter writer = CrateFileWriter.create(plugin, crate);
        writer.writeAll();
        writer.save();
        return crate;
    }

    @Override
    public boolean deleteCrate(Crate crate) {
        File file = getFileByCrate(crate);
        if (file == null) return false;
        return file.delete();
    }

    @Override
    public List<Crate> getCrates() {
        List<Crate> crates = new ArrayList<>();
        List<File> files = getFiles();
        if (files == null) return crates;
        for (File file : files) {
            Crate crate = getCrateByFile(file);
            if (crate != null) crates.add(crate);
        }
        return crates;
    }

    @Override
    public Crate getCrateByFile(File file) {
        if (file == null) return null;

        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            this.plugin.getLogger().log(Level.SEVERE,
                    String.format("Failed to read file '%s'. Is the formatting correct? " + TICKET_WARN,
                            file.getAbsolutePath()),
                    e);
            return null;
        }

        this.plugin.debug(String.format("Reading data from file '%s'.", file.getAbsolutePath()));
        CrateFileReader reader = CrateFileReader.create(plugin, config);
        return reader.readAll();
    }

    @Override
    public Crate getCrateByIdentifier(String identifier) {
        identifier = cleanIdentifier(identifier);

        for (Map.Entry<File, FileConfiguration> entry : getConfigurations().entrySet()) {
            CrateFileReader reader = CrateFileReader.create(plugin, entry.getValue());
            if (reader.readIdentifier().equalsIgnoreCase(identifier)) return getCrateByFile(entry.getKey());
        }
        return null;
    }

    @Override
    public File getFileByCrate(Crate crate) {
        String identifier = cleanIdentifier(crate.getSettings().getIdentifier());
        for (Map.Entry<File, FileConfiguration> entry : getConfigurations().entrySet()) {
            CrateFileReader reader = CrateFileReader.create(plugin, entry.getValue());
            if (reader.readIdentifier().equalsIgnoreCase(identifier)) return entry.getKey();
        }
        return null;
    }
}
