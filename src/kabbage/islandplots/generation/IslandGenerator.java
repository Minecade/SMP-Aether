package kabbage.islandplots.generation;

import java.util.Random;

import kabbage.islandplots.IslandPlots;

import org.bukkit.Chunk;
import org.bukkit.World;

public class IslandGenerator
{
	World world;
	int x;
	int y;
	int z;
	
	Random rnd;
	long seed;
	
	public IslandGenerator(int x, int y, int z, org.bukkit.World world)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		
		seed = new Random().nextLong();
		rnd = new Random(seed);
	}
	
	public void generate(int width, int length, int height)
	{
		PerlinNoise noise = new PerlinNoise(seed, width << 4, length << 4);
//		for(int x = -width << 3; x < width << 3; x++)
//		{
//			for(int z = -length << 3; z < length << 3; z++)
//			{
//				for(int y = height / -2; y < noise.islandNoise(x, z, 6, 0.5f) * height; y++)
//					world.getBlockAt(this.x + x, y + this.y, this.z + z).setTypeId(1);
//			}
//		}
		IslandPlots.log("Generating chunks");
		for(int a = (x >> 4) - (width >> 1); a < (x >> 4) + (width >> 1); a++)
		{
			for(int b = (z >> 4) - (length >> 1); b < (z >> 4) + (length >> 1); b++)
			{
				IslandPlots.log("Generating chunk: ["+a+", "+b+"]");
				for(int i = 0; i < 16; i++)
				{
					for(int k = 0; k < 16; k++)
					{
						int worldX = i + a * 16;
						int worldZ = k + b * 16;
						for(int y = height / -2; y < noise.islandNoise(worldX - x, worldZ - z, 6, 0.5f) * height; y++)
							world.getBlockAt(worldX, y + this.y, worldZ).setTypeId(1);
					}
				}
				Chunk chunk = world.getChunkAt(a, b);
				new ChunkPopulator(world, chunk, rnd).populate();
			}
		}
	}
}
