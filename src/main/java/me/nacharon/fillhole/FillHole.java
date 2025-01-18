package me.nacharon.fillhole;

import org.bukkit.plugin.java.JavaPlugin;

public final class FillHole extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("fillhole").setExecutor(new FillHoleCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
