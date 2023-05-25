package com.app.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.TrashedSongsAdapter;
import com.app.musicplayer.databinding.ActivityTrashedSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.fragment.BottomSheetFragmentSortBy;
import com.app.musicplayer.presenter.TrashedActivityPresenter;
import com.app.musicplayer.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrashedSongsActivity extends AppCompatActivity implements TrashedActivityPresenter.View {

    ActivityTrashedSongsBinding binding;
    TrashedActivityPresenter presenter;

    String TAG = TrashedSongsActivity.class.getSimpleName();
    public static List<SongModel> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;
    boolean isKeyboardShowing = false;

    TrashedSongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityTrashedSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        presenter = new TrashedActivityPresenter(this, binding);

        try {
            binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    Rect r = new Rect();
                    binding.getRoot().getWindowVisibleDisplayFrame(r);
                    int screenHeight = binding.getRoot().getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                        }
                    } else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

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

            binding.icBack.setOnClickListener(v -> {
                try {
//                    if (isKeyboardShowing) {
//                        AppUtils.closeKeyboard(this);
//                    } else {
//                        clear();
//                        setResult(Activity.RESULT_OK, new Intent());
//                        finish();
//                    }
                    AppUtils.closeKeyboard(this);
                    clear();
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    clear();
                    if (adapter != null) adapter.clearLongPress();
                    checkTopLayout();
//                    if (isKeyboardShowing) {
//                        AppUtils.closeKeyboard(this);
//                    } else {
//                        clear();
//                        setResult(Activity.RESULT_OK, new Intent());
//                        finish();
//                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.txtSort.setOnClickListener(v -> {
                try {
                    new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                        selectedSortByRadio = selected;
                        refreshView();
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
                    if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                        showDeleteAlertDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnRestore.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                        showRestoreAlertDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
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

    @Override
    public void refreshView() {
        try {
            if (songsList != null) songsList.clear();
            if (selectedSortByRadio == 0) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameA2Z));
                songsList = DBUtils.getTrashedSongByNameAsc();
            } else if (selectedSortByRadio == 1) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameZ2A));
                songsList = DBUtils.getTrashedSongByNameDesc();
            } else if (selectedSortByRadio == 2) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_new));
                songsList = DBUtils.getTrashedSongByDateAsc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                songsList = DBUtils.getTrashedSongByDateDesc();
            } else if (selectedSortByRadio == 4) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_short));
                songsList = DBUtils.getTrashedSongByDurationAsc();
            } else if (selectedSortByRadio == 5) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_long));
                songsList = DBUtils.getTrashedSongByDurationDesc();
            } else if (selectedSortByRadio == 6) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_small));
                songsList = DBUtils.getTrashedSongBySizeAsc();
            } else if (selectedSortByRadio == 7) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_large));
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

            adapter = new TrashedSongsAdapter(songsList, TrashedSongsActivity.this, new TrashedSongsAdapter.DeleteSongsListner() {
                @Override
                public void deleteSongs(SongModel result, boolean isChecked, int position) {
                    try {
                        songsList.set(position, result);
                        checkTopLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void longPressSongs(boolean isAnyLongPress) {
                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(TrashedSongsActivity.this);
                    }
                    checkTopLayout();
                }
            });
            binding.listView.setAdapter(adapter);
            checkTopLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTopLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (adapter != null && adapter.getLongPress()) {
                    //if (adapter != null) adapter.setLongPress();
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
                showNoDataView();
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
                    refreshView();
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
                    refreshView();
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
                    refreshView();
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
            binding.cbDelete.setChecked(false);
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