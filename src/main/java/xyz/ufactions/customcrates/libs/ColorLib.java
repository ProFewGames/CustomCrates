package xyz.ufactions.customcrates.libs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import xyz.ufactions.enchantmentlib.VersionUtils;

public class ColorLib {

    public static ItemBuilder cp(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return new ItemBuilder(Material.getMaterial("STAINED_GLASS_PANE"), cbyte(color));
        } else {
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

    public static ItemBuilder cw(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return new ItemBuilder(Material.getMaterial("WOOL"), cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return new ItemBuilder(Material.WHITE_WOOL);
            else if (color == ChatColor.GOLD) return new ItemBuilder(Material.ORANGE_WOOL);
            else if (color == ChatColor.AQUA) return new ItemBuilder(Material.LIGHT_BLUE_WOOL);
            else if (color == ChatColor.YELLOW) return new ItemBuilder(Material.YELLOW_WOOL);
            else if (color == ChatColor.GREEN) return new ItemBuilder(Material.LIME_WOOL);
            else if (color == ChatColor.LIGHT_PURPLE) return new ItemBuilder(Material.PINK_WOOL);
            else if (color == ChatColor.DARK_GRAY) return new ItemBuilder(Material.GRAY_WOOL);
            else if (color == ChatColor.GRAY) return new ItemBuilder(Material.LIGHT_GRAY_WOOL);
            else if (color == ChatColor.BLUE) return new ItemBuilder(Material.CYAN_WOOL);
            else if (color == ChatColor.DARK_PURPLE) return new ItemBuilder(Material.PURPLE_WOOL);
            else if (color == ChatColor.DARK_BLUE) return new ItemBuilder(Material.BLUE_WOOL);
            else if (color == ChatColor.DARK_GREEN) return new ItemBuilder(Material.GREEN_WOOL);
            else if (color == ChatColor.RED) return new ItemBuilder(Material.RED_WOOL);
            else if (color == ChatColor.BLACK) return new ItemBuilder(Material.BLACK_WOOL);
            return new ItemBuilder(Material.BROWN_WOOL);
        }
    }

    public static ItemBuilder cb(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return new ItemBuilder(Material.getMaterial("BANNER"), cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return new ItemBuilder(Material.WHITE_BANNER);
            else if (color == ChatColor.GOLD) return new ItemBuilder(Material.ORANGE_BANNER);
            else if (color == ChatColor.AQUA) return new ItemBuilder(Material.LIGHT_BLUE_BANNER);
            else if (color == ChatColor.YELLOW) return new ItemBuilder(Material.YELLOW_BANNER);
            else if (color == ChatColor.GREEN) return new ItemBuilder(Material.LIME_BANNER);
            else if (color == ChatColor.LIGHT_PURPLE) return new ItemBuilder(Material.PINK_BANNER);
            else if (color == ChatColor.DARK_GRAY) return new ItemBuilder(Material.GRAY_BANNER);
            else if (color == ChatColor.GRAY) return new ItemBuilder(Material.LIGHT_GRAY_BANNER);
            else if (color == ChatColor.BLUE) return new ItemBuilder(Material.CYAN_BANNER);
            else if (color == ChatColor.DARK_PURPLE) return new ItemBuilder(Material.PURPLE_BANNER);
            else if (color == ChatColor.DARK_BLUE) return new ItemBuilder(Material.BLUE_BANNER);
            else if (color == ChatColor.DARK_GREEN) return new ItemBuilder(Material.GREEN_BANNER);
            else if (color == ChatColor.RED) return new ItemBuilder(Material.RED_BANNER);
            else if (color == ChatColor.BLACK) return new ItemBuilder(Material.BLACK_BANNER);
            return new ItemBuilder(Material.BROWN_BANNER);
        }
    }

    public static ItemBuilder cd(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return new ItemBuilder(Material.getMaterial("INK_SACK"), cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return new ItemBuilder(Material.WHITE_DYE);
            else if (color == ChatColor.GOLD) return new ItemBuilder(Material.ORANGE_DYE);
            else if (color == ChatColor.AQUA) return new ItemBuilder(Material.LIGHT_BLUE_DYE);
            else if (color == ChatColor.YELLOW) return new ItemBuilder(Material.YELLOW_DYE);
            else if (color == ChatColor.GREEN) return new ItemBuilder(Material.LIME_DYE);
            else if (color == ChatColor.LIGHT_PURPLE) return new ItemBuilder(Material.PINK_DYE);
            else if (color == ChatColor.DARK_GRAY) return new ItemBuilder(Material.GRAY_DYE);
            else if (color == ChatColor.GRAY) return new ItemBuilder(Material.LIGHT_GRAY_DYE);
            else if (color == ChatColor.BLUE) return new ItemBuilder(Material.CYAN_DYE);
            else if (color == ChatColor.DARK_PURPLE) return new ItemBuilder(Material.PURPLE_DYE);
            else if (color == ChatColor.DARK_BLUE) return new ItemBuilder(Material.BLUE_DYE);
            else if (color == ChatColor.DARK_GREEN) return new ItemBuilder(Material.GREEN_DYE);
            else if (color == ChatColor.RED) return new ItemBuilder(Material.RED_DYE);
            else if (color == ChatColor.BLACK) return new ItemBuilder(Material.BLACK_DYE);
            return new ItemBuilder(Material.BROWN_DYE);
        }
    }

    /**
     * 1.8 Use Only
     */
    private static byte cbyte(ChatColor color) {
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

    public static ChatColor randomColor() {
        ChatColor color = ChatColor.values()[UtilMath.r(ChatColor.values().length)];
        while (!color.isColor()) {
            color = ChatColor.values()[UtilMath.r(ChatColor.values().length)];
        }
        return color;
    }
}