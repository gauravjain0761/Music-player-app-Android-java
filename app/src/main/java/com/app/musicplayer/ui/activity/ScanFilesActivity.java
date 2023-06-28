package com.app.musicplayer.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.ScanFilesAdapter;
import com.app.musicplayer.databinding.ActivityScanFilesBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.ExecuteTask;
import com.app.musicplayer.db.FetchSongsFromLocal;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.contract.IActivityScanFilesContract;
import com.app.musicplayer.ui.fragment.BottomSheetFragmentSortBy;
import com.app.musicplayer.ui.presenter.ScanFilesActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.musicplayer.utils.SortComparator;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScanFilesActivity extends BaseActivity<ScanFilesActivityPresenter> implements IActivityScanFilesContract.IActivityScanFilesView {

    ActivityScanFilesBinding binding;
    ScanFilesAdapter adapter;
    String TAG = ScanFilesActivity.class.getSimpleName();
    public static List<SongEntity> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;

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
        binding = ActivityScanFilesBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected ScanFilesActivityPresenter createPresenter(Context context) {
        return new ScanFilesActivityPresenter(context, this);
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
            try {
                ObjectAnimator.class.getMethod("setDurationScale", float.class).invoke(binding.imageViewRotate, 1f);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            try {
                ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(binding.imageViewRotate, 1f);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(ScanFilesActivity.this, v);
                finish();
            });

            binding.cbDelete.setOnClickListener(v -> {
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

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
                    if (songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size() > 0) {
                        int moveCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                        DBUtils.insertMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                            @Override
                            public void onTaskComplete() {
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                setResult(Activity.RESULT_OK, new Intent());
                                                finish();
                                                Toast.makeText(getApplicationContext(), moveCount + " " + getResources().getString(R.string.alert_saved_msg), Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            new ExecuteTask() {

                @Override
                public void onPreExecute() {
                    try {
                        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(4000);
                        rotate.setRepeatCount(ValueAnimator.INFINITE);
                        rotate.setInterpolator(new LinearInterpolator());
                        binding.imageViewRotate.startAnimation(rotate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doInBackground() {
                    try {
                        if (songsList != null) songsList.clear();
                        songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
                        Collections.sort(songsList, new SortComparator.SortByName.Ascending());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPostExecute() {
                    try {
                        binding.layoutScan.setVisibility(View.GONE);
                        refreshView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
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

            if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
            if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(ScanFilesActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutList.setVisibility(android.view.View.VISIBLE);
            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.layoutListTopView.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
            binding.layoutScan.setVisibility(android.view.View.GONE);
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

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new ScanFilesAdapter(ScanFilesActivity.this, (result, isChecked, position) -> {
                try {
                    songsList.set(position, result);
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                    if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(songsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutList.setVisibility(android.view.View.VISIBLE);
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.layoutListTopView.setVisibility(android.view.View.GONE);
            binding.layoutScan.setVisibility(android.view.View.GONE);
            binding.txtNoData.setVisibility(android.view.View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    Collections.sort(songsList, new SortComparator.SortByDate.Descending());
                } else if (selectedSortByRadio == 3) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                    Collections.sort(songsList, new SortComparator.SortByDate.Ascending());
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

                notifyAdapter();
            }
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

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
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