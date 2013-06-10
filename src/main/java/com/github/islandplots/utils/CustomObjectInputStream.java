package com.github.islandplots.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Used for converting serializable classes from the old package hierarchy to the new one
 */
public class CustomObjectInputStream extends ObjectInputStream
{
	public CustomObjectInputStream(InputStream in) throws IOException
	{
		super(in);
	}

	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException
	{
		ObjectStreamClass read = super.readClassDescriptor();
	    if (read.getName().startsWith("kabbage.islandplots."))
	        return ObjectStreamClass.lookup(Class.forName(read.getName().replace("kabbage.islandplots.", "com.github.islandplots.")));
	    return read;
	}
}