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
    EXTRAPLOTS_2,
    EXTRAPLOTS_3,
    EXTRAPLOTS_4;
    
    @Override
    public String toString()
    {
        switch(this)
        {
            case ADMIN:
                return "island.admin";
            case EXTRAPLOTS_2:
                return "island.extraplots.2";
            case EXTRAPLOTS_3:
                return "island.extraplots.3";
            case EXTRAPLOTS_4:
                return "island.extraplots.4";
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
            pm.addPermission(new Permission(EXTRAPLOTS_2.toString()));
            pm.addPermission(new Permission(EXTRAPLOTS_3.toString()));
            pm.addPermission(new Permission(EXTRAPLOTS_4.toString()));
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
    	return hasExternalPermissions(player, Permissions.EXTRAPLOTS_4.toString(), true) || isAdmin(player) ? 4 : 
    		hasExternalPermissions(player, Permissions.EXTRAPLOTS_3.toString(), true) ? 3 :
    			hasExternalPermissions(player, Permissions.EXTRAPLOTS_2.toString(), true) ? 2 :
    				1;
    				
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
    	return hasExternalPermissions(sender, Permissions.EXTRAPLOTS_4.toString(), true) || isAdmin(sender) ? 4 : 
    		hasExternalPermissions(sender, Permissions.EXTRAPLOTS_3.toString(), true) ? 3 :
    			hasExternalPermissions(sender, Permissions.EXTRAPLOTS_2.toString(), true) ? 2 :
    				1;
    }
    
    public static boolean hasExternalPermissions(CommandSender sender, String node, boolean countOp)
    {
        return (sender instanceof ConsoleCommandSender) || (sender.isOp() && countOp) || sender.hasPermission(node);
    }
}
