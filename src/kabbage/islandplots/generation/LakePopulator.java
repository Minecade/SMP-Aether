package kabbage.islandplots.generation;

import java.util.Random;

import net.minecraft.server.v1_4_R1.WorldGenReed;
import net.minecraft.server.v1_4_R1.WorldGenLakes;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.generator.BlockPopulator;

public class LakePopulator extends BlockPopulator
{
	private Random random;

	public LakePopulator(World world)
	{
		this.random = new Random(world.getSeed());
	}

	public void populate(World world, Random random, Chunk chunk)
	{
		net.minecraft.server.v1_4_R1.World mcWorld = ((CraftWorld) world).getHandle();

		int x, y, z;

		int worldChunkX = chunk.getX() * 16;
		int worldChunkZ = chunk.getZ() * 16;

		x = worldChunkX + this.random.nextInt(16) + 8;
		z = worldChunkZ + this.random.nextInt(16) + 8;
		
		int chance = world.getBiome(x, z).equals(Biome.DESERT) ? 15 : 45;

		if(random.nextInt(100) > chance)
			return;
		
		y = world.getHighestBlockYAt(x, z);

		new WorldGenLakes(Material.WATER.getId()).a(mcWorld, this.random, x, y, z);
		new WorldGenReed().a(mcWorld, this.random, x, y, z);
	}

}
