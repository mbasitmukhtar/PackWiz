package com.example.packwiz;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.packwiz.Fragments.AllUsageFragment;
import com.example.packwiz.Fragments.AnalysisFragment;
import com.example.packwiz.Fragments.CallFilterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;
    public static FragmentManager fragmentManager;
    Fragment allUsageFragment;
    Fragment callFilterFragment;
    Fragment analysisFragment;

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

        bottomNavigationView.setOnNavigationItemSelectedListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.nav_sim_dashboard:
                    allUsageFragment = new AllUsageFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, allUsageFragment).commit();
                    return true;

                case R.id.nav_call_dashboard:
                    callFilterFragment = new CallFilterFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, callFilterFragment).commit();
                    return true;

                case R.id.nav_analysis:
                    analysisFragment = new AnalysisFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, analysisFragment).commit();
                    return true;
            }
            return false;
        });

        allUsageFragment = new AllUsageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, allUsageFragment).commit();
    }
}