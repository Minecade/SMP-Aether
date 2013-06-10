package com.github.islandplots.generation;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class NullChunkGenerator extends ChunkGenerator
{
	public byte[] generate(World world, Random random, int cx, int cz)
	{
		return new byte[32768];
	}

	public Location getFixedSpawnLocation(World world, Random random)
	{
		return new Location(world, 0.0D, 64.0D, 0.0D);
	}
}