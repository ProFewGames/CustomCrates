package xyz.ufactions.customcrates.spin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.UtilTime;
import xyz.ufactions.customcrates.universal.Universal;

import java.util.concurrent.atomic.AtomicReference;

public class RouletteSpin extends Spin {

    @Override
    public void execute(Player player, Crate crate) {
        Inventory inventory = constructInventory(crate, 27);

        AtomicReference<Prize> prize = new AtomicReference<>();
        prize.set(randomPrize(crate));
        inventory.setItem(13, prize.get().getDisplayItem());
        final long start = System.currentTimeMillis();

        new BukkitRunnable() {

            @Override
            public void run() {
                if (UtilTime.elapsed(start, crate.getSettings().getSpinTime())) {
                    cancel();
                    end(player, crate, prize.get());
                    return;
                }
                if (!player.getOpenInventory().getTitle().equals(crate.getSettings().getDisplay())) {
                    cancel();
                    end(player, crate, randomPrize(crate));
                    return;
                }
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (inventory.getItem(i) == null || Universal.getInstance().isStainedGlassPane(inventory.getItem(i))) {
                        inventory.setItem(i, Universal.getInstance().colorToGlassPane(randomColor()).name(" ").build());
                    }
                }
                player.playSound(player.getLocation(), crate.getSettings().getSpinSound(), 1f, 1f);
                prize.set(randomPrize(crate));
                inventory.setItem(13, prize.get().getDisplayItem());
            }
        }.runTaskTimer(plugin, 0L, 8L);
        player.openInventory(inventory);
    }
}