package xyz.acrylicstyle.extraUtilities.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAngelRingEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final State state;
    private boolean cancelled = false;

    public PlayerAngelRingEvent(@NotNull Player player, @NotNull State state) {
        super(player);
        this.state = state;
    }

    @NotNull
    public State getState() {
        return state;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() { return handlers; }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public enum State {
        ENABLED,
        DISABLED,
    }
}
