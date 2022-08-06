package xyz.ufactions.customcrates.file;

import lombok.Getter;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.FileHandler;

import java.util.Arrays;

public class LanguageFile extends FileHandler {

    public enum LanguageType {
        ENGLISH("language_en.yml"), SPANISH("language_es.yml");

        private final String resource;

        LanguageType(String resource) {
            this.resource = resource;
        }
    }

    @Getter
    public enum LanguagePath {

        AVAILABLE_CRATES_HEADER("available-crates-header", "Crate list header", new String[]{"Message when /crate list is performed", "and is showing header for all crates."}),
        CANNOT_PLACE_KEY("cannot place key", "Key placement not allowed", new String[]{"Message when player tries to place a key."}),
        CRATE_BLOCK_NOT_FOUND("crate-block-not-found", "Configured crate block not found", new String[]{"Console printout when configured crate", "block is not found."}),
        CRATE_BROKEN("broken_crate", "Crate broken", new String[]{"Message when a crate is broken."}),
        ERROR_FILE_SAVING("error_file_saving", "File failed to save", new String[]{"Message when a file fails to save.", "Example: After a crate is broken"}),
        FAILED_LOAD_CRATE("failed-load-crate", "Failed to load crate", new String[]{"Console printout when configured", "crate failed to load."}),
        FAILED_LOAD_PRIZE("failed-load-prize", "Failed to load prize", new String[]{"Console printout when configured", "prize failed to load."}),
        FAILED_LOAD_SIGN("failed-load-sign", "Failed to load purchase sign", new String[]{"Message when a purchase sign failed to load."}),
        INCORRECT_TARGET_BLOCK("incorrect-block-target", "Target block not crate block", new String[]{"Message when target within player's", "visible range is not", "a crate block."}),
        INELIGIBLE_POUCH("invalid-pouch", "Invalid pouch", new String[]{"Message when a player attempts to", "give a pouch that has not", "been configured properly."}),
        INVALID_CRATE_INPUT("invalid-crate-input", "Unknown crate", new String[]{"Message when player attempts to locate", "a crate by name but is not found."}),
        INVALID_INTEGER("invalid-integer", "Unknown integer", new String[]{"Message when input is not an integer. (number)"}),
        KEY_GIVEN("key-given", "Key given", new String[]{"Message when player gives a key."}),
        KEY_GIVEN_ALL("key-given-all", "Key given to everybody", new String[]{"Message when player gives everybody a key."}),
        KEY_PURCHASED("key-purchased", "Key purchased", new String[]{"Message when player purchases a key", "from CustomCrates sign."}),
        KEY_RECEIVED("key-received", "Key received", new String[]{"Message when player gets a key."}),
        LOCATION_ALREADY_SET("location-already-set", "Crate already set at location", new String[]{"Message when location is already a crate."}),
        NOT_ENOUGH_MONEY("no-money", "Insufficient money", new String[]{"Message when a player does not have", "sufficient funds to purchase", "from a CustomCrates sign."}),
        NO_AVAILABLE_CRATES("no-available-crates", "No crates available", new String[]{"Message when /crate list is performed", "and no crates are available."}),
        NO_KEY("no_key", "No key", new String[]{"Message when a player attempts", "to open a crate without a key."}),
        NO_PERMISSION("no-permission", "No permission", new String[]{"Message when player attempts to perform", "an action without permission."}),
        NO_PLAYER("no-player", "Not player", new String[]{"Message when non-player attempts", "to perform an action."}),
        NO_TARGET_BLOCK("no-target-block", "No block within range", new String[]{"Message when there is no block within", "the players visible range."}),
        OPENING("opening", "Opening crate", new String[]{"Message when player opens a crate."}),
        PLAYER_NOT_FOUND("player-not-found", "Player not found", new String[]{"Message when target player is not located."}),
        PREFIX("prefix", "Prefix", new String[]{"Text that pre-appends messages."}),
        PREVIEWING("previewing", "Previewing crate", new String[]{"Message when player previews a crate."}),
        RELOADED("reloaded", "Reloaded", new String[]{"Message showed after /crate reload."}),
        RELOADING("reloading", "Reloading", new String[]{"Message showed before /crate reload."}),
        SIGN_PURCHASE_SET("sign-purchase-set", "Purchase sign set", new String[]{"Message when a purchase sign has been created."}),
        SPINTYPE_NOT_FOUND("spin-not-found", "Configured spin not found", new String[]{"Console printout when a configured", "spin type is not found."}),
        TARGET_BLOCK_SET("target-block-set", "Target set as crate", new String[]{"Target block set as crate."}),
        TARGET_BROKEN("target-broken", "Target crate broken", new String[]{"Target crate block has been broken."}),
        TARGET_NOT_CRATE("target-not-crate", "Target block not crate", new String[]{"Target block is not crate."}),
        UPDATE_AVAILABLE("update-available", "Update available", new String[]{"Message when an update to", "CustomCrates is available."}),
        UPDATE_DOWNLOADED("update-downloaded", "Update downloaded", new String[]{"Message when an update", "is downloaded from SpigotMC."}),

