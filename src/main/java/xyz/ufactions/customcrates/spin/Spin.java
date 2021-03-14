package xyz.ufactions.customcrates.spin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;

public abstract class Spin {

    public enum SpinType {
        ROULETTE(new RouletteSpin()), CSGO(new CSGOSpin()), NONE(new QuickSpin());

        private final Spin spin;

        SpinType(Spin spin) {
            this.spin = spin;
        }

        public Spin getSpin(CustomCrates plugin) {
            spin.setPlugin(plugin);
            return spin;
        }
    }

    protected CustomCrates plugin;

    public void setPlugin(CustomCrates plugin) {
        this.plugin = plugin;
    }

    public final void spin(Player player, ICrate crate) {
        try {
            player.playSound(player.getLocation(), crate.getOpeningSound(), 1f, 1f);
            for (String command : crate.openCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), F.color(command.replaceAll("%player%", player.getName())
                        .replaceAll("%crate%", crate.getDisplay())));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize pre-crate open events for crate " + crate.getIdentifier() + ".");
            if (plugin.debugging()) e.printStackTrace();
        }
        try {
            execute(player, crate);
        } catch (Exception e) {
            plugin.getLogger().warning("Error whilst spinning for type: " + this.getClass().getSimpleName() + ". Crate: " + crate.getIdentifier());
            if (plugin.debugging()) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void execute(Player player, ICrate crate);

    protected final Inventory constructInventory(ICrate crate, int size) {
        return Bukkit.createInventory(null, size, crate.getDisplay());
    }

    protected final void end(Player player, ICrate crate, Prize prizeWon) {
        player.playSound(player.getLocation(), crate.getWinSound(), 1f, 1f);
        for (String command : prizeWon.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', command.replaceAll("%player%", player.getName())));
        }
    }

    protected final Prize randomPrize(ICrate crate) {
        return crate.getPrizes().get();
    }

//    protected final Prize randomPrize(ICrate crate) {
//        double max = 0;
//        List<Prize> prizes = crate.getPrizes();
//        for (Prize prize : prizes)
//            if (prize.getChance() > max)
//                max = prize.getChance();
//        DecimalFormat format = new DecimalFormat("####.##");
//        String chanceNumberString = format.format(0 + max * UtilMath.random.nextDouble());
//        double chanceNumber = Double.parseDouble(chanceNumberString);
//        List<Prize> winnablePrizes = new ArrayList<>();
//        for (Prize prize : prizes) {
//            double chance = prize.getChance();
//            if (chanceNumber <= chance)
//                winnablePrizes.add(prize);
//        }
//        Prize wonPrize = null;
//        while (wonPrize == null) {
//            if (winnablePrizes.size() > 1) {
//                int prizeToPick = UtilMath.random.nextInt(winnablePrizes.size());
//                wonPrize = winnablePrizes.get(prizeToPick);
//            } else {
//                wonPrize = winnablePrizes.get(0);
//            }
//        }
//        return wonPrize;
//    }

    protected final ChatColor randomColor() {
        return ChatColor.values()[UtilMath.random.nextInt(ChatColor.values().length)];
    }
}