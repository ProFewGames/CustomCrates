package xyz.ufactions.customcrates.gui.item;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.PagedGUI;
import xyz.ufactions.customcrates.item.Item;
import xyz.ufactions.customcrates.item.ItemStackBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MaterialGUI extends PagedGUI {

    @AllArgsConstructor
    private static class MaterialItem {

        private final Material material;
        private final Item<InventoryClickEvent> item;
    }

    private final List<MaterialItem> materialItems;

    public MaterialGUI(Consumer<Material> consumer, List<Material> materials, CustomCrates plugin, GUI fallbackGUI, Player player) {
        this(consumer, materials.toArray(new Material[0]), plugin, fallbackGUI, player);
    }

    public MaterialGUI(Consumer<Material> consumer, Material[] materials, CustomCrates plugin, GUI fallbackGUI, Player player) {
        super("&3&lSelect Material", plugin, fallbackGUI, player);

        this.materialItems = new ArrayList<>();

        for (Material material : materials) {
            materialItems.add(new MaterialItem(material, ItemStackBuilder.of(material)
                    .lore("", "&7&o* Click to select *")
                    .build(event -> {
                        consumer.accept(material);
                        player.closeInventory();
                    })));
        }
    }

    @Override
    protected List<Item<InventoryClickEvent>> getItems() {
        return this.materialItems.stream().map(materialItem -> materialItem.item).collect(Collectors.toList());
    }

    @Override
    protected List<Item<InventoryClickEvent>> getSearchItems(String search) {
        List<Item<InventoryClickEvent>> list = new ArrayList<>();
        for (MaterialItem materialItem : this.materialItems)
            if (materialItem.material.name().toLowerCase().contains(search.toLowerCase()))
                list.add(materialItem.item);
        return list;
    }
}