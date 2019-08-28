package com.mrmag518.HideStream;

import com.mrmag518.HideStream.Files.Config;
import com.mrmag518.HideStream.Files.StreamDB;
import com.mrmag518.HideStream.Util.UUIDFetcher;
import com.mrmag518.HideStream.Util.Updater;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String l, String[] args){
        if(cmd.getName().equalsIgnoreCase("hidestream")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    if(sender.hasPermission("hidestream.command.list")) {
                        sendHelpMessage(sender);
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
                    } else if(args[0].equalsIgnoreCase("onlinecount") || args[0].equalsIgnoreCase("onlineamount") || args[0].equalsIgnoreCase("togglecount") || args[0].equalsIgnoreCase("online")) {
                        if(sender.hasPermission("hidestream.command.togglecount")) {
                            if(args.length < 2) {
                                sender.sendMessage(Main.prefix + "§cYou need to specify a number of players.");
                                sender.sendMessage(Main.prefix + "§e/hs togglecount §7<§eamountOfPlayers§7>");
                                sender.sendMessage(Main.prefix + "§3This will make it so that stream is only disabled when the specified amount of players are online.");
                            } else {
                                if(args.length > 2) {
                                    changeOnlineAmount(sender, args[1], args[2]);
                                } else {
                                    changeOnlineAmount(sender, args[1], "global");
                                }
                            }
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
                        } else if(args.length >= 2){
                            if(sender.hasPermission("hidestream.command.hideme.others")) {
                                toggleHidden(sender, args[1]);
                            } else {
                                sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                            }
                        }
                    } else if(args[0].equalsIgnoreCase("update")) {
                        if(sender.hasPermission("hidestream.command.update")) {
                            update(sender);
                        } else {
                            sender.sendMessage(Config.colorize(Config.NO_ACCESS_MESSAGE));
                        }
                    } else {
                        sender.sendMessage(Main.prefix + "§cUnknown command. Run §7/hidestream§c for help.");
                    }
                }
            } else {
                if(args.length == 0) {
                    sendHelpMessage(sender);
                } else if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        reload(sender);
                    } else if(args[0].equalsIgnoreCase("enable")) {
                        enable(sender);
                    } else if(args[0].equalsIgnoreCase("disable")) {
                        disable(sender);
                    } else if(args[0].equalsIgnoreCase("onlineamount") || args[0].equalsIgnoreCase("onlinecount") || args[0].equalsIgnoreCase("togglecount") || args[0].equalsIgnoreCase("online")) {
                        if(args.length < 2) {
                            sender.sendMessage("You need to specify a number of players.");
                            sender.sendMessage("/hs onlineamount <amountOfPlayers>");
                            sender.sendMessage("This will make it so that stream is only disabled when the specified amount of players are online.");
                        } else {
                            if(args.length > 2) {
                                changeOnlineAmount(sender, args[1], args[2]);
                            } else {
                                changeOnlineAmount(sender, args[1], "global");
                            }
                        }
                } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(args.length == 1) {
                            sender.sendMessage("You can't toggle the console! Use '/hs toggle <player>'");
                        } else if(args.length >= 2){
                            toggleHidden(sender, args[1]);
                        }
                    } else if(args[0].equalsIgnoreCase("update")) {
                        update(sender);
                    }
                    else {
                        sender.sendMessage("Unknown command. Run '/hidestream' for help.");
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        String s = Main.instance.getDescription().getFullName();
                        
        if(s.split("\\.").length > 2) {
            sender.sendMessage("------------------- §e" + s + "§f ------------------");
        } else {
            sender.sendMessage("------------------- §e" + s + "§f -------------------");
        }
        sender.sendMessage("§e/hs reload");
        sender.sendMessage(" §7-> §3Reload the configuration file.");
        sender.sendMessage("§e/hs enable");
        sender.sendMessage(" §7-> §3Enable HideStream.");
        sender.sendMessage("§e/hs disable");
        sender.sendMessage(" §7-> §3Disable HideStream.");
        sender.sendMessage("§e/hs onlineamount §7<§eamountOfPlayers§7> [§ejoin§7/§equit§7/§ekick§7/§edeath§7]");
        sender.sendMessage(" §7-> §3Change the number of players that has to be online for effect.");
        sender.sendMessage("§e/hs toggle §7[§eplayer§7]");
        sender.sendMessage(" §7-> §3Toggle stream for you or someone else.");
        sender.sendMessage("§e/hs update");
        sender.sendMessage(" §7-> §3Auto/force update HideStream.");
        sender.sendMessage("-----------------------------------------------------");
    }
    
    private void changeOnlineAmount(CommandSender sender, final String amount, final String category) {
        try {
            int i = Integer.parseInt(amount);
            
            if(i < 0) {
                sender.sendMessage(Main.prefix + "§cThe number of players must be above or equal 0.");
                return;
            }
            
            if(category.equalsIgnoreCase("global") || category.equalsIgnoreCase("all")) {
                for(String s : new String[]{"Join", "Quit", "Kick", "Death"}) {
                    Config.getConfig().set(s + ".NeedsToBeOnline", i);
                    Config.save();
                    Config.JOIN_ONLINE_AMOUNT = i;
                    Config.QUIT_ONLINE_AMOUNT = i;
                    Config.KICK_ONLINE_AMOUNT = i;
                    Config.DEATH_ONLINE_AMOUNT = i;
                }
                sender.sendMessage(Main.prefix + "§eThe amount of players that has to be online is now: §7" + i);
                sender.sendMessage(Main.prefix + "§3This change goes for §eall §3stream categories(§7Join, Quit, Kick and Death§3).");
            } else {
                if(category.equalsIgnoreCase("join") || category.equalsIgnoreCase("j")) {
                    Config.getConfig().set("Join.NeedsToBeOnline", i);
                    Config.save();
                    Config.JOIN_ONLINE_AMOUNT = i;
                } else if(category.equalsIgnoreCase("quit") || category.equalsIgnoreCase("leave") || category.equalsIgnoreCase("q")) {
                    Config.getConfig().set("Quit.NeedsToBeOnline", i);
                    Config.save();
                    Config.QUIT_ONLINE_AMOUNT = i;
                } else if(category.equalsIgnoreCase("kick") || category.equalsIgnoreCase("k")) {
                    Config.getConfig().set("Kick.NeedsToBeOnline", i);
                    Config.save();
                    Config.KICK_ONLINE_AMOUNT = i;
                } else if(category.equalsIgnoreCase("death") || category.equalsIgnoreCase("d")) {
                    Config.getConfig().set("Death.NeedsToBeOnline", i);
                    Config.save();
                    Config.DEATH_ONLINE_AMOUNT = i;
                } else {
                    sender.sendMessage(Main.prefix + "§cThe category §7" + category + " §cwas not recognized.");
                    sender.sendMessage(Main.prefix + "§3Categories: §eJoin§3, §eQuit§3, §eKick §3and §eDeath§3.");
                    sender.sendMessage(Main.prefix + "§3If you want switch the count globally, use §e/hs onlineamount §3" + i);
                    return;
                }
                sender.sendMessage(Main.prefix + "§eThe amount of players that has to be online is now: §7" + i);
                sender.sendMessage(Main.prefix + "§3This change goes for the category: §e" + category);
            }
        } catch(NumberFormatException e) {
            sender.sendMessage(Main.prefix + "§cYou need to specify a correct number. §7" + amount + "§c is not a valid number.");
        }
    }
    
    private void update(final CommandSender sender) {
        if(Config.UPDATE_CHECKING) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sender.sendMessage(Main.prefix + "§eRunning updater ..");

                    Updater updater = new Updater(Main.instance, 37123, Main.instance.getDataFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
                    
                    switch(updater.getResult()) {
                        case FAIL_DBO: 
                            sender.sendMessage(Main.prefix + "§cUpdater failed! (Could not contact dev.bukkit.org)"); 
                            break;
                        case FAIL_DOWNLOAD: 
                            sender.sendMessage(Main.prefix + "§cUpdater failed! (Failed to download file)"); 
                            break;
                        case SUCCESS:
                            sender.sendMessage(Main.prefix + "§eDownload complete! (§7" + updater.getLatestName() + "§e)");
                            sender.sendMessage(Main.prefix + "§eRestart the server to apply the update.");
                            break;
                    }
                }
            }).start();
        } else {
            sender.sendMessage(Main.prefix + "§cThe updater has not been enabled in the config!");
        }
    }
    
    private void reload(CommandSender sender) {
        if(!Main.instance.getDataFolder().exists()) Main.instance.getDataFolder().mkdir();
        Config.init();
        if(Config.PPT_ENABLED) StreamDB.init();
        
        sender.sendMessage(Main.prefix + "§eConfiguration file reloaded!");
    }
    
    private void enable(CommandSender sender) {
        if(Config.ENABLED) {
            sender.sendMessage(Main.prefix + "§eHideStream is already enabled!");
        } else {
            Config.getConfig().set("Enabled", true);
            Config.save();
            Config.ENABLED = true;
            sender.sendMessage(Main.prefix + "§eEnabled HideStream!");
        }
    }
    
    private void disable(CommandSender sender) {
        if(Config.ENABLED == false) {
            sender.sendMessage(Main.prefix + "§eHideStream is already disabled!");
        } else {
            Config.getConfig().set("Enabled", false);
            Config.save();
            Config.ENABLED = false;
            sender.sendMessage(Main.prefix + "§eDisabled HideStream!");
        }
    }
    
    private void toggleHidden(final CommandSender sender, final String target) {
        if(Config.PPT_ENABLED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UUID uuid = null;
                    OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                    
                    if(Bukkit.getOnlineMode()) {
                        if(op.isOnline()) {
                            uuid = op.getUniqueId();
                        } else {
                            sender.sendMessage(Main.prefix + "§7Fetching player UUID ..");
                            try {
                                uuid = new UUIDFetcher(Arrays.asList(target)).call().get(target);
                            } catch (Exception ex) {}
                        }
                    } else {
                        uuid = op.getUniqueId();
                    }
                    
                    if(uuid == null) {
                        sender.sendMessage(Main.prefix + "§cCould not fetch UUID for §7" + target + "§c!");
                        return;
                    }
                    
                    if(StreamDB.getDB().get(target.toLowerCase()) != null) {
                        StreamDB.getDB().set(target.toLowerCase(), null);
                        StreamDB.save();
                    }
                    
                    if(StreamDB.isHidden(uuid)) {
                        StreamDB.setHidden(uuid, false);

                        if(sender.getName().equalsIgnoreCase(target)) {
                            sender.sendMessage(Main.prefix + "§eStream messages will now be shown for §7you§e.");
                        } else {
                            sender.sendMessage(Main.prefix + "§eStream messages will now be shown for §7" + target + "§e.");
                        }
                    } else {
                        StreamDB.setHidden(uuid, true);

                        if(sender.getName().equalsIgnoreCase(target)) {
                            sender.sendMessage(Main.prefix + "§eStream messages will now be hidden for §7you§e.");
                        } else {
                            sender.sendMessage(Main.prefix + "§eStream messages will now be hidden for §7" + target + "§e.");
                        }
                    }
                }
            }).start();
        } else {
            sender.sendMessage(Main.prefix + "§eThis feature has not been enabled!");
        }
    }
}
