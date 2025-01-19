package me.nacharon.fillhole;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FillHole extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("fillhole")).setExecutor(new FillHoleCommand());
        Objects.requireNonNull(getCommand("fillhole")).setTabCompleter(new FillHoleTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
