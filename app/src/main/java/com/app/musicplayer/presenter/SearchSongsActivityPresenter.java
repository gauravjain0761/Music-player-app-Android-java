package com.app.musicplayer.presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.AppController;
import com.app.musicplayer.activity.DeleteSongsActivity;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.activity.SearchSongsActivity;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivityDeleteSongsBinding;
import com.app.musicplayer.databinding.ActivitySearchSongsBinding;
import com.app.musicplayer.db.SongModel;
import com.google.gson.Gson;

public class SearchSongsActivityPresenter {

    Context context;
    ActivitySearchSongsBinding binding;

    public SearchSongsActivityPresenter(Context con, ActivitySearchSongsBinding bind) {
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
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(context));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
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