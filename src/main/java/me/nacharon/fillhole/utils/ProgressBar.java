package me.nacharon.fillhole.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Utility class for displaying progress bars to players.
 */
public class ProgressBar {

    /**
     * Sends a progress bar to the specified player.
     *
     * @param player   The player to send the progress bar to.
     * @param taskName The name of the task being tracked.
     * @param max      The maximum progress value.
     * @param current  The current progress value.
     */
    public static void sendProgressBar(Player player, String taskName, long max, long current) {
        int barLength = 20;
        float progress = (float) current / max;
        int completedLength = (int) (barLength * progress);
        int inProgressLength = completedLength < barLength ? 1 : 0;
        int remainingLength = Math.max(0, barLength - completedLength - inProgressLength);

        StringBuilder progressBar = new StringBuilder("<gray>[");

        progressBar.append("<green>");
        progressBar.append("█".repeat(completedLength));

        if (inProgressLength > 0) {
            progressBar.append("<yellow>░");
        }

        progressBar.append("<dark_gray>");
        progressBar.append("░".repeat(remainingLength));

        progressBar.append("<gray>] ");

        int percentage = (int) (progress * 100);
        progressBar.append("<aqua>")
                .append(percentage)
                .append("% ")
                .append("<gray>(")
                .append(current)
                .append("/")
                .append(max)
                .append(")");

        Component message = MiniMessage.miniMessage().deserialize("<yellow>" + taskName + " : " + progressBar);
        player.sendMessage(message);
    }
}
