package hawox.uquest;

import hawox.uquest.interfaceevents.QuestDropEvent;
import hawox.uquest.interfaceevents.QuestFinishEvent;
import hawox.uquest.interfaceevents.QuestGetEvent;
import hawox.uquest.interfaceevents.TrackerAddEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestListener implements Listener
{
	@EventHandler
	public void onTrackerAdd(TrackerAddEvent event){
	}
	
	@EventHandler
	public void onQuestGet(QuestGetEvent event){
	}
	
	@EventHandler
	public void onQuestFinish(QuestFinishEvent event){	
	}
	
	@EventHandler
	public void onQuestDrop(QuestDropEvent event){	
	}
	
	//Get the correct methods to run
/* No longer necessary with R5-style events.  -morganm 3/3/11
	public void onCustomEvent(Event event){
		if(event instanceof TrackerAddEvent)
			onTrackerAdd((TrackerAddEvent)event);
		else if(event instanceof QuestGetEvent)
			onQuestGet((QuestGetEvent)event);
		else if(event instanceof QuestFinishEvent)
			onQuestFinish((QuestFinishEvent)event);
		else if(event instanceof QuestDropEvent)
			onQuestDrop((QuestDropEvent)event);
	}
	*/
}
