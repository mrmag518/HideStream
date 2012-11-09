package com.mrmag518.HideStream;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StreamDB {
    public static HideStream plugin;
    
    public static FileConfiguration streamDB = null;
    public static File streamDBFile = null;
    
    public static void loadStreamDB() {
        getStreamDB().options().copyDefaults(true);
        saveStreamDB();
    }
    
    public static void reloadStreamDB() {
        if (streamDBFile == null) {
            streamDBFile = new File("plugins/HideStream/streamDB.yml");
        }
        streamDB = YamlConfiguration.loadConfiguration(streamDBFile);
    }
    
    public static FileConfiguration getStreamDB() {
        if (streamDB == null) {
            reloadStreamDB();
        }
        return streamDB;
    }
    
    public static void saveStreamDB() {
        if (streamDB == null || streamDBFile == null) {
            return;
        }
        try {
            streamDB.save(streamDBFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save streamDBFile to " + streamDBFile, ex);
        }
    }
    
    public static void setHidden(String name, boolean value) {
        getStreamDB().set(name + ".hidden", value);
        saveStreamDB();
        plugin.debugLog(name + "'s hidden state value was set to: " + value);
    }
    
    public static boolean getHiddenState(String name) {
        return getStreamDB().getBoolean(name + ".hidden");
    }
}
