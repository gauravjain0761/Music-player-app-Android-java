package com.app.musicplayer.presenter;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.databinding.ActivityDeleteSongsBinding;

public class ScanFilesActivityPresenter {

    Context context;
    ActivityDeleteSongsBinding binding;

    public ScanFilesActivityPresenter(Context con, ActivityDeleteSongsBinding bind) {
        context = con;
        binding = bind;
    }

    public void notifyAdapter() {
        if (binding.listView.getAdapter() != null)
            binding.listView.getAdapter().notifyDataSetChanged();
    }

    public void setListView() {
        binding.listView.setHasFixedSize(true);
        binding.listView.setVerticalScrollBarEnabled(true);
        binding.listView.setLayoutManager(new LinearLayoutManager(context));
        binding.listView.setItemAnimator(new DefaultItemAnimator());

        binding.layoutListView.setVisibility(android.view.View.VISIBLE);
        binding.listView.setVisibility(android.view.View.VISIBLE);
        binding.txtNoData.setVisibility(android.view.View.GONE);
    }

    public void setNoDataFound() {
        binding.layoutListView.setVisibility(android.view.View.GONE);
        binding.listView.setVisibility(android.view.View.GONE);
        binding.txtNoData.setVisibility(android.view.View.VISIBLE);
    }

    public interface View {

        void reloadList();

        void showListView();

        void showNoDataFound();

    }
}
