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
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Main.instance);
    }
    private boolean IS_KICK = false;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handleUpdate(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        
        if(Config.UPDATE_CHECK && !Main.latestUpdate.equals("null") && p.hasPermission("hidestream.getupdates")) {
            Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                @Override
                public void run() {
                    p.sendMessage("§f[§3HideStream§f] §7" + Main.latestUpdate + " §eis now available!");
                    p.sendMessage("§f[§3HideStream§f] §ehttps://dev.bukkit.org/projects/hidestream");
                    p.sendMessage("§f[§3HideStream§f] §eRun §7/hs update §eto update now.");
                }
            }, 110L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {
        if(!Config.ENABLED || !Config.JOIN_HIDE) {
            return;
        }
        Player p = event.getPlayer();
        
        if(Config.JOIN_AFFECTED_WORLDS.isEmpty() || !Config.JOIN_AFFECTED_WORLDS.contains(p.getWorld().getName())) {
            return;
        }
        
        if(Config.PPT_ENABLED) {
            if(StreamDB.isHidden(p.getUniqueId())) {
                event.setJoinMessage(null);
            }
        } else {
            if(Config.JOIN_ONLINE_AMOUNT > 0) {
                if(Bukkit.getOnlinePlayers().size() < Config.JOIN_ONLINE_AMOUNT) {
                    return;
                }
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
            
            if(Config.JOIN_USE_PERMS) {
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
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleQuit(PlayerQuitEvent event) {
        if(!IS_KICK) {
            if(!Config.ENABLED || !Config.QUIT_HIDE) {
                return;
            }
            Player p = event.getPlayer();
            
            if(Config.QUIT_AFFECTED_WORLDS.isEmpty() || !Config.QUIT_AFFECTED_WORLDS.contains(p.getWorld().getName())) {
                return;
            }

            if(Config.PPT_ENABLED && StreamDB.isHidden(p.getUniqueId())) {
                event.setQuitMessage(null);
            } else {
                if(Config.QUIT_ONLINE_AMOUNT > 0) {
                    if(Bukkit.getOnlinePlayers().size() < Config.QUIT_ONLINE_AMOUNT) {
                        return;
                    }
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

                if(Config.QUIT_USE_PERMS) {
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
        } else {
            if(!Config.ENABLED || !Config.KICK_HIDE) {
                return;
            }
            Player p = event.getPlayer();
            
            if(Config.KICK_AFFECTED_WORLDS.isEmpty() || !Config.KICK_AFFECTED_WORLDS.contains(p.getWorld().getName())) {
                return;
            }

            if(Config.PPT_ENABLED && StreamDB.isHidden(p.getUniqueId())) {
                event.setQuitMessage(null);
            } else {
                if(Config.KICK_ONLINE_AMOUNT > 0) {
                    if(Bukkit.getOnlinePlayers().size() < Config.KICK_ONLINE_AMOUNT) {
                        return;
                    }
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

                if(Config.KICK_USE_PERMS) {
                    if(Config.getConfig().getBoolean("Kick.Permissions.HideOnlyIfHasPermission")) {
                        if(p.hasPermission("hidestream.hidekick")) {
                            event.setQuitMessage(null);
                        }
                    } else if(Config.getConfig().getBoolean("Kick.Permissions.HideOnlyIfWithoutPermission")) {
                        if(!p.hasPermission("hidestream.hidekick")) {
                            event.setQuitMessage(null);
                        }
                    }
                } else {
                    event.setQuitMessage(null);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void processQuit(PlayerQuitEvent event) {
        IS_KICK = false;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void processKick(PlayerKickEvent event) {
        IS_KICK = true;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDeath(PlayerDeathEvent event) {
        if(!Config.ENABLED || !Config.DEATH_HIDE) {
            return;
        }
        Player p = event.getEntity();
        
        if(Config.DEATH_AFFECTED_WORLDS.isEmpty() || !Config.DEATH_AFFECTED_WORLDS.contains(p.getWorld().getName())) {
            return;
        }
        
        if(Config.PPT_ENABLED && StreamDB.isHidden(p.getUniqueId())) {
            event.setDeathMessage(null);
        } else {
            if(Config.DEATH_ONLINE_AMOUNT > 0) {
                if(Bukkit.getOnlinePlayers().size() < Config.DEATH_ONLINE_AMOUNT) {
                    return;
                }
            }

            if(Config.DEATH_USE_PERMS) {
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