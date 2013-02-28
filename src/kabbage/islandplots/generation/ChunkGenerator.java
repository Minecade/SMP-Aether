package kabbage.islandplots.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_4_R1.BiomeBase;
import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.ChunkSection;
import net.minecraft.server.v1_4_R1.NoiseGeneratorOctaves;
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldGenCaves;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.block.CraftBlock;
import org.bukkit.generator.BlockPopulator;

public class ChunkGenerator extends org.bukkit.generator.ChunkGenerator
{
	private Random random;
	
	private World world;

	private NoiseGeneratorOctaves k;
	private NoiseGeneratorOctaves l;
	private NoiseGeneratorOctaves m;
	private NoiseGeneratorOctaves o;
	private NoiseGeneratorOctaves a;
	private NoiseGeneratorOctaves b;

	private WorldGenCaves caveGen;

	private double[] q;
	private double[] t = new double[256];

	double[] d;
	double[] e;
	double[] f;
	double[] g;
	double[] h;

	int[][] i = new int[32][32];
	
	public ChunkGenerator(org.bukkit.World world)
	{
		getDefaultPopulators(world);
	}

	public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world)
	{
		this.world = ((CraftWorld) world).getHandle();
		ArrayList<BlockPopulator> populators = new ArrayList<BlockPopulator>();
		populators.add(new LakePopulator(world));
		populators.add(new GrassPopulator(world));
		populators.add(new FlowerPopulator(world));
		populators.add(new PumpkinPopulator(world));
		populators.add(new OrePopulator(world));
		populators.add(new TreePopulator(world));
		populators.add(new SnowPopulator());
		populators.add(new MushroomPopulator(world));
		populators.add(new CactusPopulator(world));

		return populators;
	}

	private double[] a(double[] adouble, int i, int j, int k, int l, int i1, int j1)
	{
		if (adouble == null)
		{
			adouble = new double[l * i1 * j1];
		}

		double d0 = 684.412D;
		double d1 = 684.412D;

		this.g = this.a.a(this.g, i, k, l, j1, 1.121D, 1.121D, 0.5D);
		this.h = this.b.a(this.h, i, k, l, j1, 200.0D, 200.0D, 0.5D);

		d0 *= 2.0D;

		this.d = this.m.a(this.d, i, j, k, l, i1, j1, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
		this.e = this.k.a(this.e, i, j, k, l, i1, j1, d0, d1, d0);
		this.f = this.l.a(this.f, i, j, k, l, i1, j1, d0, d1, d0);

		int k1 = 0;
		int l1 = 0;

		for (int i2 = 0; i2 < l; ++i2)
		{
			for (int j2 = 0; j2 < j1; ++j2)
			{
				double d2 = (this.g[l1] + 256.0D) / 512.0D;

				if (d2 > 1.0D)
					d2 = 1.0D;

				double d3 = this.h[l1] / 8000.0D;

				if (d3 < 0.0D)
					d3 = -d3 * 0.3D;

				d3 = d3 * 3.0D - 2.0D;

				if (d3 > 1.0D)
					d3 = 1.0D;

				d3 /= 8.0D;
				d3 = 0.0D;

				if (d2 < 0.0D)
					d2 = 0.0D;

				d2 += 0.5D;
				d3 = d3 * i1 / 16.0D;

				++l1;

				double d4 = i1 / 2.0D;

				for (int k2 = 0; k2 < i1; ++k2)
				{
					double d5 = 0.0D;
					double d6 = (k2 - d4) * 8.0D / d2;

					if (d6 < 0.0D)
						d6 *= -1.0D;

					double d7 = this.e[k1] / 512.0D;
					double d8 = this.f[k1] / 512.0D;
					double d9 = (this.d[k1] / 10.0D + 1.0D) / 2.0D;

					if (d9 < 0.0D)
					{
						d5 = d7;
					} else if (d9 > 1.0D)
					{
						d5 = d8;
					} else
					{
						d5 = d7 + (d8 - d7) * d9;
					}

					d5 -= 8.0D;

					byte b0 = 32;
					double d10;

					if (k2 > i1 - b0)
					{
						d10 = (k2 - (i1 - b0)) / (b0 - 1.0F);
						d5 = d5 * (1.0D - d10) + -30.0D * d10;
					}

					b0 = 8;

					if (k2 < b0)
					{
						d10 = (b0 - k2) / (b0 - 1.0F);
						d5 = d5 * (1.0D - d10) + -30.0D * d10;
					}

					adouble[k1] = d5;
					++k1;
				}
			}
		}

		return adouble;
	}

	private void shapeLand(org.bukkit.World world, int chunkX, int chunkZ, byte[] blocks)
	{
		byte b0 = 2;
		int k = b0 + 1;

		int l = 128 / 4 + 1;
		int i1 = b0 + 1;

		this.q = this.a(this.q, chunkX * b0, 0, chunkZ * b0, k, l, i1);

		byte blockType = (byte) Material.STONE.getId();

		for (int j1 = 0; j1 < b0; ++j1)
		{
			int k1 = 0;

			while (k1 < b0)
			{
				int l1 = 0;

				while (true)
				{
					if (l1 >= 128 / 4)
					{
						++k1;
						break;
					}

					double d0 = 0.25D;
					double d1 = this.q[((j1 + 0) * i1 + (k1 + 0)) * l + l1 + 0];
					double d2 = this.q[((j1 + 0) * i1 + (k1 + 1)) * l + l1 + 0];
					double d3 = this.q[((j1 + 1) * i1 + (k1 + 0)) * l + l1 + 0];
					double d4 = this.q[((j1 + 1) * i1 + (k1 + 1)) * l + l1 + 0];
					double d5 = (this.q[((j1 + 0) * i1 + (k1 + 0)) * l + l1 + 1] - d1) * d0;
					double d6 = (this.q[((j1 + 0) * i1 + (k1 + 1)) * l + l1 + 1] - d2) * d0;
					double d7 = (this.q[((j1 + 1) * i1 + (k1 + 0)) * l + l1 + 1] - d3) * d0;
					double d8 = (this.q[((j1 + 1) * i1 + (k1 + 1)) * l + l1 + 1] - d4) * d0;

					for (int i2 = 0; i2 < 4; ++i2)
					{
						double d9 = 0.125D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int j2 = 0; j2 < 8; ++j2)
						{
							int i3 = j2 + j1 * 8 << 11 | 0 + k1 * 8 << 7 | l1
									* 4 + i2;

							int j3 = 1 << 7;
							double d14 = 0.125D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k3 = 0; k3 < 8; ++k3)
							{
								if (d15 > 0.0D)
								{
									blocks[i3] = blockType;
								}

								i3 += j3;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}

					++l1;
				}
			}
		}
	}

	private void decorateLand(int chunkX, int chunkZ, byte[] blocks, BiomeGrid biomes)
	{
		double d0 = 0.03125D;

		this.t = this.o.a(this.t, chunkX * 16, chunkZ * 16, 0, 16, 16, 1,
				d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				int i1 = (int) (this.t[z + x * 16] / 3.0D + 3.0D + this.random
						.nextDouble() * 0.25D);
				int j1 = -1;

				Biome biome = biomes.getBiome(x, z);

				byte b1, b2;

				if (biome == Biome.DESERT)
				{
					b1 = (byte) Material.SAND.getId();
					b2 = (byte) Material.SAND.getId();
				} else if (biome == Biome.HELL)
				{
					b1 = (byte) Material.NETHERRACK.getId();
					b2 = (byte) Material.NETHERRACK.getId();
				} else
				{
					b1 = (byte) Material.GRASS.getId();
					b2 = (byte) Material.DIRT.getId();

					if (biome == Biome.SWAMPLAND)
					{
						biomes.setBiome(x, z, Biome.ICE_PLAINS);
					} else if (biome == Biome.MUSHROOM_ISLAND
							|| biome == Biome.MUSHROOM_SHORE)
					{
						biomes.setBiome(x, z, Biome.FOREST);
					}
				}

				for (int y = 127; y >= 0; --y)
				{
					int l1 = x * 16 + z;
					int i2 = l1 * 128 + y;

					byte b3 = blocks[i2];

					if (b3 == 0)
					{
						j1 = -1;
					} else if (b3 == Material.STONE.getId())
					{
						if (j1 == -1)
						{
							j1 = i1;
							blocks[i2] = b1;
						} else if (j1 > 0)
						{
							--j1;
							blocks[i2] = b2;

							if (j1 == 0 && b2 == Material.SAND.getId())
							{
								j1 = this.random.nextInt(4);
								b2 = (byte) Material.SANDSTONE.getId();
							}
						}
					}
				}
			}
		}
	}
	
	private void setupNoise()
	{
		this.random = new Random(world.getSeed());

		this.k = new NoiseGeneratorOctaves(this.random, 16);
		this.l = new NoiseGeneratorOctaves(this.random, 16);
		this.m = new NoiseGeneratorOctaves(this.random, 8);
		this.o = new NoiseGeneratorOctaves(this.random, 4);
		this.a = new NoiseGeneratorOctaves(this.random, 10);
		this.b = new NoiseGeneratorOctaves(this.random, 16);

		this.caveGen = new WorldGenCaves();
	}
	
	@Override
	public byte[][] generateBlockSections(org.bukkit.World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes)
	{
		if (this.random == null)
			setupNoise();

		net.minecraft.server.v1_4_R1.World mcWorld = ((CraftWorld) world).getHandle();

		byte[] blocks = new byte[32768];

		this.random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		this.shapeLand(world, chunkX, chunkZ, blocks);

		this.caveGen.a(mcWorld.chunkProvider, mcWorld, chunkX, chunkZ, blocks);

		this.decorateLand(chunkX, chunkZ, blocks, biomes);

		byte[][] chunk = new byte[8][4096];

		for (int x = 0; x < 16; ++x)
		{
			for (int y = 0; y < 128; ++y)
			{
				for (int z = 0; z < 16; ++z)
				{
					chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blocks[(x * 16 + z)  * 128 + y];
				}
			}
		}

		return chunk;
	}

	public byte[][][] generateBlockSectionsX(org.bukkit.World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes)
	{
		if (this.random == null)
			setupNoise();

		net.minecraft.server.v1_4_R1.World mcWorld = ((CraftWorld) world).getHandle();

		byte[] blocks = new byte[32768];

		this.random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		this.shapeLand(world, chunkX, chunkZ, blocks);

		this.caveGen.a(mcWorld.chunkProvider, mcWorld, chunkX, chunkZ, blocks);

		this.decorateLand(chunkX, chunkZ, blocks, biomes);

//		byte[][] chunk = new byte[8][4096];
//
//		for (int x = 0; x < 16; ++x)
//		{
//			for (int y = 0; y < 128; ++y)
//			{
//				for (int z = 0; z < 16; ++z)
//				{
//					chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blocks[(x * 16 + z)  * 128 + y];
//				}
//			}
//		}
		
		byte[][][] chunk = new byte[16][128][16];

		for (int x = 0; x < 16; ++x)
		{
			for (int y = 0; y < 128; ++y)
			{
				for (int z = 0; z < 16; ++z)
				{
					chunk[x][y][z] = blocks[(x * 16 + z)  * 128 + y];
				}
			}
		}

		return chunk;
	}
	
	public byte[][][] generateBlocks(int x, int z)
	{
		if (this.random == null)
			setupNoise();
		random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		CustomBiomeGrid biomegrid = new CustomBiomeGrid();
		biomegrid.biome = new BiomeBase[256];
		world.getWorldChunkManager().getBiomeBlock(biomegrid.biome, x << 4, z << 4, 16, 16);
		return generateBlockSectionsX(this.world.getWorld(), this.random, x, z, biomegrid);
	}

	public Chunk getOrCreateChunk(int x, int z)
	{
		if (this.random == null)
			setupNoise();
		random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

		Chunk chunk;

		// Get default biome data for chunk
		CustomBiomeGrid biomegrid = new CustomBiomeGrid();
		biomegrid.biome = new BiomeBase[256];
		world.getWorldChunkManager().getBiomeBlock(biomegrid.biome, x << 4, z << 4, 16, 16);

		byte[][] btypes = generateBlockSections(this.world.getWorld(), this.random, x, z, biomegrid);

		chunk = new Chunk(this.world, x, z);

		ChunkSection[] csect = chunk.i();
		int scnt = Math.min(csect.length, btypes.length);

		for (int sec = 0; sec < scnt; sec++)
		{
			if (btypes[sec] == null)
				continue;
			csect[sec] = new ChunkSection(sec << 4, true, btypes[sec], null);
		}

		// Set biome grid
		byte[] biomeIndex = chunk.m();
		for (int i = 0; i < biomeIndex.length; i++)
		{
			biomeIndex[i] = (byte) (biomegrid.biome[i].id & 0xFF);
		}
		// Initialize lighting
		chunk.initLighting();

		return chunk;
	}
	
	private static class CustomBiomeGrid implements BiomeGrid
	{
        BiomeBase[] biome;

        public Biome getBiome(int x, int z)
        {
            return CraftBlock.biomeBaseToBiome(biome[(z << 4) | x]);
        }

        public void setBiome(int x, int z, Biome bio)
        {
           biome[(z << 4) | x] = CraftBlock.biomeToBiomeBase(bio);
        }
    }
}