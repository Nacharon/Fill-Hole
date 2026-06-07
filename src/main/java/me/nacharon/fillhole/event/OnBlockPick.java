package me.nacharon.fillhole.event;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import me.nacharon.fillhole.api.Config;
import me.nacharon.fillhole.api.fawe.FaweHook;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.event.Listener;

public class OnBlockPick implements Listener {

    /**
     * If the player has the worldedit wand and pick a block,
     * the cuboid selection are extended with the targeted block
     *
     * @param pickBlockEvent the pick block event
     */
    @EventHandler
    public void pickBlockHandler(PlayerPickBlockEvent pickBlockEvent) {
        Player player = pickBlockEvent.getPlayer();

        if (!player.hasPermission("fillhole.selection.extend")) return;
        if (player.getScoreboardTags().contains(Config.getDisableExpendTag())) return;
        if (player.getGameMode() != GameMode.CREATIVE) return;
        if (player.isSneaking()) return;

        LocalSession session = FaweHook.getLocalSession(player);
        if (session == null) return;
        if (!FaweHook.isHoldingSelectionWand(player)) return;

        Block targetBlock = pickBlockEvent.getBlock();
        BlockVector3 blockPosition = BlockVector3.at(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());

        RegionSelector selection = FaweHook.getRegionSelection(session, player);
        if (!selection.isDefined()) return;

        if (selection instanceof CuboidRegionSelector cubeSelection) {
            FaweHook.extendSelection(cubeSelection, blockPosition);
            session.dispatchCUISelection(FaweHook.getBukkitPlayer(player));

            pickBlockEvent.setCancelled(true);

            player.sendMessage("Extended selection to encompass (" +
                    targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ() + ")");
        }
    }
}
