package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

import kabbage.islandplots.generation.IslandGenerator;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Island implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	private static final int CHUNK_WIDTH = 16;
	private static final int CHUNK_LENGTH = 16;
	private static final int HEIGHT = 32;
	
	String world;
	int x;
	int y;
	int z;
	
	/**
	 * Empty constructor for externalization
	 */
	public Island() {}
	
	public Island(String world, int x, int y, int z)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void generate(String player)
	{
		IslandGenerator generator = new IslandGenerator(this, Bukkit.getWorld(world), x, y, z, CHUNK_WIDTH, CHUNK_LENGTH, HEIGHT, player);
		// Run it twice because it is designed to be run in two asynchronous tasks. Weird, I know, but it runs too slowly otherwise
		Bukkit.getScheduler().runTaskAsynchronously(IslandPlots.instance, generator);
		Bukkit.getScheduler().runTaskAsynchronously(IslandPlots.instance, generator);
	}
	
	public Location getSpawnPoint()
	{
		return Bukkit.getWorld(world).getHighestBlockAt(x, z).getLocation();
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
			IslandPlots.log(Level.WARNING, "Unsupported version of an Island failed to load.");
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
