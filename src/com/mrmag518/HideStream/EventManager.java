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
        String type = "[Join] ";
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            plugin.debugLog(type + p.getName() + " is hidden, disabling stream ..");
            event.setJoinMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Join.HideJoinStream")) {
                if(streamEnabled && ppt) {
                    plugin.debugLog(type + "Per player toggle: stream is enabled by default, will not disable stream for " + p.getName());
                    return;
                }
                plugin.debugLog(type + "Stream is enabled in the config, proceeding to disable stream ..");
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Join.NeedsToBeOnline")) {
                    plugin.debugLog(type + "Not enough players online to hide stream!");
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Join.Permissions.UsePermissions")) {
                    plugin.debugLog(type + "UsePermissions is enabled in the config, using permissions ..");

                    if(plugin.getConfig().getBoolean("Join.Permissions.HideOnlyIfHasPermission")) {
                        plugin.debugLog(type + "HideOnlyIfHasPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(plugin.hasPermission(p, "hidestream.hidejoin")) {
                            plugin.debugLog(type + p.getName() + " had the correct permission, proceeding to hide stream ..");
                            event.setJoinMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, won't disable stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Join.Permissions.HideOnlyIfWithoutPermission")) {
                        plugin.debugLog(type + "HideOnlyIfWithoutPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(!plugin.hasPermission(p, "hidestream.hidejoin")) {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, proceeding to hide stream ..");
                            event.setJoinMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " had the correct permission, won't disable stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor HideOnlyIfHasPermission or HideOnlyIfWithoutPermission was enabled in the config, could not take any decision.");
                    }
                } else if(OPSupport) {
                    plugin.debugLog(type + "OPSupport is enabled in the config, using OPSupport ..");

                    if(plugin.getConfig().getBoolean("Join.OPSupport.OnlyHideIfNotOP")) {
                        plugin.debugLog(type + "OnlyHideIfNotOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(!p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is not an OP! proceeding to hide stream ..");
                            event.setJoinMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is an OP, won't hide stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Join.OPSupport.OnlyHideIfOP")) {
                        plugin.debugLog(type + "OnlyHideIfOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is an OP! proceeding to hide stream ..");
                            event.setJoinMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is not an OP, won't hide stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor OnlyHideIfNotOP or OnlyHideIfOP was enabled in the config, could not take any decision.");
                    }
                } else {
                    plugin.debugLog(type + "Nor UsePermission or OPSupport is enabled in the config!");
                    plugin.debugLog(type + "Disabling stream for " + p.getName());
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
        String type = "[Quit] ";
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            plugin.debugLog(type + p.getName() + " is hidden, disabling stream ..");
            event.setQuitMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Quit.HideQuitStream")){
                if(streamEnabled && ppt) {
                    plugin.debugLog(type + "Per player toggle: stream is enabled by default, will not disable stream for " + p.getName());
                    return;
                }
                plugin.debugLog(type + "Stream is enabled in the config, proceeding to disable stream ..");
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Quit.NeedsToBeOnline")) {
                    plugin.debugLog(type + "Not enough players online to hide stream!");
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Quit.Permissions.UsePermissions")) {
                    plugin.debugLog(type + "UsePermissions is enabled in the config, using permissions ..");

                    if(plugin.getConfig().getBoolean("Quit.Permissions.HideOnlyIfHasPermission")) {
                        plugin.debugLog(type + "HideOnlyIfHasPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(plugin.hasPermission(p, "hidestream.hidequit")) {
                            plugin.debugLog(type + p.getName() + " had the correct permission, proceeding to hide stream ..");
                            event.setQuitMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, won't disable stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Quit.Permissions.HideOnlyIfWithoutPermission")) {
                        plugin.debugLog(type + "HideOnlyIfWithoutPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(!plugin.hasPermission(p, "hidestream.hidequit")) {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, proceeding to hide stream ..");
                            event.setQuitMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " had the correct permission, won't disable stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor HideOnlyIfHasPermission or HideOnlyIfWithoutPermission was enabled in the config, could not take any decision.");
                    }
                } else if(OPSupport) {
                    plugin.debugLog(type + "OPSupport is enabled in the config, using OPSupport ..");

                    if(plugin.getConfig().getBoolean("Quit.OPSupport.OnlyHideIfNotOP")) {
                        plugin.debugLog(type + "OnlyHideIfNotOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(!p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is not an OP! proceeding to hide stream ..");
                            event.setQuitMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is an OP, won't hide stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Quit.OPSupport.OnlyHideIfOP")) {
                        plugin.debugLog(type + "OnlyHideIfOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is an OP! proceeding to hide stream ..");
                            event.setQuitMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is not an OP, won't hide stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor OnlyHideIfNotOP or OnlyHideIfOP was enabled in the config, could not take any decision.");
                    }
                } else {
                    plugin.debugLog(type + "Nor UsePermission or OPSupport is enabled in the config!");
                    plugin.debugLog(type + "Disabling stream for " + p.getName());
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
        String type = "[Kick] ";
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            plugin.debugLog(type + p.getName() + " is hidden, disabling stream ..");
            event.setLeaveMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Kick.HideKickStream")){
                if(streamEnabled && ppt) {
                    plugin.debugLog(type + "Per player toggle: stream is enabled by default, will not disable stream for " + p.getName());
                    return;
                }
                plugin.debugLog(type + "Stream is enabled in the config, proceeding to disable stream ..");
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Kick.NeedsToBeOnline")) {
                    plugin.debugLog(type + "Not enough players online to hide stream!");
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Kick.Permissions.UsePermissions")) {
                    plugin.debugLog(type + "UsePermissions is enabled in the config, using permissions ..");

                    if(plugin.getConfig().getBoolean("Kick.Permissions.HideOnlyIfHasPermission")) {
                        plugin.debugLog(type + "HideOnlyIfHasPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(plugin.hasPermission(p, "hidestream.hidekick")) {
                            plugin.debugLog(type + p.getName() + " had the correct permission, proceeding to hide stream ..");
                            event.setLeaveMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, won't disable stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Kick.Permissions.HideOnlyIfWithoutPermission")) {
                        plugin.debugLog(type + "HideOnlyIfWithoutPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(!plugin.hasPermission(p, "hidestream.hidekick")) {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, proceeding to hide stream ..");
                            event.setLeaveMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " had the correct permission, won't disable stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor HideOnlyIfHasPermission or HideOnlyIfWithoutPermission was enabled in the config, could not take any decision.");
                    }
                } else if(OPSupport) {
                    plugin.debugLog(type + "OPSupport is enabled in the config, using OPSupport ..");

                    if(plugin.getConfig().getBoolean("Kick.OPSupport.OnlyHideIfNotOP")) {
                        plugin.debugLog(type + "OnlyHideIfNotOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(!p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is not an OP! proceeding to hide stream ..");
                            event.setLeaveMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is an OP, won't hide stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Kick.OPSupport.OnlyHideIfOP")) {
                        plugin.debugLog(type + "OnlyHideIfOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is an OP! proceeding to hide stream ..");
                            event.setLeaveMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is not an OP, won't hide stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor OnlyHideIfNotOP or OnlyHideIfOP was enabled in the config, could not take any decision.");
                    }
                } else {
                    plugin.debugLog(type + "Nor UsePermission or OPSupport is enabled in the config!");
                    plugin.debugLog(type + "Disabling stream for " + p.getName());
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
        String type = "[Death] ";
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            plugin.debugLog(type + p.getName() + " is hidden, disabling stream ..");
            event.setDeathMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Death.HideDeathStream")){
                if(streamEnabled && ppt) {
                    plugin.debugLog(type + "Per player toggle: stream is enabled by default, will not disable stream for " + p.getName());
                    return;
                }
                plugin.debugLog(type + "Stream is enabled in the config, proceeding to disable stream ..");
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Death.NeedsToBeOnline")) {
                    plugin.debugLog(type + "Not enough players online to hide stream!");
                    return;
                }

                if(plugin.getConfig().getBoolean("Death.Permissions.UsePermissions")) {
                    plugin.debugLog(type + "UsePermissions is enabled in the config, using permissions ..");

                    if(plugin.getConfig().getBoolean("Death.Permissions.HideOnlyIfHasPermission")) {
                        plugin.debugLog(type + "HideOnlyIfHasPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(plugin.hasPermission(p, "hidestream.hidedeath")) {
                            plugin.debugLog(type + p.getName() + " had the correct permission, proceeding to hide stream ..");
                            event.setDeathMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, won't disable stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Death.Permissions.HideOnlyIfWithoutPermission")) {
                        plugin.debugLog(type + "HideOnlyIfWithoutPermission is enabled in the config, proceeding to check permissions for " + p.getName());

                        if(!plugin.hasPermission(p, "hidestream.hidedeath")) {
                            plugin.debugLog(type + p.getName() + " did not have the correct permission, proceeding to hide stream ..");
                            event.setDeathMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " had the correct permission, won't disable stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor HideOnlyIfHasPermission or HideOnlyIfWithoutPermission was enabled in the config, could not take any decision.");
                    }
                } else if(OPSupport) {
                    plugin.debugLog(type + "OPSupport is enabled in the config, using OPSupport ..");

                    if(plugin.getConfig().getBoolean("Death.OPSupport.OnlyHideIfNotOP")) {
                        plugin.debugLog(type + "OnlyHideIfNotOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(!p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is not an OP! proceeding to hide stream ..");
                            event.setDeathMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is an OP, won't hide stream.");
                        }
                    } else if(plugin.getConfig().getBoolean("Death.OPSupport.OnlyHideIfOP")) {
                        plugin.debugLog(type + "OnlyHideIfOP is enabled in the config, proceeding to check OP status of " + p.getName());

                        if(p.isOp()) {
                            plugin.debugLog(type + p.getName() + " is an OP! proceeding to hide stream ..");
                            event.setDeathMessage(null);
                            plugin.debugLog(type + "Stream were disabled for " + p.getName());
                        } else {
                            plugin.debugLog(type + p.getName() + " is not an OP, won't hide stream.");
                        }
                    } else {
                        plugin.debugLog(type + "Nor OnlyHideIfNotOP or OnlyHideIfOP was enabled in the config, could not take any decision.");
                    }
                } else {
                    plugin.debugLog(type + "Nor UsePermission or OPSupport is enabled in the config!");
                    plugin.debugLog(type + "Disabling stream for " + p.getName());
                    event.setDeathMessage(null);
                }
            }
        }
    }
}
