package xyz.acrylicstyle.extrautilities.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.extrautilities.item.EUItem;

public abstract class EUBlock extends EUItem {
    @NotNull
    public abstract Material getType();

    public abstract boolean isRightBlock(@NotNull Block block);
}
