package xyz.ufactions.customcrates.libs;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class F {

    private static String prefix = "&7[&cCustomCrates&7] ";

    public static void setPrefix(String prefix) {
        F.prefix = prefix;
    }

    public static String format(String text) {
        return color(prefix + text);
    }

    public static String element(String text) {
        return color("&c" + text + "&7");
    }

    public static String help(String help, String desc) {
        return color("&c" + help + " &7" + desc);
    }

    public static String error(String text) {
        return format("&c" + text);
    }

    public static String list(String string) {
        return color(" &c➥ &7" + string);
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static <T> List<String> match(String lookFor, List<T> list, Function<T, String> function) {
        List<String> toReturn = new ArrayList<>();
        for (T t : list) {
            String string = function.apply(t);
            if (string.equalsIgnoreCase(lookFor)) {
                toReturn.add(string);
                break;
            } else {
                if (string.startsWith(lookFor)) toReturn.add(string);
            }
        }
        return toReturn;
    }

    public static String oo(boolean var) {
        if (var)
            return color("&aon&7");
        return color("&coff&7");
    }

    public static String concatenate(String splitter, List<String> list) {
        StringBuilder builder = new StringBuilder();

        for (String string : list) {
            if (builder.length() == 0) {
                builder.append(string);
            } else {
                builder.append(splitter).append(string);
            }
        }
        return builder.toString();
    }

    public static String capitalizeFirstLetter(String string) {
        if (string.contains(" ")) {
            StringBuilder toReturn = new StringBuilder();
            String[] array = string.split(" ");

            for (String s : array) {
                toReturn.append(capitalizeFirstLetter(s));
            }
            return toReturn.toString();
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}