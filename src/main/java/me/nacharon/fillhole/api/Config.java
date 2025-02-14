package me.nacharon.fillhole.api;

import me.nacharon.fillhole.Main;

/**
 * Provides configuration settings for the plugin.
 */
public class Config {

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