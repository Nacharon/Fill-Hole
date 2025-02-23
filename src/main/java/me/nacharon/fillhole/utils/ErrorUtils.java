package me.nacharon.fillhole.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * Utility class for handling error messages and logging.
 */
public class ErrorUtils {

    /**
     * Sends an error message to the player and logs the full stack trace in the server console.
     *
     * @param error  The exception that occurred.
     * @param player The player to notify about the error.
     */
    public static void errorMessage(Exception error, Player player) {
        Throwable cause = error;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        String errorMessage = cause.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "An unknown error occurred.";
        }

        player.sendMessage(TextUtils.textRed("Error : " + errorMessage));
        Bukkit.getLogger().log(Level.SEVERE, "Error occurred in plugin", error);
    }
}
