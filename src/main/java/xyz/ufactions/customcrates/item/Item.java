package xyz.ufactions.customcrates.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class Item<O> {

    private final ItemStack item;
    private final Consumer<O> consumer;
}