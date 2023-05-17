package com.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.app.musicplayer.AppController;
import com.app.musicplayer.R;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.databinding.FragmentPlayerBinding;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.ImageUtil;
import com.app.musicplayer.visualizer.utils.TunnelPlayerWorkaround;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlayer extends Fragment {
    String TAG = FragmentPlayer.class.getSimpleName();
    FragmentPlayerBinding binding;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Handler handler = new Handler();
    List<SongModel> songsList = new ArrayList<>();
    int position = 0;
    SongModel playSongModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void initTunnelPlayerWorkaround() {
        // Read "tunnel.decode" system property to determine
        // the workaround is needed
        if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(requireActivity())) {
            TunnelPlayerWorkaround.createSilentMediaPlayer(requireActivity());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //initTunnelPlayerWorkaround();
        requireActivity().registerReceiver(songClickReceiver, new IntentFilter("SongClick"));

        binding.collapseImageView.setOnClickListener(v -> {
            try {
                binding.playerMotion.transitionToCollapse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.closeImageView.setOnClickListener(v -> {
            try {
                closeMediaPlayer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.playPauseImageView.setOnClickListener(v -> startStopOnClick());
        binding.musicPlayerView.setOnClickListener(v -> startStopOnClick());

        binding.btnNext.setOnClickListener(v -> {
            try {
                if (songsList != null && position != songsList.size() - 1) {
                    position = position + 1;
                    if (songsList.size() > 0 && songsList.size() > position) {
                        setMediaPlayer(songsList.get(position));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.btnPrevious.setOnClickListener(v -> {
            try {
                if (songsList != null && position > 0) {
                    position = position - 1;
                    if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
                        setMediaPlayer(songsList.get(position));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private final BroadcastReceiver songClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("SongClick")) {
                    if (songsList != null) songsList.clear();
                    position = AppController.getSpSongInfo().getInt("position", 0);
                    songsList = new Gson().fromJson(AppController.getSpSongInfo().getString("songList", ""), new TypeToken<List<SongModel>>() {
                    }.getType());
                    AppController.getSpSongInfo().edit().clear().apply();

                    if (songsList.size() > 0 && songsList.size() > position) {
                        setMediaPlayer(songsList.get(position));
                    }

                    try {
                        binding.playerMotion.transitionToEnd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void startStopOnClick() {
        try {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        binding.musicPlayerView.stop();
                        //binding.visualizerView.hide();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_play);

                        Intent updatePlayBroadCast = new Intent("GetPlaySong");
                        AppController.getSpSongInfo().edit().putBoolean("isPlaying", false).putString("CurrentSong", "").apply();
                        requireActivity().sendBroadcast(updatePlayBroadCast);

                    } else {
                        mediaPlayer.start();
                        binding.musicPlayerView.start();
                        //binding.visualizerView.show();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_pause);

                        if (playSongModel != null) {
                            Intent updatePlayBroadCast = new Intent("GetPlaySong");
                            AppController.getSpSongInfo().edit().putBoolean("isPlaying", true).putString("CurrentSong", new Gson().toJson(playSongModel)).apply();
                            requireActivity().sendBroadcast(updatePlayBroadCast);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setMediaPlayer(SongModel songModel) {
        try {
            playSongModel = songModel;
            binding.audioNameTextView.setText("" + (("" + songModel.getTitle()).replace("null", "").replace("Null", "")));
            binding.artistNameTextView.setText("" + (("" + songModel.getArtistName()).replace("null", "").replace("Null", "")));
            binding.audioNameTextViewMin.setText("" + (("" + songModel.getTitle()).replace("null", "").replace("Null", "")));
            binding.artistNameTextViewMin.setText("" + (("" + songModel.getArtistName()).replace("null", "").replace("Null", "")));

            if (("" + (("" + songModel.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                binding.visualizerView.setImageResource(R.drawable.icv_songs);
            } else {
                binding.visualizerView.setImageBitmap(ImageUtil.convertToBitmap(songModel.getBitmapCover()));
            }

//            try {
//                if (("" + (("" + songModel.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
//                    binding.musicPlayerView.setCoverDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_medieview)));
//                } else {
//                    binding.musicPlayerView.setCoverDrawable(new BitmapDrawable(getResources(), ImageUtil.convertToBitmap(songModel.getBitmapCover())));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(songModel.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                try {
                    if (mp != null && mediaPlayer != null && songsList != null && position != songsList.size() - 1) {
                        position = position + 1;
                        if (songsList.size() > 0 && songsList.size() > position) {
                            setMediaPlayer(songsList.get(position));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            binding.musicPlayerView.start();
            setAudioProgress();

            Intent updatePlayBroadCast = new Intent("GetPlaySong");
            AppController.getSpSongInfo().edit().putBoolean("isPlaying", true).putString("CurrentSong", new Gson().toJson(songModel)).apply();
            requireActivity().sendBroadcast(updatePlayBroadCast);

//                int audioSessionId = mediaPlayer.getAudioSessionId();
//                if (audioSessionId != -1) binding.visualizerView.setAudioSessionId(audioSessionId);

            //binding.visualizerView.setColor(Color.argb(255, 222, 92, 143));
//                binding.visualizerView.link(mediaPlayer);
//                addCircleBarRenderer();
//                addLineRenderer();
            //addBarGraphRenderers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    // Methods for adding renderers to visualizer
//    private void addBarGraphRenderers() {
//        Paint paint = new Paint();
//        paint.setStrokeWidth(50f);
//        paint.setAntiAlias(true);
//        paint.setColor(Color.argb(200, 56, 138, 252));
//        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
//        binding.visualizerView.addRenderer(barGraphRendererBottom);
//
//        Paint paint2 = new Paint();
//        paint2.setStrokeWidth(12f);
//        paint2.setAntiAlias(true);
//        paint2.setColor(Color.argb(200, 181, 111, 233));
//        BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
//        binding.visualizerView.addRenderer(barGraphRendererTop);
//    }
//
//    private void addCircleBarRenderer() {
//        Paint paint = new Paint();
//        paint.setStrokeWidth(8f);
//        paint.setAntiAlias(true);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
//        paint.setColor(Color.argb(255, 222, 92, 143));
//        CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
//        binding.visualizerView.addRenderer(circleBarRenderer);
//    }
//
//    private void addLineRenderer() {
//        Paint linePaint = new Paint();
//        linePaint.setStrokeWidth(1f);
//        linePaint.setAntiAlias(true);
//        linePaint.setColor(Color.argb(88, 0, 128, 255));
//
//        Paint lineFlashPaint = new Paint();
//        lineFlashPaint.setStrokeWidth(5f);
//        lineFlashPaint.setAntiAlias(true);
//        lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
//        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
//        binding.visualizerView.addRenderer(lineRenderer);
//    }

    String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                int days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days, hours % 24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("00:%02d:%02d", minutes, seconds);
    }

    public void setAudioProgress() {
        try {
            binding.seekDurationTextView.setText(secToTime(((int) mediaPlayer.getDuration()) / 1000));
            binding.seekTimeTextView.setText(secToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));

            binding.musicPlayerView.setMax(mediaPlayer.getDuration());
            binding.seekBar.setProgress(0);
            binding.seekBar.setMax(mediaPlayer.getDuration());

            binding.seekBarMin.setProgress(0);
            binding.seekBarMin.setMax(mediaPlayer.getDuration());

            binding.seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        binding.seekTimeTextView.setText(secToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));
                        binding.seekBar.setProgress((int) mediaPlayer.getCurrentPosition());
                        binding.seekBarMin.setProgress((int) mediaPlayer.getCurrentPosition());
                        if (handler != null) handler.postDelayed(this, 1000);
                    }
                } catch (Exception ed) {
                    ed.printStackTrace();
                }
            }
        };
        if (handler != null) handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            closeMediaPlayer();
            if (songClickReceiver != null) requireActivity().unregisterReceiver(songClickReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                //binding.visualizerView.hide();
                HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            closeMediaPlayer();
            if (songClickReceiver != null) requireActivity().unregisterReceiver(songClickReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}