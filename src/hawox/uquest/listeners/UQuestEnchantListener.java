package hawox.uquest.listeners;

import java.util.Map;

import hawox.uquest.Quester;
import hawox.uquest.UQuest;
import hawox.uquest.UQuestUtils;
import hawox.uquest.questclasses.LoadedQuest;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
@SuppressWarnings("deprecation")
public class UQuestEnchantListener implements Listener {
    private final UQuest plugin;
    public UQuestEnchantListener(UQuest instance) {
        plugin = instance;
    }
    /* The is for enchant objectives.
     * Type: Enchant
     * Display_Name:
     * Objective_ID:
     * Level:
     * Amount:
     * Item_ID:
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItemEvent(EnchantItemEvent event) {
    	if(plugin.isEnabled() == true){
        	Player player = event.getEnchanter();
        	Quester quester = plugin.getQuestInteraction().getQuester(player);;
        	if(quester.getQuestID() != -1){
    			int questLevel = 1;
    			//Check if quest level scalling is turned on and if so add them.
    			if(plugin.isScaleQuestLevels()){
        			questLevel = plugin.getQuestInteraction().getQuestLevel(player)+1;
        		}
    			LoadedQuest loadedQuest = plugin.getQuestersQuest(quester);
    			Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
            	for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
               	    String enchantName = UQuestUtils.getEnchantName(e.getKey().getName());
            	    int itemID = event.getItem().getTypeId();
            	    int itemDamage = event.getItem().getData().getData();
            	    if(loadedQuest.checkObjective(plugin, player.getLocation(), "enchant", enchantName)){
        				int objItem = loadedQuest.getObjectiveFromTypes("enchant", enchantName).getItemID();
        				int objlevel = loadedQuest.getObjectiveFromTypes("enchant", enchantName).getLevel();
        				if(objItem == itemID && itemDamage <= 0 && e.getValue() >= objlevel ) {
        					int amountNeeded = loadedQuest.getObjectiveFromTypes("enchant", enchantName).getAmountNeeded()*questLevel;
        					String iName = UQuestUtils.formatName(event.getItem().getData().getItemType().toString());
        					int amount = quester.getTracker(plugin, enchantName);
        					if(amount < amountNeeded) {
        						event.getInventory().remove(event.getItem());
        						String[] levels = {"I","II","III","IV","V"};
        						quester.addToTracker(plugin, enchantName, 1);
        						amount = quester.getTracker(plugin, enchantName);
        						if(amount == amountNeeded) {
                					player.sendMessage(ChatColor.AQUA + iName + ChatColor.GREEN + " enchanted with " +
     									   ChatColor.DARK_PURPLE + enchantName + " " + ChatColor.GOLD + levels[e.getValue()-1] +
     									  ChatColor.GREEN + " " + amount + ChatColor.WHITE + "/" + ChatColor.GREEN + amountNeeded);
                					
        						} else {
                					player.sendMessage(ChatColor.AQUA + iName + ChatColor.GREEN + " enchanted with " +
     									   ChatColor.DARK_PURPLE + enchantName + " " + ChatColor.GOLD + levels[e.getValue()-1] +
     									  ChatColor.AQUA + " " + amount + ChatColor.WHITE + "/" + ChatColor.GREEN + amountNeeded);
        						}
        					}
        				}
        			}
            	}
        	}
        }
    }
}
