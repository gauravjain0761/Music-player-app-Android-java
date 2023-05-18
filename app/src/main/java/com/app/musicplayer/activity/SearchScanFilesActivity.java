package com.app.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.presenter.SearchScanActivityPresenter;
import com.app.musicplayer.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchScanFilesActivity extends AppCompatActivity implements SearchScanActivityPresenter.View {

    private ActivitySearchSongsBinding binding;
    SearchScanActivityPresenter presenter;
    String TAG = SearchScanFilesActivity.class.getSimpleName();
    List<SongModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        presenter = new SearchScanActivityPresenter(this, binding);
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
                            refreshView();
                        } else {
                            if (songsList != null) songsList.clear();
                            refreshView();
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

    @Override
    public void refreshView() {
        try {
            if (songsList != null && songsList.size() > 0) {
                showListView();
            } else {
                showNoDataView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            presenter.setListView();
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

    @Override
    public void showNoDataView() {
        try {
            presenter.setNoDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}