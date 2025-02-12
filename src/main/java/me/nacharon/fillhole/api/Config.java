package me.nacharon.fillhole.api;

import me.nacharon.fillhole.FillHole;

public class Config {
    public static int getMaxValidBLock() {
        return FillHole.getInstance().getConfig().getInt("settings.max_valid_blocks", 200000);
    }
}