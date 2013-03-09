package kabbage.islandplots.generation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import kabbage.islandplots.IslandPlots;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Island implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	private static final int CHUNK_WIDTH = 12;
	private static final int CHUNK_LENGTH = 12;
	private static final int HEIGHT = 32;
	
	private transient Location spawnCache;
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
		Bukkit.getScheduler().runTaskAsynchronously(IslandPlots.instance, generator);
	}
	
	public Location getSpawnPoint()
	{
		if(spawnCache != null && !isSafeSpawn(spawnCache))
			return Bukkit.getWorld(world).getHighestBlockAt(spawnCache).getLocation();
		// Find a safe spawn point
		final Vector start = new Vector(x, y + HEIGHT, z);
		Bukkit.getScheduler().runTaskAsynchronously(IslandPlots.instance, new Runnable()
		{
			@Override
			public void run()
			{
				int attempt = 0;
				try
				{
					while(Bukkit.getScheduler().callSyncMethod(IslandPlots.instance, new SyncIsSafeSpawn(start)).get())
					{
						if(attempt > 100)
							break;
						if(attempt % 10 == 0)
						{
							start.setX(start.getX()+1);
							start.setZ(z);
						} else
						{
							start.setZ(start.getZ()+1);
						}
					}
				} catch (InterruptedException|ExecutionException e)
				{
					e.printStackTrace();
				}
			}
		});
		return spawnCache = Bukkit.getWorld(world).getHighestBlockAt(new Location(Bukkit.getWorld(world), start.getX(), start.getY(), start.getZ())).getLocation();
	}
	
	class SyncIsSafeSpawn implements Callable<Boolean>
	{
		Vector spawn;
		public SyncIsSafeSpawn(Vector spawn)
		{
			this.spawn = spawn;
		}
		
		@Override
		public Boolean call() throws Exception
		{
			return isSafeSpawn(new Location(Bukkit.getWorld(world), spawn.getX(), spawn.getY(), spawn.getZ()));
		}
		
	}
	
	private List<Integer> unsafe = Arrays.asList(new Integer[]{0, 10, 11, 51});
	private boolean isSafeSpawn(Location loc)
	{
		return !unsafe.contains(Bukkit.getWorld(world).getHighestBlockAt(loc).getRelative(BlockFace.DOWN).getTypeId());
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
