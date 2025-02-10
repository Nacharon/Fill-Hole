package me.nacharon.fillhole.api.fawe;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * Utility class for interacting with FastAsyncWorldEdit (FAWE) in a Bukkit environment.
 * Provides helper methods for managing WorldEdit sessions, selections, and block modifications.
 */
public class FaweHook {

    /**
     * Converts a Bukkit {@link Player} to a WorldEdit {@link BukkitPlayer}.
     *
     * @param player The Bukkit player to convert.
     * @return The corresponding WorldEdit BukkitPlayer.
     */
    public static BukkitPlayer getBukkitPlayer(Player player) {
        return BukkitAdapter.adapt(player);
    }

    /**
     * Retrieves the LocalSession of the specified player.
     *
     * @param player The player whose WorldEdit session is retrieved.
     * @return The LocalSession associated with the player.
     */
    public static LocalSession getLocalSession(Player player) {
        BukkitPlayer bukkitPlayer = getBukkitPlayer(player);

        return WorldEdit.getInstance()
                .getSessionManager()
                .get(bukkitPlayer);
    }

    /**
     * Retrieves an EditSession for the specified player.
     *
     * @param player The player for whom the EditSession is created.
     * @return The EditSession associated with the player.
     */
    public static EditSession getEditSession(Player player) {
        LocalSession localSession = getLocalSession(player);

        return getEditSession(player, localSession);
    }

    /**
     * Retrieves an EditSession for the specified player using an existing LocalSession.
     *
     * @param player  The player for whom the EditSession is created.
     * @param session The LocalSession associated with the player.
     * @return The EditSession linked to the given LocalSession.
     */
    public static EditSession getEditSession(Player player, LocalSession session) {
        BukkitPlayer bukkitPlayer = getBukkitPlayer(player);

        return session.createEditSession(bukkitPlayer);
    }

    /**
     * Creates a ParserContext for parsing WorldEdit patterns in the context of a player.
     *
     * @param player The player whose context is used for parsing.
     * @return A configured ParserContext instance.
     */
    private static ParserContext getParserContext(Player player) {
        BukkitPlayer bukkitPlayer = getBukkitPlayer(player);

        LocalSession localSession = getLocalSession(player);

        // define context
        ParserContext context = new ParserContext();
        context.setActor(BukkitAdapter.adapt(player));
        context.setWorld(bukkitPlayer.getWorld());
        context.setExtent(bukkitPlayer.getExtent());
        context.setSession(localSession);

        return context;
    }

    /**
     * Parses a WorldEdit pattern string into a Pattern object.
     *
     * @param patternInput The pattern string (e.g., "stone,50%dirt").
     * @param player       The player context for parsing.
     * @return The parsed Pattern object.
     */
    public static Pattern getPattern(String patternInput, Player player) {
        ParserContext context = getParserContext(player);

        return WorldEdit.getInstance()
                .getPatternFactory()
                .parseFromInput(patternInput, context);
    }

    /**
     * Provides autocomplete suggestions for WorldEdit patterns.
     *
     * @param input  The partial input for which suggestions are generated.
     * @param player The player context.
     * @return A list of suggested pattern strings.
     */
    public static List<String> getPatternSuggestions(String input, Player player) {
        PatternFactory patternFactory = WorldEdit.getInstance().getPatternFactory();
        ParserContext context = getParserContext(player);

        return patternFactory.getSuggestions(input, context);
    }

    /**
     * Retrieves the current WorldEdit selection for a given player.
     *
     * @param player The player whose selection is retrieved.
     * @return The Region representing the player's selection.
     */
    public static Region getSelection(Player player) {
        return getLocalSession(player)
                .getSelection();
    }

    /**
     * Retrieves the current WorldEdit selection for a given LocalSession.
     *
     * @param session The LocalSession from which to retrieve the selection.
     * @return The Region representing the selection.
     */
    public static Region getSelection(LocalSession session) {
        return session.getSelection();
    }

    /**
     * Sets blocks in a specified region using a pattern.
     *
     * @param blocks  A set of BlockVector3 positions where the blocks should be modified.
     * @param pattern The pattern to apply to the blocks.
     * @param player  The player performing the action.
     */
    public static void setBlocks(Set<BlockVector3> blocks, Pattern pattern, Player player) {
        LocalSession localSession = getLocalSession(player);
        EditSession editSession = getEditSession(player, localSession);

        setBlocks(blocks, pattern, localSession, editSession);
    }

    /**
     * Sets blocks in a specified region using a pattern with an existing EditSession.
     *
     * @param blocks       A set of BlockVector3 positions where the blocks should be modified.
     * @param pattern      The pattern to apply to the blocks.
     * @param localSession The LocalSession associated with the player.
     * @param editSession  The EditSession where the block changes are applied.
     */
    public static void setBlocks(Set<BlockVector3> blocks, Pattern pattern, LocalSession localSession, EditSession editSession) {
        try {
            editSession.setBlocks(blocks, pattern);
        } finally {
            // Save the operation in history for potential undo
            editSession.flushQueue();
            localSession.remember(editSession);
        }
    }
}
