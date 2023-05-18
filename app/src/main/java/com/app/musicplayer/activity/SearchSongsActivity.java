package com.app.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.musicplayer.AppController;
import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.presenter.SearchSongsActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchSongsActivity extends AppCompatActivity implements SearchSongsActivityPresenter.View {

    private ActivitySearchSongsBinding binding;
    SearchSongsActivityPresenter presenter;
    String TAG = SearchSongsActivity.class.getSimpleName();
    List<SongModel> songsList = new ArrayList<>();
    int selectedSortTypeRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        presenter = new SearchSongsActivityPresenter(this, binding);
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
                            songsList = DBUtils.getSearchSongsByName(s.toString());
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
            binding.listView.setAdapter(new FragmentSongsAdapter(selectedSortTypeRadio, songsList, SearchSongsActivity.this, new FragmentSongsAdapter.SongsClickListner() {
                @Override
                public void onSongsClick(SongModel result, int position) {
                    try {
                        finish();
                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
                            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                        }
                        Intent updateDataBroadCast = new Intent("SongClick");
                        AppController.getSpSongInfo().edit().putInt("position", position).putString("songList", new Gson().toJson(songsList)).apply();
                        sendBroadcast(updateDataBroadCast);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongLongClick(SongModel result, int position) {
                    startActivityForResult(new Intent(SearchSongsActivity.this, DeleteSongsActivity.class), 101);
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
}