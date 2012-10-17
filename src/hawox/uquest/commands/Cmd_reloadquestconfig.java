package hawox.uquest.commands;


import hawox.uquest.UQuest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Cmd_reloadquestconfig implements CommandExecutor{
	private final UQuest plugin;
	
	public Cmd_reloadquestconfig(UQuest plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// permission is protected by Bukkit plugin.yml (superperms)
		
		plugin.readConfig();

		sender.sendMessage(UQuest.pluginNameBracket() + " I hope you didn't change anything under 'Database' or 'PluginSupport'!!!");
		sender.sendMessage(UQuest.pluginNameBracket() + " These will not reconfigure mid runtime and may cause UNDESIRED RESULTS!!!");

		sender.sendMessage(UQuest.pluginNameBracket() + " uQuest's config has been reloaded.");

		System.out.println(UQuest.pluginNameBracket() + " " + sender.getName() + " reloaded uQuest's config.");
		return true;
	}
}