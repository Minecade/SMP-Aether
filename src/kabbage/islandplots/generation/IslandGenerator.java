package kabbage.islandplots.generation;

import java.util.Random;

import kabbage.islandplots.IslandPlots;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

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
		for(int a = (x >> 4) - (width >> 1); a < (x >> 4) + (width >> 1); a++)
		{
			for(int b = (z >> 4) - (length >> 1); b < (z >> 4) + (length >> 1); b++)
			{
				for(int i = 0; i < 16; i++)
				{
					for(int k = 0; k < 16; k++)
					{
						int worldX = i + a * 16;
						int worldZ = k + b * 16;
						int noiseVal = (int) ((noise.islandNoise(worldX - x, worldZ - z, 12, 0.45f) + .5) * height);	//Add .5 to initial noise to increase volume
						Biome biome = world.getBlockAt(worldX, y, worldZ).getBiome();
						for(int y = 0; y < noiseVal; y++)
						{
							byte type = 1;
							//Decorate top of land mass
							if(noiseVal - y <= 3)
							{
								if (biome == Biome.DESERT)
									type = (noiseVal - y == 3 || noiseVal < 2) ? (byte) Material.SANDSTONE.getId() : (byte) Material.SAND.getId();
								else
									type = noiseVal - y == 1 ? (byte) Material.GRASS.getId() : (byte) Material.DIRT.getId();
							}
							world.getBlockAt(worldX, this.y - y, worldZ).setTypeId(1);
							world.getBlockAt(worldX, this.y + y, worldZ).setTypeId(type);
						}
					}
				}
				Chunk chunk = world.getChunkAt(a, b);
				new ChunkPopulator(world, chunk, rnd).populate();
			}
		}
	}
}
