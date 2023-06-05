package com.app.musicplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.AppController;
import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.IActivityContract;
import com.app.musicplayer.ui.presenter.SearchSongsActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SearchSongsActivity extends BaseActivity<SearchSongsActivityPresenter> implements IActivityContract.IActivityView {

    ActivitySearchSongsBinding binding;
    SearchSongsActivityPresenter presenter;
    FragmentSongsAdapter adapter;
    String TAG = SearchSongsActivity.class.getSimpleName();
    List<SongEntity> songsList = new ArrayList<>();
    int selectedSortTypeRadio = 0;
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
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected SearchSongsActivityPresenter createPresenter(Context context) {
        presenter = new SearchSongsActivityPresenter(context, this);
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
                        checkBottomLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    clear();
                    if (adapter != null) adapter.clearLongPress();
                    checkBottomLayout();
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

            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(SearchSongsActivity.this, v);
                clear();
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

            binding.fabDelete.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                    showDeleteAlertDialog();
                } else {
                    Toast.makeText(SearchSongsActivity.this, getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
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
            registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unRegisterEvent() {
        try {
            if (playSongReceiver != null)
                registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver playSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("GetPlaySong")) {
                    notifyAdapter();
//                    if (AppController.getSpSearchSongInfo().getBoolean("isPlayingSearch", false) && !AppController.getSpSearchSongInfo().getString("CurrentSongSearch", "").isEmpty()) {
//                        if (adapter != null)
//                            adapter.setPlayerInfo(new Gson().fromJson(AppController.getSpSearchSongInfo().getString("CurrentSongSearch", ""), SongEntity.class), true);
//                    } else {
//                        adapter.setPlayerInfo(null, false);
//                    }
//                    AppController.getSpSearchSongInfo().edit().clear().apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
            binding.listView.setLayoutManager(new LinearLayoutManager(SearchSongsActivity.this));
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
            adapter = new FragmentSongsAdapter(selectedSortTypeRadio, SearchSongsActivity.this, new FragmentSongsAdapter.SongsClickListener() {

                @Override
                public void deleteSongs(SongEntity result, boolean isChecked, int position) {
                    try {
                        checkBottomLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongsClick(SongEntity result, int position) {
                    try {
                        //finish();
//                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
//                            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
//                            //HomeActivity.fragmentPlayer.setPlayMotionFullScreen();
//                        }
                        Intent updateDataBroadCast = new Intent("SongClick");
                        AppController.getSpSongInfo().edit().putInt("position", position).putString("songList", new Gson().toJson(songsList)).apply();
                        sendBroadcast(updateDataBroadCast);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongLongClick(SongEntity result, int position) {
//                    Intent intent = new Intent(SearchSongsActivity.this, DeleteSongsActivity.class);
//                    intent.putExtra("selectedSortByRadio", 0);
//                    intent.putExtra("searchText", binding.etSearch.getText().toString());
//                    startActivityForResult(intent, 101);
                    //startActivityForResult(new Intent(SearchSongsActivity.this, DeleteSongsActivity.class), 101);

                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(SearchSongsActivity.this);
                    }
                    checkBottomLayout();
                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(songsList);
            checkBottomLayout();
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
            binding.fabDelete.setVisibility(android.view.View.GONE);
            binding.layoutTopCheckBox.setVisibility(android.view.View.GONE);
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

    private boolean checkBoxAllIsUnChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (songEntity.getIsChecked()) return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void checkBottomLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (adapter != null && adapter.getLongPress()) {
                    //if (adapter != null) adapter.setLongPress();
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                    if (checkBoxAllIsUnChecked()) binding.cbDelete.setChecked(false);
                } else {
                    binding.fabDelete.setVisibility(View.GONE);
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                }
            } else {
                binding.fabDelete.setVisibility(View.GONE);
                binding.layoutTopCheckBox.setVisibility(View.GONE);
            }
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

    private void getSearchedList(String text) {
        try {
            if (text != null && !text.isEmpty()) {
                if (songsList != null) songsList.clear();
                songsList = DBUtils.getSearchSongsByName(text);
                refreshView();
            } else {
                if (songsList != null) songsList.clear();
                refreshView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_trash));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                int songListCount = songsList.size();
                int deleteCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                DBUtils.trashMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                    @Override
                    public void onTaskComplete() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Intent updatePlayBroadCast = new Intent("DeletePlaySong");
                                        sendBroadcast(updatePlayBroadCast);
                                        Toast.makeText(getApplicationContext(), deleteCount + " " + getResources().getString(R.string.alert_trash_msg), Toast.LENGTH_SHORT).show();
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

//                if (songListCount == deleteCount) {
//                    setResult(Activity.RESULT_OK, new Intent());
//                    finish();
//                } else {
//                    getSearchedList(binding.etSearch.getText().toString());
//                }
                getSearchedList(binding.etSearch.getText().toString());

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
        try {
            getSearchedList(binding.etSearch.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            clear();
            unRegisterEvent();
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