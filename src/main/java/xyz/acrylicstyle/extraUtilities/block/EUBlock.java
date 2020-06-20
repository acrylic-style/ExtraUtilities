package xyz.acrylicstyle.extraUtilities.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.extraUtilities.item.EUItem;
import xyz.acrylicstyle.paper.block.TileEntity;

public abstract class EUBlock extends EUItem {
    @NotNull
    public abstract Material getType();

    public abstract boolean isRightBlock(@NotNull Block block);

    @Nullable
    protected final TileEntity getTileEntity(@NotNull Block block) { return getTileEntity(block.getLocation()); }

    @Nullable
    protected final TileEntity getTileEntity(@NotNull Location location) { return location.getWorld().getTileEntity(location); }
}
