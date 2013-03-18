package kabbage.islandplots.generation;

import java.util.Random;

import kabbage.islandplots.IslandPlots;

import net.minecraft.server.v1_5_R1.ChunkSection;
import net.minecraft.server.v1_5_R1.WorldGenCaves;
import net.minecraft.server.v1_5_R1.WorldGenDungeons;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R1.CraftWorld;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkPopulator
{
	World world;
	Chunk chunk;
	
	Random rnd;
	
	int islandHeight;
	
	volatile boolean isPopulateDone;
	
	public ChunkPopulator(World world, Chunk chunk, Random rnd, int y)
	{
		this.world = world;
		this.chunk = chunk;
		
		this.rnd = rnd;
		
		islandHeight = y;
	}
	
	public void populate() throws InterruptedException
	{
		net.minecraft.server.v1_5_R1.World nmsWorld = ((CraftWorld) world).getHandle();
		
		byte[] blocks = new byte[32768];
		for(int i = 0; i < blocks.length; i++) blocks[i] = 1;
		for(int i = 0; i < 12; i++)	//Caves don't generate often enough if we only run it once
			new WorldGenCaves().a(nmsWorld.chunkProvider, nmsWorld, chunk.getX() + i * 16, chunk.getZ() + i * 16, blocks);
		
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
		
		net.minecraft.server.v1_5_R1.Chunk nmsChunk = new net.minecraft.server.v1_5_R1.Chunk(nmsWorld, chunk.getX(), chunk.getZ());
		ChunkSection[] csect = nmsChunk.i();
		for (int sec = 0; sec < chunkBlocks.length; sec++)
		{
			if (chunkBlocks[sec] == null)
				continue;
			csect[sec] = new ChunkSection(sec << 4, true, chunkBlocks[sec], null);
		}
		byte[][][] bTypes = new byte[16][128][16];
		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 128; j++)
			{
				for(int k = 0; k < 16; k++)
				{
					byte type = (byte) nmsChunk.getTypeId(i, j, k);
					bTypes[i][j][k] = (byte) ((type == 0 && j < islandHeight - 2) ? 0 : -1);
				}
			}
		}
		IslandGenerator.SetSyncBlocks setBlocks = new IslandGenerator.SetSyncBlocks(chunk, bTypes);
		Bukkit.getScheduler().runTask(IslandPlots.instance, setBlocks);
		while(!setBlocks.isDone) Thread.sleep(50l);
		
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(LakePopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(GrassPopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(FlowerPopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(PumpkinPopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(OrePopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(TreePopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(SnowPopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(MushroomPopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncPopulate(CactusPopulator.class));
		while(!isPopulateDone) Thread.sleep(50l);
		
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncDungeonGenerate());
		while(!isPopulateDone) Thread.sleep(50l);
	}
	
	class SyncPopulate extends BukkitRunnable
	{
		Class<? extends BlockPopulator> populator;
		public SyncPopulate(Class<? extends BlockPopulator> populator)
		{
			this.populator = populator;
		}
		
		@Override
		public void run()
		{
			isPopulateDone = false;
			try
			{
				populator.getConstructor(World.class).newInstance(world).populate(world, rnd, chunk);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			isPopulateDone = true;
		}
	}
	
	class SyncDungeonGenerate extends BukkitRunnable
	{
		@Override
		public void run()
		{
			isPopulateDone = false;
			for(int i = 0; i < 6; i++)
			{
				if(new WorldGenDungeons().a(((CraftWorld) world).getHandle(), rnd, chunk.getX() << 4, islandHeight - rnd.nextInt(4) - i*4, chunk.getZ() << 4))
					IslandPlots.log("Dungeon created at: "+chunk.getX()+":"+chunk.getZ());
			}
			isPopulateDone = true;
		}
	}
}