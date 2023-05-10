package com.app.musicplayer.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.DeleteSongsAdapter;
import com.app.musicplayer.adapter.FragmentSongsAdapter;
import com.app.musicplayer.databinding.ActivityPlaylistSongsBinding;
import com.app.musicplayer.databinding.ActivityScanFilesBinding;
import com.app.musicplayer.fragment.FragmentSongs;
import com.app.musicplayer.pojo.HomeModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class ScanFilesActivity extends AppCompatActivity {

    private static ActivityScanFilesBinding binding;
    String TAG = SearchSongsActivity.class.getSimpleName();
    ArrayList<HomeModel> playList = new ArrayList<>();
    static int selectedRadio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        binding = ActivityScanFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < playList.size(); i++) {
                    HomeModel songs = playList.get(i);
                    songs.setIsChecked(true);
                    playList.set(i, songs);
                }
            } else {
                for (int i = 0; i < playList.size(); i++) {
                    HomeModel songs = playList.get(i);
                    songs.setIsChecked(false);
                    playList.set(i, songs);
                }
            }
            binding.listView.getAdapter().notifyDataSetChanged();
        });

        binding.txtSort.setOnClickListener(v -> {
            try {
                BottomSheetFragmentSortBy.class.getDeclaredConstructor().newInstance().show(getSupportFragmentManager(), "BottomSheetFragmentSortBy");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.fab.setOnClickListener(v -> {
            try {
                finish();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.imageViewBack.setOnClickListener(v -> finish());

        binding.imageViewSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchSongsActivity.class)));

        reloadList();
    }

    public static class BottomSheetFragmentSortBy extends BottomSheetDialogFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(@NonNull final Dialog dialog, int style) {
            try {
                View contentView = View.inflate(getContext(), R.layout.bottomsheet_sortby, null);
                dialog.setContentView(contentView);
                ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

                RadioGroup radioGroup = ((RadioGroup) contentView.findViewById(R.id.radioGroup));
                ((RadioButton) radioGroup.getChildAt(selectedRadio)).setChecked(true);
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    try {
                        Log.e("TAG", "layoutScan clicked called...");
                        if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(0).getId()) {
                            binding.txtSort.setText("Sort by name");
                            selectedRadio = 0;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(1).getId()) {
                            binding.txtSort.setText("Sort by name");
                            selectedRadio = 1;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(2).getId()) {
                            binding.txtSort.setText("Sort by date");
                            selectedRadio = 2;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(3).getId()) {
                            binding.txtSort.setText("Sort by date");
                            selectedRadio = 3;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(4).getId()) {
                            binding.txtSort.setText("Sort by duration");
                            selectedRadio = 4;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(5).getId()) {
                            binding.txtSort.setText("Sort by duration");
                            selectedRadio = 5;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(6).getId()) {
                            binding.txtSort.setText("Sort by size");
                            selectedRadio = 6;
                        } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(7).getId()) {
                            binding.txtSort.setText("Sort by size");
                            selectedRadio = 7;
                        }
                        dismiss();
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reloadList() {
        try {
            if (playList != null) playList.clear();
            HomeModel menu_1 = new HomeModel();
            menu_1.setTitle("Play love me a lot");
            menu_1.setMessage("YARLE");
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            if (playList != null && playList.size() > 0) {
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
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(ScanFilesActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new DeleteSongsAdapter(playList, ScanFilesActivity.this));

            binding.layoutList.setVisibility(View.GONE);
            binding.layoutScan.setVisibility(View.VISIBLE);
            binding.radarScan.startScan();
            new Handler().postDelayed(() -> {
                binding.radarScan.stopScan();
                binding.layoutList.setVisibility(View.VISIBLE);
                binding.layoutScan.setVisibility(View.GONE);
            }, 3000);
            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.txtNoData.setVisibility(View.GONE);
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