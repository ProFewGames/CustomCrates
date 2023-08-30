package xyz.ufactions.customcrates.item;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.enchantmentlib.EnchantmentLib;

import java.util.*;
import java.util.function.Consumer;

/**
 * Utility class for easy of creating items.
 */
public final class ItemStackBuilder {

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(new ItemStack(material));
    }

    public static ItemStackBuilder from(ItemStack item) {
        return new ItemStackBuilder(item);
    }

    public static ItemStackBuilder clone(ItemStack item) {
        return from(item.clone());
    }

    private ItemStack item;

    private final ItemFlag[] HIDE_FLAGS = new ItemFlag[]{
            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS,
            ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON
    };

    public ItemStackBuilder(ItemStack item) {
        Validate.notNull(item, "item");

        this.item = item;
    }

    public ItemStackBuilder applyMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta != null) {
            consumer.accept(meta);
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public Optional<ItemMeta> transformMeta() {
        return Optional.ofNullable(this.item.getItemMeta());
    }

    public ItemStackBuilder apply(Consumer<ItemStack> consumer) {
        consumer.accept(this.item);
        return this;
    }

    public ItemStackBuilder overrideItem(ItemStack item) {
        this.item = item.clone();
        return this;
    }

    public ItemStackBuilder name(String name) {
        return applyMeta(meta -> meta.setDisplayName(F.color(name)));
    }

    public String getName() {
        Optional<ItemMeta> meta = transformMeta();
        if (meta.isPresent())
            if (meta.get().hasDisplayName())
                return meta.get().getDisplayName();
        return F.capitalizeFirstLetter(getType().name());
    }

    public ItemStackBuilder type(Material material) {
        return apply(item -> item.setType(material));
    }

    public Material getType() {
        return item.getType();
    }

    public ItemStackBuilder lore(List<String> lore) {
        return lore(lore.toArray(new String[0]));
    }

    public ItemStackBuilder lore(String... lines) {
        return applyMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            for (String line : lines) {
                lore.add(F.color(line));
            }
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder clearLore() {
        return applyMeta(meta -> meta.setLore(new ArrayList<>()));
    }

    public List<String> getLore() {
        return transformMeta().map(ItemMeta::getLore).orElse(new ArrayList<>());
    }

    public ItemStackBuilder durability(int durability) {
        return apply(item -> item.setDurability((short) durability));
    }

    public short getDurability() {
        return item.getDurability();
    }

    @Deprecated
    public ItemStackBuilder data(int data) {
        return durability(data);
    }

    public ItemStackBuilder amount(int amount) {
        return apply(item -> item.setAmount(amount));
    }

    public int getEnchantmentLevel(Enchantment enchantment) {
        return item.getEnchantmentLevel(enchantment);
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        return apply(item -> item.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemStackBuilder enchant(Map<Enchantment, Integer> enchantments) {
        return apply(item -> item.addUnsafeEnchantments(enchantments));
    }

    public ItemStackBuilder disenchant(Enchantment enchantment) {
        return apply(item -> item.removeEnchantment(enchantment));
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return this.item.containsEnchantment(enchantment);
    }

    public Map<Enchantment, Integer> getEnchantments() {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : this.item.getEnchantments().entrySet()) {
            if (entry.getKey() == EnchantmentLib.getGlowEnchantment()) continue;
            enchantments.put(entry.getKey(), entry.getValue());
        }
        return enchantments;
    }

    public ItemStackBuilder clearEnchantments() {
        return apply(item -> item.getEnchantments().keySet().forEach(item::removeEnchantment));
    }

    public ItemStackBuilder glow() {
        return glow(true);
    }

    public ItemStackBuilder glow(boolean glow) {
        if (glow)
            return enchant(EnchantmentLib.getGlowEnchantment());
        else
            return disenchant(EnchantmentLib.getGlowEnchantment());
    }

    public boolean isGlowing() {
        return item.containsEnchantment(EnchantmentLib.getGlowEnchantment());
    }

    public ItemStackBuilder flag(ItemFlag... flags) {
        return applyMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemStackBuilder unflag(ItemFlag... flags) {
        return applyMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemStackBuilder flag(Map<ItemFlag, Boolean> flags) {
        return applyMeta(meta -> {
            for (Map.Entry<ItemFlag, Boolean> entry : flags.entrySet()) {
                if (entry.getValue())
                    meta.addItemFlags(entry.getKey());
                else
                    meta.removeItemFlags(entry.getKey());
            }
        });
    }

    public Set<ItemFlag> getFlags() {
        return transformMeta().map(ItemMeta::getItemFlags).orElse(Collections.emptySet());
    }

    public boolean hasFlag(ItemFlag flag) {
        return transformMeta().map(itemMeta -> itemMeta.hasItemFlag(flag)).orElse(false);
    }

    public ItemStackBuilder hideAttributes() {
        return flag(HIDE_FLAGS);
    }

    public ItemStackBuilder showAttributes() {
        return unflag(HIDE_FLAGS);
    }

    public ItemStackBuilder color(Color color) {
        return apply(item -> {
            Material type = item.getType();
            if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_HELMET) {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                if (meta != null) {
                    meta.setColor(color);
                    item.setItemMeta(meta);
                }
            }
        });
    }

    /**
     * If the item is not a skull this method will do nothing.
     * Attempts to set the skull's skin with UUID otherwise
     * will set the skull's skin with base64
     *
     * @param value Skin UUID or Base64
     * @return {@code this} for chaining
     */
    public ItemStackBuilder owner(String value) {
        return apply(item -> {
            Material material = item.getType();
            if (material != PlayerSkullBuilder.createSkull().getType()) return;
            try {
                UUID uuid = UUID.fromString(value);
                PlayerSkullBuilder.itemWithUUID(item, uuid);
            } catch (IllegalArgumentException e) {
                PlayerSkullBuilder.itemWithBase64(item, value);
            }
        });
    }

    /**
     * Set an item as unbreakable
     *
     * @param unbreakable Should this item be unbreakable
     * @return {@code this} for chaining
     */
    public ItemStackBuilder unbreakable(boolean unbreakable) {
        return applyMeta(meta -> meta.setUnbreakable(unbreakable));
    }

    public boolean isUnbreakable() {
        return transformMeta().map(ItemMeta::isUnbreakable).orElse(false);
    }

    public ItemStackBuilder model(int data) {
        return applyMeta(meta -> meta.setCustomModelData(data));
    }

    /**
     * Get the model data of item. {@link ItemStackBuilder#hasModelData()}
     * should be checked before invoking this method.
     *
     * @return The model data of item if present or 0
     * @see ItemMeta#getCustomModelData()
     */
    public int getModelData() {
        return transformMeta().map(ItemMeta::getCustomModelData).orElse(0);
    }

    public boolean hasModelData() {
        return transformMeta().map(ItemMeta::hasCustomModelData).orElse(false);
    }

    public ItemStack build() {
        return this.item;
    }

    public Item<InventoryClickEvent> build(Runnable runnable) {
        return new Item<>(build(), event -> runnable.run());
    }

    public Item<InventoryClickEvent> build(Consumer<InventoryClickEvent> consumer) {
        return new Item<>(build(), consumer);
    }

    @Override
    public String toString() {
        return "ItemStackBuilder{" +
                "item=" + item.toString() +
                '}';
    }

    @Override
    public ItemStackBuilder clone() {
        return ItemStackBuilder.clone(this.item);
    }
}