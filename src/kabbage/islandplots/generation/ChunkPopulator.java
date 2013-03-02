package kabbage.islandplots.generation;

import java.util.Random;

import kabbage.islandplots.IslandPlots;

import net.minecraft.server.v1_4_R1.ChunkSection;
import net.minecraft.server.v1_4_R1.WorldGenCaves;
import net.minecraft.server.v1_4_R1.WorldGenDungeons;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;

public class ChunkPopulator
{
	World world;
	Chunk chunk;
	
	Random rnd;
	
	int islandHeight;
	
	public ChunkPopulator(World world, Chunk chunk, Random rnd, int y)
	{
		this.world = world;
		this.chunk = chunk;
		
		this.rnd = rnd;
		
		islandHeight = y;
	}
	
	public void populate()
	{
		net.minecraft.server.v1_4_R1.World nmsWorld = ((CraftWorld) world).getHandle();
		
		WorldGenCaves caveGen = new WorldGenCaves();
		byte[] blocks = new byte[32768];
		for(int i = 0; i < blocks.length; i++) blocks[i] = 1;
		caveGen.a(nmsWorld.chunkProvider, nmsWorld, chunk.getX(), chunk.getZ(), blocks);
		
		byte[][] chunkBlocks = new byte[8][4096];
		for (int x = 0; x < 16; ++x)
		{
			for (int y = 0; y < 128; ++y)
			{
				for (int z = 0; z < 16; ++z)
				{
					chunkBlocks[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blocks[(x * 16 + z)  * 128 + y];
				}
			}
		}
		
		net.minecraft.server.v1_4_R1.Chunk nmsChunk = new net.minecraft.server.v1_4_R1.Chunk(nmsWorld, chunk.getX(), chunk.getZ());
		ChunkSection[] csect = nmsChunk.i();
		for (int sec = 0; sec < chunkBlocks.length; sec++)
		{
			if (chunkBlocks[sec] == null)
				continue;
			csect[sec] = new ChunkSection(sec << 4, true, chunkBlocks[sec], null);
		}
		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 128; j++)
			{
				for(int k = 0; k < 16; k++)
				{
					byte type = (byte) nmsChunk.getTypeId(i, j, k);
					if(type == 0 && j < islandHeight + 1) chunk.getBlock(i, j, k).setTypeId(type);
				}
			}
		}
		
		new LakePopulator(world).populate(world, rnd, chunk);
		new GrassPopulator(world).populate(world, rnd, chunk);
		new FlowerPopulator(world).populate(world, rnd, chunk);
		new PumpkinPopulator(world).populate(world, rnd, chunk);
		new OrePopulator(world).populate(world, rnd, chunk);
		new TreePopulator(world).populate(world, rnd, chunk);
		new SnowPopulator().populate(world, rnd, chunk);
		new MushroomPopulator(world).populate(world, rnd, chunk);
		new CactusPopulator(world).populate(world, rnd, chunk);
		for(int i = 0; i < 6; i++)
		{
			if(new WorldGenDungeons().a(((CraftWorld) world).getHandle(), rnd, chunk.getX() << 4, islandHeight - rnd.nextInt(4) - i*4, chunk.getZ() << 4))
				IslandPlots.log("Dungeon created at: "+chunk.getX()+":"+chunk.getZ());
		}
	}
}