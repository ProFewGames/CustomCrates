package xyz.ufactions.customcrates.universal;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.ufactions.customcrates.libs.ReflectionUtils;
import xyz.ufactions.enchantmentlib.VersionUtils;

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
        if (!VersionUtils.getVersion().equalOrGreater(VersionUtils.Version.V1_9)) {// Legacy
            return (ItemStack) MethodGetItemInHand.of(player.getInventory()).call();
        } else { // Newer
            return player.getInventory().getItemInMainHand();
        }
    }

    public boolean isStainedGlassPane(ItemStack item) {
        if (!VersionUtils.getVersion().equalOrGreater(VersionUtils.Version.V1_13)) { // Legacy
            return item.getType() == Material.getMaterial("STAINED_GLASS_PANE");
        } else { // Newer
            return item.getType().data.equals(GlassPane.class);
        }
    }

    public boolean isSign(Block block) {
        if (!VersionUtils.getVersion().equalOrGreater(VersionUtils.Version.V1_13)) { // Legacy
            return block.getType() == Material.getMaterial("SIGN") || block.getType() == Material.getMaterial("SIGN_POST") || block.getType() == Material.getMaterial("WALL_SIGN");
        } else { // Newer
            return block.getType().data.equals(Sign.class);
        }
    }
}