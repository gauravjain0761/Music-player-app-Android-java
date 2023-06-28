package com.app.musicplayer.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.ui.activity.ScanFilesActivity;
import com.app.musicplayer.ui.activity.SearchSongsActivity;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.FragmentSongsBinding;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.db.FetchSongsFromLocal;
import com.app.musicplayer.ui.contract.IFragmentSongsContract;
import com.app.musicplayer.ui.presenter.FragmentSongPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.musicplayer.utils.FileUtils1;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentSongs extends BaseFragment<FragmentSongPresenter> implements IFragmentSongsContract.IFragmentSongsView {
    String TAG = FragmentPlaylist.class.getSimpleName();
    FragmentSongsBinding binding;
    FragmentSongsAdapter adapter;
    List<SongEntity> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;
    int selectedSortTypeRadio = 0;
    boolean isLocalFilesClick = false;
    boolean isKeyboardShowing = false;

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentSongPresenter createPresenter(Context context) {
        return new FragmentSongPresenter(context, this);
    }

    @Override
    protected void initWidget(View root) {
        try {
            getPresenter().setBinding(binding);

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

            binding.cbDelete.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        if (binding.cbDelete.isChecked()) {
                            for (int i = 0; i < songsList.size(); i++) {
                                SongEntity songs = songsList.get(i);
                                songs.setIsChecked(true);
                                songsList.set(i, songs);
                            }
                        } else {
                            for (int i = 0; i < songsList.size(); i++) {
                                SongEntity songs = songsList.get(i);
                                songs.setIsChecked(false);
                                songsList.set(i, songs);
                            }
                        }
                        notifyAdapter();
                        checkBottomLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

//            binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                try {
//                    if (songsList != null && songsList.size() > 0) {
//                        if (isChecked) {
//                            for (int i = 0; i < songsList.size(); i++) {
//                                SongEntity songs = songsList.get(i);
//                                songs.setIsChecked(true);
//                                songsList.set(i, songs);
//                            }
//                        } else {
//                            for (int i = 0; i < songsList.size(); i++) {
//                                SongEntity songs = songsList.get(i);
//                                songs.setIsChecked(false);
//                                songsList.set(i, songs);
//                            }
//                        }
//                        notifyAdapter();
//                        checkBottomLayout();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    clear();
                    if (adapter != null) adapter.clearLongPress();
                    checkBottomLayout();
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
                        if (HomeActivity.fragmentPlayer != null) {
                            HomeActivity.fragmentPlayer.onSongClick(songsList, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
            HomeActivity.bindingHome.playScreenFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp), (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp));
                        AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp), (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp));
                        binding.listView.setPadding(0, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._100sdp));
                    } else {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp), (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp));
                        AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp), (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp));
                        binding.listView.setPadding(0, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._50sdp));
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
                    refreshView();
                    if (HomeActivity.fragmentPlayer != null) {
                        HomeActivity.fragmentPlayer.onSongDelete(songsList);
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
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ContextCompat.registerReceiver(requireActivity(), playSongReceiver, new IntentFilter("GetPlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
//                ContextCompat.registerReceiver(requireActivity(), deletePlaySongReceiver, new IntentFilter("DeletePlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
//            } else {
//                requireActivity().registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
//                requireActivity().registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
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
                //requireActivity().unregisterReceiver(playSongReceiver);
                if (deletePlaySongReceiver != null)
                    LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(deletePlaySongReceiver);
                //requireActivity().unregisterReceiver(deletePlaySongReceiver);
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
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(requireActivity()));
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

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new FragmentSongsAdapter(requireActivity(), new FragmentSongsAdapter.SongsClickListener() {

                @Override
                public void deleteSongs(SongEntity result, boolean isChecked, int position) {
                    try {
                        checkBottomLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongsClick(SongEntity result, int position) {
                    try {
                        if (HomeActivity.fragmentPlayer != null) {
                            HomeActivity.fragmentPlayer.onSongClick(songsList, position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongLongClick(SongEntity result, int position) {
                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(requireActivity());
                    }
                    checkBottomLayout();
                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(songsList);
            checkBottomLayout();
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
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.rgLayout.setVisibility(android.view.View.GONE);
            binding.layoutTopListView.setVisibility(android.view.View.GONE);
            binding.layoutNoData.setVisibility(android.view.View.VISIBLE);
            binding.fabDelete.setVisibility(android.view.View.GONE);
            binding.layoutTopCheckBox.setVisibility(android.view.View.GONE);
            HomeActivity.bindingHome.tabLayout.setVisibility(android.view.View.VISIBLE);
            binding.fab.setVisibility(android.view.View.VISIBLE);
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
                songsList = DBUtils.getAllSongByDateDesc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                songsList = DBUtils.getAllSongByDateAsc();
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

    private void isFileExist() {
        try {
            for (SongEntity songEntity : DBUtils.getAllSongs()) {
                Log.e("TAG", "file exist" + songEntity.getData());
                //String path = FileUtils1.getPath(requireActivity(), Uri.parse(songEntity.getData()));
                File file = new File(songEntity.getData());
                Log.e("TAG", "file exist" + file.exists());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAnyChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (songEntity.getIsChecked()) return true;
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
                int deleteCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                DBUtils.trashMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                    @Override
                    public void onTaskComplete() {
                        try {
                            requireActivity().runOnUiThread(() -> {
                                try {
                                    refreshView();
                                    if (HomeActivity.fragmentPlayer != null) {
                                        HomeActivity.fragmentPlayer.onSongDelete(songsList);
                                    }
                                    Toast.makeText(requireActivity(), deleteCount + " " + getResources().getString(R.string.alert_trash_msg), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }).setNegativeButton(getResources().getString(R.string.txt_no), (dialog, id) -> dialog.cancel());
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkBoxAllIsChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (!songEntity.getIsChecked()) return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkBoxIsAnyUnChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (!songEntity.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkBottomLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (adapter != null && adapter.getLongPress()) {
                    HomeActivity.bindingHome.tabLayout.setVisibility(View.GONE);
                    binding.layoutTopListView.setVisibility(View.GONE);
                    binding.rgLayout.setVisibility(View.GONE);
                    binding.fab.setVisibility(View.GONE);
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                    HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                } else {
                    HomeActivity.bindingHome.tabLayout.setVisibility(View.VISIBLE);
                    binding.rgLayout.setVisibility(View.VISIBLE);
                    binding.layoutTopListView.setVisibility(View.VISIBLE);
                    binding.fab.setVisibility(View.VISIBLE);
                    binding.fabDelete.setVisibility(View.GONE);
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                    if (HomeActivity.fragmentPlayer.playSongEntity != null) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                HomeActivity.bindingHome.tabLayout.setVisibility(View.VISIBLE);
                binding.rgLayout.setVisibility(View.VISIBLE);
                binding.layoutTopListView.setVisibility(View.VISIBLE);
                binding.fab.setVisibility(View.VISIBLE);
                binding.fabDelete.setVisibility(View.GONE);
                binding.layoutTopCheckBox.setVisibility(View.GONE);
                if (HomeActivity.fragmentPlayer.playSongEntity != null) {
                    HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                }
            }

            if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            new BottomSheetFragmentMenu(new BottomSheetFragmentMenu.BottomSheetClick() {
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
                                DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), FileUtils1.getPath(requireActivity(), uri)), new DBUtils.TaskComplete() {
                                    @Override
                                    public void onTaskComplete() {

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Uri uri = data.getData();
                        try {
                            DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), FileUtils1.getPath(requireActivity(), uri)), new DBUtils.TaskComplete() {
                                @Override
                                public void onTaskComplete() {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            clear();
            if (adapter != null) adapter.clearLongPress();
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        openStorageFiles();
    }

    private void clear() {
        try {
            binding.cbDelete.setChecked(false);
            if (songsList != null && songsList.size() > 0) {
                for (int i = 0; i < songsList.size(); i++) {
                    SongEntity songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
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