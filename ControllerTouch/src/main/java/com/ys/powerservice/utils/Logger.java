package com.ys.powerservice.utils;

import android.util.Log;

public class Logger {
	private static final String TAG = "[YSSysController]";
	
	private final static boolean mLogFlag = true;
    private final static int mLogLevel = Log.VERBOSE;

	private static String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();

		if (sts == null) {
			return null;
		}

		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()
					|| st.getClassName().equals(Thread.class.getName())
					|| st.getClassName().equals(Logger.class.getName())) {
				continue;
			}

			return "{Thread:" + Thread.currentThread().getName() + "}" + "[ "
					+ st.getFileName() + ":" + st.getLineNumber() + " "
					+ st.getMethodName() + " ]";
		}

		return null;
	}

	public static void i(Object str) {
		if (mLogFlag && mLogLevel <= Log.INFO) {
			String name = getFunctionName();
			if (name != null) {
				Log.i(TAG, name + " - " + str);
			} else {
				Log.i(TAG, str.toString());
			}
		}
	}

	public static void d(Object str) {
		if (mLogFlag && mLogLevel <= Log.DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.d(TAG, name + " - " + str);
			} else {
				Log.d(TAG, str.toString());
			}
		}
	}

	public static void v(Object str) {
		if (mLogFlag && mLogLevel <= Log.VERBOSE) {
			String name = getFunctionName();
			if (name != null) {
				Log.v(TAG, name + " - " + str);
			} else {
				Log.v(TAG, str.toString());
			}
		}
	}

	public static void w(Object str) {
		if (mLogFlag && mLogLevel <= Log.WARN) {
			String name = getFunctionName();
			if (name != null) {
				Log.w(TAG, name + " - " + str);
			} else {
				Log.w(TAG, str.toString());
			}
		}
	}
	
	public static void e(Object str) {
		if (mLogFlag && mLogLevel <= Log.ERROR) {
			String name = getFunctionName();
			if (name != null) {
				Log.e(TAG, name + " - " + str);
			} else {
				Log.e(TAG, str.toString());
			}
		}
	}
}
