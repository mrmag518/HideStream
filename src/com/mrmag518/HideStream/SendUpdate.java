package com.mrmag518.HideStream;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendUpdate implements Listener {
    public static HideStream plugin;
    public SendUpdate(HideStream instance)
    {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public String debugPrefix = "[HideStream DEBUG]" + " ";
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void sendUpdate(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(p.hasPermission("hidestream.recieveUpdates") || p.isOp()) {
            try {
                if (plugin.newVersion > plugin.currentVersion) {
                    p.sendMessage(ChatColor.GREEN + "A new version of HideStream is out! ("+ ChatColor.WHITE +  plugin.newVersion + ChatColor.GREEN + ")");
                    p.sendMessage(ChatColor.GREEN + "Current HideStream version running: " + ChatColor.WHITE + plugin.currentVersion + ChatColor.GREEN + ".");
                    p.sendMessage(ChatColor.GREEN + "It's recommended updating :)");
                    
                    plugin.debugLog(p.getName() + " logged in and recieved the new update notify.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
