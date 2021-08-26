package xyz.ufactions.customcrates.libs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.ButtonBuilder;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.GUIBuilder;
import xyz.ufactions.customcrates.gui.internal.button.Button;

import java.util.concurrent.CompletableFuture;

public class ResponseLib {

    public static CompletableFuture<String> getString(final CustomCrates plugin, final Player player, final int timeoutSeconds) {
        CompletableFuture<String> future = new CompletableFuture<>();
        new ConversationFactory(plugin)
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return F.format("Please enter a response.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        Bukkit.broadcastMessage("Completing");
                        future.complete(input);
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withEscapeSequence("exit")
                .withLocalEcho(false)
                .withModality(true)
                .withTimeout(timeoutSeconds)
                .buildConversation(player)
                .begin();
        return future;
    }

    public static CompletableFuture<Boolean> getBoolean(final CustomCrates plugin, final Player player, final long timeoutTicks) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Button confirm = ButtonBuilder.instance(plugin)
                .item(ColorLib.cw(ChatColor.GREEN).name(F.color("&a&lCONFIRM")))
                .slot(11)
                .onClick((p, clickType) -> {
                    player.closeInventory();
                    future.complete(true);
                })
                .build();
        Button deny = ButtonBuilder.instance(plugin)
                .item(ColorLib.cw(ChatColor.RED).name(F.color("&4&lDENY")))
                .slot(15)
                .onClick((p, clickType) -> {
                    player.closeInventory();
                    future.complete(false);
                })
                .build();
        GUIBuilder.instance(plugin, F.color("&3&lConfirmation"), GUI.GUIFiller.PANE)
                .onActionPerformed(GUI.GUIAction.CLOSE, p -> {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (!future.isDone()) {
                            future.complete(false);
                        }
                    }, 1L);
                })
                .addButton(confirm, deny)
                .color(ChatColor.AQUA)
                .size(27)
                .build().openInventory(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!future.isDone()) {
                player.closeInventory();
                player.sendMessage(F.error("Response timeout."));
                future.complete(false);
            }
        }, timeoutTicks);
        return future;
    }
}