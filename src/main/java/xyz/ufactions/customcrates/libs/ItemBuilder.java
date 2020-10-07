package xyz.ufactions.customcrates.libs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.ufactions.enchantmentlib.EnchantmentLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material material) {
        this(material, 0);
    }

    public ItemBuilder(Material material, int data) {
        this(material, 1, data);
    }

    /**
     * Regular Item Creator
     *
     * @param material The material of the item
     * @param amount   How much of the material do you want the item
     * @param data     The item's data
     */
    public ItemBuilder(Material material, int amount, int data) {
        this.item = new ItemStack(material, amount, (short) data);
    }

    public ItemBuilder glow(boolean glow) {
        item.addEnchantment(EnchantmentLib.getGlowEnchantment(), 1);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(cc((name.startsWith("&") ? "&f" : "") + name));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> finLore = new ArrayList<String>();
        for (String line : lore) {
            finLore.add(cc("&7" + line));
        }
        meta.setLore(finLore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... strings) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        for (String string : strings) {
            lore.add(cc("&7" + string));
        }
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemMeta getItemMeta() {
        return this.item.getItemMeta();
    }

    public List<String> getLore() {
        if (!this.item.hasItemMeta()) return Collections.emptyList();
        return this.item.getItemMeta().getLore();
    }

    public ItemStack build() {
        return this.item;
    }

    private String cc(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}