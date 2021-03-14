package com.example.packwiz.Fragments;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.packwiz.CallLogInfo;
import com.example.packwiz.CallLogUtils;
import com.example.packwiz.R;
import com.example.packwiz.UStats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class AllUsageFragment extends Fragment
{
    private static final String TAG = "AllUsageFragment";

    CallLogUtils logs;

    ProgressBar jazzBar, zongBar, ufoneBar, telenorBar, smsBar, mbsBar;
    TextView jazzCalls, zongCalls, ufoneCalls, telenorCalls, sms, mbs;

    public AllUsageFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null)
        {
            getRuntimePermission();

            logs = CallLogUtils.getInstance(getActivity());
            getViews();
            getCallsData();
            getSmsCount();
//            hasPermission(getActivity());
            getUsageStatsPermissionsStatus(getActivity());
        }
    }

    private void getCallsData()
    {
        logs.loadData();
        getJazzCallsNumber();
        getZongCallsNumber();
        getUfoneCallsNumber();
        getTelenorCallsNumber();

        Timer timer = new Timer();
        timer.schedule(new Refresh(), 0, 5000);
    }

    class Refresh extends TimerTask
    {

        public void run()
        {
            Log.d(TAG, "Refresh run: ");
            if (getActivity() != null)
            {
                getActivity().runOnUiThread(() ->
                {
                    Log.d(TAG, "runOnUiThread: ");
//                    logs = CallLogUtils.getInstance(getActivity());
                    logs.loadData();

                    getJazzCallsNumber();
                    getZongCallsNumber();
                    getUfoneCallsNumber();
                    getTelenorCallsNumber();

                    getSmsCount();

                });
            }
        }
    }

    private void getViews()
    {
        if (getActivity() != null)
        {
            jazzBar = getActivity().findViewById(R.id.jazzCallsProgressBar);
            zongBar = getActivity().findViewById(R.id.zongCallsProgressBar);
            ufoneBar = getActivity().findViewById(R.id.ufoneCallsProgressBar);
            telenorBar = getActivity().findViewById(R.id.telenorCallsProgressBar);
            smsBar = getActivity().findViewById(R.id.smsProgressBar);
            mbsBar = getActivity().findViewById(R.id.mbsProgressBar);

            jazzCalls = getActivity().findViewById(R.id.jazzCalls);
            zongCalls = getActivity().findViewById(R.id.zongCalls);
            ufoneCalls = getActivity().findViewById(R.id.ufoneCalls);
            telenorCalls = getActivity().findViewById(R.id.telenorCalls);
            sms = getActivity().findViewById(R.id.sms);
            mbs = getActivity().findViewById(R.id.mbs);
        }
    }


    private void getZongCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getZongCalls();
        zongBar.setProgress(d.size());
        zongCalls.setText(String.valueOf(d.size()));
    }


    private void getJazzCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getJazzCalls();
        jazzBar.setProgress(d.size());
        jazzCalls.setText(String.valueOf(d.size()));
    }

    private void getUfoneCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getUfoneCalls();
        ufoneBar.setProgress(d.size());
        ufoneCalls.setText(String.valueOf(d.size()));
    }

    private void getTelenorCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getTelenorCalls();
        telenorBar.setProgress(d.size());
        telenorCalls.setText(String.valueOf(d.size()));
//        Toast.makeText(getActivity(), "Zong Logs: " + d.size(), Toast.LENGTH_LONG).show();
    }


    public void getSmsCount()
    {
        Log.d(TAG, "getSmsCount: ");
        int sentSmsCount = 0;
        Uri SMSURI = Uri.parse("content://sms");

//        String[] smsProjection = new String[]{"thread_id", "address", "date", "type", "body"};
//        ContentResolver cr = getActivity().getContentResolver();
//
//        Cursor cursor = getActivity().getContentResolver().query(SMSURI, smsProjection, "type = ? AND date > ?", new String[]{Integer.toString(OUTGOING), Long.toString(lastOutgoingSmsTime)}, null);

        if (getActivity() != null)
        {
            Cursor cursor = getActivity().getContentResolver().query(SMSURI, null, null,
                    null, null);
            if (cursor == null)
            {
                Log.d(TAG, "sms cursor is null");
                return;
            }

            if (cursor.getCount() == 0)
            {
                Log.d(TAG, "sms cursor size is 0");
                return;
            }
            cursor.moveToNext();
            while (!cursor.isAfterLast())
            {
                if (cursor.getString(cursor.getColumnIndex("type")).equals("2"))
                {
                    sentSmsCount += 1;
//                    Log.d(TAG, "getSmsCount: while loop type: outgoing, body is" + cursor.getString(cursor.getColumnIndex("body")));
                }
//                else
//                {
////                    Log.d(TAG, "getSmsCount: while loop type: incoming maybe, body is" + cursor.getString(cursor.getColumnIndex("body")));
//                }
                cursor.moveToNext();
            }
            cursor.close();
            Log.d(TAG, "getSmsCount: count: " + sentSmsCount);
            smsBar.setProgress(sentSmsCount);
            sms.setText(String.valueOf(sentSmsCount));
        }
    }


    public void getDataUsage()
    {

    }

    public boolean hasPermission(@NonNull final Context context)
    {
        try
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);

                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
                return (mode == AppOpsManager.MODE_ALLOWED);
            }
        } catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }


        // Usage Stats is theoretically available on API v19+, but official/reliable support starts with API v21.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            return false;
        }

        final AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        if (appOpsManager == null)
        {
            return false;
        }

        final int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED)
        {
            return false;
        }

        // Verify that access is possible. Some devices "lie" and return MODE_ALLOWED even when it's not.
        final long now = System.currentTimeMillis();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.add(Calendar.MONTH, -1);
        long monthAgo = c.getTimeInMillis();

        final UsageStatsManager mUsageStatsManager;
        List<UsageStats> stats = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, monthAgo, now);
            Log.d(TAG, "hasPermission: ");
        }
        return (stats != null && !stats.isEmpty());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getUsageStatsPermissionsStatus(Context context)
    {
        Log.d(TAG, "getUsageStatsPermissionsStatus: ");

        //Check if permission enabled
        if (UStats.getUsageStatsList(getActivity()).isEmpty())
        {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        UStats.printCurrentUsageStatus(getActivity());


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
//        {
//            boolean granted = false;
//            AppOpsManager appOps = (AppOpsManager) context
//                    .getSystemService(Context.APP_OPS_SERVICE);
//            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
//                    android.os.Process.myUid(), context.getPackageName());
//
//            if (mode == AppOpsManager.MODE_DEFAULT)
//            {
//                granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
//            } else
//            {
//                granted = (mode == AppOpsManager.MODE_ALLOWED);
//            }
//
//            if (granted)
//            {
//                Log.d(TAG, "getUsageStatsPermissionsStatus: granted: " + granted);
//
//            } else
//            {
//
//            }
//        }
    }


    private void getRuntimePermission()
    {
        if (getActivity() != null)
        {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 101);
            }
        }

        if (getActivity() != null)
        {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 102);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (getActivity() != null)
                {

                    Toast.makeText(getActivity(), "Call Permission Granted!", Toast.LENGTH_LONG).show();
                }
            } else
            {
                if (getActivity() != null)
                {
                    getActivity().finish();
                }
            }
        }

        if (requestCode == 102)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (getActivity() != null)
                {
                    Toast.makeText(getActivity(), "SMS Permission Granted!", Toast.LENGTH_LONG).show();
                }
            } else
            {
                if (getActivity() != null)
                {
                    getActivity().finish();
                }
            }
        }

    }


}