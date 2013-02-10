package kabbage.islandplots.utils;

/**
 * Very simple class for handling the coordinates of plots
 */
public class Coordinate
{
	public int x;
	public int y;
	
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Coordinate))
			return false;
		Coordinate other = (Coordinate) obj;
		return other.x == x && other.y == y;
	}
	
	@Override
	public String toString()
	{
		return "["+x+", "+y+"]";
	}
}
