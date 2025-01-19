package me.nacharon.fillhole;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FillHoleTabCompleter implements TabCompleter {

    /*
     *
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

    /*
     *
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
            player.sendMessage("Error : " + e.getMessage());
        }

        return suggestions;
    }
}
