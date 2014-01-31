package com.mrmag518.HideStream.Util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author Gravity
 * @version 2.1
 */
public class Updater {
    private Plugin plugin;
    private UpdateType type;
    private String versionName;
    private String versionLink;
    private String versionType;
    private String versionGameVersion;
    private boolean announce;
    private URL url;
    private File file;
    private Thread thread;
    private int id = -1;
    private String apiKey = null;
    private static final String TITLE_VALUE = "name";
    private static final String LINK_VALUE = "downloadUrl";
    private static final String TYPE_VALUE = "releaseType";
    private static final String VERSION_VALUE = "gameVersion";
    private static final String QUERY = "/servermods/files?projectIds=";
    private static final String HOST = "https://api.curseforge.com";
    private static final String USER_AGENT = "Updater (by Gravity)";
    private static final String delimiter = "^v|[\\s_-]v";
    private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT" };
    private static final int BYTE_SIZE = 1024;
    private final YamlConfiguration config = new YamlConfiguration();
    private String updateFolder;
    private Updater.UpdateResult result = Updater.UpdateResult.SUCCESS;
    
    public enum UpdateResult {
        SUCCESS,
        NO_UPDATE,
        DISABLED,
        FAIL_DOWNLOAD,
        FAIL_DBO,
        FAIL_NOVERSION,
        FAIL_BADID,
        FAIL_APIKEY,
        UPDATE_AVAILABLE;
    }
    
    public enum UpdateType {
        DEFAULT, NO_VERSION_CHECK, NO_DOWNLOAD;
    }
    
    public enum ReleaseType {
        ALPHA, BETA, RELEASE;
    }
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public Updater(Plugin plugin, int id, File file, UpdateType type, boolean announce) {
        this.plugin = plugin;
        this.type = type;
        this.announce = announce;
        this.file = file;
        this.id = id;
        this.updateFolder = plugin.getServer().getUpdateFolder();

        final File pluginFile = plugin.getDataFolder().getParentFile();
        final File updaterFile = new File(pluginFile, "Updater");
        final File updaterConfigFile = new File(updaterFile, "config.yml");

        this.config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )" + '\n'
                + "If you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below." + '\n'
                + "Some updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        this.config.addDefault("api-key", "PUT_API_KEY_HERE");
        this.config.addDefault("disable", false);

        if (!updaterFile.exists()) {
            updaterFile.mkdir();
        }

        boolean createFile = !updaterConfigFile.exists();
        try {
            if (createFile) {
                updaterConfigFile.createNewFile();
                this.config.options().copyDefaults(true);
                this.config.save(updaterConfigFile);
            } else {
                this.config.load(updaterConfigFile);
            }
        } catch (final Exception e) {
            if (createFile) {
                plugin.getLogger().severe("The updater could not create configuration at " + updaterFile.getAbsolutePath());
            } else {
                plugin.getLogger().severe("The updater could not load configuration at " + updaterFile.getAbsolutePath());
            }
            plugin.getLogger().log(Level.SEVERE, null, e);
        }

        if (this.config.getBoolean("disable")) {
            this.result = UpdateResult.DISABLED;
            return;
        }

        String key = this.config.getString("api-key");
        if (key.equalsIgnoreCase("PUT_API_KEY_HERE") || key.equals("")) {
            key = null;
        }

        this.apiKey = key;

