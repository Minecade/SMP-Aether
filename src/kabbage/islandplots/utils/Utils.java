package kabbage.islandplots.utils;

public class Utils
{
	/**
	 * Parse a String to a int
	 * @param toParse	string to be parsed
	 * @param defaultValue	default value to return if 'toParse' is not parsable
	 * @return	int value of 'toParse', or 'defaultValue' if it is not parsable
	 */
	public static int parseInt(String toParse, int defaultValue)
	{
		try
		{
			return Integer.parseInt(toParse);
		} catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * Parse a String to a double
	 * @param toParse	string to be parsed
	 * @param defaultValue	default value to return if 'toParse' is not parsable
	 * @return	double value of 'toParse', or 'defaultValue' if it is not parsable
	 */
	public static double parseDouble(String toParse, double defaultValue)
	{
		try
		{
			return Double.parseDouble(toParse);
		} catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}
}
