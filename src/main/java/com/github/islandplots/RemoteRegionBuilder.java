package com.github.islandplots;

import static org.bukkit.ChatColor.RED;

import java.lang.reflect.Field;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Bukkit;

import com.OverCaste.plugin.RedProtect.RedProtect;
import com.OverCaste.plugin.RedProtect.Region;
import com.OverCaste.plugin.RedProtect.RegionBuilder;

public class RemoteRegionBuilder extends RegionBuilder
{
	public RemoteRegionBuilder(Location loc1, Location loc2, String regionName, String creator)
	{
		creator = creator.toLowerCase();
		if (regionName.equals(""))
		{
			for(int i = 0; true; i++)
			{
				if(creator.length() > 13)
				{
					regionName = creator.substring(0, 13) + "_" + i;
				} else
				{
					regionName = creator + "_" + i;
				}
				if (IslandPlots.instance.getRedProtect().getGlobalRegionManager().getRegion(regionName, loc1.getWorld()) == null)
				{
					if(regionName.length() > 16)
					{
						Bukkit.getConsoleSender().sendMessage(RED + "Couldn't generate automatic region regionName, please regionName it yourself.");
						return;
					}
					break;
				}
			}
		}
		if((loc1 == null)||(loc2 == null))
		{
			Bukkit.getConsoleSender().sendMessage(RED + "One or both of your selection positions aren't set!");
			return;
		}
		if (IslandPlots.instance.getRedProtect().getGlobalRegionManager().getRegion(regionName, loc1.getWorld()) != null)
		{
			Bukkit.getConsoleSender().sendMessage(RED + "That regionName is already taken, please choose another one.");
			return;
		}
		if((regionName.length() < 2) || (regionName.length() > 16))
		{
			Bukkit.getConsoleSender().sendMessage(RED + "Invalid regionName, place a 2-16 character regionName in the 2nd row.");
			return;
		}
		int minX, minZ, maxX, maxZ;
		if(loc2.getBlockX() < loc1.getBlockX())
		{
			minX = loc2.getBlockX();
			maxX = loc1.getBlockX();
		} else
		{
			maxX = loc2.getBlockX();
			minX = loc1.getBlockX();
		}
		if(loc2.getBlockZ() < loc1.getBlockZ())
		{
			minZ = loc2.getBlockZ();
			maxZ = loc1.getBlockZ();
		} else
		{
			maxZ = loc2.getBlockZ();
			minZ = loc1.getBlockZ();
		}
		for(int xl = minX; xl<=maxX; xl++)
		{
			if (IslandPlots.instance.getRedProtect().getGlobalRegionManager().regionExists(xl, minZ, loc1.getWorld())||IslandPlots.instance.getRedProtect().getGlobalRegionManager().regionExists(xl, maxZ, loc1.getWorld()))
			{
				Bukkit.getConsoleSender().sendMessage(RED + "You're overlapping another region.");
				return;
			}
		}
		for(int zl = minZ; zl<=maxZ; zl++)
		{
			if (IslandPlots.instance.getRedProtect().getGlobalRegionManager().regionExists(minX, zl, loc1.getWorld())||IslandPlots.instance.getRedProtect().getGlobalRegionManager().regionExists(maxX, zl, loc1.getWorld()))
			{
				Bukkit.getConsoleSender().sendMessage(RED + "You're overlapping another region.");
				return;
			}
		}
		LinkedList<String> owners = new LinkedList<String>();
		owners.add(creator);
		Region r = new Region(regionName, owners, new int[] {loc1.getBlockX(), loc1.getBlockX(), loc2.getBlockX(), loc2.getBlockX()}, new int[] {loc1.getBlockZ(), loc1.getBlockZ(), loc2.getBlockZ(), loc2.getBlockZ()});
		if (IslandPlots.instance.getRedProtect().getGlobalRegionManager().isSurroundingRegion(r, loc1.getWorld()))
		{
			Bukkit.getConsoleSender().sendMessage(RED + "You're overlapping another region.");
			return;
		}
		try
		{
			Field f = getClass().getDeclaredField("r");
			f.setAccessible(true);
			f.set(this, r);
		} catch(Exception e) {e.printStackTrace();}
	}
}