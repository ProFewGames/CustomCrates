package xyz.ufactions.customcrates.spin;

import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.crates.Crate;

public class QuickSpin extends Spin {

    @Override
    protected void execute(Player player, Crate crate) {
        end(player, crate, randomPrize(crate));
    }
}