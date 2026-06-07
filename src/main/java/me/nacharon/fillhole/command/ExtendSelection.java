package me.nacharon.fillhole.command;

import me.nacharon.fillhole.api.Config;
import me.nacharon.fillhole.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ExtendSelection implements CommandExecutor, TabCompleter {
    /**
     * Handles the execution of the /fillhole command.
     *
     * @param sender  the sender of the command
     * @param command the command being executed
     * @param label   the alias used to trigger the command
     * @param args    the arguments passed with the command
     * @return true if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NonNull [] args) {
        // check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtils.textRed("This command must be executed by a player!"));
            return true;
        }

        // check if the player has permission to use this command
        if (!player.hasPermission("fillhole.selection.extend")) {
            player.sendMessage(TextUtils.textRed("You do not have permission to use this command."));
            return true;
        }

        // check that the command is used correctly
        if (args.length < 1) {
            if (player.getScoreboardTags().contains(Config.getDisableExpendTag()))
                player.sendMessage(TextUtils.textYellow("Extend Selection is disabled"));
            else
                player.sendMessage(TextUtils.textYellow("Extend Selection is enabled"));
            return true;
        }
        if (args[0].equals("disable")) {
            if (player.getScoreboardTags().add(Config.getDisableExpendTag())) {
                player.sendMessage(TextUtils.textGreen("Extend Selection is now disabled"));
            }
            else {
                player.sendMessage(TextUtils.textRed("Extend Selection is already disabled"));
            }
        }
        else if (args[0].equals("enable")) {
            if (player.getScoreboardTags().remove(Config.getDisableExpendTag())) {
                player.sendMessage(TextUtils.textGreen("Extend Selection is now enabled"));
            }
            else {
                player.sendMessage(TextUtils.textRed("Extend Selection is already enabled"));
            }
        }
        else {
            player.sendMessage(TextUtils.textRed("Usage: /extendsel <enable | disable>"));
        }

        return true;
    }

    /**
     * Provides tab completion suggestions for the /fillhole command.
     *
     * @param sender  The sender of the command.
     * @param command The command being executed.
     * @param alias   The alias used to trigger the command.
     * @param args    The current arguments provided by the user.
     * @return A list of suggestions for tab completion.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NonNull [] args) {
        return List.of("enable", "disable");
    }
}
