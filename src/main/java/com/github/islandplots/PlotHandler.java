package com.github.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.github.islandplots.utils.Coordinate;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class PlotHandler implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 3;

	private transient IslandPlots plugin;

	static final int PLOT_SIZE = 300;
	static final int PLOT_PADDING = 100;
	public static final int PLOT_PRICE = 12000;

	private String world;
	Table<Integer, Integer, Plot> plotGrid;

	//List of plots people have tried to delete, but they still need to type the command a second time to confirm the deletion.
	//Periodically cleared
	public List<Plot> needConfirmationUntiDeletion = new ArrayList<Plot>();
	//List of players trying to buy a new island. They must use the command a second time to confirm the purchase.
	//Periodically cleared
	public List<String> needConfirmationUntiPurchase = new ArrayList<String>();

	/**
	 * Empty constructor for externalization
	 */
	public PlotHandler()
	{
		plugin = IslandPlots.instance;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(IslandPlots.instance, new Runnable()
		{
			@Override
			public void run()
			{
				needConfirmationUntiDeletion.clear();
				needConfirmationUntiPurchase.clear();
				Stack<Plot> toRemove = new Stack<Plot>();
				for(Plot p : plotGrid.values())
				{
					if(p.shouldRemove())
					{
						toRemove.push(p);
						plugin.getEconomy().depositPlayer(p.getOwner(), PlotHandler.PLOT_PRICE);
					}
				}
				for(Plot p : toRemove) removePlot(p);
			}
		},240L, 3600L);
	}

	public PlotHandler(String world)
	{
		this();
		this.world = world;
		plotGrid = TreeBasedTable.create();
	}

	/**
	 * Creates a new plot in a random unoccupied position in the outermost unfilled ring of the giant grid of plots.
	 * The method automatically adds the new plot to the list of owned plots of the owner.
	 * @param owner	owner of the soon to be plot
	 * @return	the created plot
	 */
	public Plot appendPlot(String owner)
	{
		Plot plot = null;
		int ring = 0;
		while(plot == null)
		{
			for(Coordinate coord : getRingOfPlotPositions(ring++))
			{
				if(!plotGrid.contains(coord.x, coord.y))
				{
					plot = new Plot(world, owner, coord.x, coord.y);
					plotGrid.put(coord.x, coord.y, plot);
					break;
				}
			}
		}
		PlayerWrapper.getWrapper(owner).addPlot(plot);
		plugin.savePlayerWrappers(false);
		plugin.savePlotHandler(false);
		return plot;
	}

	public void removePlot(Plot plot)
	{
		if(plot.getRegion() != null)
			IslandPlots.instance.getRedProtect().getGlobalRegionManager().remove(plot.getRegion());
		PlayerWrapper.getWrapper(plot.getOwner()).removePlot(plot);
		plotGrid.remove(plot.getGridX(), plot.getGridY());
	}

	/**
	 * Get a plot by it's location in the world
	 * @param loc	location in world
	 * @return		the plot
	 */
	public Plot getPlot(Location loc)
	{
		int x = (int) loc.getX();
		int z = (int) loc.getZ();
		int xPosNeg = (x < 0) ? -2 : 2;
		int zPosNeg = (z < 0) ? -2 : 2;
		return getPlot((x + PLOT_SIZE/xPosNeg) / (PLOT_SIZE + PLOT_PADDING), (z + PLOT_SIZE/zPosNeg) / (PLOT_SIZE + PLOT_PADDING));
	}

	/**
	 * Get a plot by it's location in the grid
	 * @param x		grid x
	 * @param z		grid z
	 * @return		the plot
	 */
	public Plot getPlot(int x, int z)
	{
		return plotGrid.get(x, z);
	}

	public String getWorld()
	{
		return world;
	}

	private List<Coordinate> getRingOfPlotPositions(int currentRing)
	{
		List<Coordinate> ring = new ArrayList<Coordinate>();
		for(int x = -currentRing; x <= currentRing; x++)
		{
			ring.add(new Coordinate(x, currentRing));
			ring.add(new Coordinate(x, -currentRing));
		}
		for(int y = -currentRing; y <= currentRing; y++)
		{
			ring.add(new Coordinate(currentRing, y));
			ring.add(new Coordinate(-currentRing, y));
		}
		return ring;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		int ver = in.readInt();
		if(ver == 1)
		{
			world = in.readUTF();
			in.readInt();
			plotGrid = (Table<Integer, Integer, Plot>) in.readObject();
			in.readObject();
		} else if(ver == 2)
		{
			world = in.readUTF();
			in.readInt();
			plotGrid = (Table<Integer, Integer, Plot>) in.readObject();
			in.readObject();
			in.readObject();
		} else if(ver == 3)
		{
			world = in.readUTF();
			plotGrid = (Table<Integer, Integer, Plot>) in.readObject();
			for(Plot p : plotGrid.values())
			{
				PlayerWrapper pw = PlayerWrapper.getWrapper(p.getOwner());
				if(pw != null)
					pw.addPlot(p);
			}
		} else
		{
			IslandPlots.log(Level.WARNING, "Unsupported version of the PlotHandler failed to load.");
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(VERSION);

		out.writeUTF(world);
		out.writeObject(plotGrid);
	}
}
