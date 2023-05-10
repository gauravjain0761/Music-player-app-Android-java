package com.app.musicplayer.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.musicplayer.R;
import com.app.musicplayer.activity.SearchPlaylistActivity;
import com.app.musicplayer.activity.SearchSongsActivity;
import com.app.musicplayer.adapter.FragmentPlaylistAdapter;
import com.app.musicplayer.databinding.FragmentPlaylistBinding;
import com.app.musicplayer.databinding.FragmentSongsBinding;
import com.app.musicplayer.pojo.HomeModel;
import com.app.musicplayer.utils.AppUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FragmentPlaylist extends Fragment {
    String TAG = FragmentPlaylist.class.getSimpleName();
    private static FragmentPlaylistBinding binding;
    ArrayList<HomeModel> playList = new ArrayList<>();
    static int selectedRadio = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false);
        Log.e(TAG, "onCreateView called.....");
        return binding.getRoot();
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

        binding.txtSort.setOnClickListener(v -> {
            try {
                BottomSheetFragmentSortBy.class.getDeclaredConstructor().newInstance().show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentSortBy");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        binding.imageViewSearch.setOnClickListener(v -> requireActivity().startActivity(new Intent(requireActivity(), SearchPlaylistActivity.class)));

        binding.fab.setOnClickListener(v -> showAddPlayListDialog());

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

    private void showAddPlayListDialog() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
            alertDialog.setTitle(getResources().getString(R.string.create_playlist));
            final AppCompatEditText input = new AppCompatEditText(requireActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(15, 10, 15, 10);
            input.setLines(1);
            input.setMaxLines(1);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setLayoutParams(params);
            alertDialog.setView(input);

            alertDialog.setPositiveButton(getResources().getString(R.string.txt_confirm), null);
            alertDialog.setNegativeButton(getResources().getString(R.string.txt_cancel), null);

            final AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();

            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                try {
                    if (input.getText().toString().isEmpty()) {
                        input.setError(getResources().getString(R.string.error_playlistname));
                        input.requestFocus();
                    } else {
                        alert.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                try {
                    alert.dismiss();
                    AppUtils.hideKeyboardOnClick(requireActivity(), v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadList() {
        try {
            if (playList != null) playList.clear();
            HomeModel menu_1 = new HomeModel();
            menu_1.setTitle("Play a lot music");
            menu_1.setMessage("12 songs");
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
            binding.swipe.setColorSchemeColors(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);

            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            binding.listView.setItemAnimator(new DefaultItemAnimator());
            binding.listView.setAdapter(new FragmentPlaylistAdapter(playList, requireActivity()));

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