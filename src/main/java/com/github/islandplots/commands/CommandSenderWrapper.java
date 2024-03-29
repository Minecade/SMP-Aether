package com.github.islandplots.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author joshua
 */
public class CommandSenderWrapper
{
    public static final String INSUF_PERM_MSG = "Insufficient permission.";
    protected CommandSender sender;
    protected Player player;
    protected boolean console;

    public CommandSenderWrapper(CommandSender sender)
    {
        this.sender = sender;

        player = (sender instanceof Player ? (Player) sender : null);

        console = !(sender instanceof Player);
    }



    /**
     * Returns the player representation of the sender.
     *
     * @return the Player, or null if the sender is not a player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Returns the CommandSender that this class wraps
     *
     * @return
     */
    public CommandSender getSender()
    {
        return sender;
    }

    /**
     *
     * @return whether or not the CommandSender is the console
     */
    public boolean isConsole()
    {
        return console;
    }

    /**
     * Sends the user a message indicating that he does not have sufficent
     * permission.
     */
    public void notifyInsufPermissions()
    {
        sender.sendMessage(ChatColor.RED + INSUF_PERM_MSG);
    }

    /**
     * Sends the sender a message.
     *
     * @param msg The message to be send
     */
    public void sendMessage(String msg)
    {
        sender.sendMessage(msg);
    }
}
