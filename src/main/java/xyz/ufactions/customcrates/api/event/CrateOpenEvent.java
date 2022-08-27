package xyz.ufactions.customcrates.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.ufactions.customcrates.crates.Crate;

/**
 * Called when a player opens a crate.
 */
public class CrateOpenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * The player who opened the crate.
     */
    @Getter
    private final Player player;

    /**
     * The crate being opened.
     */
    @Getter
    private final Crate crate;

    public CrateOpenEvent(Player player, Crate crate) {
        this.player = player;
        this.crate = crate;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}