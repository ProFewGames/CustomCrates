package xyz.ufactions.customcrates.updater.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ufactions.customcrates.updater.UpdateType;

public class UpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UpdateType _type;

    public UpdateEvent(UpdateType example) {
        _type = example;
    }

    public UpdateType getType() {
        return _type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}