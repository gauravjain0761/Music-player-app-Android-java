package com.app.musicplayer.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.adapter.FragmentQueueAdapter;
import com.app.musicplayer.ui.IActivityContract;
import com.app.musicplayer.databinding.FragmentQueueLastPlayedBinding;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.ui.presenter.FragmentQueueLastPlayedPresenter;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;

import java.util.List;

public class FragmentQueueLastPlayed extends BaseFragment<FragmentQueueLastPlayedPresenter> implements IActivityContract.IActivityView {
    String TAG = FragmentQueueLastPlayed.class.getSimpleName();
    FragmentQueueLastPlayedBinding binding;
    FragmentQueueAdapter adapter;
    FragmentQueueLastPlayedPresenter presenter;

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentQueueLastPlayedBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentQueueLastPlayedPresenter createPresenter(Context context) {
        presenter = new FragmentQueueLastPlayedPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void initWidget(View root) {
        try {

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

    private final BroadcastReceiver deletePlaySongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("DeletePlaySong")) {
                    notifyAdapter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver playSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("GetPlaySong")) {
                    notifyAdapter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void registerEvent() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.registerReceiver(requireActivity(), playSongReceiver, new IntentFilter("GetPlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
                ContextCompat.registerReceiver(requireActivity(), deletePlaySongReceiver, new IntentFilter("DeletePlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
            } else {
                requireActivity().registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
                requireActivity().registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {
            try {
                if (playSongReceiver != null)
                    requireActivity().unregisterReceiver(playSongReceiver);
                if (deletePlaySongReceiver != null)
                    requireActivity().unregisterReceiver(deletePlaySongReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshView() {
        try {
            setTitleCount();
            if (HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.lastQueueSongsList != null && HomeActivity.fragmentPlayer.lastQueueSongsList.size() > 0) {
                showListView();
            } else {
                showNoDataView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleCount() {
        try {
            if (HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.lastQueueSongsList != null && HomeActivity.fragmentPlayer.lastQueueSongsList.size() > 0) {
                binding.txtTitleCount.setText("(" + HomeActivity.fragmentPlayer.lastQueueSongsList.size() + ")");
            } else {
                binding.txtTitleCount.setText("(0)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(requireActivity()));
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
            adapter = new FragmentQueueAdapter(false, requireActivity(), new FragmentQueueAdapter.QueueClickListener() {
                @Override
                public void onSongsClick(SongEntity result, int position) {
                    try {
                        if (HomeActivity.fragmentPlayer != null) {
                            HomeActivity.fragmentPlayer.playLastQueueSong(position, result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deleteSongs(SongEntity result, int position) {

                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(HomeActivity.fragmentPlayer.lastQueueSongsList);
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

    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
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