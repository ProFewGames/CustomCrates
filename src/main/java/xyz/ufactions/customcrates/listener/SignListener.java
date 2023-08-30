package xyz.ufactions.customcrates.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.*;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@RequiredArgsConstructor
public class SignListener implements Listener {

    /**
     * A temporary placement in-case we decide to add more crate types in the future.
     */
    @RequiredArgsConstructor
    @Getter
    private enum GiveType {
        POUCH((byte) 0),
        KEY((byte) 1);

        private final byte value;

        public static GiveType fromValue(Byte value) {
            if (value == null) return null;
            for (GiveType type : values())
                if (type.getValue() == value) return type;
            return null;
        }
    }

    private static final NamespacedKey IDENTIFIER_NAMESPACED_KEY = NamespacedKey.minecraft("crate_sign_identifier");
    private static final NamespacedKey COST_NAMESPACED_KEY = NamespacedKey.minecraft("crate_sign_cost");
    private static final NamespacedKey AMOUNT_NAMESPACED_KEY = NamespacedKey.minecraft("crate_sign_amount");
    private static final NamespacedKey TYPE_NAMESPACED_KEY = NamespacedKey.minecraft("crate_sign_type");

    private final CustomCrates plugin;

    private CustomBlockData getBlockData(Block block) {
        return new CustomBlockData(block, this.plugin);
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        if (event.isCancelled()) return;
        String[] lines = event.getLines();
        if (lines.length != 4) return;
        if (!lines[0].equalsIgnoreCase("[crates]") && !lines[0].equalsIgnoreCase("[pouches]")) return;
        Player player = event.getPlayer();
        if (!player.hasPermission("customcrates.sign.purchase.create")) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_PERMISSION)));
            return;
        }
        if (!VaultManager.getInstance().useEconomy()) {
            player.sendMessage(F.format("&cEconomy is not enabled on this server."));
            return;
        }
        Crate crate = this.plugin.getCratesManager().getCrate(lines[2]);
        if (crate == null) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_CRATE_INPUT, lines[2])));
            return;
        }

        OptionalDouble price = UtilMath.getDouble(lines[3]);
        if (!price.isPresent()) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_INTEGER)));
            return;
        }
        OptionalInt amount = UtilMath.getInteger(lines[2]);
        if (!amount.isPresent()) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_INTEGER)));
            return;
        }

        if (price.getAsDouble() <= 0 || amount.getAsInt() <= 0) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_INTEGER)));
            return;
        }
        GiveType type = lines[0].equalsIgnoreCase("[Crates]") ? GiveType.KEY : GiveType.POUCH;
        CustomBlockData data = getBlockData(event.getBlock());
        data.set(IDENTIFIER_NAMESPACED_KEY, PersistentDataType.STRING, crate.getSettings().getIdentifier());
        data.set(AMOUNT_NAMESPACED_KEY, PersistentDataType.INTEGER, amount.getAsInt());
        data.set(COST_NAMESPACED_KEY, PersistentDataType.DOUBLE, price.getAsDouble());
        data.set(TYPE_NAMESPACED_KEY, PersistentDataType.BYTE, type.getValue());
        event.setLine(0, F.color("&7[&b&l" + F.capitalizeFirstLetter(type.name()) + "&7]"));
        event.setLine(1, UtilMath.formatNumber(amount.getAsInt()));
        event.setLine(2, F.capitalizeFirstLetter(crate.getSettings().getIdentifier()));
        event.setLine(3, "$" + UtilMath.formatNumber(price.getAsDouble()));
        player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.SIGN_PURCHASE_SET)));
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (!UtilEvent.isAction(event, UtilEvent.ActionType.R_BLOCK)) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        CustomBlockData data = getBlockData(block);
        if (!data.has(IDENTIFIER_NAMESPACED_KEY, PersistentDataType.STRING)) return;
        Player player = event.getPlayer();
        if (!player.hasPermission("customcrates.sign.purchase.use")) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_PERMISSION)));
            return;
        }
        if (!VaultManager.getInstance().useEconomy()) {
            player.sendMessage(F.format("&cEconomy is not enabled on this server."));
            return;
        }
        Crate crate = this.plugin.getCratesManager().getCrate(data.getOrDefault(IDENTIFIER_NAMESPACED_KEY, PersistentDataType.STRING, ""));
        if (crate == null) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }
        Double cost = data.get(COST_NAMESPACED_KEY, PersistentDataType.DOUBLE);
        if (cost == null) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }
        Integer amount = data.get(AMOUNT_NAMESPACED_KEY, PersistentDataType.INTEGER);
        if (amount == null) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }
        GiveType type = GiveType.fromValue(data.get(TYPE_NAMESPACED_KEY, PersistentDataType.BYTE));
        if (type == null) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }
        EconomyResponse response = VaultManager.getInstance().getEconomy().withdrawPlayer(player, cost);
        if (!response.transactionSuccess()) {
            player.sendMessage(F.format(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.NOT_ENOUGH_MONEY)));
            return;
        }
        ItemStack item = type == GiveType.KEY ? crate.getSettings().getKey().build() : crate.getSettings().getPouch().build();
        item.setAmount(amount);
        Utility.addOrDropItem(player.getInventory(), player.getLocation(), item);
        String purchased_msg = plugin.getLanguage().getString(LanguageFile.LanguagePath.KEY_PURCHASED);
        if (purchased_msg != null && !purchased_msg.isEmpty())
            player.sendMessage(F.format(purchased_msg));
    }
}