package com.github.islandplots.generation;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.github.islandplots.Island;
import com.github.islandplots.IslandPlots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_6_R2.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IslandGenerator extends BukkitRunnable
{
	Island island;
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

	volatile Stack<Chunk> toPopulate = new Stack<Chunk>();

	volatile boolean running;

	public IslandGenerator(Island island, org.bukkit.World world, int x, int y, int z, int width, int length, int height, String player)
	{
		this.island = island;
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

		running = true;
	}

	@Override
	public void run()
	{
		try
		{
			runWithThrows();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		running = false;
	}

	public void runWithThrows() throws InterruptedException
	{
		PerlinNoise noise = new PerlinNoise(seed, width << 4, length << 4);
		int aFrom = (x >> 4) - (width >> 1);
		int aTo = (x >> 4) + (width >> 1);
		int bFrom = (z >> 4) - (length >> 1);
		int bTo = (z >> 4) + (length >> 1);
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.GOLD+"Generating chunks..."));
		int chunksDone = 0;
		for(int a = aFrom; a < aTo; a++)
		{
			for(int b = bFrom; b < bTo; b++)
			{
				chunksDone++;
				Bukkit.getScheduler().runTaskAsynchronously(IslandPlots.instance, new GenerateChunk(noise, a, b));
				Thread.sleep(60L);
			}
		}
		while(toPopulate.size() < chunksDone)
			Thread.sleep(50L);


		Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.GOLD+"Populating chunks..."));
		for(Chunk chunk : toPopulate)
			new ChunkPopulator(world, chunk, rnd, y).populate();

		Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.GOLD+"Finding safe spawn point..."));
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncTeleport());
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.RED+"Welcome to your island! The entire island is yours, do " +
				"with it what you will. You can have your friends help you by typing - /is addmember <name>"));
	}

	class GenerateChunk extends BukkitRunnable
	{
		PerlinNoise noise;
		int a;
		int b;
		public GenerateChunk(PerlinNoise noise, int a, int b)
		{
			this.noise = noise;
			this.a = a;
			this.b = b;
		}

		@Override
		public void run()
		{
			try
			{
				runWithThrows();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				run();
			}
		}

		public void runWithThrows() throws InterruptedException
		{
			Future<Chunk> fChunk = Bukkit.getScheduler().callSyncMethod(IslandPlots.instance, new GetSyncChunk(a, b));
			Chunk chunk;
			try
			{
				chunk = fChunk.get();
			} catch (Exception e)
			{
				e.printStackTrace();
				//Retry
				runWithThrows();
				return;
			}
			toPopulate.push(chunk);

			Biome[][] biomes;
			Future<Biome[][]> fBiomes = Bukkit.getScheduler().callSyncMethod(IslandPlots.instance, new GetSyncBiomes(chunk));
			try
			{
				biomes = fBiomes.get();
			} catch (Exception e)
			{
				e.printStackTrace();
				//Retry
				toPopulate.pop();
				runWithThrows();
				return;
			}
			byte[][][] blocks = new byte[16][128][16];
			for(int i = 0; i < 16; i++)
			{
				for(int k = 0; k < 16; k++)
				{
					int worldX = i + a * 16;
					int worldZ = k + b * 16;
					float noiseVal = noise.islandNoise(worldX - x, worldZ - z, 5, 0.35f);
					Biome biome = biomes[i][k];
					boolean doBottom = false;
					int sectionHeight = (int) (noiseVal * height);
					for(int deltaY = 0; deltaY <= sectionHeight; deltaY++)
					{
						//Only do the bottom if there's a top. Otherwise it looks silly
						doBottom = true;
						byte type = 1;
						//Decorate top of land mass
						if(sectionHeight - deltaY <= 3)
						{
							if (biome == Biome.DESERT)
								type = sectionHeight - deltaY == 3 ? (byte) 24 : (byte) 12;
							else
								type = sectionHeight - deltaY == 0 ? (byte) 2 : (byte) 3;
						}
						blocks[i][y + deltaY][k] = type;
					}
					if(doBottom)
					{
						int oppY = 0;
						//When making the bottom half of the island, scew it so it doesn't look exactly like the top
						noiseVal = noise.addOctave(worldX - x, worldZ - z, noiseVal, 0.45f, 3);
						sectionHeight = (int) (noiseVal * height * 2.25);	//Make the bottom deeper than the top is high to make room for ores/caves
						sectionHeight += rnd.nextInt(2);
						if(sectionHeight > y - 1)
							sectionHeight = y - 1;
						for(int deltaY = 0; deltaY <= sectionHeight; deltaY++)
							blocks[i][y + --oppY][k] = 1;
					}
				}
			}
			SetSyncBlocks setBlocks = new SetSyncBlocks(chunk, blocks);
			Bukkit.getScheduler().runTask(IslandPlots.instance, setBlocks);
			while(!setBlocks.isDone) Thread.sleep(50l);
		}
	}

	class GetSyncChunk implements Callable<Chunk>
	{
		int x;
		int z;

		public GetSyncChunk(int x, int z)
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

	class SendSyncMessage extends BukkitRunnable
	{
		String player;
		String message;
		public SendSyncMessage(String player, String message)
		{
			this.player = player;
			this.message = message;
		}

		@Override
		public void run()
		{
			Player player = Bukkit.getPlayer(this.player);
			if(player != null) player.sendMessage(message);
		}
	}

	class GetSyncBiomes implements Callable<Biome[][]>
	{
		Chunk chunk;
		public GetSyncBiomes(Chunk chunk)
		{
			this.chunk = chunk;
		}

		@Override
		public Biome[][] call() throws Exception
		{
			Biome[][] biomes = new Biome[16][16];
			for(int i = 0; i < 16; i++)
			{
				for(int k = 0; k < 16; k++)
				{
					biomes[i][k] = chunk.getBlock(i, 64, k).getBiome();
				}
			}
			return biomes;
		}
	}

	class SyncTeleport extends BukkitRunnable
	{
		@Override
		public void run()
		{
			try
			{
				Player playerTele = Bukkit.getPlayer(player);
				if(player != null) playerTele.teleport(island.getSpawnPoint());
			} catch(NullPointerException e)
			{} //Null points keep coming up when the player isn't online, even with the null check...
		}
	}

	static class SetSyncBlocks extends BukkitRunnable
	{
		public volatile boolean isDone = false;

		net.minecraft.server.v1_6_R2.Chunk chunk;
		byte[][][] blocks;
		public SetSyncBlocks(Chunk chunk, byte[][][] blocks)
		{
			this.chunk = ((CraftChunk) chunk).getHandle();
			this.blocks = blocks;
		}

		@Override
		public void run()
		{
			for(byte i = 0; i < 16; i++)
			{
				for(byte j = 0; j < 127; j++)
				{
					for(int k = 0; k < 16; k++)
					{
						if(blocks[i][j][k] != -1)
							chunk.a(i, j, k, blocks[i][j][k], 0);
					}
				}
			}
			isDone = true;
		}
	}
}
