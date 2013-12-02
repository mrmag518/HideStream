package com.mrmag518.HideStream;

import com.mrmag518.HideStream.Files.Config;
import com.mrmag518.HideStream.Files.StreamDB;

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
    public static Main plugin;
    public EventManager(Main instance) {
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
        if(!Config.ENABLED) return;
        Player p = event.getPlayer();
        
        if(StreamDB.isHidden(p.getName()) && Config.PPT_ENABLED) {
            event.setJoinMessage(null);
        } else {
            if(Config.getConfig().getBoolean("Join.Enabled")) {
                if(Config.PPT_STREAM_ENABLED_BY_DEF && Config.PPT_ENABLED) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < Config.getConfig().getInt("Join.NeedsToBeOnline")) {
                    return;
                }
                
                if(Config.getConfig().getBoolean("Join.OnlyForNewPlayers")) {
                    if(p.hasPlayedBefore()) {
                        return;
                    }
                } else if(Config.getConfig().getBoolean("Join.OnlyForOldPlayers")) {
                    if(!p.hasPlayedBefore()) {
                        return;
                    }
                }
                
                if(Config.getConfig().getBoolean("Join.Permissions.UsePermissions")) {
                    if(Config.getConfig().getBoolean("Join.Permissions.HideOnlyIfHasPermission")) {
                        if(p.hasPermission("hidestream.hidejoin")) {
                            event.setJoinMessage(null);
                        }
                    } else if(Config.getConfig().getBoolean("Join.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!p.hasPermission("hidestream.hidejoin")) {
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
        if(!Config.ENABLED) return;
        Player p = event.getPlayer();
        
        if(StreamDB.isHidden(p.getName()) && Config.PPT_ENABLED) {
            event.setQuitMessage(null);
        } else {
            if(Config.getConfig().getBoolean("Quit.Enabled")){
                if(Config.PPT_STREAM_ENABLED_BY_DEF && Config.PPT_ENABLED) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < Config.getConfig().getInt("Quit.NeedsToBeOnline")) {
                    return;
                }
                
                if(Config.getConfig().getBoolean("Quit.OnlyForNewPlayers")) {
                    if(p.hasPlayedBefore()) {
                        return;
                    }
                } else if(Config.getConfig().getBoolean("Quit.OnlyForOldPlayers")) {
                    if(!p.hasPlayedBefore()) {
                        return;
                    }
                }
                
                if(Config.getConfig().getBoolean("Quit.Permissions.UsePermissions")) {
                    if(Config.getConfig().getBoolean("Quit.Permissions.HideOnlyIfHasPermission")) {
                        if(p.hasPermission("hidestream.hidequit")) {
                            event.setQuitMessage(null);
                        }
                    } else if(Config.getConfig().getBoolean("Quit.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!p.hasPermission("hidestream.hidequit")) {
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
        if(!Config.ENABLED) return;
        Player p = event.getPlayer();
        
        if(StreamDB.isHidden(p.getName()) && Config.PPT_ENABLED) {
            event.setLeaveMessage(null);
        } else {
            if(Config.getConfig().getBoolean("Kick.Enabled")){
                if(Config.PPT_STREAM_ENABLED_BY_DEF && Config.PPT_ENABLED) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < Config.getConfig().getInt("Kick.NeedsToBeOnline")) {
                    return;
                }
                
                if(Config.getConfig().getBoolean("Kick.OnlyForNewPlayers")) {
                    if(p.hasPlayedBefore()) {
                        return;
                    }
                } else if(Config.getConfig().getBoolean("Kick.OnlyForOldPlayers")) {
                    if(!p.hasPlayedBefore()) {
                        return;
                    }
                }
                
                if(Config.getConfig().getBoolean("Kick.Permissions.UsePermissions")) {
                    if(Config.getConfig().getBoolean("Kick.Permissions.HideOnlyIfHasPermission")) {
                        if(p.hasPermission("hidestream.hidekick")) {
                            event.setLeaveMessage(null);
                        }
                    } else if(Config.getConfig().getBoolean("Kick.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!p.hasPermission("hidestream.hidekick")) {
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
        if(!Config.ENABLED) return;
        Player p = event.getEntity();
        
        if(StreamDB.isHidden(p.getName()) && Config.PPT_ENABLED) {
            event.setDeathMessage(null);
        } else {
            if(Config.getConfig().getBoolean("Death.Enabled")){
                if(Config.PPT_STREAM_ENABLED_BY_DEF && Config.PPT_ENABLED) {
                    return;
                }
                
                if(Bukkit.getOnlinePlayers().length < Config.getConfig().getInt("Death.NeedsToBeOnline")) {
                    return;
                }

                if(Config.getConfig().getBoolean("Death.Permissions.UsePermissions")) {
                    if(Config.getConfig().getBoolean("Death.Permissions.HideOnlyIfHasPermission")) {
                        if(p.hasPermission("hidestream.hidedeath")) {
                            event.setDeathMessage(null);
                        }
                    } else if(Config.getConfig().getBoolean("Death.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!p.hasPermission("hidestream.hidedeath")) {
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
