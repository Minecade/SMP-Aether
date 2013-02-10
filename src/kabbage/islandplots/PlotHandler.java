package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class PlotHandler implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	
	private String world;
	private Table<Integer, Integer, Plot> plotGrid;
	private int currentRing;
	
	public PlotHandler(String world)
	{
		this.world = world;
		plotGrid = TreeBasedTable.create();
		currentRing = 0;
	}
	
	/**
	 * Creates a new plot in a random unoccupied position in the outermost unfilled ring of the giant grid of plots
	 * @param owner	owner of the soon to be plot
	 * @return		the created plot
	 */
	public Plot appendPlot(String owner)
	{
		for(Entry<Integer, Integer> coord : getRingOfPlotPositions())
		{
			if(!plotGrid.contains(coord.getKey(), coord.getValue()))
			{
				Plot plot = new Plot(owner, coord.getKey(), coord.getValue());
				return plotGrid.put(coord.getKey(), coord.getValue(), plot);
			}
		}
		// All of the plots in the current ring must be filled. Go to the next ring and try again
		currentRing++;
		return appendPlot(owner);
	}
	
	/**
	 * Gets the coordinates of the plots that occupy the outermost unfilled ring of the grid
	 * @return	map of coordinates
	 */
	private List<Entry<Integer, Integer>> getRingOfPlotPositions()
	{
		List<Entry<Integer, Integer>> ring = new ArrayList<>();
		for(int x = -currentRing; x <= currentRing; x++)
		{
			ring.add(new AbstractMap.SimpleEntry<>(x, currentRing));
			ring.add(new AbstractMap.SimpleEntry<>(x, -currentRing));
		}
		for(int y = -currentRing; y <= currentRing; y++)
		{
			ring.add(new AbstractMap.SimpleEntry<>(currentRing, y));
			ring.add(new AbstractMap.SimpleEntry<>(-currentRing, y));
		}
		return ring;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		
	}
}
