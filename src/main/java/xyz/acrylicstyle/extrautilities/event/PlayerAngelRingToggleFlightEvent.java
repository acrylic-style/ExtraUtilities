package xyz.acrylicstyle.extrautilities.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAngelRingToggleFlightEvent extends PlayerToggleFlightEvent {
    private final PlayerAngelRingEvent.State state;

    public PlayerAngelRingToggleFlightEvent(@NotNull Player player, boolean isFlying, @NotNull PlayerAngelRingEvent.State state) {
        super(player, isFlying);
        this.state = state;
    }

    @NotNull
    public PlayerAngelRingEvent.State getState() {
        return state;
    }
}
