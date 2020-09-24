package xyz.ufactions.customcrates.updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.updater.event.UpdateEvent;

public class Updater {

    public Updater(JavaPlugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (UpdateType type : UpdateType.values()) {
                if (type.Elapsed()) {
                    Bukkit.getServer().getPluginManager().callEvent(new UpdateEvent(type));
                }
            }
        }, 1L, 1L);
    }
}