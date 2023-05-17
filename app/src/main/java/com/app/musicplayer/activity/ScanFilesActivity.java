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
import com.app.musicplayer.fragment.BottomSheetFragmentSortBy;
import com.app.musicplayer.utils.SortComparator;

import java.util.ArrayList;
import java.util.Collections;
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
        try {
            binding.radarScan.startScan();
            new Handler().postDelayed(() -> {
                binding.radarScan.stopScan();
                binding.layoutList.setVisibility(View.VISIBLE);
                binding.layoutScan.setVisibility(View.GONE);
            }, 3000);

            try {
                if (songsList != null) songsList.clear();
                songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
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
                        if (binding.listView.getAdapter() != null)
                            binding.listView.getAdapter().notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.txtSort.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                            selectedSortByRadio = selected;
                            sortList();
                        }).show(getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.fab.setOnClickListener(v -> {
                try {
                    if (songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size() > 0) {
                        int moveCount = songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size();
                        DBUtils.insertMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                        Toast.makeText(getApplicationContext(), moveCount + " " + getResources().getString(R.string.alert_saved_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewBack.setOnClickListener(v -> finish());

            binding.imageViewSearch.setOnClickListener(v -> {
                //startActivity(new Intent(this, SearchSongsActivity.class))
            });

            reloadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortList() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (selectedSortByRadio == 0) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_name));
                    Collections.sort(songsList, new SortComparator.SortByName.Ascending());
                } else if (selectedSortByRadio == 1) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_name));
                    Collections.sort(songsList, new SortComparator.SortByName.Descending());
                } else if (selectedSortByRadio == 2) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date));
                    Collections.sort(songsList, new SortComparator.SortByDate.Ascending());
                } else if (selectedSortByRadio == 3) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date));
                    Collections.sort(songsList, new SortComparator.SortByDate.Descending());
                } else if (selectedSortByRadio == 4) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration));
                    Collections.sort(songsList, new SortComparator.SortByDuration.Ascending());
                } else if (selectedSortByRadio == 5) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration));
                    Collections.sort(songsList, new SortComparator.SortByDuration.Descending());
                } else if (selectedSortByRadio == 6) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size));
                    Collections.sort(songsList, new SortComparator.SortBySize.Ascending());
                } else if (selectedSortByRadio == 7) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size));
                    Collections.sort(songsList, new SortComparator.SortBySize.Descending());
                } else {
                    songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
                }

                if (binding.listView.getAdapter() != null)
                    binding.listView.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                try {
                    songsList.set(position, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            binding.layoutList.setVisibility(View.GONE);
            binding.layoutScan.setVisibility(View.VISIBLE);
            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.layoutListTopView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoDataFound() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.layoutListTopView.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}