        try {
            this.url = new URL(Updater.HOST + Updater.QUERY + id);
        } catch (final MalformedURLException e) {
            plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + id + " is invalid.", e);
            this.result = UpdateResult.FAIL_BADID;
        }

        this.thread = new Thread(new UpdateRunnable());
        this.thread.start();
    }
    
    public Updater.UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }
    
    public ReleaseType getLatestType() {
        this.waitForThread();
        if (this.versionType != null) {
            for (ReleaseType rt : ReleaseType.values()) {
                if (this.versionType.equals(rt.name().toLowerCase())) {
                    return rt;
                }
            }
        }
        return null;
    }
    
    public String getLatestGameVersion() {
        this.waitForThread();
        return this.versionGameVersion;
    }
    
    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }
    
    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }
    
    private void waitForThread() {
        if ((this.thread != null) && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (final InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }
    
    private void saveFile(File folder, String file, String link) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            final URL url2 = new URL(link);
            final int fileLength = url2.openConnection().getContentLength();
            in = new BufferedInputStream(url2.openStream());
            fout = new FileOutputStream(folder.getAbsolutePath() + File.separator + file);

            final byte[] data = new byte[Updater.BYTE_SIZE];
            int count;
            if (this.announce) {
                this.plugin.getLogger().info("About to download a new update: " + this.versionName);
            }
            long downloaded = 0;
            while ((count = in.read(data, 0, Updater.BYTE_SIZE)) != -1) {
                downloaded += count;
                fout.write(data, 0, count);
                final int percent = (int) ((downloaded * 100) / fileLength);
                if (this.announce && ((percent % 10) == 0)) {
                    this.plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
                }
            }
            
            for (final File xFile : new File(this.plugin.getDataFolder().getParent(), this.updateFolder).listFiles()) {
                if (xFile.getName().endsWith(".zip")) {
                    xFile.delete();
                }
            }
            
            final File dFile = new File(folder.getAbsolutePath() + File.separator + file);
            if (dFile.getName().endsWith(".zip")) {
                this.unzip(dFile.getCanonicalPath());
            }
            if (this.announce) {
                this.plugin.getLogger().info("Finished updating.");
            }
        } catch (final IOException ex) {
            this.plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
            this.result = Updater.UpdateResult.FAIL_DOWNLOAD;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException ex) {
            }
        }
    }
    
    private void unzip(String file) {
        try {
            final File fSourceZip = new File(file);
            final String zipPath = file.substring(0, file.length() - 4);
            ZipFile zipFile = new ZipFile(fSourceZip);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                File destinationFilePath = new File(zipPath, entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if (entry.isDirectory()) {
                    continue;
                } else {
                    final BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    int b;
                    final byte buffer[] = new byte[Updater.BYTE_SIZE];
                    final FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    final BufferedOutputStream bos = new BufferedOutputStream(fos, Updater.BYTE_SIZE);
                    while ((b = bis.read(buffer, 0, Updater.BYTE_SIZE)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                    final String name = destinationFilePath.getName();
                    if (name.endsWith(".jar") && this.pluginFile(name)) {
                        destinationFilePath.renameTo(new File(this.plugin.getDataFolder().getParent(), this.updateFolder + File.separator + name));
                    }
                }
                entry = null;
                destinationFilePath = null;
            }
            e = null;
            zipFile.close();
            zipFile = null;
            
            for (final File dFile : new File(zipPath).listFiles()) {
                if (dFile.isDirectory()) {
                    if (this.pluginFile(dFile.getName())) {
                        final File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName());
                        final File[] contents = oFile.listFiles();
                        for (final File cFile : dFile.listFiles())
                        {
                            boolean found = false;
                            for (final File xFile : contents)
                            {
                                if (xFile.getName().equals(cFile.getName())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                cFile.renameTo(new File(oFile.getCanonicalFile() + File.separator + cFile.getName()));
                            } else {
                                cFile.delete();
                            }
                        }
                    }
                }
                dFile.delete();
            }
            new File(zipPath).delete();
            fSourceZip.delete();
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
            this.result = Updater.UpdateResult.FAIL_DOWNLOAD;
        }
        new File(file).delete();
    }
    
    private boolean pluginFile(String name) {
        for (final File f : new File("plugins").listFiles()) {
            if (f.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean versionCheck(String title) {
        if (this.type != UpdateType.NO_VERSION_CHECK) {
            final String localVersion = this.plugin.getDescription().getVersion();
            if (title.split(delimiter).length == 2) {
                final String remoteVersion = title.split(delimiter)[1].split(" ")[0]; // Get the newest file's version number
                
                if (this.hasTag(localVersion) || !this.shouldUpdate(localVersion, remoteVersion)) {
                    this.result = Updater.UpdateResult.NO_UPDATE;
                    return false;
                }
            } else {
                final String authorInfo = this.plugin.getDescription().getAuthors().isEmpty() ? "" : " (" + this.plugin.getDescription().getAuthors().get(0) + ")";
                this.plugin.getLogger().warning("The author of this plugin" + authorInfo + " has misconfigured their Auto Update system");
                this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
                this.plugin.getLogger().warning("Please notify the author of this error.");
                this.result = Updater.UpdateResult.FAIL_NOVERSION;
                return false;
            }
        }
        return true;
    }
    
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return !localVersion.equalsIgnoreCase(remoteVersion);
    }
    
    private boolean hasTag(String version) {
        for (final String string : Updater.NO_UPDATE_TAG) {
            if (version.contains(string)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean read() {
        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);

            if (this.apiKey != null) {
                conn.addRequestProperty("X-API-Key", this.apiKey);
            }
            conn.addRequestProperty("User-Agent", Updater.USER_AGENT);

            conn.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }

            this.versionName = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TITLE_VALUE);
            this.versionLink = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.LINK_VALUE);
            this.versionType = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TYPE_VALUE);
            this.versionGameVersion = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.VERSION_VALUE);

            return true;
        } catch (final IOException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
                this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
                this.result = UpdateResult.FAIL_APIKEY;
            } else {
                this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
                this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                this.result = UpdateResult.FAIL_DBO;
            }
            this.plugin.getLogger().log(Level.SEVERE, null, e);
            return false;
        }
    }

    private class UpdateRunnable implements Runnable {
        @Override
        public void run() {
            if (Updater.this.url != null) {
                if (Updater.this.read()) {
                    if (Updater.this.versionCheck(Updater.this.versionName)) {
                        if ((Updater.this.versionLink != null) && (Updater.this.type != UpdateType.NO_DOWNLOAD)) {
                            String name = Updater.this.file.getName();
                            if (Updater.this.versionLink.endsWith(".zip")) {
                                final String[] split = Updater.this.versionLink.split("/");
                                name = split[split.length - 1];
                            }
                            Updater.this.saveFile(new File(Updater.this.plugin.getDataFolder().getParent(), Updater.this.updateFolder), name, Updater.this.versionLink);
                        } else {
                            Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
                        }
                    }
                }
            }
        }
    }
}
