package kabbage.islandplots.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

public enum Permissions
{
	/**
     * The node for admin commands. You get all below permissions if you have this
     */
    ADMIN,
    /**
     * Players who have permission for extra plots (5 specifically, as opposed to the default 2)
     */
    EXTRAPLOTS;
    
    @Override
    public String toString()
    {
        switch(this)
        {
            case ADMIN:
                return "island.admin";
            case EXTRAPLOTS:
                return "island.extraplots";
            default:
            	return null;
        }
    }

    /**
     * Registers all permissions in the plugin manager
     * @param pm the plugin manager in which to register permissions
     */
    public static void registerPermNodes(PluginManager pm) {
        try
        {
            pm.addPermission(new Permission(ADMIN.toString()));
            pm.addPermission(new Permission(EXTRAPLOTS.toString()));
        } catch (Exception e)
        {
        }

    }
    
    public static boolean isAdmin(Player player)
    {
    	return hasExternalPermissions(player, Permissions.ADMIN.toString(), true);
    }
    
    public static int maxPlots(Player player)
    {
    	return hasExternalPermissions(player, Permissions.EXTRAPLOTS.toString(), true) || isAdmin(player) ? 5 : 2;
    }
    
    public static boolean hasExternalPermissions(Player player, String node, boolean countOp)
    {
        return (player.isOp() && countOp) || player.hasPermission(node);
    }
    
    public static boolean isAdmin(CommandSender sender)
    {
    	return hasExternalPermissions(sender, Permissions.ADMIN.toString(), true);
    }
    
    public static int maxPlots(CommandSender sender)
    {
    	return hasExternalPermissions(sender, Permissions.EXTRAPLOTS.toString(), true) || isAdmin(sender) ? 5 : 2;
    }
    
    public static boolean hasExternalPermissions(CommandSender sender, String node, boolean countOp)
    {
        return (sender instanceof ConsoleCommandSender) || (sender.isOp() && countOp) || sender.hasPermission(node);
    }
}
