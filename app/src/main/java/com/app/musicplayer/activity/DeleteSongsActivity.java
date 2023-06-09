package com.app.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivityDeleteSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.presenter.DeleteActivityPresenter;
import com.app.musicplayer.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteSongsActivity extends AppCompatActivity implements DeleteActivityPresenter.View {

    String TAG = DeleteSongsActivity.class.getSimpleName();
    ActivityDeleteSongsBinding binding;
    DeleteActivityPresenter presenter;
    List<SongModel> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;
    String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityDeleteSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        presenter = new DeleteActivityPresenter(this, binding);

        if (getIntent() != null && getIntent().hasExtra("selectedSortByRadio")) {
            selectedSortByRadio = getIntent().getIntExtra("selectedSortByRadio", 0);
        }

        if (getIntent() != null && getIntent().hasExtra("searchText")) {
            searchText = getIntent().getStringExtra("searchText");
        }

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

                        checkTopLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.fab.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                    showDeleteAlertDialog();
                }
            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    AppUtils.hideKeyboardOnClick(DeleteSongsActivity.this, v);
                    clear();
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            refreshView();
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
    protected void onRestart() {
        super.onRestart();
        refreshView();
    }

    private void refreshList() {
        try {
            if (songsList != null) songsList.clear();
            if (searchText != null && !searchText.isEmpty()) {
                songsList = DBUtils.getSearchSongsByName(searchText);
            } else {
                if (selectedSortByRadio == 0) {
                    songsList = DBUtils.getAllSongByNameAsc();
                } else if (selectedSortByRadio == 1) {
                    songsList = DBUtils.getAllSongByNameDesc();
                } else if (selectedSortByRadio == 2) {
                    songsList = DBUtils.getAllSongByDateAsc();
                } else if (selectedSortByRadio == 3) {
                    songsList = DBUtils.getAllSongByDateDesc();
                } else if (selectedSortByRadio == 4) {
                    songsList = DBUtils.getAllSongByDurationAsc();
                } else if (selectedSortByRadio == 5) {
                    songsList = DBUtils.getAllSongByDurationDesc();
                } else if (selectedSortByRadio == 6) {
                    songsList = DBUtils.getAllSongBySizeAsc();
                } else if (selectedSortByRadio == 7) {
                    songsList = DBUtils.getAllSongBySizeDesc();
                } else {
                    songsList = DBUtils.getAllSongs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshView() {
        try {
            refreshList();
            if (songsList != null && songsList.size() > 0) {
                showListView();
            } else {
                showNoDataView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTopLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                binding.layoutListTopView.setVisibility(View.VISIBLE);
//                if (isAnyChecked()) {
//                    binding.layoutListTopView.setVisibility(View.VISIBLE);
//                } else {
//                    binding.layoutListTopView.setVisibility(View.GONE);
//                }
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
            binding.listView.setAdapter(new DeleteSongsAdapter(songsList, DeleteSongsActivity.this, (result, isChecked, position) -> {
                try {
                    songsList.set(position, result);
                    checkTopLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            checkTopLayout();
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
            setResult(Activity.RESULT_OK, new Intent());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_trash));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                int songListCount = songsList.size();
                int deleteCount = songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size();
                DBUtils.trashMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
                Toast.makeText(getApplicationContext(), deleteCount + " " + getResources().getString(R.string.alert_trash_msg), Toast.LENGTH_SHORT).show();
                Intent updatePlayBroadCast = new Intent("DeletePlaySong");
                sendBroadcast(updatePlayBroadCast);
                if (songListCount == deleteCount) {
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                } else {
                    refreshView();
                }

//                DBUtils.deleteMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
//                setResult(Activity.RESULT_OK, new Intent());
//                finish();
//                Toast.makeText(getApplicationContext(), "Songs Deleted!", Toast.LENGTH_SHORT).show();
            }).setNegativeButton(getResources().getString(R.string.txt_no), (dialog, id) -> dialog.cancel());
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        try {
            if (songsList != null && songsList.size() > 0) {
                for (int i = 0; i < songsList.size(); i++) {
                    SongModel songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}