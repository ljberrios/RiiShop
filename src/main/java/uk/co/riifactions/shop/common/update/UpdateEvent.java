package uk.co.riifactions.shop.common.update;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Update event, mainly used to avoid the creation of tasks.
 * It will run the code inside of the event with the given delay.
 * <p>
 * Example of usage:
 * <pre>
 *   public void onUpdate(UpdateEvent event) {
 *     if (event.getView() == UpdateType.TICK) {
 *       // some cool stuff in here
 *     }
 *   }
 * </pre>
 *
 * @author Thortex
 */
@Getter
public class UpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UpdateType type;

    public UpdateEvent(UpdateType type) {
        this.type = type;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
