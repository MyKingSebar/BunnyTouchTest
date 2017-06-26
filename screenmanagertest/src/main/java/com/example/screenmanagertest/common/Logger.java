/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential.
 * @author LiLiang-Ping
 */
package com.example.screenmanagertest.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Logger
{
    public final static String   tag      = "[PosterDisplay]";
    
    private final static boolean logFlag  = true;
    private final static int     logLevel = Log.VERBOSE;

    /**
     * Get The Current Function Name
     * 
     * @return
     */
    private static String getFunctionName()
    {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        if (sts == null)
        {
            return null;
        }

        for (StackTraceElement st : sts)
        {
            if (st.isNativeMethod() || st.getClassName().equals(Thread.class.getName()) || st.getClassName().equals(Logger.class.getName()))
            {
                continue;
            }

            return "{Thread:" + Thread.currentThread().getName() + "}" + "[ " + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }

        return null;
    }

    /**
     * The Log Level:i
     * 
     * @param str
     */
    public static void i(Object str)
    {
        if (logFlag && logLevel <= Log.INFO)
        {
            String name = getFunctionName();
            if (name != null)
            {
                Log.i(tag, name + " - " + str);
            }
            else
            {
                Log.i(tag, str.toString());
            }
        }
    }

    /**
     * The Log Level:d
     * 
     * @param str
     */
    public static void d(Object str)
    {
        if (logFlag && logLevel <= Log.DEBUG)
        {
            String name = getFunctionName();
            if (name != null)
            {
                Log.d(tag, name + " - " + str);
            }
            else
            {
                Log.d(tag, str.toString());
            }
        }
    }

    /**
     * The Log Level:V
     * 
     * @param str
     */
    public static void v(Object str)
    {
        if (logFlag && logLevel <= Log.VERBOSE)
        {
            String name = getFunctionName();
            if (name != null)
            {
                Log.v(tag, name + " - " + str);
            }
            else
            {
                Log.v(tag, str.toString());
            }
        }
    }

    /**
     * The Log Level:w
     * 
     * @param str
     */
    public static void w(Object str)
    {
        if (logFlag && logLevel <= Log.WARN)
        {
            String name = getFunctionName();
            if (name != null)
            {
                Log.w(tag, name + " - " + str);
            }
            else
            {
                Log.w(tag, str.toString());
            }
        }
    }

    /**
     * The Log Level:e
     * 
     * @param str
     */
    public static void e(Object str)
    {
        if (logFlag && logLevel <= Log.ERROR)
        {
            String name = getFunctionName();
            if (name != null)
            {
                Log.e(tag, name + " - " + str);
            }
            else
            {
                Log.e(tag, str.toString());
            }
        }
    }

    /**
     * The Log Level:e
     * 
     * @param ex
     */
    public static void e(Exception ex)
    {
        if (logFlag && logLevel <= Log.ERROR)
        {
            Log.e(tag, "error", ex);
        }
    }

    /**
     * The Log Level:e
     * 
     * @param log
     * @param tr
     */
    public static void e(String log, Throwable tr)
    {
        if (logFlag)
        {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + line + ":] " + log + "\n", tr);
        }
    }
    
}
