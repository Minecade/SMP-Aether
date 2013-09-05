package com.github.islandplots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.logging.Level;

import com.github.islandplots.commands.IslandPlotCommands;
import com.github.islandplots.listeners.BlockListener;
import com.github.islandplots.listeners.PlayerListener;
import com.github.islandplots.utils.Constants;
import com.github.islandplots.utils.CustomObjectInputStream;
import com.github.islandplots.utils.Utils;

import com.github.islandplots.generation.GenerationQueue;
import com.github.islandplots.generation.NullChunkGenerator;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.OverCaste.plugin.RedProtect.RedProtect;
import com.google.common.io.Files;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class IslandPlots extends JavaPlugin
{
	public static IslandPlots instance;
	private MultiverseCore multiverse;
	private RedProtect redProtect;

	private PlotHandler plotHandler;
	private GenerationQueue generationQueue;

	private Economy economy;

	@Override
	public void onEnable()
	{
		instance = this;

		loadConfiguration();
		loadDefaults();

		getCommand("island").setExecutor(new IslandPlotCommands());

		PluginManager pm = Bukkit.getPluginManager();
		multiverse = (MultiverseCore) pm.getPlugin("MultiverseCore");
		redProtect = (RedProtect) pm.getPlugin("redProtect");
		//Register listeners
		new PlayerListener().registerEvents(pm, this);
		new BlockListener().registerEvents(pm, this);

		if(!setupEconomy())
		{
			log(Level.SEVERE, "No economy plugin found to hook into. Plugin shutting down.");
			Bukkit.getPluginManager().disablePlugin(this);
		}

		loadPlayerWrappers();
		loadPlotHandler();

		generationQueue = new GenerationQueue();
	}

	@Override
	public void onDisable()
	{
		saveConfig();

		savePlayerWrappers(true);
		savePlotHandler(true);
	}

	public Economy getEconomy()
	{
		return economy;
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
        	CustomObjectInputStream ois = new CustomObjectInputStream(fis);

        	PlayerWrapper.players = (Map<String, PlayerWrapper>) ois.readObject();

            ois.close();
            fis.close();

        } catch (Exception e)
        {
        	log(Level.WARNING, "Couldn't load the PlayerWrapper database. Ignore if the island world has not yet been created.");
        	e.printStackTrace();
        }
	}

	private void loadPlotHandler()
	{
		File path = new File(Constants.PLOT_PATH);

        try
        {
        	FileInputStream fis = new FileInputStream(path);
        	CustomObjectInputStream ois = new CustomObjectInputStream(fis);

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

	void savePlayerWrappers(boolean backup)
	{
		File path = new File(Constants.PLAYERS_PATH);
		File[] backups = new File[2];
		backups[0] = path;

		for(int i = 1; i < 2; i++)
		{
			backups[i] = new File(Constants.PLUGIN_PATH+File.separator+"playersbackup"+ i +".ext");
		}

		try
		{
			if(backup)
			{
				//If the two files are equal, there's no need for a backup
				if(!Utils.fileEquals(backups[0], backups[1]))
				{
					for(int i = 1; i > 0; i--)
					{
						if(backups[i - 1].exists())
							Files.move(backups[i - 1], backups[i]);
					}
				}
			}

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

	void savePlotHandler(boolean backup)
	{
		if(plotHandler == null)
			return;
		File path = new File(Constants.PLOT_PATH);
		File[] backups = new File[2];
		backups[0] = path;

		for(int i = 1; i < 2; i++)
		{
			backups[i] = new File(Constants.PLUGIN_PATH+File.separator+"islandsbackup"+ i +".ext");
		}

		try
		{
			if(backup)
			{
				//If the two files are equal, there's no need for a backup
				if(!Utils.fileEquals(backups[0], backups[1]))
				{
					for(int i = 1; i > 0; i--)
					{
						if(backups[i - 1].exists())
							Files.move(backups[i - 1], backups[i]);
					}
				}
			}
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

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
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
