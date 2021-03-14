package xyz.ufactions.customcrates.crates;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.libs.WeightedList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.List;
import java.util.Map;

public interface ICrate {

    String getIdentifier();

    String getDisplay();

    void setDisplay(String displayName);

    Sound getOpeningSound();

    Sound getSpinSound();

    Sound getWinSound();

    Spin.SpinType getSpinType();

    long getSpinTime();

    Map<String, ItemStack> holographicItemMap();

    ItemStack getPouch();

    List<String> holographicLines();

    List<String> openCommands();

    Material getBlock();

    ItemStack getKey();

    Inventory getPreviewInventory();

    WeightedList<Prize> getPrizes();

    void giveKey(Player player, int amount);
    void givePouch(Player player, int amount);
}