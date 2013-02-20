package kabbage.islandplots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kabbage.islandplots.commands.IslandPlotCommands;
import kabbage.islandplots.listeners.BlockListener;
import kabbage.islandplots.listeners.EntityListener;
import kabbage.islandplots.listeners.PlayerListener;
import kabbage.islandplots.listeners.WorldListener;
import kabbage.islandplots.utils.Constants;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.OverCaste.plugin.RedProtect.RedProtect;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class IslandPlots extends JavaPlugin
{
	public static IslandPlots instance;
	public static Logger logger;
	private MultiverseCore multiverse;
	private RedProtect redProtect;
	
	private PlotHandler plotHandler;
	
	private IslandPlotCommands ipCommands;
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
		
		ipCommands = new IslandPlotCommands();
		getCommand("island").setExecutor(ipCommands);
		
		PluginManager pm = Bukkit.getPluginManager();
		multiverse = (MultiverseCore) pm.getPlugin("MultiverseCore");
		redProtect = (RedProtect) pm.getPlugin("redProtect");
		//Register listeners
		eListener = new EntityListener();
		pListener = new PlayerListener();
		wListener = new WorldListener();
		bListener = new BlockListener();			
		eListener.registerEvents(pm, this);
		pListener.registerEvents(pm, this);
		wListener.registerEvents(pm, this);
		bListener.registerEvents(pm, this);
		
		loadPlayerWrappers();
		loadPlotHandler();
	}

	@Override
	public void onDisable()
	{
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
        	logger.log(Level.WARNING, "Couldn't load the PlayerWrapper database. Ignore if the island world has not yet been created.");
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
        	logger.log(Level.WARNING, "Couldn't load the PlotHandler database. Ignore if the island world has not yet been created.");
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
        	logger.log(Level.WARNING, "Error saving the PlayerWrapper database.");
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
        	logger.log(Level.WARNING, "Error saving the PlotHandler database.");
        	e.printStackTrace();
        }
	}
	
	public void setPlotHandler(PlotHandler plotHandler)
	{
		this.plotHandler = plotHandler;
	}
}
