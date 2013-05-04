package com.mrmag518.HideStream;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SendUpdate implements Listener {
    public static HideStream plugin;
    public SendUpdate(HideStream instance) {
        plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void sendUpdate(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(p.hasPermission("hidestream.getupdates") || p.isOp()) {
            if(plugin.updateFound) {
                p.sendMessage(ChatColor.GREEN + "A new version of HideStream is out!");
                p.sendMessage(ChatColor.GREEN + "It's highly recommended to update, as there may be important fixes or improvements to the plugin!");

                plugin.debugLog(p.getName() + " received the new update notify.");
            }
        }
    }
}
