package xyz.ufactions.customcrates.crates3;

import org.bukkit.Material;
import org.bukkit.Sound;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.List;

public interface ICrate {

    String getIdentifier();

    void setDisplay(String title);

    String getDisplay();

    void setOpenCommands(List<String> commands);

    List<String> getOpenCommands();

    void setBlock(Material block);

    Material getBlock();

    void setSpinType(Spin.SpinType type);

    Spin.SpinType getSpinType();

    void setSpinTime(long spinTime);

    long getSpinTime();

    void setOpenSound(Sound sound);

    Sound getOpenSound();

    void setSpinSound(Sound sound);

    Sound getSpinSound();

    void setWinSound(Sound sound);

    Sound getWinSound();

    void setKey(ItemStackBuilder builder);

    ItemStackBuilder getKey();

    ItemStackBuilder getPouch();

    RandomizableList<Prize> getPrizes();

    double getHologramOffset();

    List<String> getHologramLines();
}