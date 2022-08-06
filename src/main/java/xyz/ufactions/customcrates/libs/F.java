package xyz.ufactions.customcrates.libs;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import xyz.ufactions.enchantmentlib.VersionUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class F {

    private final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    @Setter
    private String prefix;

    public String format(String text) {
        return color(prefix + text);
    }

    public String list(String string) {
        return color(" &c➥ &7" + string);
    }

    public String concatenate(String splitter, List<String> list) {
        return concatenate(splitter, 0, list.toArray(new String[0]));
    }

    public String concatenate(String splitter, int index, String[] array) {
        StringBuilder builder = new StringBuilder();

        for (int i = index; i < array.length; i++) {
            if (i != 0)
                builder.append(splitter);
            builder.append(array[i]);
        }

        return builder.toString();
    }

    public String capitalizeFirstLetter(String string) {
        if (string == null || string.isEmpty()) return string;

        string = string.replaceAll("_", " ");
        if (string.contains(" ")) {
            StringBuilder toReturn = new StringBuilder();
            String[] array = string.split(" ");

            for (String s : array) {
                if (toReturn.length() == 0) {
                    toReturn.append(capitalizeFirstLetter(s));
                } else {
                    toReturn.append(" ").append(capitalizeFirstLetter(s));
                }
            }
            return toReturn.toString();
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public String ar(boolean var) {
        return var ? "&c&lRemove" : "&a&lAdd";
    }

    // *** COLOR ***

    public String color(String text) {
        text = hexColor(text);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String hexColor(String text) {
        if (!VersionUtils.getVersion().equalOrGreater(VersionUtils.Version.V1_16)) return text;
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find())
            text = text.replace(matcher.group(), net.md_5.bungee.api.ChatColor.of(matcher.group(0)).toString());
        return text;
    }
}