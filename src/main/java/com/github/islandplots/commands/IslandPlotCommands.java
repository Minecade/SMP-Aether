package com.github.islandplots.commands;

import com.github.islandplots.IslandPlots;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IslandPlotCommands implements CommandExecutor
{
	private IslandPlots plugin;

	public IslandPlotCommands()
	{
		plugin = IslandPlots.instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		ECommand command = new ECommand(commandLabel, args);
		CommandHandler handler = new CommandHandler(sender, command);

		String helpMessage = "";
		boolean softFailure = false; //If true, the string helpMessage is sent to sender. return true.
		boolean hardFailure = false; //If true, return false. (sender gets sent the usage)
		try
		{
			if(plugin.getPlotHandler() == null && !command.get(1).equalsIgnoreCase("setworld"))
			{
				sender.sendMessage(ChatColor.RED+"Island world has not yet been created. Island related commands disabled.");
				return true;
			}
			String commandBase = command.get(1);
			if(commandBase.equalsIgnoreCase("home"))
			{
				handler.teleportHome();
			} else if(commandBase.equalsIgnoreCase("new") || commandBase.equalsIgnoreCase("create"))
			{
				handler.createPlot();
			} else if(commandBase.equalsIgnoreCase("setworld"))
			{
				handler.createIslandWorld(command.get(2));
			} else if(commandBase.equalsIgnoreCase("list"))
			{
				handler.listHomes();
			} else if(commandBase.equalsIgnoreCase("info"))
			{
				handler.sendInfo();
			} else if(commandBase.equalsIgnoreCase("abandon"))
			{
				handler.abandonPlot();
			} else if(commandBase.equalsIgnoreCase("addmember"))
			{
				handler.addPlayer(command.get(2));
			} else if(commandBase.equalsIgnoreCase("removemember"))
			{
				handler.removePlayer(command.get(2));
			} else
			{
				hardFailure = true;
			}
		} catch(IllegalArgumentException exx)
		{
			hardFailure = true;
		} catch (ArgumentCountException ex) //If the sender does not use an adequate amount of arguments
		{
			if (ex.getErrorIndex() == 1)
				hardFailure = true;
			else
				softFailure = true;
		} catch(ClassCastException e) //If the command tries to get a Player from the sender, but the sender is the console
		{
			helpMessage = "You must be a Player to execute this command.";
			softFailure = true;
		}

		if(hardFailure)
			return false;
		else if(softFailure)
		{
			sender.sendMessage(ChatColor.RED + helpMessage);
			return true;
		}
		return true;
	}
}
