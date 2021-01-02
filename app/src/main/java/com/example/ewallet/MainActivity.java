package com.example.ewallet;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    AnimatedBottomBar bottom_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottom_bar = findViewById(R.id.bottom_bar);
        if (savedInstanceState == null) {
            bottom_bar.selectTabById(R.id.tab_home, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment frg = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, frg).commit();
        }
        try {
            onBarClickListeners();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onBarClickListeners() throws PackageManager.NameNotFoundException {
        bottom_bar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                Fragment fragment = null;
                switch (tab1.getId()) {
                    case R.id.tab_req:
                        fragment = new RequestFragment();
                        break;
                    case R.id.tab_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.tab_send:
                        fragment=new SendFragment();
                        break;
                }
                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else
                    Log.e(TAG, "Error creating fragment");
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });

    }
}