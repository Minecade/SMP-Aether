package kabbage.islandplots.commands;

import kabbage.islandplots.IslandPlots;
import kabbage.islandplots.PlayerWrapper;
import kabbage.islandplots.Plot;
import kabbage.islandplots.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler
{
	private IslandPlots plugin;
	private CommandSenderWrapper senderWrapper;
	private ECommand command;
	
	public CommandHandler(CommandSender sender, ECommand command)
	{
		plugin = IslandPlots.instance;
		
		senderWrapper = new CommandSenderWrapper(sender);
		this.command = command;
	}

	public void teleportHome()
	{
		int homeID = (command.hasArgAtIndex(2)) ? Utils.parseInt(command.getArgAtIndex(2), 1) : 1;
		Player player = senderWrapper.getPlayer();
		PlayerWrapper playerW = new PlayerWrapper(senderWrapper.getPlayer());
		Plot plot = playerW.getPlot(homeID);
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"Specified plot could not be found.");
			return;
		}
		player.teleport(plot.getIsland().getSpawnPoint());
	}
}
