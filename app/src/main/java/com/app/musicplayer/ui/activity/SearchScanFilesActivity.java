package com.app.musicplayer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.ScanFilesAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.IActivityContract;
import com.app.musicplayer.ui.presenter.SearchScanActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchScanFilesActivity extends BaseActivity<SearchScanActivityPresenter> implements IActivityContract.IActivityView {

    ActivitySearchSongsBinding binding;
    SearchScanActivityPresenter presenter;
    ScanFilesAdapter adapter;
    String TAG = SearchScanFilesActivity.class.getSimpleName();
    List<SongEntity> songsList = new ArrayList<>();

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
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected SearchScanActivityPresenter createPresenter(Context context) {
        presenter = new SearchScanActivityPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void setStatusBar() {
        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initWidget() {
        try {
            binding.etSearch.mSearchActivity = this;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT);

            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(SearchScanFilesActivity.this, v);
                finish();
            });

            binding.etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        getSearchedList(s.toString());
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
                    if (ScanFilesActivity.songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size() > 0) {
                        int moveCount = ScanFilesActivity.songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(SearchScanFilesActivity.this));
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
            adapter = new ScanFilesAdapter(SearchScanFilesActivity.this, (result, isChecked, position) -> {
                try {
                    checkFabLayout();
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
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            if (binding.etSearch.getText().toString().isEmpty()) {
                binding.txtNoData.setVisibility(android.view.View.GONE);
            } else {
                binding.txtNoData.setVisibility(android.view.View.VISIBLE);
            }
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
            for (SongEntity songEntity : ScanFilesActivity.songsList) {
                if (songEntity.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getSearchedList(String text) {
        try {
            if (text != null && !text.isEmpty()) {
                if (songsList != null) songsList.clear();
                songsList = getFilterList(text);
                refreshView();
            } else {
                if (songsList != null) songsList.clear();
                refreshView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<SongEntity> getFilterList(String text) {
        List<SongEntity> filteredList = new ArrayList<SongEntity>();
        try {
            for (SongEntity item : ScanFilesActivity.songsList) {
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            getSearchedList(binding.etSearch.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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