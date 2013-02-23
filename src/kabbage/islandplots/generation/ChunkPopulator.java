package kabbage.islandplots.generation;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkPopulator
{
	World world;
	Chunk chunk;
	
	Random rnd;
	
	public ChunkPopulator(World world, Chunk chunk, Random rnd)
	{
		this.world = world;
		this.chunk = chunk;
		
		this.rnd = rnd;
	}
	
	public void populate()
	{
		new LakePopulator(world).populate(world, rnd, chunk);
		new GrassPopulator(world).populate(world, rnd, chunk);
		new FlowerPopulator(world).populate(world, rnd, chunk);
		new PumpkinPopulator(world).populate(world, rnd, chunk);
		new OrePopulator(world).populate(world, rnd, chunk);
		new TreePopulator(world).populate(world, rnd, chunk);
		new SnowPopulator().populate(world, rnd, chunk);
		new MushroomPopulator(world).populate(world, rnd, chunk);
		new CactusPopulator(world).populate(world, rnd, chunk);
	}
}