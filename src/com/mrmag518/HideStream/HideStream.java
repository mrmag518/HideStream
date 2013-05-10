package com.mrmag518.HideStream;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HideStream extends JavaPlugin {
    public final Logger log = Logger.getLogger("Minecraft");
    private EventManager streamListener = null;
    private SendUpdate sendUpdate = null;
    private FileConfiguration config;
    public static Permission perms = null;
    public double currentVersion = 0;
    public boolean debugMode = false;
    private final String debugPrefix = "[HideStream DEBUG] ";
    public boolean updateFound = false;
    
    @Override
    public void onDisable() {
        PluginDescriptionFile pdffile = getDescription();
        log.info("[" + pdffile.getName() + "]" + " v" + pdffile.getVersion() + " disabled.");
    }
    
    @Override
    public void onEnable() {
        streamListener = new EventManager(this);
        sendUpdate = new SendUpdate(this);
        currentVersion = Double.valueOf(getDescription().getVersion());
        
        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        reloadConfig();
        loadConfig();
        reloadConfig();
        
        if(getConfig().getBoolean("PerPlayerToggle.Enable")) {
            StreamDB.properLoad();
            checkStreamDB();
        }
        
        setupVault();
        
        getCommand("hidestream").setExecutor(new Commands(this));
        
        debugMode = getConfig().getBoolean("DebugMode");
        debugLog("debugMode is enabled.");
        
        PluginDescriptionFile pdffile = getDescription();
        if(getConfig().getBoolean("CheckForUpdates")) {
            log.info("[HideStream] Checking for updates ..");
            try {
                Updater updater = new Updater(this, "hidestream", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);

                Updater.UpdateResult result = updater.getResult();
                switch(result) {
                    case NO_UPDATE:
                        log.info("No update was found.");
                        break;
                    case FAIL_DBO:
                        log.warning("Failed to contact dev.bukkkit.org!");
                        break;
                    case UPDATE_AVAILABLE:
                        updateFound = true;
                        log.info("########## HideStream update ##########");
                        log.info("A new version of HideStream was found!");
                        log.info("Version found: " + updater.getLatestVersionString());
                        log.info("Version running: " + pdffile.getFullName());
                        log.info("#####################################");
                        break;
                }
            } catch(RuntimeException re) {
                log.warning("[HideStream] Failed to establish a connection to dev.bukkit.org!");
            }
        }
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        
        log.info("[" + pdffile.getName() + "]" + " v" + pdffile.getVersion() + " enabled.");
    }
    
    public void debugLog(String output) {
        if(debugMode) {
            log.info(debugPrefix + output);
        }
    }
    
    public void sendNoPerm(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + colorize(config.getString("NoCommandPermissionMsg")));
    }
    
    private String colorize(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
    
    public void loadConfig() {
        config = getConfig();
        config.options().header("For an explanation of these configuration settings, please visit\n"
                + "http://dev.bukkit.org/server-mods/hidestream/pages/config-explanation/ \n");
        
        config.addDefault("Enabled", true);
        config.addDefault("UseVault", false);
        config.addDefault("CheckForUpdates", true);
        config.addDefault("DebugMode", false);
        config.addDefault("NoCommandPermissionMsg", "&cNo permission.");
        
        checkConfig();
        
        config.addDefault("PerPlayerToggle.Enable", false);
        config.addDefault("PerPlayerToggle.StreamEnabledByDefault", true);
        
        config.addDefault("Join.HideJoinStream", true);
        config.addDefault("Join.NeedsToBeOnline", 0);
        config.addDefault("Join.Permissions.UsePermissions", false);
        config.addDefault("Join.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Join.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Join.OPSupport.Enabled", false);
        config.addDefault("Join.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Join.OPSupport.OnlyHideIfOP", false);
        
        config.addDefault("Quit.HideQuitStream", true);
        config.addDefault("Quit.NeedsToBeOnline", 0);
        config.addDefault("Quit.Permissions.UsePermissions", false);
        config.addDefault("Quit.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Quit.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Quit.OPSupport.Enabled", false);
        config.addDefault("Quit.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Quit.OPSupport.OnlyHideIfOP", false);
        
        config.addDefault("Kick.HideKickStream", true);
        config.addDefault("Kick.NeedsToBeOnline", 0);
        config.addDefault("Kick.Permissions.UsePermissions", false);
        config.addDefault("Kick.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Kick.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Kick.OPSupport.Enabled", false);
        config.addDefault("Kick.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Kick.OPSupport.OnlyHideIfOP", false);
        
        config.addDefault("Death.HideDeathStream", false);
        config.addDefault("Death.NeedsToBeOnline", 0);
        config.addDefault("Death.Permissions.UsePermissions", false);
        config.addDefault("Death.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Death.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Death.OPSupport.Enabled", false);
        config.addDefault("Death.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Death.OPSupport.OnlyHideIfOP", false);
        
        getConfig().options().copyDefaults(true);
        saveConfig();
        debugLog(config.getName() + " loaded and saved successfully.");
    }
    
    /**
     * Checks outdated settings, and converts them if found.
     */
    private void checkConfig() {
        config = getConfig();
        
        if(config.get("PerPlayerToggle.AllowToEnable") != null) {
            config.set("PerPlayerToggle.AllowToEnable", null);
            saveConfig();
        }
    }
    
    /**
     * 2.x -> 3.x convertion.
     */
    private void checkStreamDB() {
        File f = new File("plugins/HideStream/streamDB.yml");
        
        if(f.exists()) {
            boolean isOld = false;
            
            for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                if(StreamDB.getStreamDB().get(op.getName() + ".hidden") != null) {
                    isOld = true;
                    break;
                }
            }
            
            if(isOld) {
                long start = System.currentTimeMillis();
                log.info("[HideStream] Detected old version of streamDB.yml!");
                log.info("[HideStream] Will convert to new format.");
                
                for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                    if(StreamDB.getStreamDB().get(op.getName() + ".hidden") != null) {
                        boolean prevOpt = StreamDB.getStreamDB().getBoolean(op.getName() + ".hidden");
                        StreamDB.getStreamDB().set(op.getName(), prevOpt);
                        StreamDB.getStreamDB().set(op.getName() + ".hidden", null);
                    }
                }
                StreamDB.save();
                
                long end = System.currentTimeMillis();
                long diff = end - start;
                log.info("[HideStream] Convertion finished! Took " + diff + "ms.");
            }
        }
    }
    
    private void setupVault() {
        if(getConfig().getBoolean("UseVault")) {
            debugLog("UseVault is true in the config, checking Vault state ..");
            
            if(getServer().getPluginManager().getPlugin("Vault") != null) {
                debugLog("Vault found! Setting up permissions ..");
                setupPermissions();
            } else {
                log.severe("[HideStream] Vault.jar was NOT found in your plugins folder!");
                log.severe("[HideStream] You need to have Vault.jar enabled for Vault support to work!");
                log.warning("[HideStream] Setting UseVault in your config.yml to false ..");
                getConfig().set("UseVault", false);
                saveConfig();
            }
        }
    }
    
    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        if(perms != null) {
            log.info("[HideStream] Hooked to permissions plugin: " + perms.getName());
        } else {
            log.warning("[HideStream] No (vault) supported permissions plugin found!");
            log.warning("[HideStream] Will attempt to use the native bukkit permissions system.");
            log.warning("[HideStream] Setting UseVault in your config.yml to false ..");
            getConfig().set("UseVault", false);
            saveConfig();
        }
    }
    
    public boolean hasPermission(CommandSender sender, String permission) {
        if(getConfig().getBoolean("UseVault")) {
            if(perms.has(sender, permission)) {
                return true;
            } else {
                sendNoPerm(sender);
            }
        } else {
            if(sender.hasPermission(permission)) {
                return true;
            } else {
                sendNoPerm(sender);
            }
        }
        return false;
    }
    
    public boolean hasPermission(Player p, String permission) {
        if(getConfig().getBoolean("UseVault")) {
            if(perms.has(p, permission)) {
                return true;
            }
        } else {
            if(p.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
