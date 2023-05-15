package com.app.musicplayer.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.musicplayer.R;
import com.app.musicplayer.db.SongModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetPlayer extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    BottomSheetClick bottomSheetClick;
    MediaPlayer mediaPlayer;
    SongModel songModel;

    public BottomSheetPlayer(MediaPlayer mp, SongModel model, BottomSheetClick click) {
        this.bottomSheetClick = click;
        this.songModel = model;
        mediaPlayer = mp;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull final Dialog dialog, int style) {
        try {
            View contentView = View.inflate(getContext(), R.layout.fragment_player, null);
            dialog.setContentView(contentView);
            ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
//            MusicPlayerView mediaPlayerView = contentView.findViewById(R.id.mediaPlayer);
//            mediaPlayerView.start();
//
//            mediaPlayerView.setMax(((int) songModel.getDuration()) / 1000);
//            mediaPlayerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        mediaPlayerView.stop();
//                    } else {
//                        mediaPlayer.start();
//                        mediaPlayerView.start();
//                    }
//                }
//            });
//            try {
//                mediaPlayer.reset();
//                mediaPlayer.setDataSource(songModel.getData());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mediaPlayer.stop();
    }

    public interface BottomSheetClick {
        void onClick();
    }
}