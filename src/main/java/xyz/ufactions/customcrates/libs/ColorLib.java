package xyz.ufactions.customcrates.libs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.enchantmentlib.VersionUtils;

public class ColorLib {

    public static ItemStackBuilder pane(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return ItemStackBuilder.of(Material.getMaterial("STAINED_GLASS_PANE")).data(cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return ItemStackBuilder.of(Material.WHITE_STAINED_GLASS_PANE);
            else if (color == ChatColor.GOLD) return ItemStackBuilder.of(Material.ORANGE_STAINED_GLASS_PANE);
                // MAGENTA
            else if (color == ChatColor.BLUE) return ItemStackBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
            else if (color == ChatColor.YELLOW) return ItemStackBuilder.of(Material.YELLOW_STAINED_GLASS_PANE);
            else if (color == ChatColor.GREEN) return ItemStackBuilder.of(Material.LIME_STAINED_GLASS_PANE);
            else if (color == ChatColor.LIGHT_PURPLE) return ItemStackBuilder.of(Material.PINK_STAINED_GLASS_PANE);
            else if (color == ChatColor.DARK_GRAY) return ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE);
            else if (color == ChatColor.GRAY) return ItemStackBuilder.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            else if (color == ChatColor.AQUA) return ItemStackBuilder.of(Material.CYAN_STAINED_GLASS_PANE);
            else if (color == ChatColor.DARK_PURPLE) return ItemStackBuilder.of(Material.PURPLE_STAINED_GLASS_PANE);
            else if (color == ChatColor.DARK_BLUE) return ItemStackBuilder.of(Material.BLUE_STAINED_GLASS_PANE);
                // BROWN
            else if (color == ChatColor.DARK_GREEN) return ItemStackBuilder.of(Material.GREEN_STAINED_GLASS_PANE);
            else if (color == ChatColor.RED) return ItemStackBuilder.of(Material.RED_STAINED_GLASS_PANE);
            else if (color == ChatColor.BLACK) return ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE);
            return ItemStackBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE);
        }
    }

    public static ItemStackBuilder wool(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return ItemStackBuilder.of(Material.getMaterial("WOOL")).data(cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return ItemStackBuilder.of(Material.WHITE_WOOL);
            else if (color == ChatColor.GOLD) return ItemStackBuilder.of(Material.ORANGE_WOOL);
            else if (color == ChatColor.AQUA) return ItemStackBuilder.of(Material.LIGHT_BLUE_WOOL);
            else if (color == ChatColor.YELLOW) return ItemStackBuilder.of(Material.YELLOW_WOOL);
            else if (color == ChatColor.GREEN) return ItemStackBuilder.of(Material.LIME_WOOL);
            else if (color == ChatColor.LIGHT_PURPLE) return ItemStackBuilder.of(Material.PINK_WOOL);
            else if (color == ChatColor.DARK_GRAY) return ItemStackBuilder.of(Material.GRAY_WOOL);
            else if (color == ChatColor.GRAY) return ItemStackBuilder.of(Material.LIGHT_GRAY_WOOL);
            else if (color == ChatColor.BLUE) return ItemStackBuilder.of(Material.CYAN_WOOL);
            else if (color == ChatColor.DARK_PURPLE) return ItemStackBuilder.of(Material.PURPLE_WOOL);
            else if (color == ChatColor.DARK_BLUE) return ItemStackBuilder.of(Material.BLUE_WOOL);
            else if (color == ChatColor.DARK_GREEN) return ItemStackBuilder.of(Material.GREEN_WOOL);
            else if (color == ChatColor.RED) return ItemStackBuilder.of(Material.RED_WOOL);
            else if (color == ChatColor.BLACK) return ItemStackBuilder.of(Material.BLACK_WOOL);
            return ItemStackBuilder.of(Material.BROWN_WOOL);
        }
    }

    public static ItemStackBuilder banner(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return ItemStackBuilder.of(Material.getMaterial("BANNER")).data(cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return ItemStackBuilder.of(Material.WHITE_BANNER);
            else if (color == ChatColor.GOLD) return ItemStackBuilder.of(Material.ORANGE_BANNER);
            else if (color == ChatColor.AQUA) return ItemStackBuilder.of(Material.LIGHT_BLUE_BANNER);
            else if (color == ChatColor.YELLOW) return ItemStackBuilder.of(Material.YELLOW_BANNER);
            else if (color == ChatColor.GREEN) return ItemStackBuilder.of(Material.LIME_BANNER);
            else if (color == ChatColor.LIGHT_PURPLE) return ItemStackBuilder.of(Material.PINK_BANNER);
            else if (color == ChatColor.DARK_GRAY) return ItemStackBuilder.of(Material.GRAY_BANNER);
            else if (color == ChatColor.GRAY) return ItemStackBuilder.of(Material.LIGHT_GRAY_BANNER);
            else if (color == ChatColor.BLUE) return ItemStackBuilder.of(Material.CYAN_BANNER);
            else if (color == ChatColor.DARK_PURPLE) return ItemStackBuilder.of(Material.PURPLE_BANNER);
            else if (color == ChatColor.DARK_BLUE) return ItemStackBuilder.of(Material.BLUE_BANNER);
            else if (color == ChatColor.DARK_GREEN) return ItemStackBuilder.of(Material.GREEN_BANNER);
            else if (color == ChatColor.RED) return ItemStackBuilder.of(Material.RED_BANNER);
            else if (color == ChatColor.BLACK) return ItemStackBuilder.of(Material.BLACK_BANNER);
            return ItemStackBuilder.of(Material.BROWN_BANNER);
        }
    }

    public static ItemStackBuilder dye(ChatColor color) {
        if (VersionUtils.getVersion() == VersionUtils.Version.V1_8) {
            return ItemStackBuilder.of(Material.getMaterial("INK_SACK")).data(cbyte(color));
        } else {
            if (color == ChatColor.WHITE) return ItemStackBuilder.of(Material.WHITE_DYE);
            else if (color == ChatColor.GOLD) return ItemStackBuilder.of(Material.ORANGE_DYE);
            else if (color == ChatColor.AQUA) return ItemStackBuilder.of(Material.LIGHT_BLUE_DYE);
            else if (color == ChatColor.YELLOW) return ItemStackBuilder.of(Material.YELLOW_DYE);
            else if (color == ChatColor.GREEN) return ItemStackBuilder.of(Material.LIME_DYE);
            else if (color == ChatColor.LIGHT_PURPLE) return ItemStackBuilder.of(Material.PINK_DYE);
            else if (color == ChatColor.DARK_GRAY) return ItemStackBuilder.of(Material.GRAY_DYE);
            else if (color == ChatColor.GRAY) return ItemStackBuilder.of(Material.LIGHT_GRAY_DYE);
            else if (color == ChatColor.BLUE) return ItemStackBuilder.of(Material.CYAN_DYE);
            else if (color == ChatColor.DARK_PURPLE) return ItemStackBuilder.of(Material.PURPLE_DYE);
            else if (color == ChatColor.DARK_BLUE) return ItemStackBuilder.of(Material.BLUE_DYE);
            else if (color == ChatColor.DARK_GREEN) return ItemStackBuilder.of(Material.GREEN_DYE);
            else if (color == ChatColor.RED) return ItemStackBuilder.of(Material.RED_DYE);
            else if (color == ChatColor.BLACK) return ItemStackBuilder.of(Material.BLACK_DYE);
            return ItemStackBuilder.of(Material.BROWN_DYE);
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