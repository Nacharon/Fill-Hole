package me.nacharon.fillhole;


import me.nacharon.fillhole.command.FillHoleCommand;
import me.nacharon.fillhole.command.FillHoleTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


/**
 * Main class for the FillHole plugin.
 * Handles the plugin lifecycle and command registration.
 */
public final class FillHole extends JavaPlugin {

    /**
     * Called when the plugin is enabled.
     * Registers commands and their associated executors and tab completers.
     */
    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("fillhole")).setExecutor(new FillHoleCommand());
        Objects.requireNonNull(getCommand("fillhole")).setTabCompleter(new FillHoleTabCompleter());

        getLogger().info("Plugin FilHole is activate !");
    }

    /**
     * Called when the plugin is disabled.
     * Handles any necessary cleanup.
     */
    @Override
    public void onDisable() {
        getLogger().info("Plugin FilHole is deactivate !");
    }
}
