package adneom.poc_camscanner.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by gtshilombowanticale on 12-07-17.
 */

public class Utils {

    public static final String PACKAGE_NAME_CAM_SCANNER = "com.intsig.camscanner";
    public static final String URL_MARKET = "market://details?id=";
    public static final String URL_MARKET_HTTPS = "https://play.google.com/store/apps/details?id=";
    public static final Integer REQUEST_CODE = 9;
    public static final Integer REQUEST_CODE_PERMISSIONS = 11;
    public static final Integer PERMISSION_READ_EXTERNAL = 12;
    public static final Integer CODE_GALLERY_RESULT = 15;

    /**
     * This method allows to verify if an application is installed in the device
     *
     * @param context
     */
    public static boolean isCamScannerInstalled(Context context){
        boolean isInstalled = true;
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(PACKAGE_NAME_CAM_SCANNER,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("E","can't getting package Info "+e.getMessage());
        }
        if(packageInfo != null){
            //launchApplication(PACKAGE_NAME_CAM_SCANNER,context);
            isInstalled = true;
        }else{
            //redirectToMarket(PACKAGE_NAME_CAM_SCANNER,context);
            isInstalled = false;
        }
        return isInstalled;
    }

    /**
     * Start an application from this application
     *
     * @param packageName
     * @param context
     */
    public static void launchApplication(String packageName, Context context){
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(launchIntent);
    }

    /**
     * Redirect user to store if the application si not installed
     */
    public static void redirectToMarket(String packageName, Context context){
        try{
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_MARKET + packageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_MARKET_HTTPS + packageName)));
        }
    }

}
