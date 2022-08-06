package xyz.ufactions.customcrates.dialog;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DialogManager {

    private final Map<UUID, Dialog> playerDialog;

    @Getter(AccessLevel.PACKAGE)
    private final CustomCrates plugin;

    public DialogManager(CustomCrates plugin) {
        this.plugin = plugin;
        this.playerDialog = new HashMap<>();
    }

    void removeDialog(Player player) {
        this.playerDialog.remove(player.getUniqueId());
    }

    void setDialog(Dialog dialog) {
        this.playerDialog.put(dialog.getPlayer().getUniqueId(), dialog);
    }

    public Optional<Dialog> getDialog(Player player) {
        return Optional.ofNullable(this.playerDialog.get(player.getUniqueId()));
    }

    public Dialog create(Player player) {
        return new DialogImpl(this, player);
    }
}