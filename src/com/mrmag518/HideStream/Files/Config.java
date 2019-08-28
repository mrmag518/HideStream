package com.mrmag518.HideStream.Files;

import com.mrmag518.HideStream.Main;
import com.mrmag518.HideStream.Util.Log;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    private static FileConfiguration config = null;
    private static File configFile = null;
    
    public static boolean   ENABLED;
    public static boolean   UPDATE_CHECKING;
    public static boolean   PPT_ENABLED;
    public static boolean   PPT_STREAM_ENABLED_BY_DEF;
    public static String    NO_ACCESS_MESSAGE;
    
    public static boolean   JOIN_HIDE;
    public static boolean   JOIN_USE_PERMS;
    public static int       JOIN_ONLINE_AMOUNT;
    
    public static boolean   QUIT_HIDE;
    public static boolean   QUIT_USE_PERMS;
    public static int       QUIT_ONLINE_AMOUNT;
    
    public static boolean   KICK_HIDE;
    public static boolean   KICK_USE_PERMS;
    public static int       KICK_ONLINE_AMOUNT;
    
    public static boolean   DEATH_HIDE;
    public static boolean   DEATH_USE_PERMS;
    public static int       DEATH_ONLINE_AMOUNT;
    
    public static void init() {
        reload(); load(); reload();
    }
    
    private static void load() {
        config.options().header("If you need help with these settings, please visit\n"
                + "https://dev.bukkit.org/projects/hidestream/pages/config-explanation/ \n");

        config.addDefault("Enabled", true);
        config.addDefault("CheckForUpdates", true);
        config.addDefault("NoCommandPermissionMsg", "&cYou do not have permission to do that!");
        
        config.addDefault("PerPlayerToggle.Enable", false);
        config.addDefault("PerPlayerToggle.StreamEnabledByDefault", true);
        
        for(String s : new String[]{"Join", "Quit", "Kick", "Death"}) {
            if(config.get(s + ".Enabled") != null) {
                config.set(s + ".Hide" + s, config.getBoolean(s + ".Enabled"));
                config.set(s + ".Enabled", null);
            }
            
            config.addDefault(s + ".Hide" + s, true);
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
        
        JOIN_HIDE = config.getBoolean("Join.HideJoin");
        JOIN_USE_PERMS = config.getBoolean("Join.Permissions.UsePermissions");
        JOIN_ONLINE_AMOUNT = config.getInt("Join.NeedsToBeOnline");
        
        QUIT_HIDE = config.getBoolean("Quit.HideQuit");
        QUIT_USE_PERMS = config.getBoolean("Quit.Permissions.UsePermissions");
        QUIT_ONLINE_AMOUNT = config.getInt("Quit.NeedsToBeOnline");
        
        KICK_HIDE = config.getBoolean("Kick.HideKick");
        KICK_USE_PERMS = config.getBoolean("Kick.Permissions.UsePermissions");
        KICK_ONLINE_AMOUNT = config.getInt("Kick.NeedsToBeOnline");
        
        DEATH_HIDE = config.getBoolean("Death.HideDeath");
        DEATH_USE_PERMS = config.getBoolean("Death.Permissions.UsePermissions");
        DEATH_ONLINE_AMOUNT = config.getInt("Death.NeedsToBeOnline");
    }
    
    public static void reload() {
        if(configFile == null) {
            configFile = new File(Main.instance.getDataFolder(), "config.yml");
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
        } catch(IOException ex) {
            Log.severe("Could not save config.yml to " + configFile.getAbsolutePath());
        }
    }
    
    public static String colorize(String s) {
        if(s == null) return "";
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
}
