package com.app.musicplayer.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.db.DBUtils;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.databinding.FragmentPlayerBinding;
import com.app.musicplayer.ui.contract.IFragmentPlayerContract;
import com.app.musicplayer.ui.presenter.FragmentPlayerPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.musicplayer.utils.ImageUtil;
import com.app.musicplayer.utils.visualizer.utils.TunnelPlayerWorkaround;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentPlayer extends BaseFragment<FragmentPlayerPresenter> implements IFragmentPlayerContract.IFragmentPlayerView {
    String TAG = FragmentPlaylist.class.getSimpleName();
    FragmentPlayerBinding binding;
    public MediaPlayer mediaPlayer = new MediaPlayer();
    Handler handler = new Handler();
    public List<SongEntity> lastQueueSongsList = new ArrayList<>();
    public List<SongEntity> songsList = new ArrayList<>();
    public List<SongEntity> originalSongsList = new ArrayList<>();
    int position = 0;
    public SongEntity playSongEntity;
    ObjectAnimator objectAnimator;
    int playMode = 1;//1 for loop-all,2 for single,3 for random
    BottomSheetFragmentQueue bottomSheetFragmentQueue;
    int navigationBarColor;

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentPlayerBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentPlayerPresenter createPresenter(Context context) {
        return new FragmentPlayerPresenter(context, this);
    }

    @Override
    protected void initWidget(View root) {
        try {
            getPresenter().setBinding(binding);
            navigationBarColor = requireActivity().getWindow().getNavigationBarColor();
            initTunnelPlayerWorkaround();
            try {
                ObjectAnimator.class.getMethod("setDurationScale", float.class).invoke(binding.imageViewRotate, 1f);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            try {
                ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(binding.imageViewRotate, 1f);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            try {
                objectAnimator = ObjectAnimator.ofFloat(binding.imageViewRotate, View.ROTATION, 0f, 360f).setDuration(5000);
                objectAnimator.setInterpolator(new LinearInterpolator());
                objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

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

            binding.btnPlayMode.setOnClickListener(v -> {
                try {
                    Log.e("TAG", "playMode clicked" + playMode);
                    if (playMode == 1) {
                        playMode = 2;
                    } else if (playMode == 2) {
                        playMode = 3;
                    } else if (playMode == 3) {
                        playMode = 1;
                    }
                    setMediaPlayerPlayMode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.playPauseImageView.setOnClickListener(v -> startStopOnClick());
            binding.imageViewRotate.setOnClickListener(v -> startStopOnClick());

            binding.btnQueue.setOnClickListener(v -> {
                try {
                    bottomSheetFragmentQueue = null;
                    bottomSheetFragmentQueue = new BottomSheetFragmentQueue(selected -> {
                        Log.e("TAG", "selected" + selected);
                    });
                    bottomSheetFragmentQueue.show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentQueue");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnNext.setOnClickListener(v -> {
                try {
                    Log.e("TAG", "position : " + position);
                    playNextSong(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnPrevious.setOnClickListener(v -> {
                try {
                    playPreviousSong();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.playerMotion.setTransitionListener(new MotionLayout.TransitionListener() {
                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                    Log.e("TAG", "onTransitionStarted called : ");
                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                    Log.e("TAG", "onTransitionChange called : ");
                    setColorPlatte1();
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                    Log.e("TAG", "onTransitionCompleted called : ");
                    setColorPlatte2();
                }

                @Override
                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                    Log.e("TAG", "onTransitionTrigger called : ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            setMediaPlayerPlayMode();
            //binding.playerMotion.transitionToEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlayMotionFullScreen() {
        try {
            binding.playerMotion.transitionToEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void registerEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSongClick(List<SongEntity> songClickList, int positionClick) {
        try {

            if (lastQueueSongsList != null && songsList != null && songClickList != null && songsList.size() > 0 && !songClickList.equals(songsList)) {
                lastQueueSongsList.clear();
                lastQueueSongsList.addAll(songsList);
            }

//            if (lastQueueSongsList != null && originalSongsList != null && originalSongsList.size() > 0) {
////                if (!songClickList.containsAll(originalSongsList) && !originalSongsList.containsAll(songClickList)) {
////                    lastQueueSongsList.clear();
////                    lastQueueSongsList.addAll(originalSongsList);
////                }
//                if (!songClickList.equals(originalSongsList)) {
//                    lastQueueSongsList.clear();
//                    lastQueueSongsList.addAll(originalSongsList);
//                }
//            }
            if (songsList != null) songsList.clear();
            if (originalSongsList != null) originalSongsList.clear();
            position = positionClick;
            songsList.addAll(songClickList);
            originalSongsList.addAll(songClickList);
//            if (lastQueueSongsList == null || lastQueueSongsList.size() == 0) {
//                lastQueueSongsList.clear();
//                lastQueueSongsList.addAll(songClickList);
//            }
            if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
//                        try {
//                            binding.playerMotion.transitionToEnd();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                playSongEntity = songsList.get(position);
                setMediaPlayer(playSongEntity);
                setMediaPlayerPlayMode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSongDelete(List<SongEntity> songClickList) {
        try {
            if (songsList != null) songsList.clear();
            if (originalSongsList != null) originalSongsList.clear();
            songsList.addAll(songClickList);
            originalSongsList.addAll(songClickList);
            deleteSongsFromLastQueue();

            boolean isSongDelete = true;
            if (playSongEntity != null) {
                for (SongEntity model : songsList) {
                    if (model.getId() == playSongEntity.getId()) {
                        isSongDelete = false;
                    }
                }
            }
            Log.e("songsList", "on delete songsList size " + songsList.size());
            if (songsList != null && songsList.size() > 0) {
                if (isSongDelete) {
                    if (position > songsList.size()) {
                        //position = songsList.size() - 1;
                        position = 0;
                    }
                    playDeleteNextSong();
                }
            } else {
                closeMediaPlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSongsFromLastQueue() {
        try {
            lastQueueSongsList.removeIf(songs -> !DBUtils.checkSongIsTrashedInDB(songs.getSongId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTunnelPlayerWorkaround() {
        // Read "tunnel.decode" system property to determine
        // the workaround is needed
        if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(requireActivity())) {
            TunnelPlayerWorkaround.createSilentMediaPlayer(requireActivity());
        }
    }

    private void playPreviousSong() {
        try {
//            if (songsList != null && position > 0) {
//                position = position - 1;
//                if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
//                    setMediaPlayer(songsList.get(position));
//                }
//            }

//            if (songsList != null) songsList.clear();
//            if (songsList != null && originalSongsList != null) songsList.addAll(originalSongsList);

//            try {
//                if (playMode == 3) {
//                    if (songsList != null) songsList.clear();
//                    if (songsList != null && originalSongsList != null)
//                        songsList.addAll(originalSongsList);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (songsList != null && songsList.size() > 0) {
                if (position == 0) {
                    position = songsList.size() - 1;
                } else if (position >= songsList.size()) {
                    position = songsList.size() - 2;
                } else if (position > 0) {
                    position = position - 1;
                }
                Log.e("playPreviousSong", "position : " + position);
                if (songsList.size() > position) {
                    setMediaPlayer(songsList.get(position));
                } else {
                    Log.e("playPreviousSong", "Inner Loop caled");
                }
            } else {
                Log.e("playPreviousSong", "Outer Loop caled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playNextSong(boolean isFromComplete) {
        try {
//            if (songsList != null && position != songsList.size() - 1) {
//                position = position + 1;
//                if (songsList.size() > 0 && songsList.size() > position) {
//                    setMediaPlayer(songsList.get(position));
//                }
//            }

//            if (isFromComplete && playMode == 3) {
//                if (songsList != null) Collections.shuffle(songsList);
//            } else {
//                if (songsList != null) songsList.clear();
//                if (songsList != null && originalSongsList != null)
//                    songsList.addAll(originalSongsList);
//            }

//            try {
//                if (!isFromComplete && playMode == 3) {
//                    if (songsList != null) songsList.clear();
//                    if (songsList != null && originalSongsList != null)
//                        songsList.addAll(originalSongsList);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (songsList != null && songsList.size() > 0) {
                if (position >= songsList.size() - 1) {
                    position = 0;
                } else if (position != songsList.size() - 1) {
                    position = position + 1;
                }

                if (songsList.size() > position) {
                    Log.e("TAG", "media player position : " + position);
                    setMediaPlayer(songsList.get(position));
                } else {
                    Log.e("TAG", "Inner Loop caled" + position);
                }
            } else {
                Log.e("TAG", "Outer Loop caled" + position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playQueueSong(int position, SongEntity songEntity) {
        try {
            this.position = position;
            if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
                setMediaPlayer(songEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playLastQueueSong(int position, SongEntity songEntity) {
        try {

            if (songsList != null) songsList.clear();
            songsList.addAll(lastQueueSongsList);

            if (lastQueueSongsList != null && originalSongsList != null && originalSongsList.size() > 0) {
                if (!lastQueueSongsList.equals(originalSongsList)) {
                    lastQueueSongsList.clear();
                    lastQueueSongsList.addAll(originalSongsList);
                }
            }

            if (originalSongsList != null) originalSongsList.clear();
            originalSongsList.addAll(songsList);
            this.position = position;
            Intent updatePlayBroadCast = new Intent("LastQueuePlaySong");
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(updatePlayBroadCast);

            if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
                setMediaPlayerPlayMode();
                setMediaPlayer(songEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playDeleteNextSong() {
        try {
            if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
                setMediaPlayerPlayMode();
                setMediaPlayer(songsList.get(position));
                startStopOnClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerPlayMode() {
        try {
            Log.e("TAG", "playMode setMediaPlayerPlayMode" + playMode);
            //int playMode = 1;//1 for loop-all,2 for single,3 for random
            if (mediaPlayer != null) {
                if (playMode == 1) {
                    mediaPlayer.setLooping(false);
                    binding.btnPlayMode.setImageResource(R.drawable.player_ic_playmode_loop_all);
                    if (songsList != null) songsList.clear();
                    if (songsList != null && originalSongsList != null)
                        songsList.addAll(originalSongsList);
                } else if (playMode == 2) {
                    mediaPlayer.setLooping(true);
                    binding.btnPlayMode.setImageResource(R.drawable.player_ic_playmode_single_loop);
                    if (songsList != null) songsList.clear();
                    if (songsList != null && originalSongsList != null)
                        songsList.addAll(originalSongsList);
                } else if (playMode == 3) {
                    mediaPlayer.setLooping(false);
                    binding.btnPlayMode.setImageResource(R.drawable.player_ic_playmode_random);
                    if (songsList != null) Collections.shuffle(songsList);
                    try {
                        int pos = songsList.indexOf(playSongEntity);
                        Log.e("TAG", "pos : " + pos);
                        if (pos >= 0) {
                            position = pos;
                        }
                        Log.e("TAG", "position : " + position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.e("TAG", "playMode setMediaPlayerPlayMode" + songsList.size());
            Log.e("TAG", "playMode setMediaPlayerPlayMode" + mediaPlayer.isLooping());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startStopOnClick() {
        try {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        //binding.musicPlayerView.stop();
                        if (objectAnimator != null) objectAnimator.pause();
                        //binding.visualizerView.hide();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_play);

                        Intent updatePlayBroadCast = new Intent("GetPlaySong");
                        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(updatePlayBroadCast);
                    } else {
                        mediaPlayer.start();
                        //binding.musicPlayerView.start();
                        if (objectAnimator != null) objectAnimator.resume();
                        //binding.visualizerView.show();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_pause);

                        if (playSongEntity != null) {
                            Intent updatePlayBroadCast = new Intent("GetPlaySong");
                            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(updatePlayBroadCast);
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

    private void setMediaPlayer(SongEntity songEntity) {
        try {
            Log.e("TAG", "setMediaPlayer called");
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(songEntity.getData());
                mediaPlayer.setOnPreparedListener(mp -> {
                    try {
                        Log.e("TAG", "setOnPreparedListener called");
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(mp1 -> {
                            try {
                                Log.e("TAG", "setOnCompletionListener called");
                                if (objectAnimator != null) objectAnimator.pause();
                                playNextSong(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        setMediaPlayerData(songEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                Toast.makeText(requireActivity(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaPlayerData(SongEntity songEntity) {
        try {
            playSongEntity = songEntity;
            Intent updatePlayBroadCast = new Intent("GetPlaySong");
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(updatePlayBroadCast);

//            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
//                try {
//                    Log.e("TAG", "setOnErrorListener what " + what);
//                    Log.e("TAG", "setOnErrorListener extra " + extra);
//                    if (objectAnimator != null) objectAnimator.pause();
//                    playNextSong();
//                    requireActivity().runOnUiThread(() -> {
//                        try {
//                            Toast.makeText(requireActivity(), "what : " + what + " extra : " + extra, Toast.LENGTH_SHORT).show();
//                            //Toast.makeText(requireActivity(), requireActivity().getResources().getString(R.string.error_song), Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return false;
//            });

            binding.audioNameTextView.setText("" + (("" + songEntity.getTitle()).replace("null", "").replace("Null", "")));
            binding.artistNameTextView.setText("" + (("" + songEntity.getArtistName()).replace("null", "").replace("Null", "")));
            binding.audioNameTextViewMin.setText("" + (("" + songEntity.getTitle()).replace("null", "").replace("Null", "")));
            binding.artistNameTextViewMin.setText("" + (("" + songEntity.getArtistName()).replace("null", "").replace("Null", "")));

            if (("" + (("" + songEntity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                binding.visualizerView.setImageResource(R.drawable.ic_default_song);
            } else {
                binding.visualizerView.setImageBitmap(ImageUtil.convertToBitmap(songEntity.getBitmapCover()));
            }

            try {
                if (("" + (("" + songEntity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                    binding.imageViewRotate.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_song_player)));
                } else {
                    binding.imageViewRotate.setImageDrawable(new BitmapDrawable(getResources(), ImageUtil.convertToBitmap(songEntity.getBitmapCover())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.playPauseImageView.setImageResource(R.drawable.player_ic_pause);
            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);

            setAudioProgress();
            //setMediaPlayerPlayMode();
            setColorPlatte1();
            setColorPlatte2();

            if (playMode == 2) {
                mediaPlayer.setLooping(true);
            } else {
                mediaPlayer.setLooping(false);
            }


            if (objectAnimator != null) objectAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColorPlatte1() {
        try {
            try {
                if (binding.playerMotion.getCurrentState() == 2131296702) {
                    Bitmap bitmap = ((BitmapDrawable) binding.imageViewRotate.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(palette -> {
                        int color1 = 0, color2 = 0;

                        try {
                            int vibrant = palette.getVibrantColor(0x000000); // <=== color you want
                            int vibrantLight = palette.getLightVibrantColor(0x000000);
                            int vibrantDark = palette.getDarkVibrantColor(0x000000);
                            int muted = palette.getMutedColor(0x000000);
                            int mutedLight = palette.getLightMutedColor(0x000000);
                            int mutedDark = palette.getDarkMutedColor(0x000000);

                            if (vibrant != 0 && muted != 0) {
                                color1 = vibrant;
                                color2 = muted;
                            } else if (vibrantLight != 0 && mutedLight != 0) {
                                color1 = vibrantLight;
                                color2 = mutedLight;
                            } else if (vibrantDark != 0 && mutedDark != 0) {
                                color1 = vibrantDark;
                                color2 = mutedDark;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (color1 != 0 && color2 != 0) {
                                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color1, color2});
                                gd.setCornerRadius(0f);
                                binding.playerBackgroundView.setBackgroundDrawable(gd);
                                requireActivity().getWindow().setStatusBarColor(color1);
                                requireActivity().getWindow().setNavigationBarColor(color2);
                            } else {
                                requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.playerBackground));
                                //requireActivity().getWindow().setNavigationBarColor(navigationBarColor);
                                //binding.playerBackgroundView.setBackgroundColor(requireActivity().getResources().getColor(R.color.playerBackground));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.playerBackground));
                    //requireActivity().getWindow().setNavigationBarColor(navigationBarColor);
                    //binding.playerBackgroundView.setBackgroundColor(requireActivity().getResources().getColor(R.color.playerBackground));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColorPlatte2() {
        try {
            try {
                if (binding.playerMotion.getCurrentState() == 2131296702) {
                    Bitmap bitmap = ((BitmapDrawable) binding.imageViewRotate.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(palette -> {
                        int color1 = 0, color2 = 0;

                        try {
                            int vibrant = palette.getVibrantColor(0x000000); // <=== color you want
                            int vibrantLight = palette.getLightVibrantColor(0x000000);
                            int vibrantDark = palette.getDarkVibrantColor(0x000000);
                            int muted = palette.getMutedColor(0x000000);
                            int mutedLight = palette.getLightMutedColor(0x000000);
                            int mutedDark = palette.getDarkMutedColor(0x000000);

                            if (vibrant != 0 && muted != 0) {
                                color1 = vibrant;
                                color2 = muted;
                            } else if (vibrantLight != 0 && mutedLight != 0) {
                                color1 = vibrantLight;
                                color2 = mutedLight;
                            } else if (vibrantDark != 0 && mutedDark != 0) {
                                color1 = vibrantDark;
                                color2 = mutedDark;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (color1 != 0 && color2 != 0) {
                                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{color1, color2});
                                gd.setCornerRadius(0f);
                                binding.playerBackgroundView.setBackgroundDrawable(gd);
                                requireActivity().getWindow().setStatusBarColor(color1);
                                requireActivity().getWindow().setNavigationBarColor(color2);
                            } else {
                                requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.playerBackground));
                                requireActivity().getWindow().setNavigationBarColor(navigationBarColor);
                                binding.playerBackgroundView.setBackgroundColor(requireActivity().getResources().getColor(R.color.playerBackground));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.playerBackground));
                    requireActivity().getWindow().setNavigationBarColor(navigationBarColor);
                    binding.playerBackgroundView.setBackgroundColor(requireActivity().getResources().getColor(R.color.playerBackground));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAudioProgress() {
        try {
            int duration = mediaPlayer.getDuration();
            if (duration <= 0) {
                duration = (int) playSongEntity.getDuration();
            }

            binding.seekDurationTextView.setText(AppUtils.secondToTime(((int) duration) / 1000));
            binding.seekTimeTextView.setText(AppUtils.secondToTime(((int) duration) / 1000));

            //binding.musicPlayerView.setMax(mediaPlayer.getDuration());
            binding.seekBar.setProgress(0);
            binding.seekBar.setMax(duration);

            binding.seekBarMin.setProgress(0);
            binding.seekBarMin.setMax(duration);

            binding.seekBarMin.setOnTouchListener((view, motionEvent) -> true);

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
                        binding.seekTimeTextView.setText(AppUtils.secondToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));
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

    public void closeMediaPlayer() {
        try {
            if (mediaPlayer != null) {

                binding.playerMotion.transitionToCollapse();
                binding.playerMotion.setProgress(0);

                Intent updatePlayBroadCast = new Intent("GetPlaySong");
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(updatePlayBroadCast);

                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                playSongEntity = null;
                if (songsList != null) songsList.clear();
                if (originalSongsList != null) originalSongsList.clear();
                //binding.visualizerView.hide();

                if (bottomSheetFragmentQueue != null) {
                    bottomSheetFragmentQueue.dismiss();
                    bottomSheetFragmentQueue = null;
                }

//                HomeActivity.fragmentPlayer = null;
//                HomeActivity.fragmentPlayer = FragmentPlayer.class.newInstance();
//                (requireActivity().getSupportFragmentManager()).beginTransaction().replace(R.id.play_screen_frame_layout, HomeActivity.fragmentPlayer, "FragmentPlayer").commitAllowingStateLoss();
                HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);


                requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.playerBackground));
                requireActivity().getWindow().setNavigationBarColor(navigationBarColor);
                binding.playerBackgroundView.setBackgroundColor(requireActivity().getResources().getColor(R.color.playerBackground));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDeleteQueueSong() {
        try {
            boolean isSongDelete = true;
            if (playSongEntity != null) {
                for (SongEntity model : songsList) {
                    if (model.getId() == playSongEntity.getId()) {
                        isSongDelete = false;
                    }
                }
            }
            Log.e("songsList", isSongDelete + " isSongDelete on delete songsList size " + songsList.size());
            if (songsList != null && songsList.size() > 0) {
                if (isSongDelete) {
                    if (position > songsList.size()) {
//                                position = songsList.size() - 1;
                        position = 0;
                    }

                    if (songsList.size() > position) {
                        setMediaPlayer(songsList.get(position));
                        //    startStopOnClick();
                    }
                }
            } else {
                closeMediaPlayer();
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
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            closeMediaPlayer();
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