package xyz.ufactions.customcrates.crates;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.UtilMath;
import xyz.ufactions.customcrates.libs.WeightedList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.List;
import java.util.Map;

public class PhysicalCrate implements ICrate {

    private final String identifier;
    private String display;
    private final Sound openingSound;
    private final Sound spinSound;
    private final Sound winSound;
    private final Spin.SpinType spinType;
    private final long spinTime;
    private final Material block;
    private final ItemStack key;
    private final ItemStack pouch;

    private final Inventory previewInventory;

    private final WeightedList<Prize> prizes;
    private final List<String> openCommands;

    private final Map<String, ItemStack> holographicItemMap;
    private final List<String> holographicLines;

    public PhysicalCrate(String identifier, String display, Sound openingSound, Sound spinSound, Sound winSound,
                         Spin.SpinType spinType, long spinTime, Material block, ItemBuilder keyBuilder, WeightedList<Prize> prizes, List<String> openCommands,
                         List<String> holographicLines, Map<String, ItemStack> holographicItemMap, ItemStack pouch) {
        this.identifier = identifier.replaceAll(" ", "_");
        this.display = F.color(display);
        this.openingSound = openingSound;
        this.spinSound = spinSound;
        this.winSound = winSound;
        this.spinType = spinType;
        this.spinTime = spinTime;
        this.prizes = prizes;
        this.key = keyBuilder.build();
        this.block = block;
        this.openCommands = openCommands;
        this.holographicLines = holographicLines;
        this.holographicItemMap = holographicItemMap;
        this.pouch = pouch;

        this.previewInventory = Bukkit.createInventory(null, UtilMath.round(prizes.size()), this.display);

        for (int i = 0; i < prizes.size(); i++) {
            previewInventory.setItem(i, prizes.get(i).getDisplayItem());
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String displayName) {
        this.display = displayName;
    }

    @Override
    public Sound getOpeningSound() {
        return openingSound;
    }

    @Override
    public Sound getSpinSound() {
        return spinSound;
    }

    @Override
    public Sound getWinSound() {
        return winSound;
    }

    @Override
    public Spin.SpinType getSpinType() {
        return spinType;
    }

    @Override
    public long getSpinTime() {
        return spinTime;
    }

    @Override
    public Material getBlock() {
        return block;
    }

    @Override
    public ItemStack getKey() {
        return key.clone();
    }

    @Override
    public ItemStack getPouch() {
        return pouch.clone();
    }

    @Override
    public Inventory getPreviewInventory() {
        return previewInventory;
    }

    @Override
    public WeightedList<Prize> getPrizes() {
        return prizes;
    }

    @Override
    public List<String> openCommands() {
        return openCommands;
    }

    @Override
    public List<String> holographicLines() {
        return holographicLines;
    }

    @Override
    public Map<String, ItemStack> holographicItemMap() {
        return holographicItemMap;
    }

    @Override
    public void giveKey(Player player, int amount) {
        ItemStack key = getKey();
        key.setAmount(amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), key);
        } else {
            player.getInventory().setItem(player.getInventory().firstEmpty(), key);
        }
    }

    @Override
    public void givePouch(Player player, int amount) {
        ItemStack pouch = getPouch();
        pouch.setAmount(amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), pouch);
        } else {
            player.getInventory().setItem(player.getInventory().firstEmpty(), pouch);
        }
    }
}