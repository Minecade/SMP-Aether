package kabbage.islandplots.listeners;

import kabbage.islandplots.IslandPlots;
import kabbage.islandplots.Plot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
		registerBlockChange(event.getBlock().getLocation(), event.getBlock().getType(), true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void registerBlockChange(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		if(block.getType().name().contains("ORE") && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
			return;
		registerBlockChange(block.getLocation(), block.getType(), false);
	}
	
	private void registerBlockChange(Location location, Material type, boolean place)
	{
		Plot plot = plugin.getPlotHandler().getPlot(location);
		if(plot != null)
			plot.registerBlockChange(type, place);
	}
}