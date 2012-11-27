package com.mrmag518.HideStream;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    public static HideStream plugin;
    public EventManager(HideStream instance) {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    
    @EventHandler
    public void joinManagement(PlayerJoinEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player joiner = event.getPlayer();
        boolean perPlayerToggle = plugin.getConfig().getBoolean("EnablePerPlayerToggle");
        boolean OPSupport = plugin.getConfig().getBoolean("Join.OPSupport.Enabled");
        
        if(perPlayerToggle == true) 
        {
            if(StreamDB.getHiddenState(joiner.getName()) == true) 
            {
                plugin.debugLog("perPlayerToggle is true in the config.");
                event.setJoinMessage(null);
            }
        }
        else if(plugin.getConfig().getBoolean("Join.HideJoinStream")) 
        {
            plugin.debugLog("Join.HideJoinStream was true in the config, disabling join stream ..");
            
            if(plugin.getConfig().getBoolean("Join.UsePermissions") == true) 
            {
                plugin.debugLog("Join.UsePermissions was true in the config, using permissions ..");
                
                if(plugin.getConfig().getBoolean("Join.OnlyHideForUsersWithPermission") == true) 
                {
                    plugin.debugLog("Join.OnlyHideForUsersWithPermission was true in the config.");
                    
                    if(plugin.hasPermission(joiner, "hidestream.hidejoin")) 
                    {
                        event.setJoinMessage(null);
                        plugin.debugLog(joiner.getName() + " had permission hidestream.hidejoin, disabled join message.");
                    }
                } 
                else if(plugin.getConfig().getBoolean("Join.OnlyHideForUsersWithoutPermission") == true) 
                {
                    
                    plugin.debugLog("Join.OnlyHideForUsersWithoutPermission was true in the config.");
                    if(!(plugin.hasPermission(joiner, "hidestream.hidejoin"))) 
                    {
                        event.setJoinMessage(null);
                        plugin.debugLog(joiner.getName() + " did not have permission hidestream.hidejoin, disabled join message.");
                    }
                } else {
                    plugin.debugLog("Error: Nor OnlyHideForUsersWithPermission or OnlyHideForUsersWithoutPermission in stream category join is set to true.");
                }
            } else {
                if(OPSupport == true) {
                    if(plugin.getConfig().getBoolean("Join.OPSupport.OnlyHideIfNotOP") == true) {
                        if(!joiner.isOp()) {
                            event.setJoinMessage(null);
                            plugin.debugLog(joiner.getName() + " is not op, disabled join message. (OnlyHideIfNotOP = true in config)");
                        }
                    } else if(plugin.getConfig().getBoolean("Join.OPSupport.OnlyHideIfOP") == true) {
                        if(joiner.isOp()) {
                            event.setJoinMessage(null);
                            plugin.debugLog(joiner.getName() + " is op, disabled join message. (OnlyHideIfOP = true in config)");
                        }
                    } else {
                        plugin.debugLog("Error: Nor OnlyHideIfNotOP or OnlyHideIfOP in stream category quit is set to true.");
                    }
                } else {
                    event.setJoinMessage(null);
                }
            }
        }
    }
    
    @EventHandler
    public void quitManagement(PlayerQuitEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player leaver = event.getPlayer();
        boolean perPlayerToggle = plugin.getConfig().getBoolean("EnablePerPlayerToggle");
        boolean OPSupport = plugin.getConfig().getBoolean("Quit.OPSupport.Enabled");
        
        if(perPlayerToggle == true) 
        {
            if(StreamDB.getHiddenState(leaver.getName()) == true)
            {
                plugin.debugLog("perPlayerToggle is true in the config.");
                event.setQuitMessage(null);
            }
        } 
        else if(plugin.getConfig().getBoolean("Quit.HideQuitStream"))
        {
            plugin.debugLog("Quit.HideQuitStream was true in the config, disabling quit stream ..");
            
            if(plugin.getConfig().getBoolean("Quit.UsePermissions", true)) 
            {
                plugin.debugLog("Quit.UsePermissions was true in the config, using permissions ..");
                
                if(plugin.getConfig().getBoolean("Quit.OnlyHideForUsersWithPermission", true)) 
                {
                    plugin.debugLog("Quit.OnlyHideForUsersWithPermission was true in the config.");
                    
                    if(plugin.hasPermission(leaver, "hidestream.hidequit")) 
                    {
                        event.setQuitMessage(null);
                        plugin.debugLog(leaver.getName() + " had permission hidestream.hidequit, disabled leave message.");
                    }
                } 
                else if(plugin.getConfig().getBoolean("Quit.OnlyHideForUsersWithoutPermission", true)) 
                {
                    plugin.debugLog("Quit.OnlyHideForUsersWithoutPermission was true in the config.");
                    
                    if(!(plugin.hasPermission(leaver, "hidestream.hidequit"))) 
                    {
                        event.setQuitMessage(null);
                        plugin.debugLog(leaver.getName() + " did not have permission hidestream.hidequit, disabled leave message.");
                    }
                } else {
                    plugin.debugLog("Error: Nor OnlyHideForUsersWithPermission or OnlyHideForUsersWithoutPermission in stream category quit is set to true.");
                }
            } else {
                if(OPSupport == true) {
                    if(plugin.getConfig().getBoolean("Quit.OPSupport.OnlyHideIfNotOP") == true) {
                        if(!leaver.isOp()) {
                            event.setQuitMessage(null);
                            plugin.debugLog(leaver.getName() + " is not op, disabled quit message. (OnlyHideIfNotOP = true in config)");
                        }
                    } else if(plugin.getConfig().getBoolean("Quit.OPSupport.OnlyHideIfOP") == true) {
                        if(leaver.isOp()) {
                            event.setQuitMessage(null);
                            plugin.debugLog(leaver.getName() + " is op, disabled quit message. (OnlyHideIfOP = true in config)");
                        }
                    } else {
                        plugin.debugLog("Error: Nor OnlyHideIfNotOP or OnlyHideIfOP in stream category quit is set to true.");
                    }
                } else {
                    event.setQuitMessage(null);
                }
            }
        }
    }
    
    @EventHandler
    public void kickManagement(PlayerKickEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player kicked = event.getPlayer();
        boolean perPlayerToggle = plugin.getConfig().getBoolean("EnablePerPlayerToggle");
        boolean OPSupport = plugin.getConfig().getBoolean("Kick.OPSupport.Enabled");
        
        if(perPlayerToggle == true) 
        {
            if(StreamDB.getHiddenState(kicked.getName()) == true)
            {
                plugin.debugLog("perPlayerToggle is true in the config.");
                event.setLeaveMessage(null);
            }
        } 
        else if(plugin.getConfig().getBoolean("Kick.HideKickStream"))
        {
            plugin.debugLog("Kick.HideKickStream was true in the config, disabling kick stream ..");
            
            if(plugin.getConfig().getBoolean("Kick.UsePermissions", true)) 
            {
                plugin.debugLog("Kick.UsePermissions was true in the config, using permissions ..");
                
                if(plugin.getConfig().getBoolean("Kick.OnlyHideForUsersWithPermission", true)) 
                {
                    plugin.debugLog("Kick.OnlyHideForUsersWithPermission was true in the config.");
                    
                    if(plugin.hasPermission(kicked, "hidestream.hidekick")) 
                    {
                        event.setLeaveMessage(null);
                        plugin.debugLog(kicked.getName() + " had permission hidestream.hidekick, disabled leave message.");
                    }
                } 
                else if(plugin.getConfig().getBoolean("Kick.OnlyHideForUsersWithoutPermission", true)) 
                {
                    plugin.debugLog("Kick.OnlyHideForUsersWithoutPermission was true in the config.");
                    
                    if(!(plugin.hasPermission(kicked, "hidestream.hidekick"))) 
                    {
                        event.setLeaveMessage(null);
                        plugin.debugLog(kicked.getName() + " did not have permission hidestream.hidekick, disabled leave message.");
                    }
                } else {
                    plugin.debugLog("Error: Nor OnlyHideForUsersWithPermission or OnlyHideForUsersWithoutPermission in stream category kick is set to true.");
                }
            } else {
                if(OPSupport == true) {
                    if(plugin.getConfig().getBoolean("Kick.OPSupport.OnlyHideIfNotOP") == true) {
                        if(!kicked.isOp()) {
                            event.setLeaveMessage(null);
                            plugin.debugLog(kicked.getName() + " is not op, disabled kick message. (OnlyHideIfNotOP = true in config)");
                        }
                    } else if(plugin.getConfig().getBoolean("Kick.OPSupport.OnlyHideIfOP") == true) {
                        if(kicked.isOp()) {
                            event.setLeaveMessage(null);
                            plugin.debugLog(kicked.getName() + " is op, disabled kick message. (OnlyHideIfOP = true in config)");
                        }
                    } else {
                        plugin.debugLog("Error: Nor OnlyHideIfNotOP or OnlyHideIfOP in stream category kick is set to true.");
                    }
                } else {
                    event.setLeaveMessage(null);
                }
            }
        }
    }
}
