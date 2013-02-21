package kabbage.islandplots.generation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

import kabbage.islandplots.IslandPlots;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class Island implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	private static final int CHUNK_WIDTH = 8;
	private static final int CHUNK_LENGTH = 8;
	
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
	
	public void generate()
	{
		World world = Bukkit.getWorld(this.world);
		ChunkGenerator gen = new ChunkGenerator(world);
		for(int a = -CHUNK_WIDTH >> 1; a < CHUNK_WIDTH >> 1; a++)
		{
			for(int b = -CHUNK_LENGTH >> 1; b < CHUNK_LENGTH >> 1; b++)
			{
				Bukkit.broadcastMessage(((x >> 4) + a) + ":" + ((z >> 4) + b));
				Chunk chunk = world.getChunkAt((x >> 4) + a, (z >> 4) + b);
				byte[][][] blocks = gen.generateBlocks((x >> 4) + a, (z >> 4) + b);
				
				for(int i = 0; i < 16; i++)
				{
					for(int j = 0; j < 128; j++)
					{
						for(int k = 0; k < 16; k++)
						{
							int type = blocks[i][j][k];
							if(type != 0) // No need to change air to air
								chunk.getBlock(i, j, k).setTypeId(type);
						}
					}
				}
			}
		}
	}
	
	public Location getSpawnPoint()
	{
		return new Location(Bukkit.getWorld(world), x, y + 15, z);
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
