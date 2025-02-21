package me.nacharon.fillhole.command;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.Region;
import me.nacharon.fillhole.api.Config;
import me.nacharon.fillhole.api.fawe.FaweHook;
import me.nacharon.fillhole.core.FindHole;
import me.nacharon.fillhole.utils.ErrorUtils;
import me.nacharon.fillhole.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Define FillHole Command
 */
public class FillHoleCommand implements CommandExecutor {

    private static final Map<UUID, FindHole> playerTasks = new HashMap<>();

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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtils.textRed("This command must be executed by a player!"));
            return true;
        }

        // check if the player has permission to use this command
        if (!player.hasPermission("fillhole.use")) {
            player.sendMessage(TextUtils.textRed("You do not have permission to use this command."));
            return true;
        }

        // check that the command is used correctly
        if (args.length < 1) {
            player.sendMessage(TextUtils.textRed("Usage: /fillhole <pattern>"));
            return true;
        }

        if (playerTasks.containsKey(player.getUniqueId())) {
            player.sendMessage(TextUtils.textRed("One action is already in progress"));
            return true;
        }

        try {
            String patternInput = args[0];

            Pattern pattern = FaweHook.getPattern(patternInput, player);

            // get FAWE local session
            LocalSession localSession = FaweHook.getLocalSession(player);

            Region selection;
            try {
                selection = FaweHook.getSelection(localSession);
            } catch (Exception e) {
                player.sendMessage(TextUtils.textRed("Please select a cuboid selection first!"));
                return true;
            }

            // get FAWE edit session
            EditSession editSession = FaweHook.getEditSession(player, localSession);

            int maxSelectionSize = Config.getMaxSelectionSize();
            if (selection.getVolume() <= maxSelectionSize) {
                FindHole findHole = new FindHole(selection, editSession);
                playerTasks.put(player.getUniqueId(), findHole);
                findHole.fillHoleBlocks(player, localSession, pattern);
            } else
                player.sendMessage(TextUtils.textRed("The selection is too big, the max size is " + maxSelectionSize + " blocks"));

        } catch (Exception e) {
            ErrorUtils.errorMessage(e, player);
        }
        return true;
    }

    /**
     * Removes a scheduled task associated with a player.
     *
     * @param player The player whose task should be removed.
     */
    public static void removeTask(Player player) {
        playerTasks.remove(player.getUniqueId());
    }
}