package hawox.uquest.commands;


import hawox.uquest.Quester;
import hawox.uquest.UQuest;
import hawox.uquest.questclasses.LoadedQuest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd_uquest implements CommandExecutor{
	private final UQuest plugin;
	
	public Cmd_uquest(UQuest plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player){
			player = (Player)sender;
			}else{
				sender.sendMessage(UQuest.pluginNameBracket() + " This is not ment for console use silly!");
				return true;
			}
		
//		if(processQuest == true){
						
			try{
				//because we'll be using it alot, lets just nab the users saved info here so the code looks a bit neater
				Quester quester = plugin.getQuestInteraction().getQuester(player);
				
				if( (  (args[0].equalsIgnoreCase("?")) ||  (args[0].equalsIgnoreCase("help")) ) ){
					displayCommands(player);
					return true;
				}
				
				if(args[0].equalsIgnoreCase("top")){
					try{
						plugin.getQuestInteraction().listRankings(player, Integer.parseInt(args[1]));
					}catch(ArrayIndexOutOfBoundsException aiobe){
						player.sendMessage(ChatColor.RED + "You forgot to add a number! Ex: /uQuest top 5");
					}catch(NumberFormatException nfe){
						player.sendMessage(ChatColor.RED + "That's not a number! :" + ChatColor.WHITE + args[1]);
					}
					return true;
				}
					
				if( (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g") ) ){
					if( plugin.hasPermission(player, "uQuest.CanQuest.give") ) {
						//We want the first quest to be easy. Always give quest id 1 first!
						if(quester.getQuestsCompleted() < 1){
							plugin.getQuestInteraction().giveQuest(0, player);
						}else{
							plugin.getQuestInteraction().giveQuestRandom(player);
						}
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to give quests!");
						return true;					}
				}
				
				if( (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("s") ) ){
					if(plugin.hasPermission(player, "uQuest.admin")) {
						

						try{
							if(Integer.parseInt(args[1]) > (plugin.getQuestInteraction().getQuestTotal()-1) || Integer.parseInt(args[1]) < 0) {
								player.sendMessage(ChatColor.RED + "You can only choose 0 to " + (plugin.getQuestInteraction().getQuestTotal()-1));
							} else {
								if(quester.getQuestID() != -1){
									plugin.getQuestInteraction().questDrop(player);
									quester.setQuestsDropped(quester.getQuestsDropped() - 1);
								}
								plugin.getQuestInteraction().giveQuest(Integer.parseInt(args[1]), player);
							}
						} catch(ArrayIndexOutOfBoundsException aiobe) {
							player.sendMessage(ChatColor.RED + "You forgot to add a number! Ex: /uQuest set 1");
						} catch(NumberFormatException nfe) {
							player.sendMessage(ChatColor.RED + "That's not a number! :" + ChatColor.WHITE + args[1]);
						}
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission to set your quest!");
						return true;
					}
				}
							
								
				if( (args[0].equalsIgnoreCase("info")) ){
					if( plugin.hasPermission(player, "uQuest.CanQuest.info") ) {
						//make sure the player has a quest then simply read out the info of that quest eZ
						if(quester.getQuestID() == -1){
							player.sendMessage("You don't have an active quest!");
						} else{
							//player has a quest so...
							plugin.getQuestInteraction().getCurrentQuest(player,plugin.getQuestInteraction().isScaleQuestLevels()).printInfo(this.plugin, player);
						}
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to get quest info!");
						return true;					}
				}
							
				if( (args[0].equalsIgnoreCase("stats")) ){
					if( plugin.hasPermission(player, "uQuest.CanQuest.stats") ) {
						plugin.getQuestInteraction().showQuestersInfo(player);
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to get your stats!");
						return true;					}
				}
				
				if( (args[0].equalsIgnoreCase("amount")) ){
					if( plugin.hasPermission(player, "uQuest.CanQuest.amount") ) {
						//Tell the player the # of quests in the system
						player.sendMessage("There are currently " + ChatColor.GOLD + Integer.toString(plugin.getQuestInteraction().getQuestTotal()) + ChatColor.WHITE + " quests loaded!");
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to check the number of loaded quests!");
						return true;					}
				}
				
							
				if( (args[0].equalsIgnoreCase("done") || args[0].equalsIgnoreCase("d")) ){
					if( plugin.hasPermission(player, "uQuest.CanQuest.done") ) {
						if(quester.getQuestID() == -1){
							player.sendMessage(ChatColor.RED + "You don't have an active quest!");
						} else{
							if(plugin.getQuestInteraction().questTurnInAttempt(player) == true){
							
							}else{
								//quest is not done!
								player.sendMessage(ChatColor.RED + "Your quest isn't done! Type: /uQuest info");
							}
						}
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to complete quests this way!");
						return true;					}
				}
				
				if( (args[0].equalsIgnoreCase("update") || args[0].equalsIgnoreCase("u")) ){
					if( plugin.hasPermission(player, "uQuest.CanQuest.update") ) {
						if(quester.getQuestID() == -1){
							player.sendMessage(ChatColor.RED + "You don't have an active quest!");
						} else{
							LoadedQuest loadedQuest = plugin.getQuestersQuest(quester);
							if(loadedQuest.checkObjectiveType(plugin, "gather")){
								loadedQuest.gatherObectives(plugin, player, quester);
							}else{
								//quest is not done!
								player.sendMessage(ChatColor.RED + "Your don't have a gather quest! Type: /uQuest info");
							}
						}
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to complete quests this way!");
						return true;					}
				}

				if(args[0].equalsIgnoreCase("drop")) {
					if( plugin.hasPermission(player, "uQuest.CanQuest.CanDropQuest") ) {
						//do they even have a quest?
						if(quester.getQuestID() != -1){
							if(plugin.getQuestInteraction().isPlayerOnDropQuestList(player.getName()) == true){
								player.sendMessage(ChatColor.RED + "You've already dropped a quest! You need to wait before you can drop another one!");
							}else{
								//can drop quest check money
								boolean canDropQuest = true;
								int dropCharge = plugin.getQuestInteraction().getDropQuestCharge();
								if( plugin.getEconomy() != null ) {
//								if(plugin.isUseBOSEconomy() || plugin.isUseEssentials() || plugin.isUseiConomy()){
									//Enough money?
									if(plugin.getQuestInteraction().hasEnoughMoney(player, dropCharge)){
										plugin.getQuestInteraction().addMoney(player, -dropCharge);
										canDropQuest = true;
									}else{
										player.sendMessage(ChatColor.RED + "You don't have enough money to drop a quest! You need " + dropCharge + " " + plugin.getMoneyName() + "!");
										canDropQuest = false;
										return true;
									}
								}
								if(canDropQuest == true){
									//player can drop the quest!!!
									plugin.getQuestInteraction().questDrop(player);
									player.sendMessage(ChatColor.GREEN + "Quest dropped!");
									if(plugin.getQuestInteraction().getDropQuestInterval() > 0){
										plugin.getQuestInteraction().addPlayerToDropQuestList(player.getName());
										plugin.getQuestInteraction().removePlayerFromDropQuestListWithTimer(player.getName(), plugin.getQuestInteraction().getDropQuestInterval());
										player.sendMessage(ChatColor.GRAY + "You can not drop a quest for " + plugin.getQuestInteraction().getDropQuestInterval() + " minutes!");
									}
								}else{
									player.sendMessage(ChatColor.RED + "Quest dropping failed for an unknown reason!");
								}
							}
						}else{
							player.sendMessage(ChatColor.RED + "You don't have a quest to drop!");
							return true;
						}
					}else{
						player.sendMessage(ChatColor.RED + "You don't have permission to drop quests!");
						return true;
					}
				}
			}
			catch(ArrayIndexOutOfBoundsException aiobe){
				displayCommands(player);
				return true;
				}	
			//}
			//end of /questme prefix
//	    }else{
//	    	player.sendMessage(ChatColor.RED + "You don't have permission to use that!");
//	    }
		return true;
	}
	
	public void displayCommands(Player player){
		player.sendMessage(ChatColor.LIGHT_PURPLE + "uQuest is a simple random quest plugin. How to use it:");
		//Added this so if you can't use /q you can at least remove it from suggested commands. *Croyd*
		if(plugin.isUseDefaultHelp()) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "You can also use /quest or /q instead of /uquest");	
		} else {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "You can also use /quest instead of /uquest");
		}
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> Commands:");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest (?/help)" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Shows this help menu");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest stats" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Shows your stored info. Try it!");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest amount" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Shows the amount of loaded quests");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest give" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Gives you a random quest");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest done" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Attempts to turn in your current quest");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest update" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Updates/Removes gather quest items.");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest info" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Resends you your quest info/progress");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest drop" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Drops your current quest");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "   ->" + ChatColor.GREEN + "/uquest top #" + ChatColor.WHITE + "  |  " + ChatColor.BLUE + "Shows you the top 5 questers");
	}
	
}



