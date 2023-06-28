package com.app.musicplayer.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.adapter.FragmentQueueAdapter;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.databinding.FragmentQueueCurrentPlayedBinding;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.ui.contract.IFragmentQueueCurrentPlayedContract;
import com.app.musicplayer.ui.presenter.FragmentQueueCurrentPlayedPresenter;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;

import java.util.List;

public class FragmentQueueCurrentPlayed extends BaseFragment<FragmentQueueCurrentPlayedPresenter> implements IFragmentQueueCurrentPlayedContract.IFragmentQueueCurrentPlayedView {
    String TAG = FragmentQueueCurrentPlayed.class.getSimpleName();
    FragmentQueueCurrentPlayedBinding binding;
    FragmentQueueAdapter adapter;

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentQueueCurrentPlayedBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentQueueCurrentPlayedPresenter createPresenter(Context context) {
        return new FragmentQueueCurrentPlayedPresenter(context, this);
    }

    @Override
    protected void initWidget(View root) {
        try {
            getPresenter().setBinding(binding);

            binding.txtClear.setOnClickListener(v -> {
                try {
                    if (HomeActivity.fragmentPlayer != null) {
                        if (HomeActivity.fragmentPlayer.songsList != null && HomeActivity.fragmentPlayer.songsList.size() > 0) {
                            HomeActivity.fragmentPlayer.songsList.clear();
                            HomeActivity.fragmentPlayer.originalSongsList.clear();
                        }
                        HomeActivity.fragmentPlayer.onDeleteQueueSong();
                        setTitleCount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
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

    private final BroadcastReceiver lastQueuePlaySongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("LastQueuePlaySong")) {
                    refreshView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void registerEvent() {
        try {

            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(lastQueuePlaySongReceiver, new IntentFilter("LastQueuePlaySong"));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ContextCompat.registerReceiver(requireActivity(), playSongReceiver, new IntentFilter("GetPlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
//                ContextCompat.registerReceiver(requireActivity(), deletePlaySongReceiver, new IntentFilter("DeletePlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
//                ContextCompat.registerReceiver(requireActivity(), lastQueuePlaySongReceiver, new IntentFilter("LastQueuePlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
//            } else {
//                requireActivity().registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
//                requireActivity().registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
//                requireActivity().registerReceiver(lastQueuePlaySongReceiver, new IntentFilter("LastQueuePlaySong"));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {
            try {
                if (playSongReceiver != null)
                    LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(playSongReceiver);
//                    requireActivity().unregisterReceiver(playSongReceiver);
                if (deletePlaySongReceiver != null)
                    LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(deletePlaySongReceiver);
//                    requireActivity().unregisterReceiver(deletePlaySongReceiver);
                if (lastQueuePlaySongReceiver != null)
                    LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(lastQueuePlaySongReceiver);
//                    requireActivity().unregisterReceiver(lastQueuePlaySongReceiver);
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
            if (HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.songsList != null && HomeActivity.fragmentPlayer.songsList.size() > 0) {
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
            if (HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.songsList != null && HomeActivity.fragmentPlayer.songsList.size() > 0) {
                binding.txtTitleCount.setText("(" + HomeActivity.fragmentPlayer.songsList.size() + ")");
            } else {
                binding.txtTitleCount.setText("(0)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
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
            adapter = new FragmentQueueAdapter(true, requireActivity(), new FragmentQueueAdapter.QueueClickListener() {
                @Override
                public void onSongsClick(SongEntity result, int position) {
                    try {
                        if (HomeActivity.fragmentPlayer != null) {
                            HomeActivity.fragmentPlayer.playQueueSong(position, result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deleteSongs(SongEntity result, int position) {
                    try {
                        if (HomeActivity.fragmentPlayer != null) {
                            if (HomeActivity.fragmentPlayer.songsList != null && HomeActivity.fragmentPlayer.songsList.size() > 0) {
                                HomeActivity.fragmentPlayer.songsList.remove(result);
                                HomeActivity.fragmentPlayer.originalSongsList.remove(result);
                            }
                            HomeActivity.fragmentPlayer.onDeleteQueueSong();
                            setTitleCount();
                            notifyAdapter();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(HomeActivity.fragmentPlayer.songsList);
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

            setTitleCount();
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