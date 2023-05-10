package com.app.musicplayer.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.pojo.HomeModel;

import java.util.ArrayList;

public class SearchSongsActivity extends AppCompatActivity {

    private ActivitySearchSongsBinding binding;
    String TAG = SearchSongsActivity.class.getSimpleName();
    ArrayList<HomeModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageViewBack.setOnClickListener(v -> finish());

        reloadList();
    }

    private void reloadList() {
        try {
            if (songsList != null) songsList.clear();
            HomeModel menu_1 = new HomeModel();
            menu_1.setTitle("Play love me a lot");
            menu_1.setMessage("YARLE");
            songsList.add(menu_1);
            songsList.add(menu_1);
            songsList.add(menu_1);
            songsList.add(menu_1);
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
            binding.listView.setAdapter(new FragmentSongsAdapter(songsList, SearchSongsActivity.this));

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