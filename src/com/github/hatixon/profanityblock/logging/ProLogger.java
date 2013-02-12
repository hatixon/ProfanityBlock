package com.github.hatixon.profanityblock.logging;

import java.io.*;
import java.util.logging.*;

// Referenced classes of package fr.neatmonster.nocheatplus.logging:
//            LogUtil

public class ProLogger
{
    protected static class LogFileFormatter extends Formatter
    {


        public static LogFileFormatter newInstance()
        {
            return new LogFileFormatter();
        }

        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder();
            Throwable ex = record.getThrown();
            builder.append(record.getMessage());
            builder.append('\n');
            if(ex != null)
            {
                StringWriter writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }
            return builder.toString();
        }

        private LogFileFormatter()
        {
        }
    }


    public static Logger fileLogger = null;
    private static FileHandler fileHandler = null;

    public ProLogger()
    {
    }

    public static void cleanup()
    {
        fileHandler.flush();
        fileHandler.close();
        Logger logger = Logger.getLogger("ProfanityBlock");
        logger.removeHandler(fileHandler);
        fileHandler = null;
    }

    public static void setupLogger(File logFile)
    {
        Logger logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);
        java.util.logging.Handler arr$[] = logger.getHandlers();
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            java.util.logging.Handler h = arr$[i$];
            logger.removeHandler(h);
        }

        if(fileHandler != null)
        {
            fileHandler.close();
            logger.removeHandler(fileHandler);
            fileHandler = null;
        }
        try
        {
            try
            {
                logFile.getParentFile().mkdirs();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            fileHandler = new FileHandler(logFile.getCanonicalPath(), true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(LogFileFormatter.newInstance());
            logger.addHandler(fileHandler);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        fileLogger = logger;
    }

}
