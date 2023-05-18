package com.app.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchScanFilesActivity extends AppCompatActivity {

    private ActivitySearchSongsBinding binding;
    String TAG = SearchScanFilesActivity.class.getSimpleName();
    List<SongModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            binding.etSearch.requestFocus();
            AppUtils.showKeyboard(this);
            binding.imageViewBack.setOnClickListener(v -> finish());
            binding.etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (!s.toString().isEmpty()) {
                            if (songsList != null) songsList.clear();
                            songsList = getFilterList(s.toString());
                            reloadList();
                        } else {
                            if (songsList != null) songsList.clear();
                            reloadList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            binding.fab.setOnClickListener(v -> {
                try {
                    if (ScanFilesActivity.songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size() > 0) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<SongModel> getFilterList(String text) {
        List<SongModel> filteredList = new ArrayList<SongModel>();
        try {
            for (SongModel item : ScanFilesActivity.songsList) {
                if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filteredList;
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
            binding.listView.setLayoutManager(new LinearLayoutManager(SearchScanFilesActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new DeleteSongsAdapter(songsList, SearchScanFilesActivity.this, (result, isChecked, position) -> {
                try {
//                    for (int i = 0; i < ScanFilesActivity.songsList.size(); i++) {
//                        if (Objects.equals(ScanFilesActivity.songsList.get(i).getId(), result.getId())) {
//                            ScanFilesActivity.songsList.set(i, result);
//                        }
//                    }
//                    checkFabLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkFabLayout() {
        try {
            if (ScanFilesActivity.songsList != null && ScanFilesActivity.songsList.size() > 0) {
                if (isAnyChecked()) {
                    binding.fab.setVisibility(View.VISIBLE);
                } else {
                    binding.fab.setVisibility(View.GONE);
                }
            } else {
                binding.fab.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAnyChecked() {
        try {
            for (SongModel songModel : songsList) {
                if (songModel.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showNoDataFound() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}