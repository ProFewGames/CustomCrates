package xyz.ufactions.customcrates.universal;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.ReflectionUtils;
import xyz.ufactions.customcrates.libs.VersionUtils;

public class Universal {

    private static Universal instance;

    public static Universal getInstance() {
        if (instance == null) instance = new Universal();
        return instance;
    }

    private final ReflectionUtils.RefClass ClassPlayerInventory = ReflectionUtils.getRefClass(PlayerInventory.class);
    private final ReflectionUtils.RefMethod MethodGetItemInHand = ClassPlayerInventory.getMethod("getItemInHand");

    private Universal() {
    }

    public ItemStack getItemInHand(Player player) {
        if (!VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_9)) {// Legacy
            return (ItemStack) MethodGetItemInHand.of(player.getInventory()).call();
        } else { // Newer
            return player.getInventory().getItemInMainHand();
        }
    }

    public boolean isStainedGlassPane(ItemStack item) {
        if (!VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_13)) { // Legacy
            return item.getType() == Material.getMaterial("STAINED_GLASS_PANE");
        } else { // Newer
            return item.getType().data.equals(GlassPane.class);
        }
    }

    public boolean isSign(Block block) {
        if (!VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_13)) { // Legacy
            return block.getType() == Material.getMaterial("SIGN") || block.getType() == Material.getMaterial("SIGN_POST") || block.getType() == Material.getMaterial("WALL_SIGN");
        } else { // Newer
            return block.getType().data.equals(Sign.class);
        }
    }

    public ItemBuilder colorToGlassPane(ChatColor color) {
        if (!VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_13)) { // Legacy
            return new ItemBuilder(Material.getMaterial("STAINED_GLASS_PANE"), 1, cbyte(color));
        } else { // Newer
            if (color == ChatColor.WHITE) return new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE);
            else if (color == ChatColor.GOLD) return new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE);
                // MAGENTA
            else if (color == ChatColor.BLUE) return new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
            else if (color == ChatColor.YELLOW) return new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE);
            else if (color == ChatColor.GREEN) return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE);
            else if (color == ChatColor.LIGHT_PURPLE) return new ItemBuilder(Material.PINK_STAINED_GLASS_PANE);
            else if (color == ChatColor.DARK_GRAY) return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE);
            else if (color == ChatColor.GRAY) return new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            else if (color == ChatColor.AQUA) return new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE);
            else if (color == ChatColor.DARK_PURPLE) return new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE);
            else if (color == ChatColor.DARK_BLUE) return new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE);
                // BROWN
            else if (color == ChatColor.DARK_GREEN) return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE);
            else if (color == ChatColor.RED) return new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
            else if (color == ChatColor.BLACK) return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE);
            return new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE);
        }
    }

    private byte cbyte(ChatColor color) {
        if (color == ChatColor.WHITE) return 0;
        else if (color == ChatColor.GOLD) return 1;
            // MAGENTA
        else if (color == ChatColor.BLUE) return 3;
        else if (color == ChatColor.YELLOW) return 4;
        else if (color == ChatColor.GREEN) return 5;
        else if (color == ChatColor.LIGHT_PURPLE) return 6;
        else if (color == ChatColor.DARK_GRAY) return 7;
        else if (color == ChatColor.GRAY) return 8;
        else if (color == ChatColor.AQUA) return 9;
        else if (color == ChatColor.DARK_PURPLE) return 10;
        else if (color == ChatColor.DARK_BLUE) return 11;
            // BROWN
        else if (color == ChatColor.DARK_GREEN) return 13;
        else if (color == ChatColor.RED) return 14;
        else if (color == ChatColor.BLACK) return 15;
        return 2;
    }
}