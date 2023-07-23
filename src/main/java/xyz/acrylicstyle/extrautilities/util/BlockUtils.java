package xyz.acrylicstyle.extrautilities.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockUtils {
    @NotNull
    public static List<Block> getNearbyBlocks(@NotNull Location location, int radius, List<Material> materials) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = Objects.requireNonNull(location.getWorld()).getBlockAt(x, y, z);
                    if (materials.contains(block.getType())) blocks.add(block);
                }
            }
        }
        return blocks;
    }
}
