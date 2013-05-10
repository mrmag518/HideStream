package com.mrmag518.HideStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    public static HideStream plugin;
    public Commands(HideStream instance){
        plugin = instance;
    }
    private final String PREFIX = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "HideStream" + ChatColor.WHITE + "] ";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
        if(cmd.getName().equalsIgnoreCase("hidestream")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    if(plugin.hasPermission(sender, "hidestream.command.list")) {
                        plugin.debugLog(sender.getName() + " was sent the commands page for HideStream.");
                        
                        sender.sendMessage("------------------- " + ChatColor.YELLOW + "HideStream v" + plugin.currentVersion + ChatColor.WHITE + " -------------------");
                        sender.sendMessage(ChatColor.GRAY + " Commands can be executed by '/hs' and '/hidestream'.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs reload" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Reload the config.yml file.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs enable" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Enable HideStream's stream features.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs disable" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Disable HideStream's stream features.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs debug" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Toggle debug mode.");
                        sender.sendMessage(ChatColor.YELLOW + "/hs toggle" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Toggle stream for yourself.");
                        sender.sendMessage("-----------------------------------------------------");
                    }
                } else if(args.length == 1) {
                    if(args[0].toString().equalsIgnoreCase("reload")) {
                        if(plugin.hasPermission(sender, "hidestream.command.reload")) {
                            reload(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("enable")) {
                        if(plugin.hasPermission(sender, "hidestream.command.enable")) {
                            enable(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("disable")) {
                        if(plugin.hasPermission(sender, "hidestream.command.disable")) {
                            disable(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("debug")) {
                        if(plugin.hasPermission(sender, "hidestream.command.debug")) {
                            toggleDebug(sender);
                        }
                    } else if(args[0].toString().equalsIgnoreCase("toggle")) {
                        if(plugin.hasPermission(sender, "hidestream.command.hideme")) {
                            toggleHidden(sender);
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
                    sender.sendMessage("------------------- " + ChatColor.YELLOW + "HideStream v" + plugin.currentVersion + ChatColor.WHITE + " -------------------");
                    sender.sendMessage(ChatColor.GRAY + " Commands can be executed by '/hs' and '/hidestream'.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs reload" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Reload the config.yml file.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs enable" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Enable HideStream's stream features.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs disable" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Disable HideStream's stream features.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs debug" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Toggle debug mode.");
                    sender.sendMessage(ChatColor.YELLOW + "/hs toggle" + ChatColor.GRAY + " -> " + ChatColor.DARK_AQUA + "Toggle stream for yourself.");
                    sender.sendMessage("-----------------------------------------------------");
                } else if(args.length == 1) {
                    if(args[0].toString().equalsIgnoreCase("reload")) {
                        reload(sender);
                    } else if(args[0].toString().equalsIgnoreCase("enable")) {
                        enable(sender);
                    } else if(args[0].toString().equalsIgnoreCase("disable")) {
                        disable(sender);
                    } else if(args[0].toString().equalsIgnoreCase("debug")) {
                        toggleDebug(sender);
                    } else if(args[0].toString().equalsIgnoreCase("toggle")) {
                        sender.sendMessage(ChatColor.RED + "This is a player command only.");
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
    
    private void reload(CommandSender sender) {
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        plugin.reloadConfig();
        plugin.loadConfig();
        plugin.reloadConfig();
        
        if(plugin.getConfig().getBoolean("PerPlayerToggle.Enable")) {
            StreamDB.properLoad();
        }
        
        plugin.debugMode = plugin.getConfig().getBoolean("DebugMode");
        plugin.debugLog("debugMode is enabled.");
        
        sender.sendMessage(PREFIX + ChatColor.YELLOW + "Configuration file reloaded!");
        plugin.debugLog("Player '" + sender.getName() + "' reloaded the configuration file and the database, by the command /hs reload.");
    }
    
    private void enable(CommandSender sender) {
        boolean state = plugin.getConfig().getBoolean("Enabled");
        if(state != false) {
            sender.sendMessage(PREFIX + ChatColor.RED + "HideStream is already enabled!");
        } else {
            plugin.getConfig().set("Enabled", true);
            plugin.saveConfig();
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Enabled HideStream!");
            plugin.debugLog(sender.getName() + " enabled hidestream's stream features.");
        }
    }
    
    private void disable(CommandSender sender) {
        boolean state = plugin.getConfig().getBoolean("Enabled");
        if(state != true) {
            sender.sendMessage(PREFIX + ChatColor.RED + "HideStream is already disabled!");
        } else {
            plugin.getConfig().set("Enabled", false);
            plugin.saveConfig();
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Disabled HideStream!");
            plugin.debugLog(sender.getName() + " disabled hidestream's stream features.");
        }
    }
    
    private void toggleDebug(CommandSender sender) {
        boolean state = plugin.debugMode;
        if(state != true) {
            plugin.getConfig().set("DebugMode", true);
            plugin.saveConfig();
            plugin.debugMode = plugin.getConfig().getBoolean("DebugMode");
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Debug mode: on");
            plugin.debugLog("debugMode assigned to config node.");
            plugin.debugLog("debugMode is enabled.");
        } else {
            plugin.getConfig().set("DebugMode", false);
            plugin.saveConfig();
            plugin.debugMode = plugin.getConfig().getBoolean("DebugMode");
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Debug mode: off");
        }
    }
    
    private void toggleHidden(CommandSender sender) {
        if(plugin.getConfig().getBoolean("PerPlayerToggle.Enable")) {
            String name = sender.getName().toLowerCase();
            if(StreamDB.isHidden(name)) {
                StreamDB.setHidden(name, false);
                sender.sendMessage(ChatColor.YELLOW + "Stream output is now enabled for you.");
                plugin.debugLog(name + " toggled his hidden stream state to: false");
            } else {
                StreamDB.setHidden(name, true);
                sender.sendMessage(ChatColor.YELLOW + "Stream output is now disabled for you.");
                plugin.debugLog(name + " toggled his hidden stream state to: true");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This feature has not been enabled in the configuration file!");
        }
    }
}
