package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class PlayerWrapper implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	private static Map<String, PlayerWrapper> players = new HashMap<String, PlayerWrapper>();
	
	private String playerName;
	private List<Plot> plotsOwned;
	
	public PlayerWrapper(String playerName)
	{
		this.playerName = playerName;
		plotsOwned = new ArrayList<Plot>();
		
		players.put(playerName, this);
	}
	
	public PlayerWrapper(Player player)
	{
		this(player.getName());
	}
	
	public boolean addPlot()
	{
		return true;
	}
	
	public Plot getPlot(int index)
	{
		return plotsOwned.get(index);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		
	}
	
	public static PlayerWrapper getWrapper(String player)
	{
		return players.get(player);
	}
	
	public static PlayerWrapper getWrapper(Player player)
	{
		return getWrapper(player.getName());
	}
}
