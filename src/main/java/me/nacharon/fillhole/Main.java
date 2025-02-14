package me.nacharon.fillhole;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.WorldEdit;
import me.nacharon.fillhole.api.fawe.mask.FullCubeMaskParser;
import me.nacharon.fillhole.api.fawe.mask.TranslucentMaskParser;
import me.nacharon.fillhole.command.FillHoleCommand;
import me.nacharon.fillhole.command.FillHoleTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;


/**
 * Main class for the FillHole plugin.
 * Handles the plugin lifecycle and command registration.
 */
public final class Main extends JavaPlugin {

    /**
     * Called when the plugin is enabled.
     * Registers commands and their associated executors and tab completer.
     */
    @Override
    public void onEnable() {
        getLogger().info("===================================");
        getLogger().info(" FillHole Plugin Loading...");
        getLogger().info(" FAWE Version: " + Fawe.instance().getVersion());
        getLogger().info(" Minecraft Version: " + getServer().getVersion());
        getLogger().info(" Java Version: " + System.getProperty("java.version"));

        getLogger().info("  _____          ");
        getLogger().info(" |        |     |");
        getLogger().info(" |____    |_____|");
        getLogger().info(" |        |     |");
        getLogger().info(" |        |     |");

        getLogger().info(" Loading Config ...");
        // 5 minutes
        long reloadDelay = 20 * 60 * 5;
        saveDefaultConfig();
        new BukkitRunnable() {
            @Override
            public void run() {
                reloadConfig();
            }
        }.runTaskTimerAsynchronously(getInstance(), reloadDelay, reloadDelay);
        getLogger().info(" Config Loaded !");

        getLogger().info(" Registering Commands ...");
        Objects.requireNonNull(getCommand("fillhole")).setExecutor(new FillHoleCommand());
        Objects.requireNonNull(getCommand("fillhole")).setTabCompleter(new FillHoleTabCompleter());
        getLogger().info(" FillHole command Registered !");

        getLogger().info(" FillHoleTask command Registered !");

        getLogger().info(" All Commands Registered !");

        getLogger().info(" Registering Custom FAWE Masks ...");

        WorldEdit worldEdit = WorldEdit.getInstance();
        worldEdit.getMaskFactory().register(new FullCubeMaskParser(worldEdit));
        getLogger().info(" FullCubeMask Registered !");

        worldEdit.getMaskFactory().register(new TranslucentMaskParser(worldEdit));
        getLogger().info(" TranslucentMask Registered !");

        getLogger().info(" All Mask are Registered !");

        getLogger().info(" Plugin FillHole is activated !");
        getLogger().info("===================================");
    }

    /**
     * Called when the plugin is disabled.
     * Handles any necessary cleanup.
     */
    @Override
    public void onDisable() {
        getLogger().info("===================================");
        getLogger().info(" Plugin FillHole is deactivating...");
        getLogger().info(" Cleanup operations executed");
        getLogger().info(" Plugin FillHole is deactivated!");
        getLogger().info("===================================");
    }

    /**
     * Gets the instance of the main plugin.
     *
     * @return The main plugin instance.
     */
    public static Main getInstance() {
        return getPlugin(Main.class);
    }
}
