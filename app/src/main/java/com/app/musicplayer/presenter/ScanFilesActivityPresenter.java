package com.app.musicplayer.presenter;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.databinding.ActivityScanFilesBinding;

public class ScanFilesActivityPresenter {

    Context context;
    ActivityScanFilesBinding binding;

    public ScanFilesActivityPresenter(Context con, ActivityScanFilesBinding bind) {
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

    public void setNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.layoutListTopView.setVisibility(android.view.View.GONE);
            binding.layoutScan.setVisibility(android.view.View.GONE);
            binding.txtNoData.setVisibility(android.view.View.VISIBLE);
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