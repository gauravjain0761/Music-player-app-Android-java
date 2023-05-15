package com.app.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.ActivityHomeBinding;
import com.app.musicplayer.fragment.FragmentPlayer;
import com.app.musicplayer.fragment.FragmentPlaylist;
import com.app.musicplayer.fragment.FragmentSongs;
import com.app.musicplayer.utils.Constants;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public static ActivityHomeBinding bindingHome;
    String TAG = HomeActivity.class.getSimpleName();
    ArrayList<String> tabsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        bindingHome = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(bindingHome.getRoot());
        bindingHome.toolbar.setTitle("");
        setSupportActionBar(bindingHome.toolbar);

        createTabsList();
        for (String tabs : tabsList) {
            bindingHome.tabLayout.addTab(bindingHome.tabLayout.newTab().setText(tabs));
        }
        bindingHome.tabLayout.setTabGravity(TabLayout.GRAVITY_START);
        bindingHome.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        bindingHome.viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), bindingHome.tabLayout.getTabCount()));
        bindingHome.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(bindingHome.tabLayout));
        bindingHome.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    bindingHome.viewPager.setCurrentItem(tab.getPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setupPlayScreenFragment();
    }

    private void createTabsList() {
        try {
            Log.e(TAG, "createTabsList called.....");
            if (tabsList != null) tabsList.clear();

            if (tabsList != null) {
                tabsList.add(Constants.TAB_SONGS);
                tabsList.add(Constants.TAB_PLAYLIST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class TabsAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public TabsAdapter(FragmentManager fm, int NoofTabs) {
            super(fm);
            this.mNumOfTabs = NoofTabs;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentSongs();
                case 1:
                    return new FragmentPlaylist();
                default:
                    return null;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            startActivityForResult(new Intent(this, DeleteSongsActivity.class), 101);
            return true;
        }

        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupPlayScreenFragment() {
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, FragmentPlayer.class.newInstance(), "FragmentPlayer").commitAllowingStateLoss();
            bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult Permission granted!");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}