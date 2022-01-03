package xyz.ufactions.customcrates.manager;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;

public class SoundManager {

    private final CustomCrates plugin;

    public SoundManager(CustomCrates plugin) {
        this.plugin = plugin;
    }

    public void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public Sound parseOrDefault(String soundName, String... defaultSounds) {
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.debug("Could not parse sound name '" + soundName + "'.");
            Sound sound = null;

            for (String defaultSound : defaultSounds) {
                try {
                    sound = sound == null ? Sound.valueOf(defaultSound) : sound;
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (sound == null)
                plugin.debug("Could not find default sound for '" + soundName + ".");
            return sound;
        }
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