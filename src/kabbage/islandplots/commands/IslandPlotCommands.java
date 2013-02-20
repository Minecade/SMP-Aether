package kabbage.islandplots.commands;

import kabbage.islandplots.IslandPlots;

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
			switch(command.get(1).toLowerCase())
			{
			case "home":
				handler.teleportHome();
				break;
			case "new": case "create":
				handler.createPlot();
				break;
			case "setworld":
				handler.createIslandWorld(command.get(2));
				break;
			case "list":
				handler.listHomes();
				break;
			default:
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
