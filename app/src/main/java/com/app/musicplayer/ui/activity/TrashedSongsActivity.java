package com.app.musicplayer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.TrashedSongsAdapter;
import com.app.musicplayer.databinding.ActivityTrashedSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.contract.IActivityTrashedSongsContract;
import com.app.musicplayer.ui.fragment.BottomSheetFragmentSortBy;
import com.app.musicplayer.ui.presenter.TrashedActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrashedSongsActivity extends BaseActivity<TrashedActivityPresenter> implements IActivityTrashedSongsContract.IActivityTrashedSongsView {

    String TAG = TrashedSongsActivity.class.getSimpleName();
    ActivityTrashedSongsBinding binding;
    TrashedSongsAdapter adapter;
    public static List<SongEntity> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;
    boolean isKeyboardShowing = false;

    @Override
    protected boolean isSupportHeadLayout() {
        return false;
    }

    @Override
    protected boolean isNeedSetStatusBar() {
        return true;
    }

    @Override
    protected ViewBinding getContentView() {
        binding = ActivityTrashedSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected TrashedActivityPresenter createPresenter(Context context) {
        return new TrashedActivityPresenter(context, this);
    }

    @Override
    protected void setStatusBar() {
        try {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initWidget() {
        try {
            getPresenter().setBinding(binding);

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

            binding.cbDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (songsList != null && songsList.size() > 0) {
                            if (binding.cbDelete.isChecked()) {
                                for (int i = 0; i < songsList.size(); i++) {
                                    SongEntity songs = songsList.get(i);
                                    songs.setIsChecked(true);
                                    songsList.set(i, songs);
                                }
                            } else {
                                for (int i = 0; i < songsList.size(); i++) {
                                    SongEntity songs = songsList.get(i);
                                    songs.setIsChecked(false);
                                    songsList.set(i, songs);
                                }
                            }
                            notifyAdapter();
                            checkTopLayout();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//            binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                try {
//                    if (songsList != null && songsList.size() > 0) {
//                        if (isChecked) {
//                            for (int i = 0; i < songsList.size(); i++) {
//                                SongEntity songs = songsList.get(i);
//                                songs.setIsChecked(true);
//                                songsList.set(i, songs);
//                            }
//                        } else {
//                            for (int i = 0; i < songsList.size(); i++) {
//                                SongEntity songs = songsList.get(i);
//                                songs.setIsChecked(false);
//                                songsList.set(i, songs);
//                            }
//                        }
//                        notifyAdapter();
//                        checkTopLayout();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

            binding.imageViewBack.setOnClickListener(v -> {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void registerEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                songsList = DBUtils.getTrashedSongByDateDesc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                songsList = DBUtils.getTrashedSongByDateAsc();
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
    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(TrashedSongsActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new TrashedSongsAdapter(TrashedSongsActivity.this, new TrashedSongsAdapter.TrashSongsListener() {
                @Override
                public void deleteSongs(SongEntity result, boolean isChecked, int position) {
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
            adapter.submitList(songsList);
            checkTopLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.layoutTopCheckBox.setVisibility(android.view.View.GONE);
            binding.layoutTopSearch.setVisibility(android.view.View.GONE);
            binding.layoutBottomButton.setVisibility(android.view.View.GONE);
            binding.txtNoData.setVisibility(android.view.View.VISIBLE);
            binding.btnEmptyTrash.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkBoxAllIsChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (!songEntity.getIsChecked()) return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkBoxIsAnyUnChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (!songEntity.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                } else {
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                    binding.layoutTopSearch.setVisibility(View.VISIBLE);
                    binding.btnEmptyTrash.setVisibility(View.VISIBLE);
                    binding.layoutBottomButton.setVisibility(View.GONE);
                }
            } else {
                showNoDataView();
            }
            if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAnyChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (songEntity.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_delete));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                try {
                    int deleteCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                    DBUtils.deleteMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                        @Override
                        public void onTaskComplete() {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //setResult(Activity.RESULT_OK, new Intent());
                                            //finish();
                                            refreshView();
                                            Toast.makeText(getApplicationContext(), deleteCount + " " + getResources().getString(R.string.alert_delete_msg), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
                    int restoreCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                    DBUtils.restoreMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                        @Override
                        public void onTaskComplete() {
                            try {
                                runOnUiThread(() -> {
                                    try {
                                        //setResult(Activity.RESULT_OK, new Intent());
                                        //finish();
                                        refreshView();
                                        Toast.makeText(getApplicationContext(), restoreCount + " " + getResources().getString(R.string.alert_restore_msg), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
                    DBUtils.deleteMultipleSongs(songsList, new DBUtils.TaskComplete() {
                        @Override
                        public void onTaskComplete() {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //setResult(Activity.RESULT_OK, new Intent());
                                            //finish();
                                            refreshView();
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_empty_trash_msg), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
                    SongEntity songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
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
    public void onDestroy() {
        super.onDestroy();
        try {
            clear();
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSongsList(List<SongEntity> list) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSongs(SongEntity entity) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}