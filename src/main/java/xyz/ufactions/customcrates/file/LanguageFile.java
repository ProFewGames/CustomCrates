package xyz.ufactions.customcrates.file;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.FileHandler;

public class LanguageFile extends FileHandler {

    public enum LanguageType {
        ENGLISH("language_en.yml"), SPANISH("language_es.yml");

        private final String resource;

        LanguageType(String resource) {
            this.resource = resource;
        }

        public String getResource() {
            return resource;
        }
    }

    public LanguageFile(CustomCrates plugin, String fileName) {
        super(plugin, fileName, plugin.getDataFolder(), fileName);

        plugin.getLogger().info("Loading Language: " + fileName);
    }

    public String prefix() {
        return getString("prefix");
    }

    public String errorPlayingSound(String sound, String player) {
        return getString("error_playing_sound").replaceAll("%type%", sound).replaceAll("%player%", player);
    }

    public String errorFileSaving() {
        return getString("error_file_saving");
    }

    public String noKey(String name) {
        return getString("no_key").replaceAll("%name%", name);
    }

    public String crateBroken() {
        return getString("broken_crate");
    }

    public String updateAvailable() {
        return getString("update-available");
    }

    public String updateDownloaded() {
        return getString("update-downloaded");
    }

    public String spinTypeNotFound(String crate) {
        return getString("spin-not-found").replaceAll("%crate%", crate);
    }


    public String crateBlockNotFound(String crate) {
        return getString("crate-block-not-found").replaceAll("%crate%", crate);
    }


    public String failedLoadPrize(String path, String crate) {
        return getString("failed-load-prize").replaceAll("%path%", path).replaceAll("%crate%", crate);
    }


    public String failedLoadCrate(String file) {
        return getString("failed-load-crate").replaceAll("%file%", file);
    }


    public String noPlayer() {
        return getString("no-player");
    }


    public String noPermission() {
        return getString("no-permission");
    }


    public String reloading() {
        return getString("reloading");
    }


    public String reloaded() {
        return getString("reloaded");
    }


    public String noAvailableCrates() {
        return getString("no-available-crates");
    }


    public String availableCratesHeader() {
        return getString("available-crates-header");
    }


    public String noTargetBlock() {
        return getString("no-target-block");
    }


    public String targetNotCrate() {
        return getString("target-not-crate");
    }


    public String targetBroken() {
        return getString("target-broken");
    }


    public String invalidCrateInput() {
        return getString("invalid-crate-input");
    }


    public String incorrectTargetBlock(String target) {
        return getString("incorrect-block-target").replaceAll("%target%", target);
    }


    public String locationAlreadySet() {
        return getString("location-already-set");
    }


    public String targetBlockSet() {
        return getString("target-block-set");
    }


    public String previewing(String crate) {
        return getString("previewing").replaceAll("%crate%", crate);
    }


    public String opening(String crate) {
        return getString("opening").replaceAll("%crate%", crate);
    }


    public String invalidInteger() {
        return getString("invalid-integer");
    }


    public String keyReceived(int amount, String crate) {
        return getString("key-received").replaceAll("%amount%", String.valueOf(amount)).replaceAll("%crate%", crate);
    }


    public String keyGiven(String target, int amount, String crate) {
        return getString("key-given").replaceAll("%amount%", String.valueOf(amount)).replaceAll("%crate%", crate).replaceAll("%target%", target);
    }


    public String keyGivenAll(int amount, String crate, int total) {
        return getString("key-given-all").replaceAll("%amount%", String.valueOf(amount)).replaceAll("%crate%", crate).replaceAll("%total%", String.valueOf(total));
    }


    public String playerNotFound() {
        return getString("player-not-found");
    }


    public String signSet() {
        return getString("sign-purchase-set", "&7Purchase location set.");
    }


    public String failedToLoadSign() {
        return getString("failed-load-sign", "&cFailed to load this sign.");
    }


    public String notEnoughMoney() {
        return getString("no-money", "&cYou do not have enough money.");
    }


    public String purchasedKey() {
        return getString("key-purchased", "&7You have purchased a key.");
    }

    @Override
    public void onReload() {
        F.setPrefix(prefix());
    }
}