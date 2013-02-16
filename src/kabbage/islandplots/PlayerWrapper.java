package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;

public class PlayerWrapper implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	static Map<String, PlayerWrapper> players = new HashMap<String, PlayerWrapper>();
	
	private String playerName;
	private List<Plot> plotsOwned;
	
	private PlayerWrapper(String playerName)
	{
		this.playerName = playerName;
		plotsOwned = new ArrayList<Plot>();
		
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
	
	public boolean canHavePlot()
	{
		//TODO
		return true;
	}
	
	public Plot getPlot(int index)
	{
		if(plotsOwned.size() < index)
			return null;
		return plotsOwned.get(index);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		int ver = in.readInt();
		if(ver == 1)
		{
			playerName = in.readUTF();
			plotsOwned = (List<Plot>) in.readObject();
		} else
		{
			IslandPlots.logger.log(Level.WARNING, "Unsupported version of an Island failed to load.");
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(VERSION);
		
		out.writeUTF(playerName);
		out.writeObject(plotsOwned);
	}
}
