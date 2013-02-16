package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.logging.Level;

import kabbage.islandplots.generation.Island;
import kabbage.islandplots.utils.Coordinate;

public class Plot implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	String world;
	private String owner;
	private List<String> members;
	private int plotSize = 100;
	private int x;
	private int y;
	private Island island;
	
	public Plot(String world, String owner, int gridX, int gridY)
	{
		this.world = world;
		this.owner = owner;
		x = gridX;
		y = gridY;
		
		island = new Island(world, getX(), 80, getZ());
		island.generate();
	}
	
	public Plot(String world, String owner, Coordinate coord)
	{
		this(world, owner, coord.x, coord.y);
	}
	
	/**
	 * Gets the plots coordinates on the plot grid
	 * @return	coord
	 */
	public Coordinate getGridLocation()
	{
		return new Coordinate(x, y);
	}
	
	/**
	 * Gets the plots position on the x axis on the plot grid
	 * @return	x
	 */
	public int getGridX()
	{
		return x;
	}
	
	/**
	 * Gets the plots position on the y axis on the plot grid
	 * @return	y
	 */
	public int getGridY()
	{
		return y;
	}
	
	/**
	 * Gets the x coordinate the plots center lies in, in the world
	 * @return	z
	 */
	public int getX()
	{
		return x * plotSize;
	}
	
	/**
	 * Gets the z coordinate the plots center lies in, in the world
	 * @return	z
	 */
	public int getZ()
	{
		return y * plotSize;
	}
	
	/**
	 * Get the island occupying the plot
	 * @return	island
	 */
	public Island getIsland()
	{
		return island;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		int ver = in.readInt();
		if(ver == 1)
		{
			world = in.readUTF();
			owner = in.readUTF();
			members = (List<String>) in.readObject();
			plotSize = in.readInt();
			x = in.readInt();
			y = in.readInt();
			island = (Island) in.readObject();
		} else
		{
			IslandPlots.logger.log(Level.WARNING, "Unsupported version of a Plot failed to load.");
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(VERSION);
		
		out.writeUTF(world);
		out.writeUTF(owner);
		out.writeObject(members);
		out.writeInt(plotSize);
		out.writeInt(x);
		out.writeInt(y);
		out.writeObject(island);
	}
}
