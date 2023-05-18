package com.app.musicplayer.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.AppController;
import com.app.musicplayer.activity.DeleteSongsActivity;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivityDeleteSongsBinding;
import com.app.musicplayer.databinding.FragmentSongsBinding;
import com.app.musicplayer.db.SongModel;
import com.google.gson.Gson;

public class FragmentSongPresenter {

    Context context;
    FragmentSongsBinding binding;

    public FragmentSongPresenter(Context con, FragmentSongsBinding bind) {
        context = con;
        binding = bind;
    }

    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListView() {
        try {
            binding.swipe.setColorSchemeColors(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);

            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(context));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.rgLayout.setVisibility(android.view.View.VISIBLE);
            binding.layoutTopListView.setVisibility(android.view.View.VISIBLE);
            binding.layoutNoData.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.rgLayout.setVisibility(android.view.View.GONE);
            binding.layoutTopListView.setVisibility(android.view.View.GONE);
            binding.layoutNoData.setVisibility(android.view.View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface View {
        void refreshView();

        void showListView();

        void showNoDataView();
    }
}