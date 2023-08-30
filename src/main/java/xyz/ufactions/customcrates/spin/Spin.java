package xyz.ufactions.customcrates.spin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.api.event.CrateOpenEvent;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.UtilMath;
import xyz.ufactions.customcrates.libs.Utility;

public abstract class Spin {

    @AllArgsConstructor
    public enum SpinType {
        ROULETTE(new RouletteSpin(), Material.GOLD_INGOT), CSGO(new CSGOSpin(), Material.DIAMOND), NONE(new QuickSpin(), Material.BARRIER);

        private final Spin spin;
        @Getter
        private final Material displayItem;

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
                executeCommand(player, crate, command);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize pre-crate open events for crate " + crate.getSettings().getIdentifier() + ".");
            e.printStackTrace();
        }
        Bukkit.getPluginManager().callEvent(new CrateOpenEvent(player, crate));
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
        if (prizeWon.isGiveItem())
            Utility.addOrDropItem(player.getInventory(), player.getLocation(), prizeWon.getItemBuilder().build());
        for (String command : prizeWon.getCommands()) {
            executeCommand(player, crate, command);
        }
    }

    protected final Prize randomPrize(Crate crate) {
        return crate.getSettings().getPrizes().get();
    }

    protected final ChatColor randomColor() {
        return ChatColor.values()[UtilMath.random.nextInt(ChatColor.values().length)];
    }

    private void executeCommand(Player player, Crate crate, String command) {
        command = command.replaceAll("%player%", player.getName());
        command = ChatColor.translateAlternateColorCodes('&', command);
        command = command.replaceAll("%crate%", crate.getSettings().getDisplay());
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            command = PlaceholderAPI.setPlaceholders(player, command);
        if (command.startsWith("[msg]")) {
            command = command.replaceFirst("\\[msg]", "");
            player.sendMessage(command);
        } else if (command.startsWith("[bc]")) {
            command = command.replaceFirst("\\[bc]", "");
            Bukkit.broadcastMessage(command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}