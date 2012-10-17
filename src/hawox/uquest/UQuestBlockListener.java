package hawox.uquest;

import hawox.uquest.questclasses.LoadedQuest;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/*
 * Handle events for all Block related events
 */
public class UQuestBlockListener implements Listener {
    private final UQuest plugin;

    public UQuestBlockListener(final UQuest plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event){
    	if(plugin.isEnabled() == true){
        	Block block = event.getBlock();
        	Player player = event.getPlayer();
    		if(!event.isCancelled())
    			blockCheckQuest(player, block, "blockdestroy", 1);
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamage(BlockDamageEvent event) {
    	if(plugin.isEnabled() == true){
        	Block block = event.getBlock();
        	Player player = event.getPlayer();
			if(!event.isCancelled())
				blockCheckQuest(player, block, "blockdamage", 1);
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
     	if(plugin.isEnabled() == true){
        	Block block = event.getBlock();
        	Player player = event.getPlayer();
        	if(!event.isCancelled())
        		blockCheckQuest(player, block, "blockplace", 1);
     	}
     }
    
    
    public void blockCheckQuest(Player player, Block block, String type, int incressBy){
    	if(plugin.isEnabled() == true){
    		//get our quester
    		Quester quester = plugin.getQuestInteraction().getQuester(player);
     		//get the players current quest as well if they have one
    		if(quester.getQuestID() != -1){
    			int questLevel = 1;
    			if(plugin.isScaleQuestLevels()){
    				questLevel = plugin.getQuestInteraction().getQuestLevel(player)+1;
    			}
    			//check if the block they (did 'type' to) is the one they need
    			LoadedQuest loadedQuest = plugin.getQuestersQuest(quester);
    			String objectiveName = Integer.toString(block.getTypeId());
    			if(loadedQuest.checkObjective(plugin, player.getLocation(), type, objectiveName)){
    				//Updated to only check off what is needed, to add quest level, and to message player on add. *Croyd*
    				int amountNeeded = loadedQuest.getObjectiveFromTypes(type, objectiveName).getAmountNeeded()*questLevel;
    				int itemDurability = loadedQuest.getObjectiveFromTypes(type, objectiveName).getItemDurability();
    				//For Torches, they have a different durability when placed.
    				int blockDurability = block.getData();
    				//Some blocks have a different durability when placed so we'll check for them. *Croyd*
    				if(block.getTypeId() == 50 || block.getTypeId() == 76) {
    					blockDurability = 0;
    				}
    				if(quester.getTracker(plugin, objectiveName) < amountNeeded && itemDurability == blockDurability) {
    					//Awesome! Increase their broken,placed, or damaged blocks!
    					quester.addToTracker(plugin, objectiveName, 1);
    					player.sendMessage(plugin.formatUpdateMessage(type, block.getType().name(), quester.getTracker(plugin, objectiveName), amountNeeded));
    				}
    			}
    		}
    		if(plugin.isUseSQLite() == true){
    			plugin.getDB().put(player.getName(), quester);
    		}
    	}
    }
}