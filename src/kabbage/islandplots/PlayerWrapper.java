package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import kabbage.islandplots.utils.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerWrapper implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 2;
	
	static Map<String, PlayerWrapper> players = new HashMap<String, PlayerWrapper>();
	
	private String playerName;
	private List<Plot> plotsOwned;
	private long lastAbandon;
	
	/**
	 * Empty constructor for externalization
	 */
	public PlayerWrapper() {}
	
	private PlayerWrapper(String playerName)
	{
		this.playerName = playerName;
		plotsOwned = new ArrayList<Plot>();
		lastAbandon = 0L;
		
		players.put(playerName, this);
	}
	
	public PlayerWrapper(Player player)
	{
		this(player.getName());
	}
	
	public void addPlot(Plot plot)
	{
		plotsOwned.add(plot);
	}
	
	public boolean canAbandonPlot()
	{
		long timeSinceAbandon = new Date().getTime() - lastAbandon;
		return TimeUnit.MILLISECONDS.toHours(timeSinceAbandon) >= 24;
	}
	
	public boolean canHavePlot()
	{
		int size = plotsOwned.size();
		if(size == 0)
			return true;
		if(size >= Permissions.maxPlots(getPlayer()))
			return false;
		int totalLevel = 0;
		for(Plot p : plotsOwned) totalLevel += p.getLevel();
		int requiredLevel = getRequiredLevel();
		if(totalLevel >= requiredLevel)
			return false;
		return false;
	}
	
	public int getRequiredLevel()
	{
		return (int) (Math.pow(plotsOwned.size(), 2) + plotsOwned.size() * 10 + 5);
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(playerName);
	}
	
	public Plot getPlot(int index)
	{
		if(index >= plotsOwned.size() || index < 0)
			return null;
		return plotsOwned.get(index);
	}
	
	public int getPlots()
	{
		return plotsOwned.size();
	}
	
	public void removePlot(Plot p)
	{
		lastAbandon = new Date().getTime();
		Stack<Plot> toRemove = new Stack<Plot>();
		for(Plot other : plotsOwned)
		{
			if(other.getGridLocation().equals(p.getGridLocation()))
				toRemove.add(other);
		}
		plotsOwned.removeAll(toRemove);
	}
	
	public boolean isOwnedPlot(Plot p)
	{
		return plotsOwned.contains(p);
	}
	
	public static PlayerWrapper getWrapper(String player)
	{
		PlayerWrapper wrapper = players.get(player);
		if(wrapper == null) wrapper = new PlayerWrapper(player);
		return wrapper;
	}
	
	public static PlayerWrapper getWrapper(Player player)
	{
		return getWrapper(player.getName());
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		int ver = in.readInt();
		if(ver == 1)
		{
			playerName = in.readUTF();
			plotsOwned = new ArrayList<Plot>();
			lastAbandon = 0L;
		} else if(ver == 2)
		{
			playerName = in.readUTF();
			plotsOwned = new ArrayList<Plot>();
			lastAbandon = in.readLong();
		} else
		{
			IslandPlots.log(Level.WARNING, "Unsupported version of an Island failed to load.");
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(VERSION);
		
		out.writeUTF(playerName);
		out.writeLong(lastAbandon);
	}

	public void reset()
	{
		for(int i = 0; i < plotsOwned.size(); i++)
		{
			Plot p = plotsOwned.get(i);
			if(!p.getOwner().equals(playerName))
				continue;
			IslandPlots.instance.getPlotHandler().removePlot(p);
		}
		plotsOwned.clear();
	}
}
