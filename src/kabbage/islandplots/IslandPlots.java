package kabbage.islandplots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import com.google.common.io.Files;

public class IslandPlots extends JavaPlugin
{
	public static IslandPlots instance;
	public static Logger logger;
	
	private PlotHandler plotHandler;
	
	private final IslandPlotCommands ipCommands = new IslandPlotCommands();
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
		
		getCommand("island").setExecutor(ipCommands);
		
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
	
	public PlotHandler getPlotHandler()
	{
		return plotHandler;
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
        	logger.log(Level.WARNING, "Couldn't load the PlotHandler database. Ignore if this is the first time the plugin has been run.");
        	plotHandler = new PlotHandler();
        }
	}
	
	private void savePlotHandler()
	{
		File path = new File(Constants.PLOT_PATH);

		try
		{
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

            plotHandler.writeExternal(oos);

            oos.close();
            fos.close();

        } catch (IOException e)
        {
        	logger.log(Level.WARNING, "Error saving the PlotHandler database.");
        }
	}
}
