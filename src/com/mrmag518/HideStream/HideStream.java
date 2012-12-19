package com.mrmag518.HideStream;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HideStream extends JavaPlugin {
    public final Logger log = Logger.getLogger("Minecraft");
    public EventManager streamListener = null;
    public SendUpdate SU = null;
    public FileConfiguration config;
    public static Permission perms = null;
    public double currentVersion;
    public double newVersion;
    public boolean debugMode = false;
    public final String debugPrefix = "[HideStream DEBUG] ";
    
    @Override
    public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();
        log.info("[" + pdffile.getName() + "]" + " v" + pdffile.getVersion() + " Disabled succesfully.");
    }
    
    @Override
    public void onEnable() {
        streamListener = new EventManager(this);
        SU = new SendUpdate(this);
        currentVersion = Double.valueOf(getDescription().getVersion());
        
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        
        reloadConfig();
        loadConfig();
        reloadConfig();
        
        StreamDB.reload();
        StreamDB.load();
        StreamDB.reload();
        
        getCommand("hidestream").setExecutor(new Commands(this));
        
        try {
            debugMode = getConfig().getBoolean("DebugMode");
            debugLog("debugMode assigned to config node.");
            debugLog("debugMode is enabled.");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        if(getConfig().getBoolean("CheckForUpdates") == true) {
            debugLog("Starting update check scheduler ..");
            debugLog("Will check every 36000th tick.");
            //Update checker - From MilkBowl.
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        newVersion = updateCheck(currentVersion);

                        if(newVersion > currentVersion) {
                            log.info(" ");
                            log.info("#######  HideStream UpdateChecker  #######");
                            log.info("A new update for HideStream was found! " + newVersion);
                            log.info("You are currently running version: " + currentVersion);
                            log.info("You can find this new version at BukkitDev.");
                            log.info("http://dev.bukkit.org/server-mods/hidestream/");
                            log.info("#####################################");
                            log.info(" ");
                        }
                    } catch (Exception ignored) {
                    }
                }
            }, 0, 36000);
        }
        setupVault();
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        
        PluginDescriptionFile pdffile = this.getDescription();
        log.info("[" + pdffile.getName() + "]" + " v" + pdffile.getVersion() + " Enabled succesfully.");
    }
    
    public void debugLog(String output) {
        if(debugMode == true) {
            log.info(debugPrefix + output);
        }
    }
    
    public void sendNoPermissionNotify(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + colorize(config.getString("NoCommandPermissionMsg")));
    }
    
    public String colorize(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
    
    private void loadConfig() {
        config = getConfig();
        config.options().header("For an explanation of these configuration settings, please visit\n"
                + "http://dev.bukkit.org/server-mods/hidestream/pages/config-explanation/" + " \n");
        
        config.addDefault("Enabled", true);
        config.addDefault("UseVault", false);
        config.addDefault("CheckForUpdates", true);
        config.addDefault("DebugMode", false);
        config.addDefault("EnablePerPlayerToggle", false);
        config.addDefault("NoCommandPermissionMsg", "&cNo permission.");
        
        config.addDefault("Join.HideJoinStream", true);
        config.addDefault("Join.Permissions.UsePermissions", false);
        config.addDefault("Join.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Join.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Join.OPSupport.Enabled", false);
        config.addDefault("Join.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Join.OPSupport.OnlyHideIfOP", false);
        
        config.addDefault("Quit.HideQuitStream", true);
        config.addDefault("Quit.Permissions.UsePermissions", false);
        config.addDefault("Quit.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Quit.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Quit.OPSupport.Enabled", false);
        config.addDefault("Quit.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Quit.OPSupport.OnlyHideIfOP", false);
        
        config.addDefault("Kick.HideKickStream", true);
        config.addDefault("Kick.Permissions.UsePermissions", false);
        config.addDefault("Kick.Permissions.HideOnlyIfHasPermission", false);
        config.addDefault("Kick.Permissions.HideOnlyIfWithoutPermission", false);
        config.addDefault("Kick.OPSupport.Enabled", false);
        config.addDefault("Kick.OPSupport.OnlyHideIfNotOP", false);
        config.addDefault("Kick.OPSupport.OnlyHideIfOP", false);
        
        checkConfig();
        
        getConfig().options().copyDefaults(true);
        saveConfig();
        debugLog(config.getName() + " loaded and saved successfully.");
    }
    
    /**
     * Checks outdated settings, and converts them if found.
     */
    private void checkConfig() {
        config = getConfig();
        
        if(config.get("Join.UsePermissions") != null) {
            boolean storedValue = config.getBoolean("Join.UsePermissions");
            config.set("Join.Permissions.UsePermissions", storedValue);
            config.set("Join.UsePermissions", null);
            log.info("[HideStream] Converted config node 'Join.UsePermissions: " + storedValue + "', into 'Join.Permissions.UsePermissions: " + storedValue + "'.");
        }
        
        if(config.get("Join.OnlyHideForUsersWithPermission") != null) {
            boolean storedValue = config.getBoolean("Join.OnlyHideForUsersWithPermission");
            config.set("Join.Permissions.HideOnlyIfHasPermission", storedValue);
            config.set("Join.OnlyHideForUsersWithPermission", null);
            log.info("[HideStream] Converted config node 'Join.OnlyHideForUsersWithPermission: " + storedValue + "', into 'Join.Permissions.HideOnlyIfHasPermission: " + storedValue + "'.");
        }
        
        if(config.get("Join.OnlyHideForUsersWithoutPermission") != null) {
            boolean storedValue = config.getBoolean("Join.OnlyHideForUsersWithoutPermission");
            config.set("Join.Permissions.HideOnlyIfWithoutPermission", storedValue);
            config.set("Join.OnlyHideForUsersWithoutPermission", null);
            log.info("[HideStream] Converted config node 'Join.OnlyHideForUsersWithoutPermission: " + storedValue + "', into 'Join.Permissions.HideOnlyIfWithoutPermission: " + storedValue + "'.");
        }
        
        
        if(config.get("Quit.UsePermissions") != null) {
            boolean storedValue = config.getBoolean("Quit.UsePermissions");
            config.set("Quit.Permissions.UsePermissions", storedValue);
            config.set("Quit.UsePermissions", null);
            log.info("[HideStream] Converted config node 'Quit.UsePermissions: " + storedValue + "', into 'Quit.Permissions.UsePermissions: " + storedValue + "'.");
        }
        
        if(config.get("Quit.OnlyHideForUsersWithPermission") != null) {
            boolean storedValue = config.getBoolean("Quit.OnlyHideForUsersWithPermission");
            config.set("Quit.Permissions.HideOnlyIfHasPermission", storedValue);
            config.set("Quit.OnlyHideForUsersWithPermission", null);
            log.info("[HideStream] Converted config node 'Quit.OnlyHideForUsersWithPermission: " + storedValue + "', into 'Quit.Permissions.HideOnlyIfHasPermission: " + storedValue + "'.");
        }
        
        if(config.get("Quit.OnlyHideForUsersWithoutPermission") != null) {
            boolean storedValue = config.getBoolean("Quit.OnlyHideForUsersWithoutPermission");
            config.set("Quit.Permissions.HideOnlyIfWithoutPermission", storedValue);
            config.set("Quit.OnlyHideForUsersWithoutPermission", null);
            log.info("[HideStream] Converted config node 'Quit.OnlyHideForUsersWithoutPermission: " + storedValue + "', into 'Quit.Permissions.HideOnlyIfWithoutPermission: " + storedValue + "'.");
        }
        
        
        if(config.get("Kick.UsePermissions") != null) {
            boolean storedValue = config.getBoolean("Kick.UsePermissions");
            config.set("Kick.Permissions.UsePermissions", storedValue);
            config.set("Kick.UsePermissions", null);
            log.info("[HideStream] Converted config node 'Kick.UsePermissions: " + storedValue + "', into 'Kick.Permissions.UsePermissions: " + storedValue + "'.");
        }
        
        if(config.get("Kick.OnlyHideForUsersWithPermission") != null) {
            boolean storedValue = config.getBoolean("Kick.OnlyHideForUsersWithPermission");
            config.set("Kick.Permissions.HideOnlyIfHasPermission", storedValue);
            config.set("Kick.OnlyHideForUsersWithPermission", null);
            log.info("[HideStream] Converted config node 'Kick.OnlyHideForUsersWithPermission: " + storedValue + "', into 'Kick.Permissions.HideOnlyIfHasPermission: " + storedValue + "'.");
        }
        
        if(config.get("Kick.OnlyHideForUsersWithoutPermission") != null) {
            boolean storedValue = config.getBoolean("Kick.OnlyHideForUsersWithoutPermission");
            config.set("Kick.Permissions.HideOnlyIfWithoutPermission", storedValue);
            config.set("Kick.OnlyHideForUsersWithoutPermission", null);
            log.info("[HideStream] Converted config node 'Kick.OnlyHideForUsersWithoutPermission: " + storedValue + "', into 'Kick.Permissions.HideOnlyIfWithoutPermission: " + storedValue + "'.");
        }
    }
    
    private void setupVault() {
        if(getConfig().getBoolean("UseVault") == true) 
        {
            debugLog("UseVault is true in the config, checking Vault state ..");
            
            if(getServer().getPluginManager().getPlugin("Vault") != null) 
            {
                debugLog("Vault found! Setting up permissions ..");
                setupPermissions();
            } else {
                log.severe("[HideStream] Vault.jar was NOT found in your plugins folder!");
                log.severe("[HideStream] You need to have Vault.jar enabled for Vault support to work!");
                log.warning("[HideStream] Settings UseVault in your config.yml to false ..");
                getConfig().set("UseVault", false);
                saveConfig();
            }
        }
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        log.info("[HideStream] Hooked to permissions plugin: " + perms.getName());
        return perms != null;
    }
    
    public boolean hasPermission(CommandSender sender, String permission) {
        if(getConfig().getBoolean("UseVault") == true) {
            if(perms.has(sender, permission)) {
                return true;
            } else {
                sendNoPermissionNotify(sender);
                return false;
            }
        } else {
            if(sender.hasPermission(permission)) {
                return true;
            } else {
                sendNoPermissionNotify(sender);
                return false;
            }
        }
    }
    
    public boolean hasPermission(Player p, String permission) {
        if(getConfig().getBoolean("UseVault") == true) {
            if(perms.has(p, permission)) {
                return true;
            } else {
                return false;
            }
        } else {
            if(p.hasPermission(permission)) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    //Update checker (from MilkBowl's Vault, all credits to him)
    public double updateCheck(double currentVersion) throws Exception {
        String pluginUrlString = "http://dev.bukkit.org/server-mods/hidestream/files.rss";
        try {
            URL url = new URL(pluginUrlString);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element)firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return Double.valueOf(firstNodes.item(0).getNodeValue().replaceFirst(".", "").replace("v", "").trim());
            }
        }
        catch (Exception localException) {
        }
        return currentVersion;
    }
}
