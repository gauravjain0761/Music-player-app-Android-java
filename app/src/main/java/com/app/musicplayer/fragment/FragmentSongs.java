package com.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.musicplayer.AppController;
import com.app.musicplayer.R;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.activity.ScanFilesActivity;
import com.app.musicplayer.activity.SearchSongsActivity;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.FragmentSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.FetchSongsFromLocal;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.presenter.FragmentSongPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.musicplayer.utils.FileUtils1;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentSongs extends Fragment implements FragmentSongPresenter.View {
    String TAG = FragmentSongs.class.getSimpleName();
    FragmentSongsBinding binding;
    FragmentSongPresenter presenter;
    List<SongModel> songsList = new ArrayList<>();
    int selectedSortTypeRadio = 0;
    int selectedSortByRadio = 0;
    FragmentSongsAdapter adapter;

    boolean isLocalFilesClick = false;
    boolean isKeyboardShowing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            presenter = new FragmentSongPresenter(requireActivity(), binding);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.registerReceiver(requireActivity(), playSongReceiver, new IntentFilter("GetPlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
                ContextCompat.registerReceiver(requireActivity(), deletePlaySongReceiver, new IntentFilter("DeletePlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
            } else {
                requireActivity().registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
                requireActivity().registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
            }

            binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    Rect r = new Rect();
                    binding.getRoot().getWindowVisibleDisplayFrame(r);
                    int screenHeight = binding.getRoot().getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                        }
                    } else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        if (isChecked) {
                            for (int i = 0; i < songsList.size(); i++) {
                                SongModel songs = songsList.get(i);
                                songs.setIsChecked(true);
                                songsList.set(i, songs);
                            }
                        } else {
                            for (int i = 0; i < songsList.size(); i++) {
                                SongModel songs = songsList.get(i);
                                songs.setIsChecked(false);
                                songsList.set(i, songs);
                            }
                        }
                        presenter.notifyAdapter();

                        checkBottomLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    clear();
                    if (adapter != null) adapter.clearLongPress();
                    checkBottomLayout();
//                    if (isKeyboardShowing) {
//                        AppUtils.closeKeyboard(this);
//                    } else {
//                        clear();
//                        setResult(Activity.RESULT_OK, new Intent());
//                        finish();
//                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
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
                    refreshView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.txtSort.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                            Log.e("TAG", "selected" + selected);
                            selectedSortByRadio = selected;
                            Log.e("TAG", "selected" + selectedSortByRadio);
                            refreshView();
                        }).show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewSearch.setOnClickListener(v -> {
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
                            AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
                            AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
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

            HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
            HomeActivity.bindingHome.playScreenFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
                        AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
                    } else {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(R.dimen.fab_margin));
                        AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(R.dimen.fab_margin));
                    }
                    int newVis = HomeActivity.bindingHome.playScreenFrameLayout.getVisibility();
                    if ((int) HomeActivity.bindingHome.playScreenFrameLayout.getTag() != newVis) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.fabDelete.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                    showDeleteAlertDialog();
                } else {
                    Toast.makeText(requireActivity(), getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
                }
            });

            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAnyChecked() {
        try {
            for (SongModel songModel : songsList) {
                if (songModel.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage(getResources().getString(R.string.alert_trash));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                int songListCount = songsList.size();
                int deleteCount = songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()).size();
                DBUtils.trashMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
                Toast.makeText(requireActivity(), deleteCount + " " + getResources().getString(R.string.alert_trash_msg), Toast.LENGTH_SHORT).show();

                refreshView();
                if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                    Intent updateDataBroadCast = new Intent("SongListAfterDelete");
                    AppController.getSpSongInfo().edit().putString("songList", new Gson().toJson(songsList)).apply();
                    requireActivity().sendBroadcast(updateDataBroadCast);
                }
////                Intent updatePlayBroadCast = new Intent("DeletePlaySong");
////                requireActivity().sendBroadcast(updatePlayBroadCast);
//                refreshView();

//                DBUtils.deleteMultipleSongs(songsList.stream().filter(SongModel::getIsChecked).collect(Collectors.toList()));
//                setResult(Activity.RESULT_OK, new Intent());
//                finish();
//                Toast.makeText(getApplicationContext(), "Songs Deleted!", Toast.LENGTH_SHORT).show();
            }).setNegativeButton(getResources().getString(R.string.txt_no), (dialog, id) -> dialog.cancel());
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkBottomLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (adapter != null && adapter.getLongPress()) {
                    //if (adapter != null) adapter.setLongPress();
                    HomeActivity.bindingHome.tabLayout.setVisibility(View.GONE);
                    binding.layoutTopListView.setVisibility(View.GONE);
                    binding.rgLayout.setVisibility(View.GONE);
                    binding.fab.setVisibility(View.GONE);
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                } else {
                    HomeActivity.bindingHome.tabLayout.setVisibility(View.VISIBLE);
                    binding.rgLayout.setVisibility(View.VISIBLE);
                    binding.layoutTopListView.setVisibility(View.VISIBLE);
                    binding.fab.setVisibility(View.VISIBLE);
                    binding.fabDelete.setVisibility(View.GONE);
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                }
            } else {
                HomeActivity.bindingHome.tabLayout.setVisibility(View.VISIBLE);
                binding.rgLayout.setVisibility(View.VISIBLE);
                binding.layoutTopListView.setVisibility(View.VISIBLE);
                binding.fab.setVisibility(View.VISIBLE);
                binding.fabDelete.setVisibility(View.GONE);
                binding.layoutTopCheckBox.setVisibility(View.GONE);
            }
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
                    refreshList();
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                        Intent updateDataBroadCast = new Intent("SongListAfterDelete");
                        AppController.getSpSongInfo().edit().putString("songList", new Gson().toJson(songsList)).apply();
                        requireActivity().sendBroadcast(updateDataBroadCast);
                    }
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
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
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

    private void refreshList() {
        try {
            if (songsList != null) songsList.clear();
            Log.e("TAG", "refreshList selected" + selectedSortByRadio);

            if (selectedSortByRadio == 0) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameA2Z));
                songsList = DBUtils.getAllSongByNameAsc();
            } else if (selectedSortByRadio == 1) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameZ2A));
                songsList = DBUtils.getAllSongByNameDesc();
            } else if (selectedSortByRadio == 2) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_new));
                songsList = DBUtils.getAllSongByDateAsc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                songsList = DBUtils.getAllSongByDateDesc();
            } else if (selectedSortByRadio == 4) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_short));
                songsList = DBUtils.getAllSongByDurationAsc();
            } else if (selectedSortByRadio == 5) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_long));
                songsList = DBUtils.getAllSongByDurationDesc();
            } else if (selectedSortByRadio == 6) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_small));
                songsList = DBUtils.getAllSongBySizeAsc();
            } else if (selectedSortByRadio == 7) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_large));
                songsList = DBUtils.getAllSongBySizeDesc();
            } else {
                songsList = DBUtils.getAllSongs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshView() {
        try {
            refreshList();
            if (songsList != null && songsList.size() > 0) {
                showListView();
            } else {
                showNoDataView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            presenter.setListView();
            adapter = new FragmentSongsAdapter(selectedSortTypeRadio, songsList, requireActivity(), new FragmentSongsAdapter.SongsClickListner() {

                @Override
                public void deleteSongs(SongModel result, boolean isChecked, int position) {
                    try {
                        checkBottomLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongsClick(SongModel result, int position) {
                    try {
                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
                            AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
                            AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
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
//                    Intent intent = new Intent(requireActivity(), DeleteSongsActivity.class);
//                    intent.putExtra("selectedSortByRadio", selectedSortByRadio);
//                    intent.putExtra("searchText", "");
//                    startActivityForResult(intent, 101);

                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(requireActivity());
                    }
                    checkBottomLayout();
                }
            });
            binding.listView.setAdapter(adapter);
            checkBottomLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            presenter.setNoDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        try {
            binding.cbDelete.setChecked(false);
            if (songsList != null && songsList.size() > 0) {
                for (int i = 0; i < songsList.size(); i++) {
                    SongModel songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (playSongReceiver != null) requireActivity().unregisterReceiver(playSongReceiver);
            if (deletePlaySongReceiver != null)
                requireActivity().unregisterReceiver(deletePlaySongReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}