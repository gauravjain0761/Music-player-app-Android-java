package com.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import androidx.fragment.app.Fragment;

import com.app.musicplayer.R;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.databinding.FragmentPlayerBinding;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.visualizer.renderer.CircleBarRenderer;
import com.app.musicplayer.visualizer.renderer.LineRenderer;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        Log.e(TAG, "onCreateView called.....");
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

        binding.closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    closeMediaPlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.playPauseImageView.setOnClickListener(v -> {
            Log.e(TAG, "play click called.....");
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        binding.visualizerView.hide();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_play);
                    } else {
                        mediaPlayer.start();
                        binding.visualizerView.show();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_pause);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        binding.btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

//    private void addCircleBarRenderer() {
//        Paint paint = new Paint();
//        paint.setStrokeWidth(8f);
//        paint.setAntiAlias(true);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
//        paint.setColor(Color.argb(255, 222, 92, 143));
//        CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
//        binding.visualizerView.addRenderer(circleBarRenderer);
//    }

//    private void addLineRenderer()
//    {
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

    private BroadcastReceiver songClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.v(TAG, "songClickReceiver onReceive is called");
                if (intent.getAction() != null && intent.getAction().equals("SongClick")) {
                    if (songsList != null) songsList.clear();
                    position = intent.getIntExtra("position", 0);
                    songsList = new Gson().fromJson(intent.getStringExtra("songList"), new TypeToken<List<SongModel>>() {
                    }.getType());

                    if (songsList.size() > 0 && songsList.size() > position) {
                        setMediaPlayer(songsList.get(position));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setMediaPlayer(SongModel songModel) {
        try {

            if (mediaPlayer != null) {
                binding.audioNameTextView.setText("" + (("" + songModel.getTitle()).replace("null", "").replace("Null", "")));
                binding.artistNameTextView.setText("" + (("" + songModel.getArtistName()).replace("null", "").replace("Null", "")));
                binding.audioNameTextViewMin.setText("" + (("" + songModel.getTitle()).replace("null", "").replace("Null", "")));
                binding.artistNameTextViewMin.setText("" + (("" + songModel.getArtistName()).replace("null", "").replace("Null", "")));

                mediaPlayer.reset();
                mediaPlayer.setDataSource(songModel.getData());
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
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
                    }
                });


                setAudioProgress();
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1) binding.visualizerView.setAudioSessionId(audioSessionId);

                //binding.visualizerView.setColor(Color.argb(255, 222, 92, 143));
//                binding.visualizerView.link(mediaPlayer);
//                addCircleBarRenderer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        binding.seekDurationTextView.setText(secToTime(((int) mediaPlayer.getDuration()) / 1000));
        binding.seekTimeTextView.setText(secToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));


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
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
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
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }
        });

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        binding.seekTimeTextView.setText(secToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));
                        binding.seekBar.setProgress((int) mediaPlayer.getCurrentPosition());
                        binding.seekBarMin.setProgress((int) mediaPlayer.getCurrentPosition());

                        handler.postDelayed(this, 1000);
                    }
                } catch (Exception ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
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
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                binding.visualizerView.hide();
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