package com.app.musicplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivityPlaylistSongsBinding;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.AppUtils;

import java.util.ArrayList;

public class PlaylistSongsActivity extends AppCompatActivity {

    private ActivityPlaylistSongsBinding binding;
    String TAG = SearchSongsActivity.class.getSimpleName();
    ArrayList<SongModel> playList = new ArrayList<>();
    int selectedSortTypeRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityPlaylistSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(PlaylistSongsActivity.this, v);
                finish();
            });

            binding.imageViewSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchSongsActivity.class)));
            reloadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadList() {
        try {
            if (playList != null) playList.clear();
            SongModel menu_1 = new SongModel();
            menu_1.setTitle("Play love me a lot");
            menu_1.setComposer("YARLE");
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            if (playList != null && playList.size() > 0) {
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
            binding.listView.setLayoutManager(new LinearLayoutManager(PlaylistSongsActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new FragmentSongsAdapter(selectedSortTypeRadio, playList, PlaylistSongsActivity.this, new FragmentSongsAdapter.SongsClickListner() {

                @Override
                public void deleteSongs(SongModel result, boolean isChecked, int position) {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongsClick(SongModel result, int position) {

                }

                @Override
                public void onSongLongClick(SongModel result, int position) {

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
            binding.txtNoData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}