        GUI_LANGUAGE_QUESTION("gui.language question", "Set Language Question", new String[]{"Prompted question to input new language setting."}),
        GUI_LANGUAGE_RESPONSE("gui.language set", "Language Set Response", new String[]{"Response when player inputs new language."}),
        GUI_CREATE_CRATE_QUESTION("gui.create crate question", "Create Crate Question", new String[]{"Question to input new crate name"}),
        GUI_CREATE_CRATE_RESPONSE("gui.create crate response", "Create Crate Response", new String[]{"When crate is created"}),
        GUI_PRIZE_CHANCE_QUESTION("gui.prize chance question", "Prize Chance Question", new String[]{"Question for prize chance"}),
        GUI_PRIZE_CHANCE_UNKNOWN_FORMAT("gui.prize chance unknown format", "Prize Chance Unknown Format", new String[]{"When input cannot be parsed to double"}),
        GUI_PRIZE_NAME_QUESTION("gui.prize name question", "Prize Name Question", new String[]{"Question to input the name of a prize"}),
        GUI_ADD_COMMAND_QUESTION("gui.add command question", "Add command question", new String[]{"Add command question"}),
        GUI_ADD_COMMAND_USERNAME_FLAG("gui.add command username flag detected", "Add command username flag detected", new String[]{"When command does not contain player flag", "but does contain player name"}),
        GUI_ADD_LORE_QUESTION("gui.add lore question", "Add line of lore", new String[]{"Question to prompt addition line of lore"}),
        GUI_DISPLAY_NAME_QUESTION("gui.display name question", "Display Name Question", new String[]{"Question to change item display name"}),
        GUI_DURABILITY_QUESTION("gui.durability question", "Durability Question", new String[]{"Question to change item durability"}),
        GUI_DURABILITY_UNKNOWN_FORMAT("gui.durability unknown format", "Durability Unknown Format", new String[]{"Unknown durability format"}),
        GUI_OWNER_QUESTION("gui.owner question", "Owner question", new String[]{"Skull owner question"}),
        GUI_RGB_QUESTION("gui.rgb question", "RGB question", new String[]{"Leather Armor RGB question"}),
        GUI_RGB_UNKNOWN_FORMAT("gui.rgb unknown format", "Unknown RGB Format", new String[]{"RGB not in correct format"}),
        GUI_RGB_WRONG_ARGUMENTS("gui.rgb wrong arguments", "Wrong RGB arguments", new String[]{"RGB arguments is not 3"}),
        GUI_RGB_PARSE_FAILED("gui.rgb failed parse", "Failed to parse RGB integer", new String[]{"Failure to parse integer to RGB value"}),
        GUI_RGB_VALUE_TOO_LARGE("gui.rgb value too large", "RGB value too large", new String[]{"RGB Value is greater than the allowed 255"}),
        GUI_RGB_VALUE_TOO_SMALL("gui.rgb value too small", "RGB value too small", new String[]{"RGB Value is less than the allowed 0"}),
        GUI_MODEL_QUESTION("gui.model question", "Model Question", new String[]{"Question prompted when custom model data is changed"}),
        GUI_MODEL_INVALID("gui.model invalid", "Invalid Model Integer", new String[]{"Provided model data is not integer"}),
        GUI_PRIZE_EXISTS("gui.prize exists", "Prize already exists with name", new String[]{"Warning when prize with name already exists"}),
        GUI_CRATE_DISPLAY_QUESTION("gui.crate display question", "Crate Display Question", new String[]{"Question when crate display name is changed"}),

        DIALOG_TITLE("dialog.title", "Dialog Title", new String[]{"Dialog Top Title"}),
        DIALOG_SUBTITLE("dialog.subtitle", "Dialog Subtitle", new String[]{"Dialog Bottom Subtitle"});

        private final String path;
        private final String friendlyName;
        private final String[] description;

        LanguagePath(String path, String friendlyName, String[] description) {
            this.path = path;
            this.friendlyName = friendlyName;
            this.description = Arrays.stream(description).map(string -> "&f" + string).toArray(String[]::new);
        }
    }

    public LanguageFile(CustomCrates plugin, LanguageType type) {
        super(plugin, type.resource, plugin.getDataFolder(), type.resource);

        plugin.getLogger().info("Loading Language: " + fileName);
    }

    public String getString(LanguagePath path) {
        return getString(path.getPath());
    }

    public String getString(LanguagePath path, Object... objects) {
        String string = getString(path);
        for (int index = 0; index < objects.length; index++)
            string = string.replace("{" + index + "}", String.valueOf(objects[index]));
        return string;
    }

    @Override
    public void onReload() {
        F.setPrefix(getString(LanguagePath.PREFIX));
    }
}