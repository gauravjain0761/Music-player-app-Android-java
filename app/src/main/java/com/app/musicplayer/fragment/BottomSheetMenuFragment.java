package com.app.musicplayer.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.musicplayer.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetMenuFragment extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    BottomSheetClick bottomSheetClick;

    public BottomSheetMenuFragment(BottomSheetClick click) {
        this.bottomSheetClick = click;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull final Dialog dialog, int style) {
        try {
            View contentView = View.inflate(getContext(), R.layout.bottomsheet_add_songs, null);
            dialog.setContentView(contentView);
            ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

            contentView.findViewById(R.id.layoutScan).setOnClickListener(v -> {
                try {
                    Log.e("TAG", "layoutScan clicked called...");
                    bottomSheetClick.onScanFilesClick();
                    //requireActivity().startActivity(new Intent(requireActivity(), ScanFilesActivity.class));
                    dismiss();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            contentView.findViewById(R.id.layoutLocalFiles).setOnClickListener(v -> {
                try {
                    Log.e("TAG", "layoutLocalFiles clicked called...");
                    dismiss();
                    dialog.dismiss();
                    bottomSheetClick.onLocalFilesClick();
                    //DBUtils.insertMultipleSongs(FetchSongsFromLocal.querySongs(requireActivity()));
                    //reloadList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface BottomSheetClick {
        void onLocalFilesClick();

        void onScanFilesClick();
    }
}