/*if( (split[1].equalsIgnoreCase("top"))){
	try{
	int topAmount = Integer.parseInt(split[2]);
	ArrayList<Quester> topQuestersInOrder = null;
	ArrayList<Quester> tempQuesterList = plugin.theQuesterList;
	
	//just to advoid warnings and potentioal nulls there HAS to be a player here since one needs to be logged it
	//topQuestersInOrder.add(tempQuesterList.get(0));
	
	//this could be made better. Ask \\!
	for(int i=0; i< topAmount; i++){
		Quester currentBest = tempQuesterList.get(0);
		if(currentBest != null){
			for(int j=1; j<tempQuesterList.size(); j++){
				Quester current = tempQuesterList.get(j);
				if(current.getQuestsCompleted() > currentBest.getQuestsCompleted()){
					currentBest = current;
				}
				//if it's equal, see if they have more money earned!
				if(current.getQuestsCompleted() == currentBest.getQuestsCompleted()){
					if(current.getMoneyEarnedFromQuests() > currentBest.getMoneyEarnedFromQuests()){
						currentBest = current;
					}
				}
			}
			topQuestersInOrder.add(currentBest);
			tempQuesterList.remove(currentBest);
		}
	}
	//Got the list! (I think...)
	//display the ranks out to the user!
	player.sendMessage(ChatColor.GRAY + "I need to fix the ranking, may not display correctly.");
	player.sendMessage(ChatColor.DARK_BLUE + "******Best Online Questers******");
	for(int i=0; i<topQuestersInOrder.size(); i++){
		Quester currentQuester = topQuestersInOrder.get(i);
		if(topQuestersInOrder.get(i) != null){
			
			player.sendMessage(" *" + (i+1) + ".) " + ChatColor.DARK_GREEN + currentQuester.theQuester.getName() + ChatColor.GRAY + " | Quests:" + currentQuester.getQuestsCompleted() + " | Earnings:" + currentQuester.getMoneyEarnedFromQuests());
		} else {
			//name is empty so just show empty
			player.sendMessage(" *" + (i+1) + ".) Empty Slot");
		}
	}
	}
	catch(NumberFormatException nfe){ player.sendMessage(ChatColor.RED + "That's not a number!"); }
	catch(ArrayIndexOutOfBoundsException aiobe){ player.sendMessage(ChatColor.RED + "Please type a number after top!"); }
}*/