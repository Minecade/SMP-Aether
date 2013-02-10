package kabbage.islandplots;

import java.util.logging.Logger;

import kabbage.islandplots.listeners.BlockListener;
import kabbage.islandplots.listeners.EntityListener;
import kabbage.islandplots.listeners.PlayerListener;
import kabbage.islandplots.listeners.WorldListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class IslandPlots extends JavaPlugin
{
	public static IslandPlots instance;
	public static Logger logger;
	
	//Listeners
	private EntityListener eListener;
	private PlayerListener pListener;
	private WorldListener wListener;
	private BlockListener bListener;
	
	@Override
	public void onEnable()
	{
		instance = this;
		logger = getLogger();
		
		PluginManager pm = Bukkit.getPluginManager();
		//Register listeners
		eListener = new EntityListener();
		pListener = new PlayerListener();
		wListener = new WorldListener();
		bListener = new BlockListener();			
		eListener.registerEvents(pm, this);
		pListener.registerEvents(pm, this);
		wListener.registerEvents(pm, this);
		bListener.registerEvents(pm, this);
	}

	@Override
	public void onDisable()
	{
		
	}
}
