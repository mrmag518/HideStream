package com.mrmag518.HideStream;

import com.mrmag518.HideStream.Files.Config;
import com.mrmag518.HideStream.Files.StreamDB;
import com.mrmag518.HideStream.Util.Log;
import com.mrmag518.HideStream.Util.MetricsLite;
import com.mrmag518.HideStream.Util.Updater;

import java.io.IOException;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public double currentVersion;
    public boolean updateFound = false;
    public Updater updater = null;
    
    @Override
    public void onDisable() {
        Log.info("Version " + getDescription().getVersion() + " disabled.");
    }
    
    @Override
    public void onEnable() {
        EventManager manager = new EventManager(this);
        final PluginDescriptionFile pdf = getDescription();
        currentVersion = Double.valueOf(pdf.getVersion());
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        Config.init();
        if(Config.PPT_ENABLED) StreamDB.init();
        getCommand("hidestream").setExecutor(new Commands(this));
        
        if(Config.UPDATE_CHECKING) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateCheck(pdf);
                }
            });
            t.start();
            
            getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    updateCheck(pdf);
                }
            }, 20*60*60*4, 20*60*60*4);
        }
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {}
        Log.info("Version " + pdf.getVersion() + " enabled.");
    }
    
    private void updateCheck(PluginDescriptionFile pdf) {
        Log.info("Running update checker ..");
        
        try {
            updater = new Updater(this, 37123, getFile(), Updater.UpdateType.NO_DOWNLOAD, false);

            Updater.UpdateResult result = updater.getResult();
            switch(result) {
                case NO_UPDATE:
                    Log.info("No update was found.");
                    break;
                case FAIL_DBO:
                    Log.warning("Failed to contact dev.bukkkit.org!");
                    break;
                case UPDATE_AVAILABLE:
                    updateFound = true;
                    Log.info(" --- ");
                    Log.info("A new version has been found! (" + updater.getLatestName() + ")");
                    Log.info("You are currently running " + pdf.getFullName());
                    Log.info(" --- ");
                    break;
                case DISABLED:
                    Log.info("Updater checker has been disabled in the updater config.");
                case FAIL_APIKEY:
                    Log.warning("The API key you have provided is incorrect!");
            }
        } catch(RuntimeException re) {
            Log.warning("Failed to establish a connection to dev.bukkit.org!");
        }
    }
}
