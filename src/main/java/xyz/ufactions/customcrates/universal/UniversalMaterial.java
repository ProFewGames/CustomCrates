package xyz.ufactions.customcrates.universal;

import org.bukkit.Material;
import xyz.ufactions.customcrates.libs.VersionUtils;

/**
 * Only to be used for any back-end use like GUIs or whatever
 * Not to be used for front-end use like user configuration-
 * Use traditional Material$ for that pls. thx
 */
public enum UniversalMaterial {
    REDSTONE_TORCH_ON(VersionUtils.Version.V1_13, "REDSTONE_TORCH_ON", "REDSTONE_TORCH");

    private final Material material;

    UniversalMaterial(VersionUtils.Version version, String legacyName, String latestName) {
        this.material = Material.getMaterial(VersionUtils.getVersion().greaterOrEquals(version) ? latestName : legacyName);
    }

    public Material get() {
        return material;
    }
}