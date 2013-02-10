package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import kabbage.islandplots.utils.Coordinate;

public class Plot implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	public static int plotSize = 100;
	
	private String owner;
	private List<String> members;
	private int x;
	private int y;
	
	public Plot(String owner, int centerX, int centerY)
	{
		this.owner = owner;
		x = centerX;
		y = centerY;
		
		new Island(x, 80, y).generate();
	}
	
	public Plot(String owner, Coordinate coord)
	{
		this(owner, coord.x, coord.y);
	}
	
	public Coordinate getLocation()
	{
		return new Coordinate(x, y);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
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
