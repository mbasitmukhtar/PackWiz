package com.example.packwiz.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.packwiz.CallLogInfo;
import com.example.packwiz.CallLogUtils;
import com.example.packwiz.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CallFilterFragment extends Fragment
{
    private static final String TAG = "CallFilterFragment";
    CallLogUtils logs;

    ProgressBar incomingBar, outgoingBar, missedBar, talkedBar;
    TextView incomingCalls, outgoingCalls, missedCalls, talkedCalls;

    public CallFilterFragment()
    {
        Log.d(TAG, "CallFilterFragment: constructor");
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
        return inflater.inflate(R.layout.fragment_call_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null)
        {
            getRuntimePermission();

            logs = CallLogUtils.getInstance(getActivity());
            getViews();

            getCallsData();
        }

    }

    private void getCallsData()
    {
        Log.d(TAG, "getCallsData: ");
        logs.loadData();
        getIncomingCallsNumber();
        getOutgoingCallsNumber();
        getMissedCallsNumber();
        getTalkedCallsNumber();

        Timer timer = new Timer();
        timer.schedule(new Refresh(), 0, 10000);
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
                    logs.loadData();

                    getIncomingCallsNumber();
                    getOutgoingCallsNumber();
                    getMissedCallsNumber();
                    getTalkedCallsNumber();
                });
            }
        }
    }


    private void getViews()
    {
        Log.d(TAG, "getViews: ");
        if (getActivity() != null)
        {
            incomingBar = getActivity().findViewById(R.id.incomingCallsProgressBar);
            outgoingBar = getActivity().findViewById(R.id.outgoingCallsProgressBar);
            missedBar = getActivity().findViewById(R.id.missedCallsProgressBar);
            talkedBar = getActivity().findViewById(R.id.talkedCallsProgressBar);

            incomingCalls = getActivity().findViewById(R.id.incomingCalls);
            outgoingCalls = getActivity().findViewById(R.id.outgoingCalls);
            missedCalls = getActivity().findViewById(R.id.missedCalls);
            talkedCalls = getActivity().findViewById(R.id.talkedCalls);
        }
    }


    private void getIncomingCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getIncommingCalls();
        incomingBar.setProgress(d.size());
        incomingCalls.setText(String.valueOf(d.size()));
    }


    private void getOutgoingCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getOutgoingCalls();
        outgoingBar.setProgress(d.size());
        outgoingCalls.setText(String.valueOf(d.size()));
    }

    private void getMissedCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getMissedCalls();
        missedBar.setProgress(d.size());
        missedCalls.setText(String.valueOf(d.size()));
    }

    private void getTalkedCallsNumber()
    {
        ArrayList<CallLogInfo> d = logs.getTalkedCalls();
        talkedBar.setProgress(d.size());
        talkedCalls.setText(String.valueOf(d.size()));
//        Toast.makeText(getActivity(), "Zong Logs: " + d.size(), Toast.LENGTH_LONG).show();
    }


    private void getRuntimePermission()
    {
        Log.d(TAG, "getRuntimePermission: ");
        if (getActivity() != null)
        {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 123);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (getActivity() != null)
                {

                    Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_LONG).show();
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