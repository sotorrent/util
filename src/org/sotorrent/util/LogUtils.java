package org.sotorrent.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtils {
    private static Path defaultLogDir = Paths.get(System.getProperty("user.dir"), "logs");

    public static Logger getClassLogger(Class c) throws IOException {
        return getClassLogger(c, true, defaultLogDir);
    }

    public static Logger getClassLogger(Class c, Path logFileDir) throws IOException {
        return getClassLogger(c, true, logFileDir);
    }

    public static Logger getClassLogger(Class c, boolean consoleOutput) throws IOException {
        return getClassLogger(c, consoleOutput, defaultLogDir);
    }

    public static Logger getClassLogger(Class c, boolean consoleOutput, Path logFileDir) throws IOException {
        // ensure that log directory exists
        try {
            if (!Files.exists(logFileDir)) {
                Files.createDirectory(logFileDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String logFile = Paths.get(logFileDir.toString(), c.getSimpleName() + ".log").toString();

        // configure logger
        Logger logger = Logger.getLogger(c.getName());
        if (!consoleOutput) {
            logger.setUseParentHandlers(false); // disable handlers inherited from root logger
        }
        Handler fileHandler = new FileHandler(logFile);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

        return logger;
    }
}
