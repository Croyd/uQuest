package hawox.uquest.commands;


import hawox.uquest.UQuest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Cmd_reloadquests implements CommandExecutor{
	private final UQuest plugin;
	
	public Cmd_reloadquests(UQuest plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// permission is protected by Bukkit plugin.yml (superperms)
		
		int questsBefore = plugin.getQuestInteraction().getQuestTotal();
		plugin.theQuestsLoadAllIntoArray();
		int questsAfter = plugin.getQuestInteraction().getQuestTotal();

		sender.sendMessage(UQuest.pluginNameBracket() + " uQuest's quest list has been reloaded.");
		sender.sendMessage(UQuest.pluginNameBracket() + " Total before: " + Integer.toString(questsBefore) + " | Total After: " + Integer.toString(questsAfter) );

		System.out.println(UQuest.pluginNameBracket() + " " + sender.getName() + " reloaded uQuest's quests.");
		System.out.println(UQuest.pluginNameBracket() + " Total before: " + Integer.toString(questsBefore) + " | Total After: " + Integer.toString(questsAfter) );
		return true;
	}
}