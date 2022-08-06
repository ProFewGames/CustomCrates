package xyz.ufactions.customcrates.gui.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.enchantmentlib.EnchantmentLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class EnchantmentGUI extends GUI {

    private final Consumer<ItemStackBuilder> consumer;
    private final ItemStackBuilder builder;
    private final GUI gui;

    public EnchantmentGUI(Consumer<ItemStackBuilder> consumer, ItemStackBuilder builder, GUI gui, CustomCrates plugin, Player player) {
        super(plugin, player);

        this.consumer = consumer;
        this.builder = builder;
        this.gui = gui;

        setTitle("&3&lEnchantment Editor");
        List<Enchantment> enchantments = new ArrayList<>(Arrays.asList(Enchantment.values()));
        enchantments.remove(EnchantmentLib.getGlowEnchantment());
        enchantments.sort(Comparator.comparing(Enchantment::getName));
        setInventorySize(enchantments.size());

        for (int index = 0; index < enchantments.size(); index++)
            setEnchantmentItem(index, enchantments.get(index));
    }

    private void setEnchantmentItem(int slot, Enchantment enchantment) {
        setItem(slot, ItemStackBuilder.of(Material.ENCHANTED_BOOK)
                .name("&b&l" + F.capitalizeFirstLetter(enchantment.getName()))
                .lore("&7&o* Click to " + F.ar(builder.hasEnchantment(enchantment)) + " &7&oenchantment *")
                .build(event -> {
                    if (builder.hasEnchantment(enchantment))
                        builder.disenchant(enchantment);
                    else
                        builder.enchant(enchantment);
                    setEnchantmentItem(slot, enchantment);
                }));
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        this.consumer.accept(this.builder);
        this.gui.open();
    }
}