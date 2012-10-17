package hawox.uquest;

import hawox.uquest.questclasses.LoadedQuest;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
/*
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
*/
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/*
 * Handle events for all Entity related events
 */
public class UQuestEntityListener implements Listener {
    private final UQuest plugin;

    public UQuestEntityListener(UQuest instance) {
        plugin = instance;
    }  
    
    
	/*
	 * Trying a new way of tracking monster deaths. When a player damages a mob we tag that entity id as theirs.
	 * Now when that entity dies, whatever player is tagged as hitting it last will get the kill. Also, if this
	 * works the way I hope, I don't have to purge a monster killed id list.
	 */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event){
    	//check if the entity is tagged
    	Entity dead = event.getEntity();
    	int id = dead.getEntityId();
    	if(plugin.getMobsTagged().containsKey(id)){
    		Player killer = plugin.getServer().getPlayer(plugin.getMobsTagged().get(id));
    		
    		if(dead instanceof Creature)
    		    playerKilledCreature(killer, (Creature) dead);
    		
    		// Checkes if it is a player or if not a player a livingentity(slime, magmacube, and enderdragon) *Croyd*
    		if(dead instanceof Player) {
				playerKilledPlayer(killer, (Player) dead);
    		} else if (dead instanceof LivingEntity){
    			playerKilledLivingEntity(killer, (LivingEntity) dead);
    		}
    		
			//death counted, remove from tagged list
		    plugin.getMobsTagged().remove(id);
    	}
    		
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) throws NullPointerException{
    	if(event.isCancelled())
    		return;
    	Entity damager = null;
    	Creature creature = null;
    	Player damagedPlayer = null;
    	Player player = null;
    	LivingEntity lEntity = null;
    	Arrow arrow = null;
    	if (event instanceof EntityDamageByEntityEvent)
          damager = ((EntityDamageByEntityEvent)event).getDamager();
    	//Put this in to deal with arrow damage. Kept throwing NullPointer when skeletons attacked so made it a try catch.
    	try {
        	if(damager instanceof Arrow)
    			arrow = (Arrow) damager;
    		
    		if( arrow.getShooter() instanceof Player) 
    			player = (Player) arrow.getShooter();    		
    	}catch(NullPointerException npe) {
    		if(damager instanceof Player)
        		player = (Player) damager;    		
    	}
		// Added for Slimes, Magmacubes, and the EnderDragon *Croyd*
		if(event.getEntity() instanceof LivingEntity)
			lEntity = (LivingEntity)event.getEntity();

    	if(event.getEntity() instanceof Creature)
    		creature = (Creature)event.getEntity();
    	
    	if(event.getEntity() instanceof Player)
    		damagedPlayer = (Player)event.getEntity();
    	
    	if( (player != null) && ( (creature != null) || (damagedPlayer != null) || (lEntity != null) )     ){
    		//We have a player and a creature/damaged-player
    		int id = -1594; //random number
    		try{
    			id = creature.getEntityId();
    		}
    		catch(NullPointerException npe){
     			//it's a player they hit
    			// It's a player or livingentity, lets check for that *Croyd*
    			if(damagedPlayer != null) {
    				id = damagedPlayer.getEntityId();	
    			} else {
    				id = lEntity.getEntityId();
    			}
     		}
    		if(id != -1594){//Have a valid id
    			String pName = player.getName();
    			plugin.getMobsTagged().put(id,pName);
    		}
    	}
    }
    // Slimes, Magmacubes, and EnderDragons falls under Living Entity not Creature or Player so.. *Croyd*
    public void playerKilledLivingEntity(Player player, LivingEntity lEntity){
    	if(plugin.isEnabled() == true){
    		//Get out Quester
    		Quester quester;
    		try{
    			quester = plugin.getQuestInteraction().getQuester(player);
    		}catch(NullPointerException npe){
    			return;//player logged out since they hurt a monster so exit out
    		}
    		//Get the player's current quest if they have one.
    		if(quester.getQuestID() != -1){  
        		//Set the quest level to default.
    			int questLevel = 1;
    			//Check if quest level scalling is turned on and if so add them.
    			if(plugin.isScaleQuestLevels()){
        			questLevel = plugin.getQuestInteraction().getQuestLevel(player)+1;
        		} 
    			//Get the quest.
    			LoadedQuest loadedQuest = plugin.getQuestersQuest(quester);
    			//Get the entity.
    			EntityType eType = lEntity.getType();
    			//Get the name and make sure it is lower case.
    			String creatureType = eType.getName().toLowerCase();
    			//The living entities this function will accept.
    			String[] allowedMobs = { "slime","enderdragon", "magmacube" };
    			//If the quest calls for just killing anything then use this.
    			if( loadedQuest.checkObjective(plugin, player.getLocation(), "kill", "kill_any")){
    				quester.addToTracker(plugin, "kill_any", 1);
    			}
    			//Runs through allowed mobs for comparison.
    			for(int cCount = 0; cCount < allowedMobs.length; cCount++) {
    				//Do this if the create type matches.
    				if(creatureType.equals(allowedMobs[cCount])) {
    					//If the quest objective requires this entity then do the following.
    	    			if(loadedQuest.checkObjective(plugin, player.getLocation(),"kill", creatureType)) {
    	    				//Takes the default amount needed to complete the quest and multiplies the quest level.
    	    				int amountNeeded = loadedQuest.getObjectiveFromTypes("kill", creatureType).getAmountNeeded()*questLevel;
    	    				//Makes sure we don't go over how many we need in the count.
    	    				if(quester.getTracker(plugin, creatureType) < amountNeeded) {
    	    					//Updates the quest with what is needed.
    	    					quester.addToTracker(plugin, creatureType, 1);
    	    					//Informs the player that they have killed an entity they needed and how many more they need.
    	    					player.sendMessage(plugin.formatUpdateMessage("kill", eType.getName(), quester.getTracker(plugin, creatureType), amountNeeded));
    	    				}
    	    			}    					
    				}
    			}
    			if(plugin.isUseSQLite() == true){
        			plugin.getDB().put(player.getName(), quester);
        		}
    		}
        }
    }    
    
    //skeleton, pig, sheep, cow, chicken, squid, spider, zombie, creeper, ghast, giant, zombie pigman 
    public void playerKilledCreature(Player player, Creature creature){
    	if(plugin.isEnabled() == true){
        	//get our quester
    		Quester quester;
    		try{
    			quester = plugin.getQuestInteraction().getQuester(player);
    		}catch(NullPointerException npe){
    			return;//player logged out since they hurt a monster so exit out
    		}
    		//get the players current quest as well if they have one
    		if(quester.getQuestID() != -1){  
        		int questLevel = 1;
    			if(plugin.isScaleQuestLevels()){
        			questLevel = plugin.getQuestInteraction().getQuestLevel(player)+1;
        		} 
    			LoadedQuest loadedQuest = plugin.getQuestersQuest(quester);
//    			if(loadedQuest.checkType("kill")){
    			//check if the monster they killed in the one they needed
/*
I removed all the if statements in hope that a for and array check would be better and neater.
Updated to only check off what is needed, to add quest level, and to message player on add. *Croyd* 
*/
    			EntityType eType = creature.getType();
    			String creatureType = eType.getName().toLowerCase();
    			String[] allowedMobs = { "skeleton","pig","sheep","cow","chicken","squid","spider","zombie","creeper",
    					"ghast","giant","pigzombie","wolf","enderman","cavespider","villager","blaze","irongolem",
    					"mushroomcow","ocelot","silverfish","snowman" };
    			if( loadedQuest.checkObjective(plugin, player.getLocation(), "kill", "kill_any")){
    				quester.addToTracker(plugin, "kill_any", 1);
    			}
    			for(int cCount = 0; cCount < allowedMobs.length; cCount++) { //This checks for allowed creatures and if the player has the quest for it.
    				if(creatureType.equals(allowedMobs[cCount])) {
    	    			if(loadedQuest.checkObjective(plugin, player.getLocation(),"kill", creatureType)) {
    	    				int amountNeeded = loadedQuest.getObjectiveFromTypes("kill", creatureType).getAmountNeeded()*questLevel;
    	    				if(quester.getTracker(plugin, creatureType) < amountNeeded) {    	    				
    	    					quester.addToTracker(plugin, creatureType, 1);
    	    					player.sendMessage(plugin.formatUpdateMessage("kill", eType.getName(), quester.getTracker(plugin, creatureType), amountNeeded));
    	    				}
    	    			}    					
    				}
    			}
/*    				if( (creature instanceof Skeleton) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","skeleton"))){
    					quester.addToTracker(plugin, "skeleton", 1);
    				}
    				if( (creature instanceof Pig) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","pig"))){
    					quester.addToTracker(plugin, "pig", 1);
    					//system.out.println("Pig killed!");
    				}
    				if( (creature instanceof Sheep) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","sheep"))){
    					quester.addToTracker(plugin, "sheep", 1);
    				}
    				if( (creature instanceof Cow) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","cow"))){
    					quester.addToTracker(plugin, "cow", 1);
    				}
    				if( (creature instanceof Chicken) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","chicken"))){
    					quester.addToTracker(plugin, "chicken", 1);
    				}
    				if( (creature instanceof Squid) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","squid"))){
    					quester.addToTracker(plugin, "squid", 1);
    				}
    				if( (creature instanceof Spider) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","spider"))){
    					quester.addToTracker(plugin, "spider", 1);
    				}
    				if( (creature instanceof Zombie) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","zombie"))){
    					quester.addToTracker(plugin, "zombie", 1);
    				}
    				if( (creature instanceof Creeper) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","creeper"))){
    					quester.addToTracker(plugin, "creeper", 1);
    				}
    				if( (creature instanceof Slime) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","slime"))){
    					quester.addToTracker(plugin, "slime", 1);
    				}
    				if( (creature instanceof Ghast) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","ghast"))){
    					quester.addToTracker(plugin, "ghast", 1);
    				}
    				if( (creature instanceof Giant) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","giant"))){
    					quester.addToTracker(plugin, "giant", 1);
    				}
    				if( (creature instanceof PigZombie) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","pigzombie"))){
    					quester.addToTracker(plugin, "pigzombie", 1);
    				}
    				if( (creature instanceof Wolf) && (loadedQuest.checkObjective(plugin, player.getLocation(),"kill","wolf"))){
    					quester.addToTracker(plugin, "wolf", 1);
    				}
    			*/	
//    			}
    			if(plugin.isUseSQLite() == true){
        			plugin.getDB().put(player.getName(), quester);
        		}
    		}
        }
    }
    
    public void playerKilledPlayer(Player player, Player damagedPlayer){
    	if(plugin.isEnabled() == true){
    		Quester quester;
    		try{
    			quester = plugin.getQuestInteraction().getQuester(player);
    		}catch(NullPointerException npe){
    			return;//player logged out since they hurt a monster so exit out
    		}
    		//get the players current quest as well if they have one
    		if(quester.getQuestID() != -1){  
        		int questLevel = 1;
    			if(plugin.isScaleQuestLevels()){
        			questLevel = plugin.getQuestInteraction().getQuestLevel(player)+1;
        		} 
    			LoadedQuest loadedQuest = plugin.getQuestersQuest(quester);
//    			if(loadedQuest.checkType("kill")){
    				//check if the monster they killed in the one they needed
    			// Updated to only show what is needed, to add quest level, and to message player on add. *Croyd*
				if(loadedQuest.checkObjective(plugin, player.getLocation(),"kill","player")){
					int amountNeeded = loadedQuest.getObjectiveFromTypes("kill", "player").getAmountNeeded()*questLevel;
					if( quester.getTracker(plugin, "player") < amountNeeded){
						quester.addToTracker(plugin, "player", 1);
						player.sendMessage(plugin.formatUpdateMessage("kill", damagedPlayer.getName(), quester.getTracker(plugin, "player"), amountNeeded));
					}
				}else if(loadedQuest.checkObjective(plugin, player.getLocation(),"kill",damagedPlayer.getName())) {
					int amountNeeded = loadedQuest.getObjectiveFromTypes("kill", damagedPlayer.getName()).getAmountNeeded()*questLevel;
					if(quester.getTracker(plugin, damagedPlayer.getName()) <  amountNeeded) {
						quester.addToTracker(plugin, damagedPlayer.getName(), 1);
						player.sendMessage(plugin.formatUpdateMessage("kill", damagedPlayer.getName(), quester.getTracker(plugin, damagedPlayer.getName()), amountNeeded));	    					
					}
    			}
//    			}
    			if(plugin.isUseSQLite() == true){
        			plugin.getDB().put(player.getName(), quester);
        		}
    		}
        }
    }
    
    
    
    
    //Add's ID to the list and then removes it after a set time
/*    public void addToMobList(String id){
		if(!(plugin.mobsKilled.contains(id))){
			plugin.mobsKilled.add(id);
			plugin.getMobList_Timer().schedule(new Runnable() {
				public void run() {
					if(!(plugin.getMobsKilled().isEmpty())){
						//remove it from the list
						plugin.getMobsKilled().remove(0);
					}
				}
			}, 1, TimeUnit.MINUTES);
		}
    	
    }*/
}