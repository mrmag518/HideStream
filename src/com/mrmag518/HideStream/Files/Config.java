package com.mrmag518.HideStream.Files;

import com.mrmag518.HideStream.Main;
import com.mrmag518.HideStream.Util.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private static FileConfiguration config = null;
    private static File configFile = null;
    private static final Main plugin = (Main)Bukkit.getPluginManager().getPlugin("HideStream");
    
    public static boolean   ENABLED;
    public static boolean   UPDATE_CHECKING;
    public static String    NO_ACCESS_MESSAGE;
    public static boolean   PPT_ENABLED;
    public static boolean   PPT_STREAM_ENABLED_BY_DEF;
    
    public static void init() {
        reload();
        load();
        reload();
        Log.info("Configuration loaded.");
    }
    
    private static void load() {
        config.options().header("For an explanation of these configuration settings, please visit\n"
                + "http://dev.bukkit.org/server-mods/hidestream/pages/config-explanation/ \n");

        config.addDefault("Enabled", true);
        config.addDefault("CheckForUpdates", true);
        config.addDefault("NoCommandPermissionMsg", "&cYou do not have permission to do that!");
        
        config.addDefault("PerPlayerToggle.Enable", false);
        config.addDefault("PerPlayerToggle.StreamEnabledByDefault", true);
        
        for(String s : new String[]{"Join", "Quit", "Kick", "Death"}) {
            config.addDefault(s + ".Enabled", true);
            config.addDefault(s + ".NeedsToBeOnline", 0);
            if(!s.equals("Death")) {
                config.addDefault(s + ".OnlyForNewPlayers", false);
                config.addDefault(s + ".OnlyForOldPlayers", false);
            }
            config.addDefault(s + ".Permissions.UsePermissions", false);
            config.addDefault(s + ".Permissions.HideOnlyIfHasPermission", false);
            config.addDefault(s + ".Permissions.HideOnlyIfWithoutPermission", false);
        }
        
        getConfig().options().copyDefaults(true);
        save();
        
        ENABLED = config.getBoolean("Enabled");
        UPDATE_CHECKING = config.getBoolean("CheckForUpdates");
        NO_ACCESS_MESSAGE = config.getString("NoCommandPermissionMsg");
        PPT_ENABLED = config.getBoolean("PerPlayerToggle.Enable");
        PPT_STREAM_ENABLED_BY_DEF = config.getBoolean("PerPlayerToggle.StreamEnabledByDefault");
    }
    
    public static void reload() {
        if(configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    public static FileConfiguration getConfig() {
        if(config == null) reload();
        return config;
    }
    
    public static void save() {
        if(config == null || configFile == null) {
            return;
        }
        
        try {
            config.save(configFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config.yml to " + configFile, ex);
        }
    }
    
    public static String colorize(String s) {
        if(s == null) return "";
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
}
