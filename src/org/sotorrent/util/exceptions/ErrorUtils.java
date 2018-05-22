package org.sotorrent.util.exceptions;

import java.io.*;

public class ErrorUtils {
    public static void redirectConsoleMessagesToFile(File outputFile) throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream(outputFile));
        System.setOut(out);
        System.setErr(out);
    }

    public static String exceptionStackTraceToString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
