package com.mrmag518.HideStream.Util;

import java.util.logging.Logger;

public class Log {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String prefix = "[HideStream] ";
    
    public static void info(String output) {
        log.info(prefix + output);
    }
    
    public static void severe(String output) {
        log.severe(prefix + output);
    }
    
    public static void warning(String output) {
        log.warning(prefix + output);
    }
}
