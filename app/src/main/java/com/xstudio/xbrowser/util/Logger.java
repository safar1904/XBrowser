package com.xstudio.xbrowser.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.xstudio.xbrowser.ThisApplication;

public class Logger
{

    public static final int DEBUG = 4;
    public static final int INFO = 3;
    public static final int WARN = 2;
    public static final int ERROR = 1;
    public static final int NONE = 0;
    private static int sLogLevel = NONE;
    private static String sLogDirectory = "/sdcard/.logcat";
    private static File logDir;
    private static File logFile;


    public static void debug(final String tag, final String message)
    {
        if (sLogLevel < DEBUG) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    write("DEBUG:" + tag, message);
                } catch (IOException e) {}
            }
        }.start();
    }

    public static void info(final String tag, final String message)
    {
        if (sLogLevel < INFO) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    write("INFO:" + tag, message);
                } catch (IOException e) {}
            }
        }.start();
    }

    public static void warn(final String tag, final String message)
    {
        if (sLogLevel < WARN) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    write("WARNING:" + tag, message);
                } catch (IOException e) {}
            }
        }.start();
    }

    public static void error(final String tag, final String message) {
        if (sLogLevel < ERROR) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    write("ERROR:" + tag, message);
                } catch (IOException e) {}
            }
        }.start();
    }

    public static void crash(final Throwable e) {
        try {
            write(e);
        } catch (IOException err) {}
    }

    public static void setLogLevel(int level) {
        sLogLevel = level;
    }

    public static void setLogDirectory(String path) {
        sLogDirectory = path;
    }

    private synchronized static void write(String tag, String msg) throws IOException
    {
        if (logDir == null) {
            logDir = new File(sLogDirectory + File.separator + ThisApplication.getInstance().getPackageName());
        }
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        if (logFile == null) {
            logFile = new File(logDir, "Log.txt");
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(logFile, true);
            writer.write("[" + tag + "] " + msg + "\n");
        } finally {
            if (writer != null) writer.close();
        }
    }

    private synchronized static void write(Throwable e) throws IOException
    {
        if (logDir == null) {
            logDir = new File(sLogDirectory + File.separator + ThisApplication.getInstance().getPackageName());
        }
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        if (logFile == null) {
            logFile = new File(logDir, "Log.txt");
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(logFile, true);
            writer.write("\n[CRASH] : " + e.getMessage() + "\n");
            for (StackTraceElement trace : e.getStackTrace()) {
                writer.write(trace.toString() + "\n");
            }
        } finally {
            if (writer != null) writer.close();
        }
    }

}
