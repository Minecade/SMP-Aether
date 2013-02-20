package kabbage.islandplots.commands;

import kabbage.islandplots.IslandPlots;
import kabbage.islandplots.PlayerWrapper;
import kabbage.islandplots.Plot;
import kabbage.islandplots.PlotHandler;
import kabbage.islandplots.utils.Utils;

import org.bukkit.Bukkit;
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
	
	public void createIslandWorld(String worldName)
	{
		if(!senderWrapper.isAdmin())
			senderWrapper.sendMessage(ChatColor.RED+"You do not have permission to do this.");
		Bukkit.dispatchCommand(senderWrapper.getSender(), "mv create "+worldName+" normal -g NullTerrain");
		plugin.setPlotHandler(new PlotHandler(worldName));
	}
	
	public void createPlot()
	{
		String playerName = senderWrapper.getPlayer().getName();
		if(!PlayerWrapper.getWrapper(playerName).canHavePlot())
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not allowed to have another plot.");
			return;
		}
		Plot plot = plugin.getPlotHandler().appendPlot(playerName);
		senderWrapper.getPlayer().teleport(plot.getIsland().getSpawnPoint());
	}

	public void teleportHome()
	{
		int homeID = (command.hasArgAtIndex(2)) ? Utils.parseInt(command.getArgAtIndex(2), 1) : 1;
		Player player = senderWrapper.getPlayer();
		PlayerWrapper playerW = PlayerWrapper.getWrapper(senderWrapper.getPlayer());
		Plot plot = playerW.getPlot(homeID - 1);
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"Specified plot could not be found.");
			return;
		}
		player.teleport(plot.getIsland().getSpawnPoint());
	}
	
	public void listHomes()
	{
		int page = (command.hasArgAtIndex(2)) ? Utils.parseInt(command.getArgAtIndex(2), 1) : 1;
		PlayerWrapper playerW = PlayerWrapper.getWrapper(senderWrapper.getPlayer());
		senderWrapper.sendMessage(ChatColor.GOLD+"Owned Island Plots:");
		for(int i = page * 10; i < page * 10 + 10; i++)
		{
			Plot p = playerW.getPlot(i);
			if(p == null)
				break;
			//Args: Color, plot number, color, grid loc x, grid loc y
			senderWrapper.sendMessage(String.format("%sPlot %d: %s[%d, %d]", ChatColor.DARK_AQUA, i+1, ChatColor.DARK_GRAY, p.getGridX(), p.getGridY()));
		}
		senderWrapper.sendMessage(String.format("Page %d/%d", page, playerW.getPlots() / 10));
	}
}
