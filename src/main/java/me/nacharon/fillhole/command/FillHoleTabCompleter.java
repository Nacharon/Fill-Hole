package me.nacharon.fillhole.command;


import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import me.nacharon.fillhole.utils.PluginUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Handles tab completion for the /fillhole command.
 */
public class FillHoleTabCompleter implements TabCompleter {

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
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (sender instanceof Player player) {

            if (player.hasPermission("fillhole.use")) {

                if (args.length == 1) {
                    // add pattern suggestions
                    return getPatternSuggestions(args[0], player);
                }
            }
        }

        return completions;
    }

    /**
     * Fetches pattern suggestions based on user input.
     *
     * @param input  The partial pattern input from the user.
     * @param player The player requesting suggestions.
     * @return A list of pattern suggestions matching the input.
     */
    private List<String> getPatternSuggestions(String input, Player player) {
        List<String> suggestions = new ArrayList<>();

        try {
            PatternFactory patternFactory = WorldEdit.getInstance().getPatternFactory();

            ParserContext context = new ParserContext();
            context.setActor(null);
            context.setWorld(null);

            // get pattern suggestions
            suggestions = patternFactory.getSuggestions(input, context);
        } catch (InputParseException e) {
            player.sendMessage(PluginUtils.textRed("Error : " + e.getMessage()));
        }

        return suggestions;
    }
}
