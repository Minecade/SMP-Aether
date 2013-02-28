package kabbage.islandplots.generation;

import kabbage.islandplots.IslandPlots;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;

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
		net.minecraft.server.v1_4_R1.World nmsWorld = ((CraftWorld) world).getHandle();
		for(int a = (x >> 4) - (width >> 1); a < (x >> 4) + (width >> 1); a++)
		{
			for(int b = (z >> 4) - (length >> 1); b < (z >> 4) + (length >> 1); b++)
			{
				IslandPlots.log("Generating chunk: ["+a+", "+b+"]");
				Chunk chunk = world.getChunkAt(a, b);
				chunk.load();
				for(int i = 0; i < 16; i++)
				{
					for(int k = 0; k < 16; k++)
					{
						int worldX = i + a * 16;
						int worldZ = k + b * 16;
						float noiseVal = (float) (noise.islandNoise(worldX - x, worldZ - z, 6, 0.45f) + .5);	//Add .5 to initial noise to increase volume
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
									type = y + 1 > sectionHeight ? (byte) Material.SANDSTONE.getId() : (byte) Material.SAND.getId();
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
				new ChunkPopulator(world, chunk, rnd).populate();
			}
		}
	}
}
