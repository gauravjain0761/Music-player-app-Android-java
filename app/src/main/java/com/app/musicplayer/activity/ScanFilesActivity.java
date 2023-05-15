package com.app.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivityScanFilesBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.FetchSongsFromLocal;
import com.app.musicplayer.db.SongModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScanFilesActivity extends AppCompatActivity {

    ActivityScanFilesBinding binding;
    String TAG = SearchSongsActivity.class.getSimpleName();
    List<SongModel> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityScanFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (songsList != null) songsList.clear();
        songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);

        binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < songsList.size(); i++) {
                    SongModel songs = songsList.get(i);
                    songs.setIsChecked(true);
                    songsList.set(i, songs);
                }
            } else {
                for (int i = 0; i < songsList.size(); i++) {
                    SongModel songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
            binding.listView.getAdapter().notifyDataSetChanged();
        });

        binding.txtSort.setOnClickListener(v -> {
            try {
//                new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
//                    selectedSortByRadio = selected;
//                    reloadList();
//                }).show(getSupportFragmentManager(), "BottomSheetFragmentSortBy");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.fab.setOnClickListener(v -> {
            try {
                if (songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size() > 0) {
                    DBUtils.insertMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                    Toast.makeText(getApplicationContext(), "Songs Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No Song Selected!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.imageViewBack.setOnClickListener(v -> finish());

        binding.imageViewSearch.setOnClickListener(v -> {
            //startActivity(new Intent(this, SearchSongsActivity.class))
        });

        reloadList();
    }

    private void reloadList() {
        try {
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
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(ScanFilesActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new DeleteSongsAdapter(songsList, ScanFilesActivity.this, (result, isChecked, position) -> {
                result.setIsChecked(isChecked);
                songsList.set(position, result);
            }));
            binding.layoutList.setVisibility(View.GONE);
            binding.layoutScan.setVisibility(View.VISIBLE);
            binding.radarScan.startScan();
            new Handler().postDelayed(() -> {
                binding.radarScan.stopScan();
                binding.layoutList.setVisibility(View.VISIBLE);
                binding.layoutScan.setVisibility(View.GONE);
            }, 3000);
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
}