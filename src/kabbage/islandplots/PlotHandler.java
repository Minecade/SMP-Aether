package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import kabbage.islandplots.utils.Coordinate;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class PlotHandler implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 2;
	
	static final int PLOT_SIZE = 300;
	static final int PLOT_PADDING = 100;
	
	private String world;
	private Table<Integer, Integer, Plot> plotGrid;
	private int currentRing;
	private List<Coordinate> openPlots;	//List of plots opened up after having been removed
	
	private Map<Plot, Integer> toDeletion;	//Map of level 0 plots to their time until deleting (in minutes)
	
	/**
	 * Empty constructor for externalization
	 */
	public PlotHandler()
	{
		//Every minute, plots scheduled to be deleted are set one minute closer to being deleted. If they leveled up, proving they aren't
		//inactive, they get removed from the map of plots to be deleted. If they are 0 minutes away from deletion, delete them.
		Bukkit.getScheduler().scheduleSyncRepeatingTask(IslandPlots.instance, new Runnable()
		{
			@Override
			public void run()
			{
				Stack<Plot> toRemoveFromDelete = new Stack<Plot>();
				Stack<Plot> toRemove = new Stack<Plot>();
				for(Entry<Plot, Integer> e : toDeletion.entrySet())
				{
					if(e.getKey().getLevel() > 0)
						toRemoveFromDelete.add(e.getKey());
					else
					{
						e.setValue(e.getValue() - 1);
						if(e.getValue() <= 0)
							toRemove.add(e.getKey());
					}
					
				}
				for(Plot p : toRemoveFromDelete) toDeletion.remove(p);
				for(Plot p : toRemove) removePlot(p);
			}
		},1200L, 1200L);
	}
	
	public PlotHandler(String world)
	{
		this();
		this.world = world;
		plotGrid = TreeBasedTable.create();
		currentRing = 0;
		openPlots = new ArrayList<Coordinate>();
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
		if(!openPlots.isEmpty())
		{
			Coordinate coord = openPlots.remove(0);
			plot = new Plot(world, owner, coord);
			plotGrid.put(coord.x, coord.y, plot);
		} else
		{
			for(Coordinate coord : getRingOfPlotPositions())
			{
				if(!plotGrid.contains(coord.x, coord.y))
				{
					plot = new Plot(world, owner, coord.x, coord.y);
					plotGrid.put(coord.x, coord.y, plot);
					break;
				}
			}
		}
		if(plot != null)
		{
			PlayerWrapper.getWrapper(owner).addPlot(plot);
			toDeletion.put(plot, 1440);
			return plot;
		}
		// All of the plots in the current ring must be filled. Go to the next ring and try again
		currentRing++;
		return appendPlot(owner);
	}
	
	public void removePlot(Plot plot)
	{
		IslandPlots.instance.getRedProtect().getGlobalRegionManager().remove(plot.getRegion());
		openPlots.add(plot.getGridLocation());
		plotGrid.remove(plot.getGridX(), plot.getGridX());
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
	
	/**
	 * Gets the coordinates of the plots that occupy the outermost unfilled ring of the grid
	 * @return	map of coordinates
	 */
	private List<Coordinate> getRingOfPlotPositions()
	{
		List<Coordinate> ring = new ArrayList<>();
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
			currentRing = in.readInt();
			plotGrid = (Table<Integer, Integer, Plot>) in.readObject();
			openPlots = (List<Coordinate>) in.readObject();
			toDeletion = new HashMap<Plot, Integer>();
		} else if(ver == 2)
		{
			world = in.readUTF();
			currentRing = in.readInt();
			plotGrid = (Table<Integer, Integer, Plot>) in.readObject();
			openPlots = (List<Coordinate>) in.readObject();
			toDeletion = (Map<Plot, Integer>) in.readObject();
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
		out.writeInt(currentRing);
		out.writeObject(plotGrid);
		out.writeObject(openPlots);
		out.writeObject(toDeletion);
	}
}
