package kabbage.islandplots.generation;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class PerlinNoise
{
	int prime;
	float width;
	float height;
	
	public PerlinNoise(long seed, float width, float height)
	{
		Random rnd = new SecureRandom();
		rnd.setSeed(seed);
		prime = BigInteger.probablePrime(16, rnd).intValue();
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Returns a random float between -1.0 and 1.0. I do not fully understand the algorithm, though it uses prime numbers.
	 * It will always generate the same float with the same inputs, assuming the 'prime' variable hasn't changed.
	 * The 'prime' variable is set to a random prime value based on the seed parameter in the constructor, and is not changed henceforth.
	 * The method is used as the base way of getting noise. It's values are smoothed and interpolated to get to the final product.
	 * @param x		x coordinate
	 * @param z		z coordinate
	 * @return		noise of coordinate
	 */
	float noise(float x, float z)
	{
		int n=(int)x+(int)z*57;
		n=(n<<13)^n;
		int nn=(n*(n*n*prime+19990303)+1376312589)&0x7fffffff;
		return (float) (1.0-((double)nn/1073741824.0));
	}


	/**
	 * Interpolates between four points. Uses cosine interpolation to smooth it out.
	 * @param a		first point
	 * @param b		second point
	 * @param c		third point
	 * @param d		fourth point
	 * @param muX	a value between 0.0 and 1.0 representing how far to interpolate between the points on the x axis.
	 * @param muZ	how far to interpolate between the points on the z axis
	 * @return		noise between four points based on the 'mu' parameter
	 */
	float interpolate(float a, float b, float c, float d, float muX, float muZ)
	{
		// Adjust mu so that it scales using cosine interpolation. This causes the possible values to curve as mu approaches 0.0 and 1.0
		muX *= Math.PI;
		muX = (float) ((1.0 - Math.cos(muX)) * 0.5);
		muZ *= Math.PI;
		muZ = (float) ((1.0 - Math.cos(muZ)) * 0.5);
		
		// Get the values between a and b, and c and d
		float betweenAB = (float) (a * (1.0 - muX) + b * muX);
		float betweenCD = (float) (c * (1.0 - muX) + d * muX);
		
		// Now use the above calculated values, and get the value between those to return are final result
		return (float) (betweenAB * (1.0 - muZ) + betweenCD * muZ);
	}
	
	/**
	 * Calculates the smoothed noise of a specified coordinate
	 * @param x		x coordinate
	 * @param z		z coordinate
	 * @return		noise of coordinate
	 */
	float smoothNoise(float x, float z)
	{
		float corners = (noise(x-1, z-1) + noise(x+1, z-1) + noise(x-1, z+1) + noise(x+1, z+1)) / 16;
		float sides = (noise(x-1, z) + noise(x+1, z) + noise(x, z-1) + noise(x, z+1)) / 8;
		float center = noise(x, z) / 4;

		return corners + sides + center;
	}
	
	/**
	 * Calculates the interpolated noise of a specified coordinate
	 * @param x		x coordinate
	 * @param z		z coordinate
	 * @return		noise of coordinate
	 */
	float interpolatedNoise(float x, float z)
	{
		// Floor the values
		int floorX = (int) Math.floor(x);
		int floorZ = (int) Math.floor(z);
		
		// Get the surrounding areas so we can interpolate the center, giving us a value that transitions from those areas
		float a = smoothNoise(floorX, floorZ);
		float b = smoothNoise(floorX + 1, floorZ);
		float c = smoothNoise(floorX, floorZ + 1);
		float d = smoothNoise(floorX + 1, floorZ + 1);
		
		// Interpolate them and return the result
		return interpolate(a, b, c, d, x - floorX, z - floorZ);
	}
	/**
	 * Adds an octave to existing noise
	 * @param x				x coordinate
	 * @param z				z coordinate
	 * @param noise			current noise
	 * @param persistence	persistence to use in calculation. Higher persistence means higher amplitude in higher octaves,
	 * 						which means more roughness. Conversely, lower persistence will result in less roughness. This should
	 * 						be a value between 0.0 and 1.0
	 * @param octaveNum		how many octaves already added to the noise. This parameter doesn't have to be accurate
	 * @return				resulting noise after an octave has been added to it
	 */
	public float addOctave(float x, float z, float noise, float persistence, float octaveNum)
	{
		// Increase frequency and decrease amplitude for each sucessive octave
		int frequency = (int) Math.pow(2, octaveNum);
		float amplitude = (float) Math.pow(persistence, octaveNum);

		// Add this octaves noise to the total noise
		noise += interpolatedNoise(x * frequency/15, z * frequency/15) * amplitude;
		return noise;
	}

	/**
	 * Calculates perlin noise for a specified coordinate
	 * @param x				x coordinate
	 * @param z				z coordinate
	 * @param octaves		number of octaves to calculate
	 * @param persistence	persistence to use in calculation. Higher persistence means higher amplitude in higher octaves,
	 * 						which means more roughness. Conversely, lower persistence will result in less roughness. This should
	 * 						be a value between 0.0 and 1.0
	 * @return				resulting noise of the coordinate
	 */
	public float perlinNoise(float x, float z, int octaves, float persistence)
	{
		// Total noise
		float noise = 0;
		for(int i = 0; i < octaves; i++)
		{
			// Increase frequency and decrease amplitude for each sucessive octave
			int frequency = (int) Math.pow(2, i);
			float amplitude = (float) Math.pow(persistence, i);
			
			// Add this octaves noise to the total noise
			noise += interpolatedNoise(x * frequency/15, z * frequency/15) * amplitude;
		}
		return noise;
	}
	
	public float islandNoise(float x, float z, int octaves, float persistence)
	{
		float noise = perlinNoise(x, z, octaves, persistence);
		float distFromCenterNormalized = (float) Math.sqrt(Math.pow(x / (width), 2) + Math.pow(z / (height), 2));
		distFromCenterNormalized = (float) Math.pow(distFromCenterNormalized, .75);	//Makes the effect increase much more as you get further away from center
		noise -= distFromCenterNormalized * 1.5;
		return noise;
	}
}
