package com.app.musicplayer.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivityScanFilesBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.FetchSongsFromLocal;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.fragment.BottomSheetFragmentSortBy;
import com.app.musicplayer.presenter.ScanFilesActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.musicplayer.utils.SortComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScanFilesActivity extends AppCompatActivity implements ScanFilesActivityPresenter.View {

    ActivityScanFilesBinding binding;
    ScanFilesActivityPresenter presenter;
    String TAG = SearchSongsActivity.class.getSimpleName();
    public static List<SongModel> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityScanFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        presenter = new ScanFilesActivityPresenter(this, binding);
        try {
            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(ScanFilesActivity.this, v);
                finish();
            });

            new AsyncScanList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncScanList extends AsyncTask<String, String, List<SongModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(4000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
            binding.imageViewRotate.startAnimation(rotate);
//            binding.radarScan.startScan();
        }

        @Override
        protected List<SongModel> doInBackground(String... strings) {
            try {
                if (songsList != null) songsList.clear();
                songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
                Collections.sort(songsList, new SortComparator.SortByName.Ascending());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return songsList;
        }

        @Override
        protected void onPostExecute(List<SongModel> list) {
            super.onPostExecute(list);
            try {
                //binding.radarScan.stopScan();
                binding.layoutScan.setVisibility(View.GONE);
                showLayout();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showLayout() {
        try {
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
                        presenter.notifyAdapter();
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
                            Log.e("TAG", " selected " + selectedSortByRadio);
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

            binding.imageViewSearch.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0) {
                    startActivityForResult(new Intent(this, SearchScanFilesActivity.class), 1001);
                }
            });

            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshView();
    }

    private void sortList() {
        try {
            if (songsList != null && songsList.size() > 0) {
                Log.e("TAG", " selected " + selectedSortByRadio);
                Collections.sort(songsList, new SortComparator.SortByName.Ascending());
                if (selectedSortByRadio == 0) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameA2Z));
                    Collections.sort(songsList, new SortComparator.SortByName.Ascending());
                } else if (selectedSortByRadio == 1) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameZ2A));
                    Collections.sort(songsList, new SortComparator.SortByName.Descending());
                } else if (selectedSortByRadio == 2) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_new));
                    Collections.sort(songsList, new SortComparator.SortByDate.Ascending());
                } else if (selectedSortByRadio == 3) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                    Collections.sort(songsList, new SortComparator.SortByDate.Descending());
                } else if (selectedSortByRadio == 4) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_short));
                    Collections.sort(songsList, new SortComparator.SortByDuration.Ascending());
                } else if (selectedSortByRadio == 5) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_long));
                    Collections.sort(songsList, new SortComparator.SortByDuration.Descending());
                } else if (selectedSortByRadio == 6) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_small));
                    Collections.sort(songsList, new SortComparator.SortBySize.Ascending());
                } else if (selectedSortByRadio == 7) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_large));
                    Collections.sort(songsList, new SortComparator.SortBySize.Descending());
                } else {
                    songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
                }

                presenter.notifyAdapter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            binding.listView.setAdapter(new DeleteSongsAdapter(songsList, ScanFilesActivity.this, (result, isChecked, position) -> {
                try {
                    songsList.set(position, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            presenter.setNoDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1001 && resultCode == RESULT_OK) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}