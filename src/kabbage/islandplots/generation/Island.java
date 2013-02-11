package kabbage.islandplots.generation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class Island implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	String world;
	int x;
	int y;
	int z;
	
	public Island(String world, int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void generate()
	{
		for(int i = x - 35; i < x + 35; i++)
		{
			for(int j = y - 35; j < y + 35; j++)
			{
				for(int k = y - 35; k < y + 35; k++)
				{
					Bukkit.getWorld(world).getBlockAt(i, j, k).setType(Material.STONE);
				}
			}
		}
	}
	
	public Location getSpawnPoint()
	{
		return new Location(Bukkit.getWorld(world), x, y + 35, z);
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
