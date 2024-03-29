package com.github.islandplots.commands;

import java.util.List;

import com.github.islandplots.IslandPlots;
import com.github.islandplots.PlayerWrapper;
import com.github.islandplots.Plot;
import com.github.islandplots.PlotHandler;
import com.github.islandplots.utils.Permissions;
import com.github.islandplots.utils.Utils;

import net.milkbowl.vault.economy.Economy;
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
		PlayerWrapper pw = PlayerWrapper.getWrapper(player);
		Plot plot = plugin.getPlotHandler().getPlot(player.getLocation());
		if(plot == null)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You are not currently in a plot to remove.");
			return;
		}
		if(!plot.getOwner().equals(player.getName()))
		{
			if(pw.isOwnedPlot(plot))
			{
				pw.removePlot(plot);
				senderWrapper.sendMessage(ChatColor.RED+"You don't own this plot, but it seems as though you have one of your homes set as it.");
				senderWrapper.sendMessage(ChatColor.RED+"This is now fixed.");
			} else
			{
				senderWrapper.sendMessage(ChatColor.RED+"You don't own this plot.");
			}
			return;
		}
		if(!pw.canAbandonPlot())
		{
			senderWrapper.sendMessage(ChatColor.RED+"You may only abandon one plot every day, both to prevent abuse and to reduce the amount of time" +
					" island generation takes.");
			return;
		}
		if(plugin.getPlotHandler().needConfirmationUntiDeletion.contains(plot))
		{
			plugin.getPlotHandler().removePlot(plot);
			senderWrapper.sendMessage(ChatColor.RED+"Plot abandoned, cost refunded.");
			plugin.getEconomy().depositPlayer(player.getName(), PlotHandler.PLOT_PRICE);

			if(pw.getPlot(0) != null)
				player.teleport(pw.getPlot(0).getSpawnPoint());
			else
				player.teleport(Bukkit.getWorld(plugin.getConfig().getString("spawn-world")).getSpawnLocation());
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
		PlayerWrapper pw = PlayerWrapper.getWrapper(playerName);
		if(!pw.canHavePlot())
		{
			if(pw.getPlots() >= Permissions.maxPlots(senderWrapper.getSender()))
				senderWrapper.sendMessage(ChatColor.RED+"You may only have up to "+Permissions.maxPlots(senderWrapper.getSender())+" plots at a time.");
			else
				senderWrapper.sendMessage(ChatColor.RED+"You require a total level across all owned plots of "+pw.getRequiredLevel()+" to get a new plot.");
			return;
		}
		Economy economy = plugin.getEconomy();
		if(economy.getBalance(playerName) < PlotHandler.PLOT_PRICE)
		{
			senderWrapper.sendMessage(ChatColor.RED+"You don't have enough money to do this! A new island costs: "+PlotHandler.PLOT_PRICE);
			return;
		}
		if(plugin.getGenerationQueue().isFull())
		{
			senderWrapper.sendMessage(ChatColor.RED+"Too many plots are already queued to be generated. Please try again later.");
			return;
		}
		if(!plugin.getPlotHandler().needConfirmationUntiPurchase.contains(playerName))
		{
			senderWrapper.sendMessage(ChatColor.RED+"Buying a new island will cost "+PlotHandler.PLOT_PRICE+". Type the command again to confirm your purchase.");
			plugin.getPlotHandler().needConfirmationUntiPurchase.add(playerName);
			return;
		}
		if(!plugin.getGenerationQueue().isEmpty())
			senderWrapper.sendMessage(ChatColor.GOLD+"Your plot is now queued for creation. You will be informed when your plot is next in queue.");
		plugin.getPlotHandler().appendPlot(playerName);
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
		senderWrapper.sendMessage(String.format("Page %d/%d", page + 1, (playerW.getPlots()-1) / 5 + 1));
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
			senderWrapper.sendMessage(ChatColor.GOLD+"Members: "+members.toString().replaceAll("[\\]|\\[]", ""));
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
