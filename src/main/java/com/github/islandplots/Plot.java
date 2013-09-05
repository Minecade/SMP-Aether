package com.github.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.OverCaste.plugin.RedProtect.DefineRegionBuilder;
import com.OverCaste.plugin.RedProtect.Region;
import com.OverCaste.plugin.RedProtect.RegionBuilder;
import com.OverCaste.plugin.RedProtect.RegionManager;

import com.github.islandplots.utils.Coordinate;

public class Plot implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 4;

	private static Map<Material, Integer> blockWorths;
	private static Map<Material, Integer> blockBreakWorths;

	static
	{
		blockWorths = new HashMap<Material, Integer>();
		blockBreakWorths = new HashMap<Material, Integer>();

		blockWorths.put(Material.REDSTONE_BLOCK, 50);
		blockWorths.put(Material.IRON_BLOCK, 100);
		blockWorths.put(Material.LAPIS_BLOCK, 100);
		blockWorths.put(Material.GOLD_BLOCK, 200);
		blockWorths.put(Material.EMERALD_BLOCK, 400);
		blockWorths.put(Material.DIAMOND_BLOCK, 750);
		blockWorths.put(Material.COBBLESTONE, 4);
		blockWorths.put(Material.COBBLESTONE_STAIRS, 4);
		blockWorths.put(Material.SANDSTONE_STAIRS, 8);
		blockWorths.put(Material.WOOD, 8);
		blockWorths.put(Material.WOOD_STAIRS, 8);
		blockWorths.put(Material.BOOKSHELF, 40);
		blockWorths.put(Material.GLASS, 20);
		blockWorths.put(Material.BRICK, 40);
		blockWorths.put(Material.QUARTZ_BLOCK, 100);
		blockWorths.put(Material.QUARTZ_STAIRS, 100);
		blockWorths.put(Material.WOOL, 10);
		blockWorths.put(Material.SMOOTH_BRICK, 32);
		blockWorths.put(Material.SMOOTH_STAIRS, 32);
		blockWorths.put(Material.WOOD_STEP, 4);
		blockWorths.put(Material.STEP, 4);

		blockBreakWorths.put(Material.COAL_ORE, 20);
		blockBreakWorths.put(Material.REDSTONE_ORE, 40);
		blockBreakWorths.put(Material.IRON_ORE, 40);
		blockBreakWorths.put(Material.QUARTZ_ORE, 40);
		blockBreakWorths.put(Material.GOLD_ORE, 75);
		blockBreakWorths.put(Material.LAPIS_ORE, 75);
		blockBreakWorths.put(Material.EMERALD_ORE, 100);
		blockBreakWorths.put(Material.DIAMOND_ORE, 200);
		blockBreakWorths.put(Material.STONE, 1);
		blockBreakWorths.put(Material.SAND, 1);
		blockBreakWorths.put(Material.SANDSTONE, 2);
		blockBreakWorths.put(Material.LOG, 3);
		blockBreakWorths.put(Material.WHEAT, 15);
		blockBreakWorths.put(Material.MELON_BLOCK, 25);
		blockBreakWorths.put(Material.POTATO, 15);
		blockBreakWorths.put(Material.CARROT, 15);
		blockBreakWorths.put(Material.COCOA, 25);
		blockBreakWorths.put(Material.OBSIDIAN, 25);
	}

	String world;
	private String owner;
	private List<String> members;
	private int x;
	private int y;
	private Island island;
	private Region region;
	private int level;
	private int wealth;
	private long creationTime;

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
		wealth = 0;
		creationTime = new Date().getTime();

		buildRegion();

		island = new Island(this, world, getX(), 80, getZ());
		island.generate(owner);
	}

	Plot(String world, String owner, Coordinate coord)
	{
		this(world, owner, coord.x, coord.y);
	}

	public void addMember(String name)
	{
		members.add(name);
		getRegion().addMember(name);
	}

	private void buildRegion()
	{
		RegionManager manager = IslandPlots.instance.getRedProtect().getGlobalRegionManager();
		Region current = manager.getRegion(new Location(Bukkit.getWorld(world), getX(), 64, getZ()));
		if(current != null)
			manager.remove(current);
		Location topCorner = new Location(Bukkit.getWorld(world), getX() + PlotHandler.PLOT_SIZE / 2 - 1, 256, getZ() + PlotHandler.PLOT_SIZE / 2 - 1);
		Location bottomCorner = new Location(Bukkit.getWorld(world), getX() - PlotHandler.PLOT_SIZE / 2, 0, getZ() - PlotHandler.PLOT_SIZE / 2);
		RegionBuilder builder = new RemoteRegionBuilder(topCorner, bottomCorner, "Plot "+x+":"+y, owner);
		region = builder.build();
		try
		{
			manager.add(region, Bukkit.getWorld(world));
		} catch(NullPointerException e)
		{}
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

	public int getLevel()
	{
		return level;
	}

	public List<String> getMembers()
	{
		return new ArrayList<String>(members);
	}

	public int getNextWealth()
	{
		return (int) (100*Math.pow(level, 2) + 50);
	}

	public String getOwner()
	{
		return owner;
	}

	/**
	 * Get the region protecting the plot
	 * @return	region
	 */
	public Region getRegion()
	{
		if(region == null)
			region = IslandPlots.instance.getRedProtect().getGlobalRegionManager().getRegion(new Location(Bukkit.getWorld(world), getX(), 64, getZ()));
		if(region == null)
			buildRegion();
		return region;
	}

	public Location getSpawnPoint()
	{
		return island.getSpawnPoint();
	}

	public int getWealth()
	{
		return wealth;
	}

	private void levelUp()
	{
		level++;
		Player player = Bukkit.getPlayer(owner);
		if(player != null)
			player.sendMessage(ChatColor.GOLD+"Your plot, "+this+", has leveled up. It is now level "+level+"!");
	}

	public void registerBlockChange(Material type, boolean place)
	{
		if(place)
		{
			if(blockWorths.containsKey(type))
				wealth += blockWorths.get(type);
		} else
		{
			if(blockBreakWorths.containsKey(type))
				wealth += blockBreakWorths.get(type);
			else if(blockWorths.containsKey(type))
				wealth -= blockWorths.get(type);
		}
		if(wealth >= getNextWealth())
			levelUp();
	}

	public void removeMember(String name)
	{
		members.remove(name);
		getRegion().removeMember(name);
	}

	/**
	 * Whether or not this plot should be removed due to being abandoned. Based off of the current level
	 * of the plot, and how long it's been in existence.
	 * @return	whether or not to remove
	 */
	public boolean shouldRemove()
	{
		long timeExisted = new Date().getTime() - creationTime;
		long hoursExisted = TimeUnit.MILLISECONDS.toHours(timeExisted);
		if(hoursExisted >= 50 * 24)
		{
			if(level < 20) return true;
		} else if(hoursExisted >= 15 * 24)
		{
			if(level < 10) return true;
		} else if(hoursExisted >= 5 * 24)
		{
			if(level < 5) return true;
		}
		return false;
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
			wealth = in.readInt();
			creationTime = new Date().getTime();
		} else if(ver == 4)
		{
			world = in.readUTF();
			owner = in.readUTF();
			members = (List<String>) in.readObject();
			x = in.readInt();
			y = in.readInt();
			island = (Island) in.readObject();
			level = in.readInt();
			wealth = in.readInt();
			creationTime = in.readLong();
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
		out.writeInt(wealth);
		out.writeLong(creationTime);
	}
}
