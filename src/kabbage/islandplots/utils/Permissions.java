package kabbage.islandplots.utils;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

public enum Permissions
{
	/**
     * The node for admin commands. You get all below permissions if you have this
     */
    ADMIN,
    /**
     * Players who have extended permissions from a normal user
     */
    VIP;
    
    @Override
    public String toString()
    {
        switch(this)
        {
            case ADMIN:
                return "island.admin";
            case VIP:
                return "island.vip";
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
            pm.addPermission(new Permission(VIP.toString()));
        } catch (Exception e)
        {
        }

    }
}
