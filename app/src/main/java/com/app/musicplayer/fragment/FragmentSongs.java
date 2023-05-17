package com.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.AppController;
import com.app.musicplayer.R;
import com.app.musicplayer.activity.DeleteSongsActivity;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.activity.ScanFilesActivity;
import com.app.musicplayer.activity.SearchSongsActivity;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.FragmentSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.FetchSongsFromLocal;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.AppUtils;
import com.app.musicplayer.utils.FileUtils1;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FragmentSongs extends Fragment {
    String TAG = FragmentSongs.class.getSimpleName();
    FragmentSongsBinding binding;
    List<SongModel> songsList = new ArrayList<>();
    int selectedSortTypeRadio = 0;
    int selectedSortByRadio = 0;
    FragmentSongsAdapter adapter;

    boolean isLocalFilesClick = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            requireActivity().registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));

            binding.swipe.setOnRefreshListener(() -> {
                try {
                    binding.swipe.setRefreshing(false);
                    reloadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.rgLayout.setOnCheckedChangeListener((group, checkedId) -> {
                try {
                    if (group.getCheckedRadioButtonId() == group.getChildAt(0).getId()) {
                        selectedSortTypeRadio = 0;
                    } else if (group.getCheckedRadioButtonId() == group.getChildAt(1).getId()) {
                        selectedSortTypeRadio = 1;
                    } else {
                        selectedSortTypeRadio = 2;
                    }
                    reloadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.txtSort.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                            selectedSortByRadio = selected;
                            reloadList();
                        }).show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewSearch.setOnClickListener(v ->
            {
                if (songsList != null && songsList.size() > 0) {
                    requireActivity().startActivityForResult(new Intent(requireActivity(), SearchSongsActivity.class), 1001);
                }
            });

            binding.fab.setOnClickListener(v -> {
                try {
                    showBottomMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.layoutPlayAll.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
                            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                        }
                        Intent updateDataBroadCast = new Intent("SongClick");
                        AppController.getSpSongInfo().edit().putInt("position", 0).putString("songList", new Gson().toJson(songsList)).apply();
                        requireActivity().sendBroadcast(updateDataBroadCast);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            reloadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 222) {
                if (null != data) { // checking empty selection
                    if (null != data.getClipData()) { // checking multiple selection or not
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            try {
                                DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), FileUtils1.getPath(requireActivity(), uri)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Uri uri = data.getData();
                        try {
                            DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), FileUtils1.getPath(requireActivity(), uri)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            reloadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final BroadcastReceiver playSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("GetPlaySong")) {
                    if (AppController.getSpSongInfo().getBoolean("isPlaying", false) && !AppController.getSpSongInfo().getString("CurrentSong", "").isEmpty()) {
                        if (adapter != null)
                            adapter.setPlayerInfo(new Gson().fromJson(AppController.getSpSongInfo().getString("CurrentSong", ""), SongModel.class), true);
                    } else {
                        adapter.setPlayerInfo(null, false);
                    }
                    AppController.getSpSongInfo().edit().clear().apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        openStorageFiles();
    }

    private void openStorageFiles() {
        try {
            if (isLocalFilesClick) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), 222);
            } else {
                requireActivity().startActivityForResult(new Intent(requireActivity(), ScanFilesActivity.class), 1001);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBottomMenu() {
        try {
            new BottomSheetMenuFragment(new BottomSheetMenuFragment.BottomSheetClick() {
                @Override
                public void onLocalFilesClick() {
                    try {
                        isLocalFilesClick = true;
                        if (AppUtils.allPermissionsGranted(requireActivity())) {
                            openStorageFiles();
                        } else {
                            AppUtils.getRuntimePermissions(requireActivity());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onScanFilesClick() {
                    isLocalFilesClick = false;
                    if (AppUtils.allPermissionsGranted(requireActivity())) {
                        openStorageFiles();
                    } else {
                        AppUtils.getRuntimePermissions(requireActivity());
                    }
                }
            }).show(requireActivity().getSupportFragmentManager(), "BottomSheetMenuFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadList() {
        try {
            if (songsList != null) songsList.clear();
            if (selectedSortByRadio == 0) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_name));
                songsList = DBUtils.getAllSongByNameAsc();
            } else if (selectedSortByRadio == 1) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_name));
                songsList = DBUtils.getAllSongByNameDesc();
            } else if (selectedSortByRadio == 2) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date));
                songsList = DBUtils.getAllSongByDateAsc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date));
                songsList = DBUtils.getAllSongByDateDesc();
            } else if (selectedSortByRadio == 4) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration));
                songsList = DBUtils.getAllSongByDurationAsc();
            } else if (selectedSortByRadio == 5) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration));
                songsList = DBUtils.getAllSongByDurationDesc();
            } else if (selectedSortByRadio == 6) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size));
                songsList = DBUtils.getAllSongBySizeAsc();
            } else if (selectedSortByRadio == 7) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size));
                songsList = DBUtils.getAllSongBySizeDesc();
            } else {
                songsList = DBUtils.getAllSongs();
            }

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
            binding.swipe.setColorSchemeColors(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);

            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            adapter = new FragmentSongsAdapter(selectedSortTypeRadio, songsList, requireActivity(), new FragmentSongsAdapter.SongsClickListner() {
                @Override
                public void onSongsClick(SongModel result, int position) {
                    try {
                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
                            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                        }
                        Intent updateDataBroadCast = new Intent("SongClick");
                        AppController.getSpSongInfo().edit().putInt("position", position).putString("songList", new Gson().toJson(songsList)).apply();
                        requireActivity().sendBroadcast(updateDataBroadCast);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongLongClick(SongModel result, int position) {
                    startActivityForResult(new Intent(requireActivity(), DeleteSongsActivity.class), 101);
                }
            });
            binding.listView.setAdapter(adapter);

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.rgLayout.setVisibility(View.VISIBLE);
            binding.layoutTopListView.setVisibility(View.VISIBLE);
            binding.layoutNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoDataFound() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);

            binding.rgLayout.setVisibility(View.GONE);
            binding.layoutTopListView.setVisibility(View.GONE);

            binding.layoutNoData.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}