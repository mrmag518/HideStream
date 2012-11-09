package com.mrmag518.HideStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    public static HideStream plugin;
    public Commands(HideStream instance)
    {
        plugin = instance;
    }
    public String PREFIX = ChatColor.WHITE+"["+ChatColor.DARK_AQUA+"HideStream"+ChatColor.WHITE+"]" + " ";
    public String debugPrefix = "[HideStream DEBUG]" + " ";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
        if(l.equalsIgnoreCase("hidestream") || l.equalsIgnoreCase("hs")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    if(plugin.hasPermission(sender, "hidestream.command.list")) {
                        if(plugin.debugMode == true) {plugin.log.info(debugPrefix + "Did command: /hidestream");}
                        
                        sender.sendMessage(ChatColor.DARK_AQUA + "--- HideStream Commands ---");
                        sender.sendMessage(ChatColor.DARK_AQUA + " Commands can be executed by '/hs' or '/hidestream'.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs reload" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Reload the config.yml file.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs enable" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Enable HideStream's stream features.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs disable" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Disable HideStream's stream features.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs debug" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Toggle debug mode.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs hideme" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Toggle join, quit & leave stream for yourself.");
                    }
                } else if(args.length == 1) {
                    if(args[0].toString().equalsIgnoreCase("reload")) {
                        if(plugin.hasPermission(sender, "hidestream.command.reload")) {
                            return reload(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("enable")) {
                        if(plugin.hasPermission(sender, "hidestream.command.enable")) {
                            return enable(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("disable")) {
                        if(plugin.hasPermission(sender, "hidestream.command.disable")) {
                            return disable(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("debug")) {
                        if(plugin.hasPermission(sender, "hidestream.command.debug")) {
                            return toggleDebug(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("hideme") || args[0].toString().equalsIgnoreCase("showme")) {
                        if(plugin.hasPermission(sender, "hidestream.command.hideme")) {
                            return toggleHidden(sender);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid argument.");
                    }
                } else if(args.length >= 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid usage of arguments.");
                    return false;
                }
            } else {
                if(args.length == 0) {
                    sender.sendMessage(ChatColor.DARK_AQUA + "--- HideStream Commands ---");
                    sender.sendMessage(ChatColor.DARK_AQUA + " Commands can be executed by '/hs' or '/hidestream'.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs reload" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Reload the config.yml file.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs enable" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Enable HideStream's stream features.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs disable" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Disable HideStream's stream features.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs debug" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Toggle debug mode.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs hideme" + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "Hide your own stream.");
                } else if(args.length == 1) {
                    if(args[0].toString().equalsIgnoreCase("reload")) {
                        return reload(sender);
                    } else if(args[0].toString().equalsIgnoreCase("enable")) {
                        return enable(sender);
                    } else if(args[0].toString().equalsIgnoreCase("disable")) {
                        return disable(sender);
                    } else if(args[0].toString().equalsIgnoreCase("debug")) {
                        return toggleDebug(sender);
                    } else if(args[0].toString().equalsIgnoreCase("hideme") 
                            || args[0].toString().equalsIgnoreCase("showme")) {
                        sender.sendMessage(ChatColor.RED + "Player command only.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid argument.");
                    }
                } else if(args.length >= 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid usage of arguments.");
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean reload(CommandSender sender) {
        if(!(plugin.getDataFolder().exists())) {
            plugin.getDataFolder().mkdir();
        }
        plugin.reloadConfig();
        StreamDB.reloadStreamDB();
        
        try{
            plugin.debugMode = plugin.getConfig().getBoolean("DebugMode");
            plugin.debugLog("debugMode assigned to config node.");
            plugin.debugLog("debugMode is enabled.");
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        sender.sendMessage(PREFIX + ChatColor.YELLOW + "Configuration file reloaded!");
        if(plugin.debugMode == true) {plugin.log.info(debugPrefix + sender.getName() + " reloaded the configuration file.");}
        return true;
    }
    
    public boolean enable(CommandSender sender) {
        boolean state = plugin.getConfig().getBoolean("Enabled");
        if(state == true) {
            sender.sendMessage(PREFIX + ChatColor.RED + "HideStream is already enabled!");
        } else {
            plugin.getConfig().set("Enabled", true);
            plugin.saveConfig();
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Enabled HideStream!");
            plugin.debugLog(sender.getName() + " enabled hidestream's stream features.");
        }
        return true;
    }
    
    public boolean disable(CommandSender sender) {
        boolean state = plugin.getConfig().getBoolean("Enabled");
        if(state == false) {
            sender.sendMessage(PREFIX + ChatColor.RED + "HideStream is already disabled!");
        } else {
            plugin.getConfig().set("Enabled", false);
            plugin.saveConfig();
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Disabled HideStream!");
            plugin.debugLog(sender.getName() + " disabled hidestream's stream features.");
        }
        return true;
    }
    
    public boolean toggleDebug(CommandSender sender) {
        boolean state = plugin.debugMode;
        if(state == false) {
            plugin.getConfig().set("DebugMode", true);
            plugin.saveConfig();
            plugin.debugMode = plugin.getConfig().getBoolean("DebugMode");
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Debug mode: on");
            plugin.debugLog("debugMode assigned to config node.");
            plugin.debugLog("debugMode is enabled.");
        } else if(state == true) {
            plugin.getConfig().set("DebugMode", false);
            plugin.saveConfig();
            plugin.debugMode = plugin.getConfig().getBoolean("DebugMode");
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Debug mode: off");
        }
        return true;
    }
    
    public boolean toggleHidden(CommandSender sender) {
        if(plugin.getConfig().getBoolean("EnablePerPlayerToggle") == true) {
            String name = sender.getName();
            if(StreamDB.getHiddenState(name) == true) {
                StreamDB.setHidden(name, false);
                sender.sendMessage(ChatColor.YELLOW + "Join, quit & kick messsages is now enabled for you.");
                plugin.debugLog(name + " toggled his/hers hidden stream state to: false");
            } else {
                StreamDB.setHidden(name, true);
                sender.sendMessage(ChatColor.YELLOW + "Join, quit & kick messsages is now disabled for you.");
                plugin.debugLog(name + " toggled his/hers hidden stream state to: true");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This feature has not been enabled in the configuration file!");
        }
        return true;
    }
}
