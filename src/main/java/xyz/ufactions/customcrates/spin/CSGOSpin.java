package xyz.ufactions.customcrates.spin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.ColorLib;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.UtilTime;
import xyz.ufactions.customcrates.universal.UniversalMaterial;

import java.util.ArrayList;
import java.util.List;

public class CSGOSpin extends Spin {

    private final int delta = 10;

    @Override
    protected void execute(Player player, Crate crate) {
        Inventory inventory = constructInventory(crate, 27);

        // Border
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16)
                continue;
            if (i == 4 || i == 22) {
                inventory.setItem(i, new ItemBuilder(UniversalMaterial.REDSTONE_TORCH_ON.get()).name(" ").build());
                continue;
            }
            inventory.setItem(i, ColorLib.pane(randomColor()).name(" ").build());
        }

        final int delta = 10;

        // 9 Prizes
        List<Prize> prizes = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            prizes.add(i, randomPrize(crate));
            inventory.setItem(i + delta, prizes.get(i).getItemBuilder().build());
        }

        final long start = System.currentTimeMillis();

        player.openInventory(inventory);

        newTask(start, player, inventory, crate, prizes, false).runTaskTimer(plugin, 0, 1);
    }

    private BukkitRunnable newTask(final long start, final Player player, final Inventory inventory, final Crate crate, final List<Prize> prizes, final boolean slowDown) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                if (UtilTime.elapsed(start, crate.getSettings().getSpinTime())) {
                    cancel();
                    end(player, crate, prizes.get(3));
                    return;
                }
                if (UtilTime.elapsed(start, crate.getSettings().getSpinTime() - 1300) && !slowDown) {
                    cancel();
                    newTask(start, player, inventory, crate, prizes, true).runTaskTimer(plugin, 0, 3);
                    return;
                }
                if (!player.getOpenInventory().getTitle().equals(crate.getSettings().getDisplay())) {
                    cancel();
                    end(player, crate, randomPrize(crate));
                    return;
                }
                player.playSound(player.getLocation(), crate.getSettings().getSpinSound(), 1f, 1f);
                // Scrolling prize list
                for (int i = 0; i < prizes.size(); i++) {
                    int slot = i + delta;
                    if (i == prizes.size() - 1) {
                        prizes.set(i, randomPrize(crate));
                        inventory.setItem(slot, prizes.get(i).getItemBuilder().build());
                        continue;
                    }
                    Prize next = prizes.get(i + 1);
                    inventory.setItem(slot, next.getItemBuilder().build());
                    prizes.set(i, next);
                }
            }
        };
    }
}