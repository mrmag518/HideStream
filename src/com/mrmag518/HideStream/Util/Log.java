package com.mrmag518.HideStream.Util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String prefix = "[HideStream] ";
    
    public static void info(String output) {
        log.log(Level.INFO,prefix + "{0}", output);
    }
    
    public static void severe(String output) {
        log.log(Level.SEVERE,prefix + "{0}", output);
    }
    
    public static void warning(String output) {
        log.log(Level.WARNING,prefix + "{0}", output);
    }
}
