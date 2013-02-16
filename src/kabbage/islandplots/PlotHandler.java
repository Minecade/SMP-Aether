package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import kabbage.islandplots.utils.Coordinate;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class PlotHandler implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	private String world;
	private Table<Integer, Integer, Plot> plotGrid;
	private int currentRing;
	private List<Coordinate> openPlots;	//List of plots opened up after having been removed
	
	/**
	 * Empty constructor for externalization
	 */
	public PlotHandler()
	{
		
	}
	
	public PlotHandler(String world)
	{
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
		}
		for(Coordinate coord : getRingOfPlotPositions())
		{
			if(!plotGrid.contains(coord.x, coord.y))
			{
				plot = new Plot(world, owner, coord.x, coord.y);
				plotGrid.put(coord.x, coord.y, plot);
				break;
			}
		}
		if(plot != null)
		{
			PlayerWrapper.getWrapper(owner).addPlot(plot);
			return plot;
		}
		// All of the plots in the current ring must be filled. Go to the next ring and try again
		currentRing++;
		return appendPlot(owner);
	}
	
	public void removePlot(Plot plot)
	{
		openPlots.add(plot.getGridLocation());
		plotGrid.remove(plot.getGridX(), plot.getGridX());
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
		IslandPlots.logger.log(Level.INFO, ring.toString());
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
		} else
		{
			IslandPlots.logger.log(Level.WARNING, "Unsupported version of the PlotHandler failed to load.");
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
	}
}
