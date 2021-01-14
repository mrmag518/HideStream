package com.mrmag518.HideStream;

import com.mrmag518.HideStream.Files.Config;
import com.mrmag518.HideStream.Files.StreamDB;
import com.mrmag518.HideStream.Util.Log;
import com.mrmag518.HideStream.Util.MetricsLite;
import com.mrmag518.HideStream.Util.Updater;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static String latestUpdate = "null";
    public static String prefix = "§f[§3HideStream§f] ";
    public static Main instance = null;
    
    @Override
    public void onDisable() {
        Log.info("Version " + getDescription().getVersion() + " disabled.");
    }
    
    @Override
    public void onEnable() {
        instance = this;
        new EventManager().register();
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        Config.init();
        if(Config.PPT_ENABLED) StreamDB.init();
        getCommand("hidestream").setExecutor(new Commands());
        MetricsLite metrics = new MetricsLite(this, 37123);
        
        if(Config.UPDATE_CHECK) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                updateCheck();
            }, 0, 20*60*60*24);
        }
        Log.info("Version " + getDescription().getVersion() + " enabled. (Metrics: " + metrics.isEnabled() + ". CheckForUpdates: " + Config.UPDATE_CHECK + ")");
    }
    
    public File getDataFile() {
        return getFile();
    }
    
    private void updateCheck() {
        Log.info("Running updater ..");
        
        Updater updater = new Updater(this, 37123, getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        
        Updater.UpdateResult result = updater.getResult();
        switch(result) {
            case NO_UPDATE:
                Log.info("No update was found.");
                break;
            case UPDATE_AVAILABLE:
                latestUpdate = updater.getLatestName();
                Log.info("------------------------------------");
                Log.info(latestUpdate + " is now available!");
                Log.info("Run '/hs update' to update now.");
                Log.info("------------------------------------");
                break;
            case DISABLED:
                Log.info("Update checking has been disabled in the updater config.");
                break;
            case FAIL_APIKEY:
                Log.warning("The API key you have provided is incorrect!");
                break;
        }
    }
}
