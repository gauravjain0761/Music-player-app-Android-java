package com.app.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.ActivityHomeBinding;
import com.app.musicplayer.fragment.FragmentPlaylist;
import com.app.musicplayer.fragment.FragmentSongs;
import com.app.musicplayer.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    String TAG = HomeActivity.class.getSimpleName();
    ArrayList<String> tabsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);

        createTabsList();
        for (String tabs : tabsList) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(tabs));
        }
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_START);
        binding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        binding.viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), binding.tabLayout.getTabCount()));
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    binding.viewPager.setCurrentItem(tab.getPosition());
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
            startActivity(new Intent(this, DeleteSongsActivity.class));
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}