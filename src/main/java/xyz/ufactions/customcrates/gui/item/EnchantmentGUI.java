package xyz.ufactions.customcrates.gui.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;
import xyz.ufactions.enchantmentlib.EnchantmentLib;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EnchantmentGUI extends GUI {

    private final Consumer<ItemStackBuilder> consumer;
    private final ItemStackBuilder builder;
    private final GUI gui;

    private boolean openFallbackGUI = true;

    public EnchantmentGUI(Consumer<ItemStackBuilder> consumer, ItemStackBuilder builder, GUI gui, CustomCrates plugin, Player player) {
        super(plugin, player);

        this.consumer = consumer;
        this.builder = builder;
        this.gui = gui;

        setTitle("&3&lEnchantment Editor");
        List<Enchantment> enchantments = new ArrayList<>(Arrays.asList(Enchantment.values()));
        enchantments.remove(EnchantmentLib.getGlowEnchantment());
        enchantments.sort(Comparator.comparing(Enchantment::getName));

        setInventorySize(enchantments.size() + 1);
    }

    @Override
    protected void onOpen() {
        List<Enchantment> enchantments = new ArrayList<>(Arrays.asList(Enchantment.values()));
        enchantments.remove(EnchantmentLib.getGlowEnchantment());
        enchantments.sort(Comparator.comparing(Enchantment::getName));

        AtomicInteger index = new AtomicInteger();

        enchantments.removeIf(enchantment -> {
            boolean has = builder.hasEnchantment(enchantment);
            if (has)
                setEnchantmentItem(index.getAndIncrement(), enchantment);
            return has;
        });

        setItem(index.getAndIncrement(), ItemStackBuilder.of(Material.AIR).build(() -> {
        }));

        for (Enchantment enchantment : enchantments)
            setEnchantmentItem(index.incrementAndGet(), enchantment);
    }

    private void setEnchantmentItem(int slot, Enchantment enchantment) {
        setItem(slot, ItemStackBuilder.of(Material.ENCHANTED_BOOK)
                .name("&3&l" + F.capitalizeFirstLetter(enchantment.getName()))
                .lore("",
                        "&e&l" + F.STAR_SYMBOL + " Current Level",
                        "&f" + F.BAR_SYMBOL + " " + UtilMath.formatNumber(builder.getEnchantmentLevel(enchantment)),
                        "",
                        "&7&o* Click to " + F.ar(builder.hasEnchantment(enchantment)) + " &7&oenchantment *")
                .build(event -> {
                    if (builder.hasEnchantment(enchantment)) {
                        builder.disenchant(enchantment);
                        onOpen();
                    } else {
                        openFallbackGUI = false;
                        player.closeInventory();
                        Question question = Question.create("What level do you want to enchant this item?")
                                .repeatIfFailed(true)
                                .inputPredicate(input -> {
                                    OptionalInt level = UtilMath.getInteger(input);
                                    if (!level.isPresent()) return false;
                                    builder.enchant(enchantment, level.getAsInt());
                                    open();
                                    openFallbackGUI = true;
                                    return true;
                                })
                                .build();
                        plugin.getDialogManager()
                                .create(player)
                                .askQuestion(question)
                                .begin();
                    }
                }));
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        this.consumer.accept(this.builder);
        if (openFallbackGUI)
            this.gui.open();
    }
}