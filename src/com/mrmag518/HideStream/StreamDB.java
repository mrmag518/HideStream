package com.mrmag518.HideStream;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StreamDB {
    private static final HideStream plugin = (HideStream) Bukkit.getPluginManager().getPlugin("HideStream");
    public static FileConfiguration streamDB = null;
    public static File streamDBFile = null;
    
    public static void load() {
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
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save streamDBFile to " + streamDBFile, ex);
        }
    }
    
    public static void setHidden(String name, boolean value) {
        getStreamDB().set(name + ".hidden", value);
        save();
        plugin.debugLog(name + "'s hidden state value was set to: " + String.valueOf(value));
    }
    
    public static boolean getHiddenState(String name) {
        return getStreamDB().getBoolean(name + ".hidden");
    }
}
