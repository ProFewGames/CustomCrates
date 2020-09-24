package xyz.ufactions.customcrates.libs;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class UtilEvent {

    public enum ActionType {
        L,
        L_AIR,
        L_BLOCK,
        R,
        R_AIR,
        R_BLOCK
    }

    public static boolean isAction(PlayerInteractEvent event, ActionType action) {
        if (action == ActionType.L)
            return (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK);

        if (action == ActionType.L_AIR)
            return (event.getAction() == Action.LEFT_CLICK_AIR);

        if (action == ActionType.L_BLOCK)
            return (event.getAction() == Action.LEFT_CLICK_BLOCK);

        if (action == ActionType.R)
            return (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);

        if (action == ActionType.R_AIR)
            return (event.getAction() == Action.RIGHT_CLICK_AIR);

        if (action == ActionType.R_BLOCK)
            return (event.getAction() == Action.RIGHT_CLICK_BLOCK);

        return false;
    }
}
