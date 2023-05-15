package com.app.musicplayer.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        Log.e(TAG, "onCreateView called.....");
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated called.....");

        binding.swipe.setOnRefreshListener(() -> {
            try {
                Log.e(TAG, "btnRefresh clicked called...");
                binding.swipe.setRefreshing(false);
                reloadList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.rgLayout.setOnCheckedChangeListener((group, checkedId) -> {
            if (group.getCheckedRadioButtonId() == group.getChildAt(0).getId()) {
                selectedSortTypeRadio = 0;
            } else if (group.getCheckedRadioButtonId() == group.getChildAt(1).getId()) {
                selectedSortTypeRadio = 1;
            } else {
                selectedSortTypeRadio = 2;
            }
            reloadList();
        });

        binding.txtSort.setOnClickListener(v -> {
            try {
                new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                    selectedSortByRadio = selected;
                    reloadList();
                }).show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentSortBy");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.imageViewSearch.setOnClickListener(v -> {
            requireActivity().startActivityForResult(new Intent(requireActivity(), SearchSongsActivity.class), 1001);
        });

        binding.fab.setOnClickListener(v -> {
            try {
                if (AppUtils.allPermissionsGranted(requireActivity())) {
                    showBottomMenu();
                } else {
                    AppUtils.getRuntimePermissions(requireActivity());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.txtPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                    }
//                    else {
//                        HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
//                    }
                    Intent updateDataBroadCast = new Intent("SongClick");
                    String songList = new Gson().toJson(songsList);
                    updateDataBroadCast.putExtra("position", 0);
                    updateDataBroadCast.putExtra("songList", songList);
                    requireActivity().sendBroadcast(updateDataBroadCast);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        reloadList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult Permission granted!");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        showBottomMenu();
    }

    private void showBottomMenu() {
        try {
            new BottomSheetMenuFragment(new BottomSheetMenuFragment.BottomSheetClick() {
                @Override
                public void onLocalFilesClick() {
                    Intent videoIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    videoIntent.setType("audio/*");
                    videoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), 222);
                }

                @Override
                public void onScanFilesClick() {
                    requireActivity().startActivityForResult(new Intent(requireActivity(), ScanFilesActivity.class), 1001);
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
                binding.txtSort.setText("Sort by name");
                songsList = DBUtils.getAllSongByNameAsc();
            } else if (selectedSortByRadio == 1) {
                binding.txtSort.setText("Sort by name");
                songsList = DBUtils.getAllSongByNameDesc();
            } else if (selectedSortByRadio == 2) {
                binding.txtSort.setText("Sort by date");
                songsList = DBUtils.getAllSongByDateAsc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText("Sort by date");
                songsList = DBUtils.getAllSongByDateDesc();
            } else if (selectedSortByRadio == 4) {
                binding.txtSort.setText("Sort by duration");
                songsList = DBUtils.getAllSongByDurationAsc();
            } else if (selectedSortByRadio == 5) {
                binding.txtSort.setText("Sort by duration");
                songsList = DBUtils.getAllSongByDurationDesc();
            } else if (selectedSortByRadio == 6) {
                binding.txtSort.setText("Sort by size");
                songsList = DBUtils.getAllSongBySizeAsc();
            } else if (selectedSortByRadio == 7) {
                binding.txtSort.setText("Sort by size");
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
            binding.listView.setAdapter(new FragmentSongsAdapter(selectedSortTypeRadio, songsList, requireActivity(), new FragmentSongsAdapter.SongsClickListner() {
                @Override
                public void onSongsClick(SongModel result, int position) {
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                    }
//                    else {
//                        HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
//                    }
                    Intent updateDataBroadCast = new Intent("SongClick");
                    String songList = new Gson().toJson(songsList);
                    updateDataBroadCast.putExtra("position", position);
                    updateDataBroadCast.putExtra("songList", songList);
                    requireActivity().sendBroadcast(updateDataBroadCast);
                }

                @Override
                public void onSongLongClick(SongModel result, int position) {
                    showDeleteAlertDialog(result);
                }
            }));

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteAlertDialog(SongModel result) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage("Are you sure!! Do you want to delete ?");
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (dialog, id) -> {
                DBUtils.deleteSingleSongs(result);
                reloadList();
                Toast.makeText(requireActivity(), "Song Deleted!", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("No", (dialog, id) -> dialog.cancel());
            builder.create().show();
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