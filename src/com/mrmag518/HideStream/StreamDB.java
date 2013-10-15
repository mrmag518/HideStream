package com.mrmag518.HideStream;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StreamDB {
    public static FileConfiguration streamDB = null;
    public static File streamDBFile = null;
    
    public static void properLoad() {
        reload();
        load();
        reload();
    }
    
    public static void load() {
        getStreamDB().options().header("This file will only be in function if 'PerPlayerToggle' is enabled.\n");
        getStreamDB().options().copyDefaults(true);
        save();
    }
    
    public static void reload() {
        if (streamDBFile == null) {
            streamDBFile = new File("plugins/HideStream/streamDB.yml");
        }
        streamDB = YamlConfiguration.loadConfiguration(streamDBFile);
    }
    
    public static FileConfiguration getStreamDB() {
        if (streamDB == null) {
            reload();
        }
        return streamDB;
    }
    
    public static void save() {
        if (streamDB == null || streamDBFile == null) {
            return;
        }
        try {
            streamDB.save(streamDBFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save streamDB.yml to " + streamDBFile, ex);
        }
    }
    
    public static void setHidden(String victim, boolean value) {
        victim = victim.toLowerCase();
        getStreamDB().set(victim, value);
        save();
    }
    
    public static boolean isHidden(String victim) {
        victim = victim.toLowerCase();
        return getStreamDB().getBoolean(victim);
    }
}
