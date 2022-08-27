package xyz.ufactions.customcrates.libs;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@UtilityClass
public class Utility {

    public void addOrDropItem(Inventory inventory, Location dropLocation, ItemStack item) {
        Validate.notNull(dropLocation);
        Validate.notNull(dropLocation.getWorld());
        Validate.notNull(inventory);
        Validate.notNull(item);

        HashMap<Integer, ItemStack> leftOvers = inventory.addItem(item);
        if (leftOvers.isEmpty()) return;
        for (ItemStack leftOver : leftOvers.values())
            dropLocation.getWorld().dropItemNaturally(dropLocation, leftOver);
    }
}