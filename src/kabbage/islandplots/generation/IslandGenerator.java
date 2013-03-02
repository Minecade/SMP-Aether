package kabbage.islandplots.generation;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import kabbage.islandplots.IslandPlots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IslandGenerator extends BukkitRunnable
{
	World world;
	int x;
	int y;
	int z;
	int width;
	int length;
	int height;
	
	Random rnd;
	long seed;
	
	String player;
	
	public IslandGenerator(org.bukkit.World world, int x, int y, int z, int width, int length, int height, String player)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.length = length;
		this.height = height;
		
		seed = new Random().nextLong();
		rnd = new Random(seed);
		this.player = player;
	}
	
	@Override
	public void run()
	{
		PerlinNoise noise = new PerlinNoise(seed, width << 4, length << 4);
		net.minecraft.server.v1_4_R1.World nmsWorld = ((CraftWorld) world).getHandle();
		Stack<Chunk> toPopulate = new Stack<Chunk>();
		int doneSoFar = 0;
		int todo = width * length;
		int last = 0;
		for(int a = (x >> 4) - (width >> 1); a < (x >> 4) + (width >> 1); a++)
		{
			for(int b = (z >> 4) - (length >> 1); b < (z >> 4) + (length >> 1); b++)
			{
				//Inform the player generating the island of progress
				int percentDone = (int) ((++doneSoFar / (float) todo) * 100);
				if(percentDone % 10 == 0 && percentDone != last)
				{
					Player player = Bukkit.getPlayer(this.player);
					if(player != null) player.sendMessage(ChatColor.GOLD+"Generating chunks: "+percentDone+"%...");
				}
				last = percentDone;
				
				//Now actually do the generating
				Chunk chunk = world.getChunkAt(a, b);
				chunk.load();
				toPopulate.add(chunk);
				for(int i = 0; i < 16; i++)
				{
					for(int k = 0; k < 16; k++)
					{
						int worldX = i + a * 16;
						int worldZ = k + b * 16;
						float noiseVal = (float) (noise.islandNoise(worldX - x, worldZ - z, 8, 0.45f) + .5);	//Add .5 to initial noise to increase volume
						Biome biome = world.getBlockAt(worldX, y, worldZ).getBiome();
						boolean doBottom = false;
						int sectionHeight = (int) (noiseVal * height);
						for(int y = 0; y <= sectionHeight; y++)
						{
							//Only do the bottom if there's a top. Otherwise it looks silly
							doBottom = true;
							byte type = 1;
							//Decorate top of land mass
							if(sectionHeight - y <= 3)
							{
								if (biome == Biome.DESERT)
									type = y == 0 ? (byte) Material.SANDSTONE.getId() : (byte) Material.SAND.getId();
								else
									type = sectionHeight - y == 0 ? (byte) Material.GRASS.getId() : (byte) Material.DIRT.getId();
							}
							nmsWorld.setTypeId(worldX, this.y + y, worldZ, type);
						}
						if(doBottom)
						{
							int oppY = 0;
							for(int o = 4; o < 6; o++) 	//When making the bottom half of the island, scew it so it doesn't look exactly like the top
								noiseVal = noise.addOctave(worldX - x, worldZ - z, noiseVal, 0.45f, o);
							sectionHeight *= 1.4;	//Make the bottom deeper than the top is high to make room for ores/caves
							for(int y = 0; y <= sectionHeight; y++)
								nmsWorld.setTypeId(worldX, this.y + --oppY, worldZ, 1);
						}
					}
				}
			}
		}
		doneSoFar = 0;
		last = 0;
		for(Chunk chunk : toPopulate) 
		{
			//Inform the player populating the island of progress
			int percentDone = (int) ((++doneSoFar / (float) todo) * 100);
			if(percentDone % 10 == 0 && percentDone != last)
			{
				Player player = Bukkit.getPlayer(this.player);
				if(player != null) player.sendMessage(ChatColor.GOLD+"Populating chunks: "+percentDone+"%...");
			}
			last = percentDone;
			
			//Now actually populate the island
			new ChunkPopulator(world, chunk, rnd, y).populate();
		}
	}
	
	class FutureGetChunk implements Callable<Chunk>
	{
		int x;
		int z;
		
		public FutureGetChunk(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
		@Override
		public Chunk call() throws Exception
		{
			Chunk chunk = world.getChunkAt(x, z);
			chunk.load();
			return chunk;
		}
		
	}
}
