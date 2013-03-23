package kabbage.islandplots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.logging.Level;

import kabbage.islandplots.commands.IslandPlotCommands;
import kabbage.islandplots.listeners.BlockListener;
import kabbage.islandplots.listeners.PlayerListener;
import kabbage.islandplots.utils.Constants;

import kabbage.islandplots.generation.GenerationQueue;
import kabbage.islandplots.generation.NullChunkGenerator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.OverCaste.plugin.RedProtect.RedProtect;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class IslandPlots extends JavaPlugin
{
	public static IslandPlots instance;
	private MultiverseCore multiverse;
	private RedProtect redProtect;
	
	private PlotHandler plotHandler;
	private GenerationQueue generationQueue;
	
	private IslandPlotCommands ipCommands;
	//Listeners
	private PlayerListener pListener;
	private BlockListener bListener;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		loadConfiguration();
		loadDefaults();
		
		ipCommands = new IslandPlotCommands();
		getCommand("island").setExecutor(ipCommands);
		
		PluginManager pm = Bukkit.getPluginManager();
		multiverse = (MultiverseCore) pm.getPlugin("MultiverseCore");
		redProtect = (RedProtect) pm.getPlugin("redProtect");
		//Register listeners
		pListener = new PlayerListener();
		bListener = new BlockListener();			
		pListener.registerEvents(pm, this);
		bListener.registerEvents(pm, this);
		
		loadPlayerWrappers();
		loadPlotHandler();
		
		generationQueue = new GenerationQueue();
	}

	@Override
	public void onDisable()
	{
		saveConfig();
		
		savePlayerWrappers();
		savePlotHandler();
	}
	
	public MultiverseCore getMultiverse()
	{
		return multiverse;
	}
	
	public RedProtect getRedProtect()
	{
		return redProtect;
	}
	
	public PlotHandler getPlotHandler()
	{
		return plotHandler;
	}
	
	public GenerationQueue getGenerationQueue()
	{
		return generationQueue;
	}
	
	private void loadConfiguration()
	{
		getConfig().options().copyDefaults(true); 
		saveConfig();
	}
	
	private void loadDefaults()
	{
		FileConfiguration cfg = getConfig().options().configuration();
		cfg.addDefault("spawn-world", "world");
		
		saveConfig();
	}
	
	@SuppressWarnings("unchecked")
	private void loadPlayerWrappers()
	{
		File path = new File(Constants.PLAYERS_PATH);

        try
        {
        	FileInputStream fis = new FileInputStream(path);
        	ObjectInputStream ois = new ObjectInputStream(fis);

        	PlayerWrapper.players = (Map<String, PlayerWrapper>) ois.readObject();

            ois.close();
            fis.close();

        } catch (Exception e)
        {
        	log(Level.WARNING, "Couldn't load the PlayerWrapper database. Ignore if the island world has not yet been created.");
        }
	}
	
	private void loadPlotHandler()
	{
		File path = new File(Constants.PLOT_PATH);

        try
        {
        	FileInputStream fis = new FileInputStream(path);
        	ObjectInputStream ois = new ObjectInputStream(fis);

        	plotHandler = new PlotHandler();
        	plotHandler.readExternal(ois);

            ois.close();
            fis.close();

        } catch (Exception e)
        {
        	log(Level.WARNING, "Couldn't load the PlotHandler database. Ignore if the island world has not yet been created.");
        	e.printStackTrace();
        }
	}
	
	private void savePlayerWrappers()
	{
		File path = new File(Constants.PLAYERS_PATH);
		new File(Constants.PLUGIN_PATH).mkdir();
		
		try
		{
			path.createNewFile();
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(PlayerWrapper.players);

            oos.close();
            fos.close();

        } catch (IOException e)
        {
        	log(Level.WARNING, "Error saving the PlayerWrapper database.");
        	e.printStackTrace();
        }
	}
	
	private void savePlotHandler()
	{
		if(plotHandler == null)
			return;
		File path = new File(Constants.PLOT_PATH);
		new File(Constants.PLUGIN_PATH).mkdir();
		
		try
		{
			path.createNewFile();
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

            plotHandler.writeExternal(oos);

            oos.close();
            fos.close();

        } catch (IOException e)
        {
        	log(Level.WARNING, "Error saving the PlotHandler database.");
        	e.printStackTrace();
        }
	}
	
	public void setPlotHandler(PlotHandler plotHandler)
	{
		this.plotHandler = plotHandler;
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		return new NullChunkGenerator();
	}
	
	public static void log(Level level, String log)
	{
		instance.getLogger().log(level, log);
	}
	
	public static void log(String log)
	{
		log(Level.INFO, log);
	}
}
