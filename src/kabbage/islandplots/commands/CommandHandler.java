package kabbage.islandplots.commands;

import java.util.List;

import kabbage.islandplots.IslandPlots;
import kabbage.islandplots.PlayerWrapper;
import kabbage.islandplots.Plot;
import kabbage.islandplots.PlotHandler;
import kabbage.islandplots.utils.Permissions;
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
	
	public void abandonPlot()
	{
		Player player = senderWrapper.getPlayer();
		Plot plot = plugin.getPlotHandler().getPlot(player.getLocation());
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not currently in a plot to remove.");
			return;
		}
		if(!plot.getOwner().equals(player.getName()))
		{
			senderWrapper.sendMessage(ChatColor.RED+"You don't own this plot.");
			return;
		}
		if(plugin.getPlotHandler().needConfirmationUntiDeletion.contains(plot))
		{
			player.teleport(Bukkit.getWorld(plugin.getConfig().getString("spawn-world")).getSpawnLocation());
			PlayerWrapper.getWrapper(player).reset();
			senderWrapper.sendMessage(ChatColor.RED+"Plot abandoned.");
		} else
		{
			senderWrapper.sendMessage(ChatColor.RED+"Are you absolutely positive you wish to abandon this plot? The action is irreversible.");
			senderWrapper.sendMessage(ChatColor.RED+"Type the command a second time to confirm.");
			plugin.getPlotHandler().needConfirmationUntiDeletion.add(plot);
		}
	}
	
	public void addPlayer(String playerName)
	{
		Player player = senderWrapper.getPlayer();
		Plot plot = plugin.getPlotHandler().getPlot(player.getLocation());
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not currently in a plot to remove.");
			return;
		}
		if(!plot.getOwner().equals(player.getName()))
		{
			senderWrapper.sendMessage(ChatColor.RED+"You don't own this plot.");
			return;
		}
		plot.addMember(playerName);
	}
	
	public void createIslandWorld(String worldName)
	{
		if(!Permissions.isAdmin(senderWrapper.getSender()))
		{
			senderWrapper.sendMessage(ChatColor.RED+"You do not have permission to do this.");
			return;
		}
		Bukkit.dispatchCommand(senderWrapper.getSender(), "mv create "+worldName+" normal -g IslandPlots");
		plugin.setPlotHandler(new PlotHandler(worldName));
	}
	
	public void createPlot()
	{
		String playerName = senderWrapper.getPlayer().getName();
		if(!PlayerWrapper.getWrapper(playerName).canHavePlot())
		{
			senderWrapper.sendMessage(ChatColor.RED+"You may only have one plot.");
			return;
		}
		plugin.getPlotHandler().appendPlot(playerName);
	}
	
	public void fixPlayer(String player)
	{
		if(!Permissions.isAdmin(senderWrapper.getSender()))
		{
			senderWrapper.sendMessage(ChatColor.RED+"You do not have permission to do this.");
			return;
		}
		PlayerWrapper pw = PlayerWrapper.getWrapper(player);
		if(pw == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"Player not found.");
			return;
		}
		pw.reset();
		senderWrapper.sendMessage(ChatColor.GOLD+"Player 'fixed'. Probably...");
	}
	
	public void listHomes()
	{
		int page = (command.hasArgAtIndex(2)) ? Utils.parseInt(command.getArgAtIndex(2), 0)-1 : 0;
		PlayerWrapper playerW = PlayerWrapper.getWrapper(senderWrapper.getPlayer());
		senderWrapper.sendMessage(ChatColor.GOLD+"Owned Island Plots:");
		for(int i = page * 5; i < page * 5 + 5; i++)
		{
			Plot p = playerW.getPlot(i);
			if(p == null)
				break;
			//Args: Color, plot number, color, grid loc x, grid loc y
			senderWrapper.sendMessage(String.format("%sPlot %d: %s[%d, %d]", ChatColor.DARK_AQUA, i+1, ChatColor.DARK_GRAY, p.getGridX(), p.getGridY()));
		}
		senderWrapper.sendMessage(String.format("Page %d/%d", page + 1, playerW.getPlots() / 5 + 1));
	}
	
	public void makePermanent()
	{
		Plot plot = plugin.getPlotHandler().getPlot(senderWrapper.getPlayer().getLocation());
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not currently in a plot to make permanent.");
			return;
		}
		plugin.getPlotHandler().makePermanent(plot);
		senderWrapper.sendMessage(ChatColor.GREEN+"Plot successfully made permanent.");
	}
	
	public void removePlayer(String playerName)
	{
		Player player = senderWrapper.getPlayer();
		Plot plot = plugin.getPlotHandler().getPlot(player.getLocation());
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not currently in a plot to remove.");
			return;
		}
		if(!plot.getOwner().equals(player.getName()))
		{
			senderWrapper.sendMessage(ChatColor.RED+"You don't own this plot.");
			return;
		}
		plot.removeMember(playerName);
	}

	public void sendInfo()
	{
		Plot plot = plugin.getPlotHandler().getPlot(senderWrapper.getPlayer().getLocation());
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not currently in a plot to get information on.");
			return;
		}
		senderWrapper.sendMessage(ChatColor.DARK_AQUA+plot.toString());
		senderWrapper.sendMessage(ChatColor.GOLD+"Owner: "+plot.getOwner());
		List<String> members = plot.getMembers();
		if(members.size() > 0)
			senderWrapper.sendMessage(ChatColor.GOLD+"Members: "+members.toString().replaceAll("]|[", ""));
		senderWrapper.sendMessage(ChatColor.GOLD+"Level: "+plot.getLevel());
		senderWrapper.sendMessage(ChatColor.GOLD+"Wealth: "+plot.getWealth()+"/"+plot.getNextWealth());
	}
	
	public void teleportHome()
	{
		Player player = senderWrapper.getPlayer();
		int homeID = (command.hasArgAtIndex(2)) ? Utils.parseInt(command.getArgAtIndex(2), 1) : 1;
		PlayerWrapper homeOwner = PlayerWrapper.getWrapper(player.getName());
		Plot plot = homeOwner.getPlot(homeID - 1);
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"Specified plot could not be found.");
			return;
		}
		if(!plot.getOwner().equals(player.getName()))
		{
			homeOwner.removePlot(plot);
			senderWrapper.sendMessage(ChatColor.RED+"Error. Try again later.");
			return;
		}
		player.teleport(plot.getIsland().getSpawnPoint());
	}
}
