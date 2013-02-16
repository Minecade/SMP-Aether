package kabbage.islandplots.generation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

import kabbage.islandplots.IslandPlots;

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
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void generate()
	{
		for(int i = x - 15; i < x + 15; i++)
		{
			for(int j = y - 15; j < y + 15; j++)
			{
				for(int k = z - 15; k < z + 15; k++)
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
		int ver = in.readInt();
		if(ver == 1)
		{
			world = in.readUTF();
			x = in.readInt();
			y = in.readInt();
			z = in.readInt();
		} else
		{
			IslandPlots.logger.log(Level.WARNING, "Unsupported version of an Island failed to load.");
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(VERSION);
		
		out.writeUTF(world);
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
	}
}
