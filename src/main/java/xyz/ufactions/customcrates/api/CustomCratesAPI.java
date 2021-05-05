package xyz.ufactions.customcrates.api;

import xyz.ufactions.customcrates.CustomCrates;

public final class CustomCratesAPI {

    private static CustomCrates plugin;

    /**
     * Only to be instantiated by CustomCrates
     */
    public static void initialize(CustomCrates plugin) {
        if (CustomCratesAPI.plugin == null) CustomCratesAPI.plugin = plugin;
    }

    // TODO Add more functions
}