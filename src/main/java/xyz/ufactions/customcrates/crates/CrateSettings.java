package xyz.ufactions.customcrates.crates;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.List;
import java.util.Map;

/**
 * All the crate's configurable settings are contained here. (e.g. Crate Display, Identifier, Prizes, etc...)
 */
@Getter
@Setter
public class CrateSettings {

    private final String identifier;
    private String display;
    private List<String> openCommands;
    private Material block;
    private Spin.SpinType spinType;
    private final long spinTime;
    private final Sound openingSound;
    private final Sound spinSound;
    private final Sound winSound;

    private ItemStackBuilder key;

    private final ItemStackBuilder pouch;

    private final RandomizableList<Prize> prizes;

    private final Map<String, ItemStackBuilder> holographicItemMap;
    private final List<String> holographicLines;

    public CrateSettings(String identifier, String display, List<String> openCommands, Material block, Spin.SpinType spinType,
                         long spinTime, Sound openingSound, Sound spinSound, Sound winSound, ItemStackBuilder key, ItemStackBuilder pouch,
                         RandomizableList<Prize> prizes, Map<String, ItemStackBuilder> holographicItemMap, List<String> holographicLines) {
        this.identifier = identifier;
        this.display = F.color(display);
        this.openCommands = openCommands;
        this.block = block;
        this.spinType = spinType;
        this.spinTime = spinTime;
        this.openingSound = openingSound;
        this.spinSound = spinSound;
        this.winSound = winSound;
        this.key = key;
        this.pouch = pouch;
        this.prizes = prizes;
        this.holographicItemMap = holographicItemMap;
        this.holographicLines = holographicLines;
    }

    public void addCommand(String command) {
        this.openCommands.add(command);
    }

    public void removeCommand(String command) {
        this.openCommands.remove(command);
    }
}