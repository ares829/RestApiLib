package idv.ares.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class Utils {
	
    public static boolean isNetworkConnected(Context ctx) {
    	ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	return (networkInfo != null) ? networkInfo.isConnected() : false;
    	
    }
    
    public static String getVersionName(Context ctx) {
        String version = "Unknown";
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(ctx.getClass().getSimpleName(), "Version name not found in package");
        }
        return version;
    }
    
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize1stChar(model);
        } else { 
            return capitalize1stChar(manufacturer) + " " + model;
        } 
    } 
          
    private static String capitalize1stChar(String s) {
        if (s == null || s.length() <= 0) {
            return ""; 
        } 
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else { 
            return Character.toUpperCase(first) + s.substring(1);
        } 
    }
}
