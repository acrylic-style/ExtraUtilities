package xyz.acrylicstyle.extraUtilities.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import util.CollectionList;

import java.util.List;

public class BlockUtils {
    @NotNull
    public static CollectionList<Block> getNearbyBlocks(@NotNull Location location, int radius, List<Material> materials) {
        CollectionList<Block> blocks = new CollectionList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (materials.contains(block.getType())) blocks.add(block);
                }
            }
        }
        return blocks;
    }
}
