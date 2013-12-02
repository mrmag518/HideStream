package com.mrmag518.HideStream;

import com.mrmag518.HideStream.Files.Config;
import com.mrmag518.HideStream.Files.StreamDB;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    public static Main plugin;
    public Commands(Main instance){
        plugin = instance;
    }
    private final String PREFIX = "§f[§3HideStream§f] ";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
        if(cmd.getName().equalsIgnoreCase("hidestream")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    if(sender.hasPermission("hidestream.command.list")) {
                        sender.sendMessage("------------------- §eHideStream v" + plugin.currentVersion + "§f -------------------");
                        sender.sendMessage("§e/hs reload");
                        sender.sendMessage(" §7-> §3Reload the configuration file.");
                        sender.sendMessage("§e/hs enable");
                        sender.sendMessage(" §7-> §3Enable HideStream.");
                        sender.sendMessage("§e/hs disable");
                        sender.sendMessage(" §7-> §3Disable HideStream.");
                        sender.sendMessage("§e/hs toggle [player]");
                        sender.sendMessage(" §7-> §3Toggle stream for you or someone else.");
                        sender.sendMessage("-----------------------------------------------------");
                    } else {
                        sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                    }
                } else if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        if(sender.hasPermission("hidestream.command.reload")) {
                            reload(sender);
                        } else {
                            sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                        }
                    } else if(args[0].equalsIgnoreCase("enable")) {
                        if(sender.hasPermission("hidestream.command.enable")) {
                            enable(sender);
                        } else {
                            sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                        }
                    } else if(args[0].equalsIgnoreCase("disable")) {
                        if(sender.hasPermission("hidestream.command.disable")) {
                            disable(sender);
                        } else {
                            sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                        }
                    } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(args.length == 1) {
                            if(sender.hasPermission("hidestream.command.hideme")) {
                                toggleHidden(sender, sender.getName());
                            } else {
                                sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                            }
                        } else if(args.length == 2){
                            if(sender.hasPermission("hidestream.command.hideme.others")) {
                                toggleHidden(sender, args[1]);
                            } else {
                                sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                            }
                        }
                    } else {
                        sender.sendMessage(PREFIX + "§cUnknown command. Run '§7/hidestream§c' for help.");
                    }
                }
            } else {
                if(args.length == 0) {
                    sender.sendMessage("------------------- HideStream v" + plugin.currentVersion + " -------------------");
                    sender.sendMessage("/hs reload");
                    sender.sendMessage(" -> Reload the configuration file.");
                    sender.sendMessage("/hs enable");
                    sender.sendMessage(" -> Enable HideStream.");
                    sender.sendMessage("/hs disable");
                    sender.sendMessage(" -> Disable HideStream.");
                    sender.sendMessage("/hs toggle [player]");
                    sender.sendMessage(" -> Toggle stream for you or someone else.");
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
                            toggleHidden(sender, args[1]);
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
        Config.init();
        if(Config.PPT_ENABLED) StreamDB.init();
        
        sender.sendMessage(PREFIX + "§eConfiguration file reloaded!");
    }
    
    private void enable(CommandSender sender) {
        if(Config.ENABLED) {
            sender.sendMessage(PREFIX + "§eHideStream is already enabled!");
        } else {
            Config.getConfig().set("Enabled", true);
            Config.save();
            Config.ENABLED = true;
            sender.sendMessage(PREFIX + "§eEnabled HideStream!");
        }
    }
    
    private void disable(CommandSender sender) {
        if(Config.ENABLED == false) {
            sender.sendMessage(PREFIX + "§eHideStream is already disabled!");
        } else {
            Config.getConfig().set("Enabled", false);
            Config.save();
            Config.ENABLED = false;
            sender.sendMessage(PREFIX + "§eDisabled HideStream!");
        }
    }
    
    private void toggleHidden(CommandSender sender, String target) {
        if(Config.getConfig().getBoolean("PerPlayerToggle.Enable")) {
            target = target.toLowerCase();
            
            if(StreamDB.isHidden(target)) {
                StreamDB.setHidden(target, false);
                
                if(sender.getName().equalsIgnoreCase(target)) {
                    sender.sendMessage(PREFIX + "§eStream output will now be shown for you.");
                } else {
                    sender.sendMessage(PREFIX + "§eStream output will now be shown for " + target + ".");
                }
            } else {
                StreamDB.setHidden(target, true);
                
                if(sender.getName().equalsIgnoreCase(target)) {
                    sender.sendMessage(PREFIX + "§eStream output will now be hidden for you.");
                } else {
                    sender.sendMessage(PREFIX + "§eStream output will now be hidden for " + target + ".");
                }
            }
        } else {
            sender.sendMessage(PREFIX + "§eThis feature has not been enabled in the configuration file!");
        }
    }
}
