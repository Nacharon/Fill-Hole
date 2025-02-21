package me.nacharon.fillhole.api;

import me.nacharon.fillhole.Main;

/**
 * Provides configuration settings for the plugin.
 */
public class Config {

    /**
     * Gets the config reload delay value.
     *
     * @return The delay of config reload.
     */
    public static long getReloadDelay() {
        return Main.getInstance().getConfig().getLong("settings.reload_delay", 20 * 60 * 5);
    }

    /**
     * Retrieves the maximum selection size allowed for operations.
     *
     * @return The maximum selection size as defined in the configuration.
     */
    public static int getMaxSelectionSize() {
        return Main.getInstance().getConfig().getInt("settings.max_selection_size", 10000000);
    }

    /**
     * Gets the initial processed cycle value for filtering.
     *
     * @return The number of cycles.
     */
    public static int getInitProcessedCycle() {
        return Main.getInstance().getConfig().getInt("settings.filter_processed_cycle", 1000000);
    }

    /**
     * Gets the initial tick cycle value for filtering.
     *
     * @return The tick cycle duration.
     */
    public static long getInitTickCycle() {
        return Main.getInstance().getConfig().getLong("settings.filter_tick_cycle", 5L);
    }

    /**
     * Gets the processed cycle value for filling holes.
     *
     * @return The number of cycles.
     */
    public static int getFillHoleProcessedCycle() {
        return Main.getInstance().getConfig().getInt("settings.fill_hole_processed_cycle", 100000);
    }

    /**
     * Gets the tick cycle value for filling holes.
     *
     * @return The tick cycle duration.
     */
    public static long getFillHoleTickCycle() {
        return Main.getInstance().getConfig().getLong("settings.fill_hole_tick_cycle", 40L);
    }
}