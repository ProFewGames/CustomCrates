package xyz.ufactions.customcrates.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.item.PlayerSkullBuilder;
import xyz.ufactions.customcrates.libs.ColorLib;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemGUI extends GUI {

    private final Consumer<ItemStackBuilder> consumer;
    private final ItemStackBuilder builder;
    protected final GUI fallbackGUI;

    protected boolean fallback = true;

    public ItemGUI(Consumer<ItemStackBuilder> consumer, CustomCrates plugin, ItemStackBuilder builder, GUI fallbackGUI, Player player) {
        super(plugin, player, 54);

        setTitle("&3&lItem Editor");

        this.builder = builder;
        this.consumer = consumer;
        this.fallbackGUI = fallbackGUI;
    }

    private void apply(Consumer<ItemStackBuilder> consumer) {
        consumer.accept(builder);
        this.consumer.accept(builder);
    }

    @Override
    protected void onOpen() {
        this.fallback = true;

        setDisplayItem();
        setGlowItem();
        setUnbreakableItem();
        setColorItem();
        setHeadItem();
        setModelDataItem();
        setLoreItem();
        setEnchantmentItem();

        // *** MATERIAL ***
        setItem(24, ItemStackBuilder.of(builder.getType())
                .hideAttributes()
                .name("&b&lChange Material")
                .lore(
                        "",
                        "&e&lCurrent Material:",
                        "&f&l" + F.capitalizeFirstLetter(builder.getType().name()),
                        "",
                        "&7&o* Click to modify *"
                )
                .build(event -> {
                    this.fallback = false;
                    new MaterialGUI(material -> apply(builder -> builder.type(material)), Arrays.asList(Material.values()), plugin, this, player).open();
                }));

        // *** DISPLAY NAME ***
        setItem(20, ItemStackBuilder.of(Material.PAPER)
                .name("&b&lModify Name")
                .lore(
                        "",
                        "&e&lCurrent Name:",
                        "&f" + builder.getName(),
                        "",
                        "&7&o* Click to modify *"
                )
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_DISPLAY_NAME_QUESTION))
                            .stripColor(false)
                            .inputPredicate(input -> {
                                apply(builder -> builder.name(input));
                                open();
                                return true;
                            })
                            .build();
                    this.fallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));

        //*** DURABILITY ***
        setItem(28, ItemStackBuilder.of(Material.DIAMOND_PICKAXE)
                .hideAttributes()
                .durability(builder.getDurability())
                .name("&b&lModify Durability")
                .lore(
                        "",
                        "&e&lCurrent Durability:",
                        "&f" + builder.getDurability(),
                        "",
                        "&7&o* Click to modify *"
                )
                .build(event -> {
                    Question question = Question.create(this.plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_DURABILITY_QUESTION))
                            .stripColor(true)
                            .repeatIfFailed(true)
                            .inputPredicate(input -> {
                                short durability;
                                try {
                                    durability = Short.parseShort(input);
                                } catch (NumberFormatException e) {
                                    player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_DURABILITY_UNKNOWN_FORMAT, Short.MIN_VALUE, Short.MAX_VALUE)));
                                    return false;
                                }
                                apply(builder -> builder.durability(durability));
                                open();
                                return true;
                            })
                            .build();
                    this.fallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));

        // *** FLAGS ***
        setItem(43, ColorLib.banner(ChatColor.RED)
                .name("&b&lModify Flags")
                .lore(
                        "",
                        "&e&lCurrent Flags:"
                )
                .lore(builder.getFlags().stream().map(flag -> "&f&l" + flag.name()).collect(Collectors.toList()))
                .lore(
                        "",
                        "&7&o* Click to modify *"
                )
                .build(event -> {
                    this.fallback = false;
                    new FlagsGUI(this.consumer, plugin, builder, this, player).open();
                }));
    }

    private void setEnchantmentItem() {
        setItem(34, ItemStackBuilder.of(Material.ENCHANTED_BOOK)
                .name("&b&lModify Enchantments")
                .lore("", "&e&lCurrent Enchantments")
                .lore(builder.getEnchantments().keySet().stream().map(enchantment -> "&f&l" + enchantment.getName()).collect(Collectors.toList()))
                .lore("", "&7&o* Click to modify *")
                .build(event -> {
                    this.fallback = false;
                    new EnchantmentGUI(this.consumer, builder, this, plugin, player).open();
                }));
    }

    private void setLoreItem() {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("&e&lCurrent Lore:");
        if (builder.getLore().isEmpty())
            lore.add("&c&lNo Lore");
        else
            lore.addAll(builder.getLore());
        lore.add("");
        lore.add("&7&o* Click to modify *");
        setItem(22, ItemStackBuilder.of(Material.BOOK)
                .name("&b&lModify Lore")
                .lore(lore)
                .build(event -> {
                    this.fallback = false;
                    new LoreGUI(this.consumer, builder, this, plugin, player).open();
                }));
    }

    private void setHeadItem() {
        if (builder.getType() == PlayerSkullBuilder.createSkull().getType())
            setItem(39, ItemStackBuilder.of(PlayerSkullBuilder.createSkull().getType())
                    .name("&b&lSet Skull Texture")
                    .lore("", "&e&lCurrent Skull Texture")
                    .lore(PlayerSkullBuilder.getSkullOwner(builder.build()) == null ? "&e&l* View Skull Item *" : "&f&l" + PlayerSkullBuilder.getSkullOwner(builder.build()).toString())
                    .lore("", "&fAcceptable inputs: [UUID | Base64]", "&7&o* Click to change *")
                    .build(event -> {
                        Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_OWNER_QUESTION))
                                .stripColor(true)
                                .inputPredicate(input -> {
                                    apply(builder -> builder.owner(input));
                                    open();
                                    return true;
                                })
                                .build();
                        this.fallback = false;
                        player.closeInventory();
                        plugin.getDialogManager()
                                .create(player)
                                .askQuestion(question)
                                .begin();
                    }));
        else
            getSlot(39).clear();
    }

    private void setColorItem() {
        if (builder.transformMeta().isPresent() && builder.transformMeta().get() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = ((LeatherArmorMeta) builder.transformMeta().get());
            setItem(41, ColorLib.dye(ChatColor.BLACK)
                    .name("&b&lModify Leather Armor Color")
                    .lore(
                            "",
                            "&e&lCurrent RGB:",
                            "&f&l" + meta.getColor().getRed() + ", " + meta.getColor().getGreen() + ", " + meta.getColor().getBlue(),
                            "",
                            "&7&o* Click to set &cR&aG&bB &7&ovalues *"
                    )
                    .build(event -> {
                        Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_QUESTION))
                                .repeatIfFailed(true)
                                .stripColor(true)
                                .inputPredicate(input -> {
                                    if (!input.contains(",")) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_UNKNOWN_FORMAT))); // TODO
                                        return false;
                                    }
                                    String[] array = input.split(",");
                                    if (array.length != 3) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_WRONG_ARGUMENTS)));
                                        return false;
                                    }
                                    int red;
                                    int green;
                                    int blue;
                                    try {
                                        red = Integer.parseInt(array[0].trim());
                                        green = Integer.parseInt(array[1].trim());
                                        blue = Integer.parseInt(array[2].trim());
                                    } catch (NumberFormatException e) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_PARSE_FAILED)));
                                        return false;
                                    }
                                    if (red > 255) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_VALUE_TOO_LARGE, red)));
                                        return false;
                                    }
                                    if (green > 255) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_VALUE_TOO_LARGE, green)));
                                        return false;
                                    }
                                    if (blue > 255) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_VALUE_TOO_LARGE, blue)));
                                        return false;
                                    }
                                    if (red < 0) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_VALUE_TOO_SMALL, red)));
                                        return false;
                                    }
                                    if (green < 0) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_VALUE_TOO_SMALL, green)));
                                        return false;
                                    }
                                    if (blue < 0) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_RGB_VALUE_TOO_SMALL, blue)));
                                        return false;
                                    }
                                    Color color = Color.fromRGB(red, green, blue);
                                    apply(builder -> builder.color(color));
                                    open();
                                    return true;
                                })
                                .build();
                        this.fallback = false;
                        player.closeInventory();
                        plugin.getDialogManager()
                                .create(player)
                                .askQuestion(question)
                                .begin();
                    }));
        } else {
            getSlot(41).clear();
        }
    }

    private void setUnbreakableItem() {
        setItem(32, ItemStackBuilder.of(Material.BEDROCK)
                .name("&b&lModify Unbreakable")
                .lore(
                        "",
                        "&e&lCurrent Value:",
                        builder.isUnbreakable() ? "&a&lUnbreakable" : "&c&lBreakable",
                        "",
                        "&7&o* Click to " + F.ar(builder.isUnbreakable()) + " &7&ounbreakable modifier *"
                )
                .build(event -> {
                    apply(builder -> builder.unbreakable(!builder.isUnbreakable()));
                    setDisplayItem();
                    setUnbreakableItem();
                }));
    }

    private void setDisplayItem() {
        setItem(13, builder.clone()
                .lore(
                        "",
                        "&c&lThis is just a display item"
                )
                .build(event -> {
                }));
    }

    private void setGlowItem() {
        setItem(30, ItemStackBuilder.of(Material.GLOWSTONE)
                .name("&b&lToggle Glow")
                .lore(
                        "",
                        "&e&lCurrent Value:",
                        builder.isGlowing() ? "&a&lGlowing" : "&c&lNot Glowing",
                        "",
                        "&7&o* Click to " + F.ar(builder.isGlowing()) + " &7&oglowing effect *"
                ).build(event -> {
                    apply(builder -> builder.glow(!builder.isGlowing()));
                    setDisplayItem();
                    setGlowItem();
                }));
    }

    private void setModelDataItem() {
        setItem(37, ItemStackBuilder.of(Material.GOLDEN_APPLE)
                .name("&b&lSet Custom Model Data")
                .lore(
                        "",
                        "&e&lCurrent Model Data:",
                        builder.hasModelData() ? "&f&l" + builder.getModelData() : "&f&lnone",
                        "",
                        "&7&o* Click to set model data *"
                )
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_MODEL_QUESTION))
                            .stripColor(true)
                            .repeatIfFailed(true)
                            .inputPredicate(input -> {
                                try {
                                    int model = Integer.parseInt(input);
                                    apply(builder -> builder.model(model));
                                    open();
                                    return true;
                                } catch (NumberFormatException e) {
                                    player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_MODEL_INVALID)));
                                    return false;
                                }
                            })
                            .build();
                    this.fallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        if (this.fallback) this.fallbackGUI.open();
    }
}