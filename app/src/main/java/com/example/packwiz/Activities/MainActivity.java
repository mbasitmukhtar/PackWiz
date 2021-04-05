package com.example.packwiz.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.packwiz.Fragments.AllUsageFragment;
import com.example.packwiz.Fragments.AnalysisFragment;
import com.example.packwiz.Fragments.CallFilterFragment;
import com.example.packwiz.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;
    public static FragmentManager fragmentManager;
    Fragment allUsageFragment;
    Fragment callFilterFragment;
    Fragment analysisFragment;

    boolean callFilterFragmentFirstTime;
    boolean analysisFragmentFirstTime;

    Fragment active;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavigation();
    }

    public void initNavigation()
    {
        Log.d(TAG, "initNavigation: ");
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        fragmentManager = getSupportFragmentManager();
        allUsageFragment = new AllUsageFragment();

        callFilterFragmentFirstTime = true;
        analysisFragmentFirstTime = true;

        active = allUsageFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, allUsageFragment, "1").attach(allUsageFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.nav_sim_dashboard:
                    getSupportFragmentManager().beginTransaction().hide(active).show(allUsageFragment).commit();
                    active = allUsageFragment;
                    return true;
                case R.id.nav_call_dashboard:
                    if (callFilterFragmentFirstTime)
                    {
                        callFilterFragment = new CallFilterFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, callFilterFragment, "2").hide(active).show(callFilterFragment).commit();
                        callFilterFragmentFirstTime = false;
                    } else
                    {
                        getSupportFragmentManager().beginTransaction().hide(active).show(callFilterFragment).commit();
                    }
                    active = callFilterFragment;
                    return true;
                case R.id.nav_analysis:
                    if (analysisFragmentFirstTime)
                    {
                        analysisFragment = new AnalysisFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, analysisFragment, "3").hide(active).show(analysisFragment).commit();
                        analysisFragmentFirstTime = false;
                    } else
                    {
                        getSupportFragmentManager().beginTransaction().hide(active).show(analysisFragment).commit();
                    }
                    active = analysisFragment;
                    return true;
            }
            return false;
        });

    }
}