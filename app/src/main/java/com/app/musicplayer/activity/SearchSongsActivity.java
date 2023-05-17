package com.app.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.AppController;
import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.AppUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchSongsActivity extends AppCompatActivity {

    private ActivitySearchSongsBinding binding;
    String TAG = SearchSongsActivity.class.getSimpleName();
    List<SongModel> songsList = new ArrayList<>();
    int selectedSortTypeRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                            reloadList();
                        } else {
                            if (songsList != null) songsList.clear();
                            reloadList();
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

    private void reloadList() {
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
            binding.listView.setLayoutManager(new LinearLayoutManager(SearchSongsActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
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

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoDataFound() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}