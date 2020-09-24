package xyz.ufactions.customcrates.spin;

import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.crates.ICrate;

public class QuickSpin extends Spin {

    @Override
    protected void execute(Player player, ICrate crate) {
        end(player, crate, randomPrize(crate));
    }
}