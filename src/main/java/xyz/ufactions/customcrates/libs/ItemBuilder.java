package xyz.ufactions.customcrates.libs;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import xyz.ufactions.enchantmentlib.EnchantmentLib;

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
		if (glow)
			item.addEnchantment(EnchantmentLib.getGlowEnchantment(), 1);
		return this;
	}

	public ItemBuilder amount(int amount) {
		this.item.setAmount(amount);
		return this;
	}

	public ItemBuilder name(String name) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setDisplayName(cc((name.startsWith("&") ? "" : "&f") + name));
		this.item.setItemMeta(meta);
		return this;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder owner(String id) {
		SkullMeta meta = (SkullMeta) this.item.getItemMeta();
		boolean isId = true;
		try {
			UUID uuid = UUID.fromString(id);
			if (uuid == null) {
				isId = false;
			}
		} catch (IllegalArgumentException e) {
			isId = false;
		}
		if (!isId && id.contains("http://textures.minecraft.net/texture/")) {
			meta.setLocalizedName(id);
			GameProfile profile = new GameProfile(UUID.randomUUID(), "John Doe");
			byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", id).getBytes());
			profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
			Field profileField = null;
			try {
				profileField = meta.getClass().getDeclaredField("profile");
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			profileField.setAccessible(true);
			try {
				profileField.set(meta, profile);
				this.item.setItemMeta(meta);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (isId) {
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(id)));
			meta.setOwner(id);
			this.item.setItemMeta(meta);
		}
		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		ItemMeta meta = this.item.getItemMeta();
		List<String> finLore = new ArrayList<String>();
		for (String line : lore) {
			finLore.add(cc((line.startsWith("&") ? "" : "&f") + line));
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

	public void enchant(Map<Enchantment, Integer> enchantments) {
		this.item.addUnsafeEnchantments(enchantments);
	}

	public ItemBuilder setItemMeta(ItemMeta meta) {
		this.item.setItemMeta(meta);
		return this;
	}

	public ItemMeta getItemMeta() {
		return this.item.getItemMeta();
	}

	public List<String> getLore() {
		if (!this.item.hasItemMeta())
			return Collections.emptyList();
		return this.item.getItemMeta().getLore();
	}

	public ItemStack build() {
		return this.item;
	}

	private String cc(String string) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
			String color = string.substring(matcher.start(), matcher.end());
			string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
		}
		string = ChatColor.translateAlternateColorCodes('&', string);
		return string;
	}

}