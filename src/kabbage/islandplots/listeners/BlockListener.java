package kabbage.islandplots.listeners;

import kabbage.islandplots.IslandPlots;
import kabbage.islandplots.Plot;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

public class BlockListener implements Listener
{
	private IslandPlots plugin;
	
	public BlockListener()
	{
		plugin = IslandPlots.instance;
	}
	
	public void registerEvents(PluginManager pm, IslandPlots plugin)
	{
		pm.registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void registerBlockChange(BlockPlaceEvent event)
	{
		registerBlockChange(event.getBlock().getLocation());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void registerBlockChange(BlockBreakEvent event)
	{
		registerBlockChange(event.getBlock().getLocation());
	}
	
	private void registerBlockChange(Location location)
	{
		Plot plot = plugin.getPlotHandler().getPlot(location);
		if(plot != null)
			plot.registerBlockChange();
	}
}