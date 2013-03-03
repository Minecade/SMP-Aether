package kabbage.islandplots.generation;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import kabbage.islandplots.IslandPlots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftChunk;
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
	}
	
	public void runWithThrows() throws InterruptedException
	{
		PerlinNoise noise = new PerlinNoise(seed, width << 4, length << 4);
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
					Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.GOLD+"Generating chunks: "+percentDone+"%..."));
				last = percentDone;
				
				//Now actually do the generating
				Future<Chunk> fChunk = Bukkit.getScheduler().callSyncMethod(IslandPlots.instance, new GetSyncChunk(a, b));
				Chunk chunk;
				try
				{
					chunk = fChunk.get();
				} catch (Exception e)
				{
					e.printStackTrace();
					//Retry
					b--;
					doneSoFar--;
					continue;
				}
				chunk.load();
				toPopulate.add(chunk);
				
				Biome[][] biomes;
				Future<Biome[][]> fBiomes = Bukkit.getScheduler().callSyncMethod(IslandPlots.instance, new GetSyncBiomes(chunk));
				try
				{
					biomes = fBiomes.get();
				} catch (Exception e)
				{
					e.printStackTrace();
					//Retry
					toPopulate.remove(chunk);
					b--;
					doneSoFar--;
					continue;
				}
				byte[][][] blocks = new byte[16][128][16];
				for(int i = 0; i < 16; i++)
				{
					for(int k = 0; k < 16; k++)
					{
						int worldX = i + a * 16;
						int worldZ = k + b * 16;
						float noiseVal = (float) (noise.islandNoise(worldX - x, worldZ - z, 8, 0.45f) + .5);	//Add .5 to initial noise to increase volume
						Biome biome = biomes[i][k];
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
									type = y == 0 ? (byte) 24 : (byte) 12;
								else
									type = sectionHeight - y == 0 ? (byte) 2 : (byte) 3;
							}
							blocks[i][this.y + y][k] = type;
						}
						if(doBottom)
						{
							int oppY = 0;
							for(int o = 4; o < 6; o++) 	//When making the bottom half of the island, scew it so it doesn't look exactly like the top
								noiseVal = noise.addOctave(worldX - x, worldZ - z, noiseVal, 0.45f, o);
							sectionHeight *= 1.4;	//Make the bottom deeper than the top is high to make room for ores/caves
							for(int y = 0; y <= sectionHeight; y++)
								blocks[i][this.y + --oppY][k] = 1;
						}
					}
				}
				SetSyncBlocks setBlocks = new SetSyncBlocks(chunk, blocks);
				Bukkit.getScheduler().runTask(IslandPlots.instance, setBlocks);
				while(!setBlocks.isDone) Thread.sleep(50l);
			}
		}
		doneSoFar = 0;
		last = 0;
		for(Chunk chunk : toPopulate) 
		{
			//Inform the player populating the island of progress
			int percentDone = (int) ((++doneSoFar / (float) todo) * 100);
			if(percentDone % 10 == 0 && percentDone != last)
				Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.GOLD+"Populating chunks: "+percentDone+"%..."));
			last = percentDone;

			//Now actually populate the island
			new ChunkPopulator(world, chunk, rnd, y).populate();
		}
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SendSyncMessage(player, ChatColor.GOLD+"Finding safe spawn point..."));
		Bukkit.getScheduler().runTask(IslandPlots.instance, new SyncTeleport());
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
			Player playerTele = Bukkit.getPlayer(player);
			if(player != null) playerTele.teleport(island.getSpawnPoint());
		}
	}
	
	static class SetSyncBlocks extends BukkitRunnable
	{
		public volatile boolean isDone = false;
		
		net.minecraft.server.v1_4_R1.Chunk chunk;
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
							chunk.a(i, j, k, blocks[i][j][k]);
					}
				}
			}
			isDone = true;
		}
	}
}
