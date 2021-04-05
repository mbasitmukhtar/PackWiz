package com.example.packwiz;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

public class NetworkStatistics
{
    private static final String TAG = "NetworkStatistics";
    public Context context;

    public NetworkStatistics(Context mcontext)
    {
        context = mcontext;
    }


    public void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(), context.getPackageName());

            if (mode == AppOpsManager.MODE_ALLOWED)
            {
                Log.d(TAG, "checkPermission: has permission");
                try
                {
                    getDataUsageInformation();
                } catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                Log.d(TAG, "checkPermission: does not have permission");
                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        }

    }

    private void getDataUsageInformation() throws PackageManager.NameNotFoundException, RemoteException
    {
        Log.d(TAG, "getDataUsageInformation: GB's received: " + ((android.net.TrafficStats.getMobileRxBytes() / 1024) / 1024) / 1024);
        Log.d(TAG, "getDataUsageInformation: GB's transmitted: " + ((android.net.TrafficStats.getMobileTxBytes() / 1024) / 1024) / 1024);

        Log.d(TAG, "getDataUsageInformation: Total GB's received: " + ((android.net.TrafficStats.getTotalRxBytes() / 1024) / 1024) / 1024);
        Log.d(TAG, "getDataUsageInformation: Total GB's transmitted: " + ((android.net.TrafficStats.getTotalTxBytes() / 1024) / 1024) / 1024);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
//        {
//            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//            String subscriberId = telephonyManager.getSubscriberId();
//            PackageManager packageManager = context.getPackageManager();
//
//            ApplicationInfo info = packageManager.getApplicationInfo("com.example.app", 0);
//
//            int uid = info.uid;
//
//            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberId, 0, System.currentTimeMillis());          
//
//            Log.d(TAG, "getDataUsageInformation: getRxBytes: " + bucket.getRxBytes());
//            Log.d(TAG, "getDataUsageInformation: getRxBytes: " + bucket.getTxBytes());
//
//            long rxBytes = 0L; 
//            long txBytes = 0L; 
//            NetworkStats.Bucket bucket1 = new NetworkStats.Bucket(); 
//            while (networkStats.hasNextBucket()) { 
//                networkStats.getNextBucket(bucket1);
//                rxBytes += bucket1.getRxBytes();
//                txBytes += bucket1.getTxBytes();
//            }
//            networkStats.close();
//        }

    }
}
