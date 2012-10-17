package hawox.uquest.interfaceevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class UQuestEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

	protected UQuestEvent() {
	}
	
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
