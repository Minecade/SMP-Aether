package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Island implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	String world;
	double x;
	double y;
	double z;
	
	public Island(String world, double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void generate()
	{
		
	}
	
	public Location getSpawnPoint()
	{
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		
	}
}
