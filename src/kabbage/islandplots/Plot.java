package kabbage.islandplots;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Plot implements Externalizable
{
	private static final long serialVersionUID = "PLAYERWRAPPER".hashCode();
	private static final int VERSION = 1;
	public static int plotSize = 100;
	
	private String owner;
	private List<String> members;
	private double x;
	private double z;
	
	public Plot(String owner, double centerX, double centerZ)
	{
		this.owner = owner;
		x = centerX;
		z = centerZ;
		
		new Island(x, 80, z).generate();
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
