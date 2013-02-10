package kabbage.islandplots.listeners;

import kabbage.islandplots.IslandPlots;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EntityListener implements Listener
{
	private IslandPlots plugin;

	public EntityListener()
	{
		plugin = IslandPlots.instance;
	}

	public void registerEvents(PluginManager pm, IslandPlots plugin)
	{
		pm.registerEvents(this, plugin);
	}
}