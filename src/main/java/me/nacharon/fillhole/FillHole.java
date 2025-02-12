package me.nacharon.fillhole;


import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.WorldEdit;
import me.nacharon.fillhole.api.fawe.mask.FullCubeMaskParser;
import me.nacharon.fillhole.api.fawe.mask.TranslucentMaskParser;
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
        getLogger().info("===================================");
        getLogger().info(" FillHole Plugin Loading...");
        getLogger().info(" FAWE version: " + Fawe.instance().getVersion());
        getLogger().info(" Minecraft version: " + getServer().getVersion());
        getLogger().info(" Java version: " + System.getProperty("java.version"));

        getLogger().info("  _____          ");
        getLogger().info(" |        |     |");
        getLogger().info(" |____    |_____|");
        getLogger().info(" |        |     |");
        getLogger().info(" |        |     |");

        Objects.requireNonNull(getCommand("fillhole")).setExecutor(new FillHoleCommand());
        Objects.requireNonNull(getCommand("fillhole")).setTabCompleter(new FillHoleTabCompleter());
        getLogger().info(" Commands Registered");

        getLogger().info(" Registering custom FAWE masks...");

        WorldEdit worldEdit = WorldEdit.getInstance();
        worldEdit.getMaskFactory().register(new FullCubeMaskParser(worldEdit));
        getLogger().info(" FullCubeMask registered!");

        worldEdit.getMaskFactory().register(new TranslucentMaskParser(worldEdit));
        getLogger().info(" TranslucentMask registered!");

        getLogger().info(" All mask are registered!");

        getLogger().info(" Plugin FillHole is activated!");
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

    public static FillHole getInstance() {
        return getPlugin(FillHole.class);
    }
}
