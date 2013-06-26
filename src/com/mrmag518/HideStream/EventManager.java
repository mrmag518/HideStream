package com.mrmag518.HideStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    public static HideStream plugin;
    public EventManager(HideStream instance) {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player p = event.getPlayer();
        boolean OPSupport = plugin.getConfig().getBoolean("Join.OPSupport.Enabled");
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setJoinMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Join.HideJoinStream")) {
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Join.NeedsToBeOnline")) {
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Join.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Join.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidejoin")) {
                            event.setJoinMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Join.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidejoin")) {
                            event.setJoinMessage(null);
                        }
                    }
                } else if(OPSupport) {
                    if(plugin.getConfig().getBoolean("Join.OPSupport.OnlyHideIfNotOP")) {
                        if(!p.isOp()) {
                            event.setJoinMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Join.OPSupport.OnlyHideIfOP")) {
                        if(p.isOp()) {
                            event.setJoinMessage(null);
                        }
                    }
                } else {
                    event.setJoinMessage(null);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleQuit(PlayerQuitEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player p = event.getPlayer();
        boolean OPSupport = plugin.getConfig().getBoolean("Quit.OPSupport.Enabled");
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setQuitMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Quit.HideQuitStream")){
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Quit.NeedsToBeOnline")) {
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Quit.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Quit.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidequit")) {
                            event.setQuitMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Quit.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidequit")) {
                            event.setQuitMessage(null);
                        }
                    }
                } else if(OPSupport) {
                    if(plugin.getConfig().getBoolean("Quit.OPSupport.OnlyHideIfNotOP")) {
                        if(!p.isOp()) {
                            event.setQuitMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Quit.OPSupport.OnlyHideIfOP")) {
                        if(p.isOp()) {
                            event.setQuitMessage(null);
                        }
                    }
                } else {
                    event.setQuitMessage(null);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleKick(PlayerKickEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player p = event.getPlayer();
        boolean OPSupport = plugin.getConfig().getBoolean("Kick.OPSupport.Enabled");
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setLeaveMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Kick.HideKickStream")){
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Kick.NeedsToBeOnline")) {
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Kick.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Kick.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidekick")) {
                            event.setLeaveMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Kick.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidekick")) {
                            event.setLeaveMessage(null);
                        }
                    }
                } else if(OPSupport) {
                    if(plugin.getConfig().getBoolean("Kick.OPSupport.OnlyHideIfNotOP")) {
                        if(!p.isOp()) {
                            event.setLeaveMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Kick.OPSupport.OnlyHideIfOP")) {
                        if(p.isOp()) {
                            event.setLeaveMessage(null);
                        }
                    }
                } else {
                    event.setLeaveMessage(null);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDeath(PlayerDeathEvent event) {
        if(plugin.getConfig().getBoolean("Enabled") == false) {
            return;
        }
        Player p = event.getEntity();
        boolean OPSupport = plugin.getConfig().getBoolean("Death.OPSupport.Enabled");
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setDeathMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Death.HideDeathStream")){
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Death.NeedsToBeOnline")) {
                    return;
                }

                if(plugin.getConfig().getBoolean("Death.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Death.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidedeath")) {
                            event.setDeathMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Death.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidedeath")) {
                            event.setDeathMessage(null);
                        }
                    }
                } else if(OPSupport) {
                    if(plugin.getConfig().getBoolean("Death.OPSupport.OnlyHideIfNotOP")) {
                        if(!p.isOp()) {
                            event.setDeathMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Death.OPSupport.OnlyHideIfOP")) {
                        if(p.isOp()) {
                            event.setDeathMessage(null);
                        }
                    }
                } else {
                    event.setDeathMessage(null);
                }
            }
        }
    }
}
