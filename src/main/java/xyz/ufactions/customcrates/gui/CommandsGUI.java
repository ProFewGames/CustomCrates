package xyz.ufactions.customcrates.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.Slot;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.universal.UniversalMaterial;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CommandsGUI extends GUI {

    private final Supplier<List<String>> commandSupplier;
    private final Consumer<List<String>> commandConsumer;
    private final GUI gui;

    private boolean fallback = true;

    public CommandsGUI(Consumer<List<String>> commandConsumer, Supplier<List<String>> commandSupplier, CustomCrates plugin, GUI gui, Player player) {
        super(plugin, player);

        this.commandSupplier = commandSupplier;
        this.commandConsumer = commandConsumer;
        this.gui = gui;

        setInventorySize(commandSupplier.get().size() + 1);
        setTitle("&3&lCommand Editor");

        setItem(0, ItemStackBuilder.of(UniversalMaterial.SIGN.get())
                .name("&b&lAdd Command")
                .lore("&7&o* Click to add command *")
                .build(event -> {
                    Question question = Question.create(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_ADD_COMMAND_QUESTION))
                            .stripColor(false)
                            .inputPredicate(input -> {
                                if (input.toLowerCase().contains(player.getName().toLowerCase()) && !input.toLowerCase().contains("%player%")) {
                                    player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.GUI_ADD_COMMAND_USERNAME_FLAG)));
                                    input = input.replaceFirst("(?i)" + player.getName(), "%player%");
                                }
                                List<String> commands = commandSupplier.get();
                                commands.add(input);
                                commandConsumer.accept(commands);
                                open();
                                return true;
                            }).build();
                    this.fallback = false;
                    player.closeInventory();
                    plugin.getDialogManager()
                            .create(player)
                            .askQuestion(question)
                            .begin();
                }));
    }

    @Override
    protected void onOpen() {
        this.fallback = true;

        seatCommands();
    }

    private void seatCommands() {
        for (Slot slot : getSlots())
            if (slot.getSlot() != 0)
                slot.clear();

        List<String> commands = commandSupplier.get();
        for (int index = 0; index < commands.size(); index++) {
            String command = commands.get(index);
            setItem(index + 1, ItemStackBuilder.of(Material.PAPER)
                    .name(" ")
                    .lore("", "&f" + command, "", "&7&o * Click to remove * ")
                    .build(event -> {
                        commands.remove(command);
                        commandConsumer.accept(commands);
                        seatCommands();
                    }));
        }
    }

    @Override
    protected void handleClose(InventoryCloseEvent event) {
        if (this.fallback) this.gui.open();
    }
}