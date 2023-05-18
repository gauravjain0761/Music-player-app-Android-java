package com.app.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.databinding.ActivityTrashedSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.fragment.BottomSheetFragmentSortBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrashedSongsActivity extends AppCompatActivity {

    private ActivityTrashedSongsBinding binding;
    String TAG = TrashedSongsActivity.class.getSimpleName();
    public static List<SongModel> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityTrashedSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);

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
                        if (binding.listView.getAdapter() != null)
                            binding.listView.getAdapter().notifyDataSetChanged();

                        checkTopLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.icBack.setOnClickListener(v -> {
                try {
                    clear();
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.txtSort.setOnClickListener(v -> {
                try {
                    new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                        selectedSortByRadio = selected;
                        reloadList();
                    }).show(getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.btnEmptyTrash.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        showEmptyTrashAlertDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        showDeleteAlertDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnRestore.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        showRestoreAlertDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewSearch.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0) {
                    startActivityForResult(new Intent(this, SearchTrashedActivity.class), 1001);
                }
            });

            reloadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setListView();
    }

    private void reloadList() {
        try {
            if (songsList != null) songsList.clear();
            if (selectedSortByRadio == 0) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_name));
                songsList = DBUtils.getTrashedSongByNameAsc();
            } else if (selectedSortByRadio == 1) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_name));
                songsList = DBUtils.getTrashedSongByNameDesc();
            } else if (selectedSortByRadio == 2) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date));
                songsList = DBUtils.getTrashedSongByDateAsc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date));
                songsList = DBUtils.getTrashedSongByDateDesc();
            } else if (selectedSortByRadio == 4) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration));
                songsList = DBUtils.getTrashedSongByDurationAsc();
            } else if (selectedSortByRadio == 5) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration));
                songsList = DBUtils.getTrashedSongByDurationDesc();
            } else if (selectedSortByRadio == 6) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size));
                songsList = DBUtils.getTrashedSongBySizeAsc();
            } else if (selectedSortByRadio == 7) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size));
                songsList = DBUtils.getTrashedSongBySizeDesc();
            } else {
                songsList = DBUtils.getAllTrashSongs();
            }
            setListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListView() {
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
            binding.listView.setLayoutManager(new LinearLayoutManager(TrashedSongsActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new DeleteSongsAdapter(songsList, TrashedSongsActivity.this, (result, isChecked, position) -> {
                try {
                    songsList.set(position, result);
                    checkTopLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
            checkTopLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTopLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (isAnyChecked()) {
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                    binding.layoutTopSearch.setVisibility(View.GONE);
                    binding.btnEmptyTrash.setVisibility(View.GONE);
                    binding.layoutBottomButton.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                    binding.layoutTopSearch.setVisibility(View.VISIBLE);
                    binding.btnEmptyTrash.setVisibility(View.VISIBLE);
                    binding.layoutBottomButton.setVisibility(View.GONE);
                }
            } else {
                showNoDataFound();
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
            binding.layoutTopCheckBox.setVisibility(View.GONE);
            binding.layoutTopSearch.setVisibility(View.GONE);
            binding.layoutBottomButton.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_delete));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                try {
                    int deleteCount = songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size();
                    DBUtils.deleteMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
//                    setResult(Activity.RESULT_OK, new Intent());
//                    finish();
                    Toast.makeText(getApplicationContext(), deleteCount + " " + getResources().getString(R.string.alert_delete_msg), Toast.LENGTH_SHORT).show();
                    reloadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).setNegativeButton(getResources().getString(R.string.txt_no), (dialog, id) -> dialog.cancel());
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRestoreAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_restore));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                try {
                    int restoreCount = songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size();
                    DBUtils.restoreMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
//                    setResult(Activity.RESULT_OK, new Intent());
//                    finish();
                    Toast.makeText(getApplicationContext(), restoreCount + " " + getResources().getString(R.string.alert_restore_msg), Toast.LENGTH_SHORT).show();
                    reloadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).setNegativeButton(getResources().getString(R.string.txt_no), (dialog, id) -> dialog.cancel());
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmptyTrashAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_empty_trash));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                try {
                    DBUtils.deleteMultipleSongs(songsList);
                    //setResult(Activity.RESULT_OK, new Intent());
                    //finish();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_empty_trash_msg), Toast.LENGTH_SHORT).show();
                    reloadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        try {
            clear();
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}