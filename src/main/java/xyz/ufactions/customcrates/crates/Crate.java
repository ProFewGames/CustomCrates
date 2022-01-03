package xyz.ufactions.customcrates.crates;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.libs.UtilMath;

import java.util.Map;

public class Crate {

    private final CrateSettings settings;
    private final Inventory previewInventory;

    public Crate(CrateSettings settings) {
        this.settings = settings;

        this.previewInventory = Bukkit.createInventory(null, UtilMath.round(settings.getPrizes().size()),
                settings.getDisplay());

        int delta = 0;
        for (Map.Entry<Double, RandomizableList<Prize>.Entry> entry : settings.getPrizes().getEntries().entrySet()) {
            previewInventory.setItem(delta++, entry.getValue().object.getDisplayItem());
        }
    }

    public CrateSettings getSettings() {
        return settings;
    }

    public Inventory getPreviewInventory() {
        return previewInventory;
    }

    public boolean validPouch() {
        return getSettings().getPouch().build().getType() != Material.AIR;
    }

    public void giveKey(Player player, int amount) {
        ItemStack key = settings.getKey().build();
        key.setAmount(amount);
    	int itemsToFill = countItemsToFillPartialStacks(player.getInventory(),key);
        if (getEmptySlots(player.getInventory()) == 0) {
        	if(amount <= itemsToFill) {
        		player.getInventory().addItem(key);
        	}else {
        		ItemStack partialGive = key;
        		partialGive.setAmount(itemsToFill);
        		player.getInventory().addItem(partialGive);
        		key.setAmount(amount-itemsToFill);
        		overDrop(player,key);
        	}
        } else {
        	if(amount <= itemsToFill) {
        		player.getInventory().addItem(key);
        	}else {
        		ItemStack partialGive = key;
        		partialGive.setAmount(itemsToFill);
        		player.getInventory().addItem(partialGive);
        		key.setAmount(amount-itemsToFill);
        		giveAndDrop(player,key);
        	}
        }
    }

    public void givePouch(Player player, int amount) {
        ItemStack pouch = settings.getPouch().build();
        pouch.setAmount(amount);
        int itemsToFill = countItemsToFillPartialStacks(player.getInventory(),pouch);
        if (getEmptySlots(player.getInventory()) == 0) {
        	if(amount <= itemsToFill) {
        		player.getInventory().addItem(pouch);
        	}else {
        		ItemStack partialGive = pouch;
        		partialGive.setAmount(itemsToFill);
        		player.getInventory().addItem(partialGive);
        		pouch.setAmount(amount-itemsToFill);
        		overDrop(player,pouch);
        	}
        } else {
        	if(amount <= itemsToFill) {
        		player.getInventory().addItem(pouch);
        	}else {
        		ItemStack partialGive = pouch;
        		partialGive.setAmount(itemsToFill);
        		player.getInventory().addItem(partialGive);
        		pouch.setAmount(amount-itemsToFill);
        		giveAndDrop(player,pouch);
        	}
        }
    }
    
    /**
     * This is just like World.dropItemNaturally, but it takes an Entity as a parameter instead of a Location
     * and it is able to drop more than the maximum stackable amount of the given ItemStack.
     * @param loc
     * @param drop
     */
    public void overDrop(Entity loc, ItemStack drop) {
    	int max = drop.getMaxStackSize();
    	int amount = drop.getAmount();
    	
    	
    	if (amount <= max){
            loc.getWorld().dropItemNaturally(loc.getLocation(), drop);
    	}else {
    		int itemsLeft = amount;
			ItemStack partialDrop = drop;
    		while (itemsLeft > max){
    			partialDrop.setAmount(max);
    			loc.getWorld().dropItemNaturally(loc.getLocation(), partialDrop);
    			itemsLeft -= max;
    		}
    		drop.setAmount(itemsLeft);
    		loc.getWorld().dropItemNaturally(loc.getLocation(), drop);
    	}
    }
    
    /**
     * Takes the Player's Inventory and distributes the ItemStack in its free slots,
     * given the maximum stackable amount. The rest is dropped in the Player's location.
     * This requires NO partial stacks of the same ItemStack type to be in the inventory.
     * @param player
     * @param drop
     */
    private void giveAndDrop(Player player, ItemStack item) {
    	int max = item.getMaxStackSize();
    	int amount = item.getAmount();
    	int emptySlots = getEmptySlots(player.getInventory());
    	int neededSlots;
    	
    	if (amount%max == 0) {
        	neededSlots = amount/max;    		
    	}else {
    		neededSlots = amount/max + 1;
    	}
    	
    	if (neededSlots > emptySlots) {
        	ItemStack partialGive = item;
        	partialGive.setAmount(max*emptySlots);
    		player.getInventory().addItem(partialGive);
    		item.setAmount(amount-max*emptySlots);
    		overDrop(player,item);
    	}else {
    		player.getInventory().addItem(item);
    	}
    }
    
    public int getEmptySlots(Inventory inv) {
        ItemStack[] cont = inv.getContents();
        int i = 0;
        for (ItemStack item : cont) {
          if (item != null && item.getType() != Material.AIR) {
            i++;
          }
        }
        return 36 - i;
    }
    
    public int countItemsToFillPartialStacks(Inventory inv, ItemStack itemType) {
        ItemStack[] cont = inv.getContents();
        int i = 0;
        for (ItemStack item : cont) {
          if (item != null && item.getItemMeta().getDisplayName().equals(itemType.getItemMeta().getDisplayName())) {
            i += (item.getMaxStackSize()-item.getAmount());
          }
        }
        return i;
    }
    
    
}