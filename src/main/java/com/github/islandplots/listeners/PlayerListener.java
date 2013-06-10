package com.github.islandplots.listeners;

import com.github.islandplots.IslandPlots;
import com.github.islandplots.PlayerWrapper;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class PlayerListener implements Listener
{
	private IslandPlots plugin;

	public PlayerListener()
	{
		plugin = IslandPlots.instance;
	}

	public void registerEvents(PluginManager pm, IslandPlots plugin)
	{
		pm.registerEvents(this, plugin);
	}

	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		PlayerWrapper pw = PlayerWrapper.getWrapper(event.getPlayer());
		if(pw.getPlots() == 0)
		{
			String worldName = plugin.getConfig().getString("spawn-world", "world");
			MultiverseWorld world = plugin.getMultiverse().getMVWorldManager().getMVWorld(worldName);
			if(world == null)
				return;
			event.setRespawnLocation(world.getSpawnLocation());
		} else
		{
			event.setRespawnLocation(pw.getPlot(0).getSpawnPoint());
		}
	}
}
