package com.mrmag518.HideStream;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HideStream extends JavaPlugin {
    public final Logger log = Logger.getLogger("Minecraft");
    private FileConfiguration config;
    public static Permission perms = null;
    public double currentVersion = 0;
    public boolean updateFound = false;
    public final String[] types = {"Join", "Quit", "Kick", "Death"};
    
    @Override
    public void onDisable() {
        PluginDescriptionFile pdffile = getDescription();
        log.info("[" + pdffile.getName() + "]" + " v" + pdffile.getVersion() + " disabled.");
    }
    
    @Override
    public void onEnable() {
        EventManager streamListener = new EventManager(this);
        SendUpdate sendUpdate = new SendUpdate(this);
        currentVersion = Double.valueOf(getDescription().getVersion());
        
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        
        reloadConfig();
        loadConfig();
        reloadConfig();
        
        if(getConfig().getBoolean("PerPlayerToggle.Enable")) {
            StreamDB.properLoad();
        }
        getCommand("hidestream").setExecutor(new Commands(this));
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
        } catch (IOException e) {}
        
        log.info("[" + pdffile.getName() + "]" + " v" + pdffile.getVersion() + " enabled.");
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
        config.addDefault("CheckForUpdates", true);
        config.addDefault("NoCommandPermissionMsg", "&cYou do not have permission to do that!");
        
        config.addDefault("PerPlayerToggle.Enable", false);
        config.addDefault("PerPlayerToggle.StreamEnabledByDefault", true);
        
        for(String s : types) {
            config.addDefault(s + ".Enabled", true);
            config.addDefault(s + ".NeedsToBeOnline", 0);
            if(!s.equalsIgnoreCase("death")) {
                config.addDefault(s + ".OnlyForNewPlayers", false);
                config.addDefault(s + ".OnlyForOldPlayers", false);
            }
            config.addDefault(s + ".Permissions.UsePermissions", false);
            config.addDefault(s + ".Permissions.HideOnlyIfHasPermission", false);
            config.addDefault(s + ".Permissions.HideOnlyIfWithoutPermission", false);
        }
        
        checkConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        log.info("[HideStream] Loaded configuration file.");
    }
    
    private void checkConfig() {
        config = getConfig();
        
        if(config.get("UseVault") != null) {
            config.set("UseVault", null);
        }
        
        if(config.get("PerPlayerToggle.AllowToEnable") != null) {
            config.set("PerPlayerToggle.AllowToEnable", null);
        }
        
        if(config.get("DebugMode") != null) {
            config.set("DebugMode", null);
        }
        
        for(String s : types) {
            if(config.get(s + ".OPSupport.Enabled") != null) {
                config.set(s + ".OPSupport.Enabled", null);
            }
            
            if(config.get(s + ".OPSupport.OnlyHideIfNotOP") != null) {
                config.set(s + ".OPSupport.OnlyHideIfNotOP", null);
            }
            
            if(config.get(s + ".OPSupport.OnlyHideIfOP") != null) {
                config.set(s + ".OPSupport.OnlyHideIfOP", null);
            }
            
            if(config.get(s + ".Hide" + s + "Stream") != null) {
                config.set(s + ".Enabled", config.getBoolean(s + ".Hide" + s + "Stream"));
                config.set(s + ".Hide" + s + "Stream", null);
            }
        }
        
        saveConfig();
    }
    
    public boolean hasPermission(Player player, String permission, boolean notify) {
        if(!player.hasPermission(permission)) {
            if(notify) player.sendMessage(colorize(config.getString("NoCommandPermissionMsg")));
            return false;
        }
        return true;
    }
    
    public boolean enabled() {
        return config.getBoolean("Enabled");
    }
}
