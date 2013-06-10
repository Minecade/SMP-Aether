package com.github.islandplots.generation;

import java.util.concurrent.ArrayBlockingQueue;

import com.github.islandplots.IslandPlots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GenerationQueue
{
	IslandGenerator current;
	public ArrayBlockingQueue<IslandGenerator> queue;
	public GenerationQueue()
	{
		queue = new ArrayBlockingQueue<IslandGenerator>(8);
		Bukkit.getScheduler().runTaskTimer(IslandPlots.instance, new QueueHandler(), 20L, 20L);
	}

	public void add(IslandGenerator generator)
	{
		queue.offer(generator);
	}

	public boolean isFull()
	{
		return queue.remainingCapacity() == 0;
	}

	public boolean isEmpty()
	{
		return queue.isEmpty() && current == null;
	}

	class QueueHandler extends BukkitRunnable
	{
		@Override
		public void run()
		{
			if(current != null && !current.running)
				current = null;
			if(queue.isEmpty())
				return;
			if(current != null && current.running)
				return;
			current = queue.poll();
			Player player = Bukkit.getPlayer(current.player);
			if(player != null)
				player.sendMessage(ChatColor.GOLD+"Generation now beginning.");
			Bukkit.getScheduler().runTaskAsynchronously(IslandPlots.instance, current);
		}
	}
}
