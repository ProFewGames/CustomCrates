package xyz.ufactions.customcrates.factory;

import com.google.common.base.Preconditions;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.ufactions.customcrates.CustomCrates;

import java.util.logging.Level;

public class SoundFactory {

    private final CustomCrates plugin;

    public SoundFactory(CustomCrates plugin) {
        this.plugin = plugin;
    }

    public void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public Sound parseOrDefault(@NotNull String... possibleSoundNames) {
        Preconditions.checkArgument(possibleSoundNames.length != 0,
                "This should not have happened! No possible sound names were provided to the SoundFactory.");

        for (String possibleSoundName : possibleSoundNames) {
            try {
                return Sound.valueOf(possibleSoundName.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.plugin.debug(String.format("Attempted to parse sound name '%s' but it could not be found.", possibleSoundName));
            }
        }

        this.plugin.getLogger().log(Level.SEVERE, "The SoundFactory class could not find a valid sound in the provided " +
                "array '%s'. Are you running a modified version of Spigot? Please open a ticket on Discord.");
        return null;
    }

    public Sound parse(String soundName) {
        if (soundName == null) return null;
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}