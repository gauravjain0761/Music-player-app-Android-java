package com.app.musicplayer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivityPlaylistSongsBinding;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.contract.IActivityPlaylistSongsContract;
import com.app.musicplayer.ui.presenter.PlaylistSongsActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongsActivity extends BaseActivity<PlaylistSongsActivityPresenter> implements IActivityPlaylistSongsContract.IActivityPlaylistSongsView {

    ActivityPlaylistSongsBinding binding;
    FragmentSongsAdapter adapter;
    String TAG = SearchSongsActivity.class.getSimpleName();
    ArrayList<SongEntity> playList = new ArrayList<>();

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
        binding = ActivityPlaylistSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected PlaylistSongsActivityPresenter createPresenter(Context context) {
        return new PlaylistSongsActivityPresenter(context, this);
    }

    @Override
    protected void setStatusBar() {
        try {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initWidget() {
        try {
            getPresenter().setBinding(binding);
            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(PlaylistSongsActivity.this, v);
                finish();
            });

            binding.imageViewSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchSongsActivity.class)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void registerEvent() {

    }

    @Override
    protected void unRegisterEvent() {

    }

    @Override
    public void refreshView() {
        try {
            if (playList != null) playList.clear();
            SongEntity menu_1 = new SongEntity();
            menu_1.setTitle("Play love me a lot");
            menu_1.setComposer("YARLE");
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            if (playList != null && playList.size() > 0) {
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
            binding.listView.setLayoutManager(new LinearLayoutManager(PlaylistSongsActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new FragmentSongsAdapter(PlaylistSongsActivity.this, new FragmentSongsAdapter.SongsClickListener() {

                @Override
                public void deleteSongs(SongEntity result, boolean isChecked, int position) {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongsClick(SongEntity result, int position) {

                }

                @Override
                public void onSongLongClick(SongEntity result, int position) {

                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(playList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.txtNoData.setVisibility(View.VISIBLE);
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