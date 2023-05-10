package com.app.musicplayer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivityDeleteSongsBinding;
import com.app.musicplayer.pojo.HomeModel;

import java.util.ArrayList;

public class DeleteSongsActivity extends AppCompatActivity {

    private ActivityDeleteSongsBinding binding;
    String TAG = DeleteSongsActivity.class.getSimpleName();
    ArrayList<HomeModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityDeleteSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);

        binding.swipe.setOnRefreshListener(() -> {
            try {
                Log.e(TAG, "btnRefresh clicked called...");
                binding.swipe.setRefreshing(false);
                reloadList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < songsList.size(); i++) {
                    HomeModel songs = songsList.get(i);
                    songs.setIsChecked(true);
                    songsList.set(i, songs);
                }
            } else {
                for (int i = 0; i < songsList.size(); i++) {
                    HomeModel songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
            binding.listView.getAdapter().notifyDataSetChanged();
        });

        binding.fab.setOnClickListener(v -> showDeleteAlertDialog());

        reloadList();
    }

    private void reloadList() {
        try {
            if (songsList != null) songsList.clear();
            HomeModel menu_1 = new HomeModel();
            menu_1.setTitle("Play love me a lot");
            menu_1.setMessage("YARLE");
            songsList.add(menu_1);
            songsList.add(menu_1);
            songsList.add(menu_1);
            songsList.add(menu_1);
            if (songsList != null && songsList.size() > 0) {
                showListView();
            } else {
                showNoDataFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showListView() {
        try {
            binding.swipe.setColorSchemeColors(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);

            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(DeleteSongsActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new DeleteSongsAdapter(songsList, DeleteSongsActivity.this));

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoDataFound() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
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
        if (id == R.id.action_delete) {
            showDeleteAlertDialog();
            return true;
        }
        if (id == R.id.action_settings) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure!! Do you want to delete ?");
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (dialog, id) -> {
                finish();
                Toast.makeText(getApplicationContext(), "Songs Deleted!", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("No", (dialog, id) -> dialog.cancel());
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}