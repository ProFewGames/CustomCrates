package xyz.ufactions.customcrates.spin;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;

public abstract class Spin {

    public enum SpinType {
        ROULETTE(new RouletteSpin()), CSGO(new CSGOSpin()), NONE(new QuickSpin());

        private final Spin spin;

        SpinType(Spin spin) {
            this.spin = spin;
        }

        public Spin getSpin(CustomCrates plugin) {
            spin.setPlugin(plugin);
            return spin;
        }
    }

    protected CustomCrates plugin;

    public void setPlugin(CustomCrates plugin) {
        this.plugin = plugin;
    }

    public final void spin(Player player, Crate crate) {
        try {
            player.playSound(player.getLocation(), crate.getSettings().getOpeningSound(), 1f, 1f);
            for (String command : crate.getSettings().getOpenCommands()) {
                executeCommand(player, command);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize pre-crate open events for crate " + crate.getSettings().getIdentifier() + ".");
            e.printStackTrace();
        }
        try {
            execute(player, crate);
        } catch (Exception e) {
            plugin.getLogger().warning("Error whilst spinning for type: " + this.getClass().getSimpleName() + ". Crate: " + crate.getSettings().getIdentifier());
            e.printStackTrace();
        }
    }

    protected abstract void execute(Player player, Crate crate);

    protected final Inventory constructInventory(Crate crate, int size) {
        return Bukkit.createInventory(null, size, crate.getSettings().getDisplay());
    }

    protected final void end(Player player, Crate crate, Prize prizeWon) {
        player.playSound(player.getLocation(), crate.getSettings().getWinSound(), 1f, 1f);
        for (String command : prizeWon.getCommands()) {
            executeCommand(player, command);
        }
    }

    protected final Prize randomPrize(Crate crate) {
        return crate.getSettings().getPrizes().get();
    }

    protected final ChatColor randomColor() {
        return ChatColor.values()[UtilMath.random.nextInt(ChatColor.values().length)];
    }

    private void executeCommand(Player player, String command) {
        command = command.replaceAll("%player%", player.getName());
        command = ChatColor.translateAlternateColorCodes('&', command);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            command = PlaceholderAPI.setPlaceholders(player, command); // TODO: 2/1/2022 More efficient way?
        if (command.startsWith("[msg]")) {
            command = command.replaceFirst("[msg]", "");
            player.sendMessage(command);
        } else if (command.startsWith("[bc]")) {
            command = command.replaceFirst("[bc]", "");
            Bukkit.broadcastMessage(command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}