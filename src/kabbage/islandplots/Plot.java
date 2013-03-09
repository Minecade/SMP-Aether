package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.OverCaste.plugin.RedProtect.DefineRegionBuilder;
import com.OverCaste.plugin.RedProtect.Region;
import com.OverCaste.plugin.RedProtect.RegionBuilder;

import kabbage.islandplots.generation.Island;
import kabbage.islandplots.utils.Coordinate;

public class Plot implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 3;
	
	String world;
	private String owner;
	private List<String> members;
	private int x;
	private int y;
	private Island island;
	private Region region;
	private int level;
	private int blockChanges;
	
	/**
	 * Empty constructor for externalization
	 */
	public Plot() {}
	
	Plot(String world, String owner, int gridX, int gridY)
	{
		this.world = world;
		this.owner = owner;
		members = new ArrayList<String>();
		x = gridX;
		y = gridY;
		level = 0;
		blockChanges = 0;
		
		Location topCorner = new Location(Bukkit.getWorld(world), getX() + PlotHandler.PLOT_SIZE / 2 - 1, 256, getZ() + PlotHandler.PLOT_SIZE / 2 - 1);
		Location bottomCorner = new Location(Bukkit.getWorld(world), getX() - PlotHandler.PLOT_SIZE / 2, 0, getZ() - PlotHandler.PLOT_SIZE / 2);
		RegionBuilder builder = new DefineRegionBuilder(Bukkit.getPlayer(owner), topCorner, bottomCorner, "Plot "+gridX+":"+gridY, owner);
		region = builder.build();
		IslandPlots.instance.getRedProtect().getGlobalRegionManager().add(region, Bukkit.getWorld(world));
		
		island = new Island(world, getX(), 80, getZ());
		island.generate(owner);
	}
	
	Plot(String world, String owner, Coordinate coord)
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
		return x * PlotHandler.PLOT_SIZE + x * PlotHandler.PLOT_PADDING;
	}
	
	/**
	 * Gets the z coordinate the plots center lies in, in the world
	 * @return	z
	 */
	public int getZ()
	{
		return y * PlotHandler.PLOT_SIZE + y * PlotHandler.PLOT_PADDING;
	}
	
	/**
	 * Get the island occupying the plot
	 * @return	island
	 */
	public Island getIsland()
	{
		return island;
	}
	
	/**
	 * Get the region protecting the plot
	 * @return	region
	 */
	public Region getRegion()
	{
		return region;
	}
	
	public void registerBlockChange()
	{
		blockChanges++;
		if(blockChanges % 25 == 0)
		{
			if(level < 5)
			{
				if(blockChanges >= 50*level*level + 25)
					level++;
			} else
			{
				
			}
		}
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public List<String> getMembers()
	{
		return members;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	@Override
	public String toString()
	{
		return "Plot: ["+x+", "+y+"]";
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
			x = in.readInt();
			y = in.readInt();
			island = (Island) in.readObject();
			level = 0;
		} else if(ver == 2)
		{
			world = in.readUTF();
			owner = in.readUTF();
			members = (List<String>) in.readObject();
			in.readInt();
			in.readInt();
			x = in.readInt();
			y = in.readInt();
			island = (Island) in.readObject();
			level = 0;
		} else if(ver == 3)
		{
			world = in.readUTF();
			owner = in.readUTF();
			members = (List<String>) in.readObject();
			x = in.readInt();
			y = in.readInt();
			island = (Island) in.readObject();
			level = in.readInt();
			blockChanges = in.readInt();
		}
		else
			IslandPlots.log(Level.WARNING, "Unsupported version of a Plot failed to load.");
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(VERSION);
		
		out.writeUTF(world);
		out.writeUTF(owner);
		out.writeObject(members);
		out.writeInt(x);
		out.writeInt(y);
		out.writeObject(island);
		out.writeInt(level);
		out.writeInt(blockChanges);
	}
}
