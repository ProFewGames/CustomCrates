package xyz.ufactions.customcrates.libs;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.ufactions.customcrates.CustomCrates;

public class VaultManager {

    private static VaultManager instance;

    public static VaultManager getInstance() {
        return instance;
    }

    public static void initialize(CustomCrates plugin) {
        if (instance == null) instance = new VaultManager(plugin.getConfigurationFile().debugging());
    }

    private Economy economy;

    private VaultManager(boolean debug) {
        if (!setupEconomy()) {
            if (debug)
                System.err.println("(EconomyManager) Failed to register economy.");
        }
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean useEconomy() {
        return economy != null;
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}