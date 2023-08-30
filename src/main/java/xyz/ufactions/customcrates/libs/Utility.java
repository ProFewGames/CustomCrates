package xyz.ufactions.customcrates.libs;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class Utility {

    public void addOrDropItem(Inventory inventory, Location dropLocation, ItemStack item) {
        Validate.notNull(dropLocation);
        Validate.notNull(dropLocation.getWorld());
        Validate.notNull(inventory);
        Validate.notNull(item);

        // TODO Removed items dropping on the floor as it seems to be causing issues dropping a full stack.
        inventory.addItem(item);
    }
}