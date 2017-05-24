package idv.ares.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.apache.log4j.Level;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Logger {
	private static final String TAG = Logger.class.getSimpleName();

	private static final LogConfigurator logConfigurator = new LogConfigurator();

	public static void init() {
		logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "appLog.log");
		logConfigurator.setRootLevel(Level.DEBUG);
		// Set log level of a specific logger
		logConfigurator.setLevel("org.apache", Level.ALL);
		logConfigurator.setMaxFileSize(2048000);
		logConfigurator.setUseFileAppender(true);
		logConfigurator.setUseLogCatAppender(true);
		logConfigurator.configure();
	}
	
	private static boolean isDebug = true;	//BuildConfig.DEBUG;	// Make it configurable
	private static final org.apache.log4j.Logger log4 = org.apache.log4j.Logger.getLogger(Logger.class);

	public static void openDbgMsg(boolean flag) {
		if (flag) {
			init();
		}
		isDebug = flag;
	}

	public static void v(String tag, String msg) {
		if (isDebug) {
			Log.v(tag, msg);
			log4.info(msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
			log4.debug(msg);
		}
	}

	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
			log4.info(msg);
		}
	}

	public static void w(String tag, String msg) {
		if (isDebug) {
			Log.w(tag, msg);
			log4.warn(msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
			log4.error(msg);
		}
	}

	public static void wtf(String tag, String msg) {
		if (isDebug) {
			Log.wtf(tag, msg);
			log4.fatal(msg);
		}
	}

	public static Intent createLogFileMailIntent(Context ctx, String[] mails) {
		String archive = Environment.getExternalStorageDirectory() + File.separator + "appLog.zip";
		zipLogFile(archive);

		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("application/zip");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, mails);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Log File");
		
		StringBuffer sb = new StringBuffer();
		sb.append("Device Model: " + Utils.getDeviceName() + "\n");
		sb.append("Device Version: " + Build.VERSION.RELEASE + "\n");
		sb.append("App Version: " + Utils.getVersionName(ctx) + "\n");
		emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		 
		// Log.v(getClass().getSimpleName(), "sPhotoUri=" + Uri.parse("file:/"+ sPhotoFileName));
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + archive));

		return emailIntent;
	}

	private static void zipLogFile(String archive) {
		String src = logConfigurator.getFileName();
		try {
			final int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(archive);

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

			byte data[] = new byte[2048];

			FileInputStream fi = new FileInputStream(src);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(src.substring(src.lastIndexOf("/") + 1));
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();

			out.close();
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
