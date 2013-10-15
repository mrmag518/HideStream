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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handleUpdate(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        
        if(p.hasPermission("hidestream.getupdates") && plugin.updateFound) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    p.sendMessage("§f[§3HideStream§f] §e" + plugin.updater.getLatestName() + " is now available!");
                    p.sendMessage("§f[§3HideStream§f] §e§ohttp://dev.bukkit.org/bukkit-plugins/hidestream/");
                }
            }, 100);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {
        if(!plugin.enabled()) return;
        
        Player p = event.getPlayer();
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setJoinMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Join.Enabled")) {
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Join.NeedsToBeOnline")) {
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Join.OnlyForNewPlayers")) {
                    if(p.hasPlayedBefore()) {
                        return;
                    }
                } else if(plugin.getConfig().getBoolean("Join.OnlyForOldPlayers")) {
                    if(!p.hasPlayedBefore()) {
                        return;
                    }
                }
                
                if(plugin.getConfig().getBoolean("Join.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Join.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidejoin", false) || p.isOp()) {
                            event.setJoinMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Join.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidejoin", false) && !p.isOp()) {
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
        if(!plugin.enabled()) return;
        
        Player p = event.getPlayer();
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setQuitMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Quit.Enabled")){
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Quit.NeedsToBeOnline")) {
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Quit.OnlyForNewPlayers")) {
                    if(p.hasPlayedBefore()) {
                        return;
                    }
                } else if(plugin.getConfig().getBoolean("Quit.OnlyForOldPlayers")) {
                    if(!p.hasPlayedBefore()) {
                        return;
                    }
                }
                
                if(plugin.getConfig().getBoolean("Quit.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Quit.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidequit", false) || p.isOp()) {
                            event.setQuitMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Quit.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidequit", false) && !p.isOp()) {
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
        if(!plugin.enabled()) return;
        
        Player p = event.getPlayer();
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setLeaveMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Kick.Enabled")){
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Kick.NeedsToBeOnline")) {
                    return;
                }
                
                if(plugin.getConfig().getBoolean("Kick.OnlyForNewPlayers")) {
                    if(p.hasPlayedBefore()) {
                        return;
                    }
                } else if(plugin.getConfig().getBoolean("Kick.OnlyForOldPlayers")) {
                    if(!p.hasPlayedBefore()) {
                        return;
                    }
                }
                
                if(plugin.getConfig().getBoolean("Kick.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Kick.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidekick", false) || p.isOp()) {
                            event.setLeaveMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Kick.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidekick", false) && !p.isOp()) {
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
        if(!plugin.enabled()) return;
        
        Player p = event.getEntity();
        boolean streamEnabled = plugin.getConfig().getBoolean("PerPlayerToggle.StreamEnabledByDefault");
        boolean ppt = plugin.getConfig().getBoolean("PerPlayerToggle.Enable");
        
        if(StreamDB.isHidden(p.getName()) && ppt) {
            event.setDeathMessage(null);
        } else {
            if(plugin.getConfig().getBoolean("Death.Enabled")){
                if(streamEnabled && ppt) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < plugin.getConfig().getInt("Death.NeedsToBeOnline")) {
                    return;
                }

                if(plugin.getConfig().getBoolean("Death.Permissions.UsePermissions")) {
                    if(plugin.getConfig().getBoolean("Death.Permissions.HideOnlyIfHasPermission")) {
                        if(plugin.hasPermission(p, "hidestream.hidedeath", false) || p.isOp()) {
                            event.setDeathMessage(null);
                        }
                    } else if(plugin.getConfig().getBoolean("Death.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!plugin.hasPermission(p, "hidestream.hidedeath", false) && !p.isOp()) {
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
