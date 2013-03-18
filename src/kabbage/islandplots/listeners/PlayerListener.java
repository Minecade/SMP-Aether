package kabbage.islandplots.listeners;

import kabbage.islandplots.IslandPlots;
import kabbage.islandplots.PlayerWrapper;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;

public class PlayerListener implements Listener
{
	public void registerEvents(PluginManager pm, IslandPlots plugin)
	{
		pm.registerEvents(this, plugin);
	}
	
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		PlayerWrapper pw = PlayerWrapper.getWrapper(event.getPlayer());
		if(pw.getPlots() == 0)
			return;
		event.setRespawnLocation(pw.getPlot(0).getSpawnPoint());
	}
}
