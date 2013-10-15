package com.mrmag518.HideStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    public static HideStream plugin;
    public Commands(HideStream instance){
        plugin = instance;
    }
    private final String PREFIX = "§f[§3HideStream§f] ";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
        if(cmd.getName().equalsIgnoreCase("hidestream")) {
            if(sender instanceof Player) {
                Player p = (Player)sender;
                
                if(args.length == 0) {
                    if(plugin.hasPermission(p, "hidestream.command.list", true)) {
                        sender.sendMessage("------------------- §eHideStream v" + plugin.currentVersion + "§f -------------------");
                        sender.sendMessage("§e/hs reload");
                        sender.sendMessage(" §7-> §3Reload the configuration file.");
                        sender.sendMessage("§e/hs enable");
                        sender.sendMessage(" §7-> §3Enable HideStream's stream features.");
                        sender.sendMessage("§e/hs disable");
                        sender.sendMessage(" §7-> §3Disable HideStream's stream features.");
                        sender.sendMessage("§e/hs toggle [player]");
                        sender.sendMessage(" §7-> §3Toggle stream for yourself or someone else.");
                        sender.sendMessage("-----------------------------------------------------");
                    }
                } else if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        if(plugin.hasPermission(p, "hidestream.command.reload", true)) {
                            reload(sender);
                        }
                    } else if(args[0].equalsIgnoreCase("enable")) {
                        if(plugin.hasPermission(p, "hidestream.command.enable", true)) {
                            enable(sender);
                        }
                    } else if(args[0].equalsIgnoreCase("disable")) {
                        if(plugin.hasPermission(p, "hidestream.command.disable", true)) {
                            disable(sender);
                        }
                    } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(args.length == 1) {
                            if(plugin.hasPermission(p, "hidestream.command.hideme", true)) {
                                toggleHidden(sender, sender.getName(), true);
                            }
                        } else if(args.length == 2){
                            if(plugin.hasPermission(p, "hidestream.command.hideme.others", true)) {
                                toggleHidden(sender, args[1], false);
                            }
                        }
                    } else {
                        sender.sendMessage(PREFIX + "§cUnknown command. Run '/hidestream' for help.");
                    }
                }
            } else {
                if(args.length == 0) {
                    sender.sendMessage("------------------- HideStream v" + plugin.currentVersion + " -------------------");
                    sender.sendMessage("/hs reload");
                    sender.sendMessage(" -> Reload the configuration file.");
                    sender.sendMessage("/hs enable");
                    sender.sendMessage(" -> Enable HideStream's stream features.");
                    sender.sendMessage("/hs disable");
                    sender.sendMessage(" -> Disable HideStream's stream features.");
                    sender.sendMessage("/hs toggle [player]");
                    sender.sendMessage(" -> Toggle stream for yourself or someone else.");
                    sender.sendMessage("-----------------------------------------------------");
                } else if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        reload(sender);
                    } else if(args[0].equalsIgnoreCase("enable")) {
                        enable(sender);
                    } else if(args[0].equalsIgnoreCase("disable")) {
                        disable(sender);
                    } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(args.length == 1) {
                            sender.sendMessage("You can't toggle console! Use '/hs toggle <player>'");
                        } else if(args.length == 2){
                            toggleHidden(sender, args[1], false);
                        }
                    } else {
                        sender.sendMessage("Unknown command. Run '/hidestream' for help.");
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private void reload(CommandSender sender) {
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        
        plugin.reloadConfig();
        plugin.loadConfig();
        plugin.reloadConfig();
        
        if(plugin.getConfig().getBoolean("PerPlayerToggle.Enable")) StreamDB.properLoad();
        sender.sendMessage(PREFIX + "§eConfiguration file reloaded!");
    }
    
    private void enable(CommandSender sender) {
        boolean state = plugin.getConfig().getBoolean("Enabled");
        
        if(state != false) {
            sender.sendMessage(PREFIX + "§eHideStream is already enabled!");
        } else {
            plugin.getConfig().set("Enabled", true);
            plugin.saveConfig();
            sender.sendMessage(PREFIX + "§eEnabled HideStream!");
        }
    }
    
    private void disable(CommandSender sender) {
        boolean state = plugin.getConfig().getBoolean("Enabled");
        
        if(state != true) {
            sender.sendMessage(PREFIX + "§eHideStream is already disabled!");
        } else {
            plugin.getConfig().set("Enabled", false);
            plugin.saveConfig();
            sender.sendMessage(PREFIX + "§eDisabled HideStream!");
        }
    }
    
    private void toggleHidden(CommandSender sender, String victim, boolean self) {
        if(plugin.getConfig().getBoolean("PerPlayerToggle.Enable")) {
            victim = victim.toLowerCase();
            
            if(StreamDB.isHidden(victim)) {
                StreamDB.setHidden(victim, false);
                if(self) {
                    sender.sendMessage(PREFIX + "§eStream output will now be shown for you.");
                } else {
                    sender.sendMessage(PREFIX + "§eStream output will now be shown for " + victim + ".");
                }
            } else {
                StreamDB.setHidden(victim, true);
                if(self) {
                    sender.sendMessage(PREFIX + "§eStream output will now be hidden for you.");
                } else {
                    sender.sendMessage(PREFIX + "§eStream output will now be hidden for " + victim + ".");
                }
            }
        } else {
            sender.sendMessage(PREFIX + "§eThis feature has not been enabled in the configuration file!");
        }
    }
}
