package xyz.ufactions.customcrates.gui;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.PagedGUI;
import xyz.ufactions.customcrates.item.Item;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageGUI extends PagedGUI {

    @AllArgsConstructor
    private static class LanguageItem {
        private final LanguageFile.LanguagePath languagePath;
        private final Item<InventoryClickEvent> item;
    }

    private final List<LanguageItem> languageItems;

    public LanguageGUI(CustomCrates plugin, GUI fallbackGUI, Player player) {
        super("&3&lLanguage Editor", plugin, fallbackGUI, player);

        this.languageItems = new ArrayList<>();

        for (LanguageFile.LanguagePath languagePath : LanguageFile.LanguagePath.values()) {
            this.languageItems.add(new LanguageItem(languagePath, ItemStackBuilder.of(Material.PAPER)
                    .name("&b&l" + languagePath.getFriendlyName())
                    .lore(languagePath.getDescription())
                    .lore("", "&b&lCurrently Set:", "&f" + plugin.getLanguage().getString(languagePath), "", "&7&o* Click to edit *")
                    .build(event -> {
                        Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_LANGUAGE_QUESTION))
                                .stripColor(false)
                                .inputPredicate(input -> {
                                    if (input.endsWith("\"") && input.startsWith("\""))
                                        input = input.substring(1, input.length() - 1);
                                    plugin.getLanguage().set(languagePath.getPath(), input);
                                    try {
                                        plugin.getLanguage().save();
                                        plugin.reload();
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_LANGUAGE_RESPONSE)));
                                    } catch (IOException e) {
                                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.ERROR_FILE_SAVING)));
                                        e.printStackTrace();
                                    }
                                    open();
                                    return true;
                                }).build();
                        this.openFallback = false;
                        player.closeInventory();
                        plugin.getDialogManager()
                                .create(player)
                                .askQuestion(question)
                                .begin();
                    })));
        }
    }

    @Override
    protected List<Item<InventoryClickEvent>> getItems() {
        return this.languageItems.stream().map(languageItem -> languageItem.item).collect(Collectors.toList());
    }

    @Override
    protected List<Item<InventoryClickEvent>> getSearchItems(String search) {
        List<Item<InventoryClickEvent>> list = new ArrayList<>();
        for (LanguageItem languageItem : this.languageItems)
            if (languageItem.languagePath.getFriendlyName().toLowerCase().startsWith(search) ||
                    languageItem.languagePath.getPath().toLowerCase().contains(search))
                list.add(languageItem.item);
        return list;
    }
}