package com.mrmag518.HideStream.Files;

import com.mrmag518.HideStream.Util.Log;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StreamDB {
    public static FileConfiguration database = null;
    public static File databaseFile = null;
    
    public static void init() {
        reload(); load(); reload();
    }
    
    public static void load() {
        getDB().options().header("This file will only be used if 'PerPlayerToggle' is enabled.\n");
        getDB().options().copyDefaults(true);
        save();
    }
    
    public static void reload() {
        if (databaseFile == null) {
            databaseFile = new File("plugins" + File.separator + "HideStream" + File.separator + "streamDB.yml");
        }
        database = YamlConfiguration.loadConfiguration(databaseFile);
    }
    
    public static FileConfiguration getDB() {
        if (database == null) reload();
        return database;
    }
    
    public static void save() {
        if (database == null || databaseFile == null) {
            return;
        }
        
        try {
            database.save(databaseFile);
        } catch (IOException ex) {
            Log.severe("Could not save streamDB.yml to " + databaseFile.getAbsolutePath());
            ex.printStackTrace();
        }
    }
    
    public static void setHidden(String target, boolean value) {
        getDB().set(target.toLowerCase(), value);
        save();
    }
    
    public static boolean isHidden(String target) {
        return getDB().getBoolean(target.toLowerCase());
    }
}
