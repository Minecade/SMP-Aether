package com.github.islandplots.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

	/**
	 * Determines whether or not the player has a website account.
	 * @param playerName	name of the player
	 * @return				true if player has account, else false
	 */
	public static boolean hasWebsiteAccount(String playerName)
	{
		return true;	//TODO
	}

	/**
	 * Checks if two text files contain the same contents.
	 * @param file1	first file being compared
	 * @param file2	second file being compared
	 * @return		whether or not the two files contain the same contents
	 */
	public static boolean fileEquals(File file1, File file2)
	{
		if(!file1.exists() || !file2.exists())
			return false;
		try
		{
			new BufferedReader(new InputStreamReader(System.in));
			String s1 = "";
			String s2 = "";
			String y = "", z = "";

			BufferedReader bfr1 = new BufferedReader(new FileReader(file1));

			BufferedReader bfr2 = new BufferedReader(new FileReader(file2));

			while ((y = bfr1.readLine()) != null)
				s1 += y;

			while ((z = bfr2.readLine()) != null)
				s2 += z;

			bfr1.close();
			bfr2.close();

			if (s2.equals(s1))
				return true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
