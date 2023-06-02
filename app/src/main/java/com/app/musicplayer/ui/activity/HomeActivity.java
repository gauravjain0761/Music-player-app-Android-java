package com.app.musicplayer.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.ActivityHomeBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.fragment.FragmentPlayer;
import com.app.musicplayer.ui.fragment.FragmentPlaylist;
import com.app.musicplayer.ui.fragment.FragmentSongs;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static ActivityHomeBinding bindingHome;
    String TAG = HomeActivity.class.getSimpleName();
    ArrayList<String> tabsList = new ArrayList<>();
    public static FragmentPlayer fragmentPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        bindingHome = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(bindingHome.getRoot());
        bindingHome.toolbar.setTitle("");
        setSupportActionBar(bindingHome.toolbar);
        try {
            deleteNotExistFile();
            fragmentPlayer = FragmentPlayer.class.newInstance();
            createTabsList();
            for (String tabs : tabsList) {
                bindingHome.tabLayout.addTab(bindingHome.tabLayout.newTab().setText(tabs));
            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            deleteNotExistFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTabsList() {
        try {
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

        public TabsAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.mNumOfTabs = tabCount;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @NonNull
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            startActivityForResult(new Intent(this, TrashedSongsActivity.class), 101);
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteNotExistFile() {
        try {
            Log.e("HomeTag", "song size : " + DBUtils.getAllSongs().size());
            List<SongEntity> oldList = DBUtils.getAllSongs();
            for (SongEntity songEntity : oldList) {
                File file = new File(songEntity.getData());
                if (!file.exists()) {
                    Log.e("HomeTag", "file exist" + file.exists());
                    DBUtils.deleteSingleSongs(songEntity, new DBUtils.TaskComplete() {
                        @Override
                        public void onTaskComplete() {

                        }
                    });
                }
            }
            Log.e("HomeTag", "after song size : " + DBUtils.getAllSongs().size());
            List<SongEntity> newList = DBUtils.getAllSongs();
            if (oldList != null && newList != null && oldList.size() != newList.size()) {
                Log.e("HomeTag", "listner called : ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupPlayScreenFragment() {
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.play_screen_frame_layout, fragmentPlayer, "FragmentPlayer").commitAllowingStateLoss();
            bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}