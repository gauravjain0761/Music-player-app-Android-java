package com.app.musicplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.R;
import com.app.musicplayer.activity.HomeActivity;
import com.app.musicplayer.databinding.AdapterFragmentSongsBinding;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.fragment.FragmentPlayer;
import com.app.musicplayer.utils.ImageUtil;
import com.app.musicplayer.visualizer.VisualizerView;
import com.app.musicplayer.visualizer.renderer.LineRenderer;

import java.util.List;
import java.util.Objects;

public class FragmentSongsAdapter extends RecyclerView.Adapter<FragmentSongsAdapter.MyViewHolder> {
    String TAG = FragmentSongsAdapter.class.getSimpleName();
    Context context;
    List<SongModel> songList;
    int selectedSortTypeRadio;
    SongsClickListner songsClickListner;

    SongModel playSongModel = null;
    Boolean isPlaying = false;
    FragmentPlayer fragmentPlayer = null;

    boolean isAnyLongPress = false;

    public void setLongPress() {
        try {
            isAnyLongPress = true;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getLongPress() {
        return isAnyLongPress;
    }

    public void clearLongPress() {
        try {
            isAnyLongPress = false;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FragmentSongsAdapter(int selected, List<SongModel> list, Context context, SongsClickListner clickListner) {
        this.context = context;
        songList = list;
        selectedSortTypeRadio = selected;
        songsClickListner = clickListner;
        getFragment();
        getPlayerInfo();
    }

    private void getFragment() {
        try {
            fragmentPlayer = HomeActivity.fragmentPlayer;
            //fragmentPlayer = (FragmentPlayer) ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("FragmentPlayer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlayerInfo() {
        if (fragmentPlayer != null && fragmentPlayer.mediaPlayer != null && fragmentPlayer.playSongModel != null && fragmentPlayer.mediaPlayer.isPlaying()) {
            playSongModel = fragmentPlayer.playSongModel;
            isPlaying = true;
        }
    }

    public void setPlayerInfo(SongModel sModel, Boolean playing) {
        playSongModel = sModel;
        isPlaying = playing;
        getFragment();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(AdapterFragmentSongsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            bindListLayout(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindListLayout(MyViewHolder holder, int position) {
        try {
            SongModel result = songList.get(position);
            holder.binding.txtTitle.setText("" + (("" + result.getTitle()).replace("null", "").replace("Null", "")));
            holder.binding.txtMsg.setText("" + (("" + result.getArtistName()).replace("null", "").replace("Null", "")));
//            if (selectedSortTypeRadio == 0) {
//                holder.binding.txtMsg.setText("" + (("" + result.getAlbumName()).replace("null", "").replace("Null", "")));
//            } else if (selectedSortTypeRadio == 1) {
//                holder.binding.txtMsg.setText("" + (("" + result.getArtistName()).replace("null", "").replace("Null", "")));
//            } else {
//                holder.binding.txtMsg.setText("" + (("" + result.getGenreName()).replace("null", "").replace("Null", "")));
//            }

            if (("" + (("" + result.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                holder.binding.imageView.setImageResource(R.drawable.icv_songs);
            } else {
                holder.binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(result.getBitmapCover()));
            }

            holder.binding.imageView.setVisibility(View.VISIBLE);
            holder.binding.gifView.setVisibility(View.GONE);
            holder.binding.txtTitle.setTextColor(context.getResources().getColor(R.color.white));


            //holder.binding.visualizerView.setVisibility(View.GONE);
//            holder.binding.imageView.setVisibility(View.VISIBLE);
//            holder.binding.visualizerView.clearRenderers();
//            holder.binding.visualizerView.clearAnimation();
//            holder.binding.visualizerView.clearFocus();
//            holder.binding.visualizerView.flash();
//            holder.binding.visualizerView.release();

            if (!isAnyLongPress && fragmentPlayer != null && fragmentPlayer.mediaPlayer != null && playSongModel != null && Objects.equals(playSongModel.getId(), result.getId()) && isPlaying) {
                Log.e("isPlaying", "isPlaying : " + isPlaying);

                holder.binding.gifView.setVisibility(View.VISIBLE);
                holder.binding.imageView.setVisibility(View.VISIBLE);
                holder.binding.txtTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));

//                holder.binding.visualizerView.clearRenderers();
//                holder.binding.visualizerView.clearAnimation();
//                holder.binding.visualizerView.clearFocus();
//                holder.binding.visualizerView.flash();
//                holder.binding.visualizerView.release();
//
//                holder.binding.visualizerView.setVisibility(View.VISIBLE);
//                holder.binding.imageView.setVisibility(View.GONE);
//
//                holder.binding.visualizerView.link(fragmentPlayer.mediaPlayer);
//                addLineRenderer(holder.binding.visualizerView);
            }

            holder.binding.cbDelete.setChecked(result.getIsChecked());
            if (isAnyLongPress) {
                holder.binding.cbDelete.setVisibility(View.VISIBLE);
            } else {
                holder.binding.cbDelete.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final AdapterFragmentSongsBinding binding;

        public MyViewHolder(AdapterFragmentSongsBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        if (isAnyLongPress) {
                            SongModel songModel = songList.get(getPosition());
                            songsClickListner.deleteSongs(songModel, !songModel.getIsChecked(), getPosition());
                            songModel.setIsChecked(!songModel.getIsChecked());
                            notifyDataSetChanged();
                        } else {
                            songsClickListner.onSongsClick(songList.get(getPosition()), getPosition());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    try {
                        SongModel songModel = songList.get(getPosition());
                        songModel.setIsChecked(isChecked);
                        songsClickListner.deleteSongs(songModel, isChecked, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.getRoot().setOnLongClickListener(v -> {
                    try {
                        isAnyLongPress = true;
                        songsClickListner.onSongLongClick(songList.get(getPosition()), getPosition());
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addLineRenderer(VisualizerView visualizerView) {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.argb(88, 0, 128, 255));

        Paint lineFlashPaint = new Paint();
        lineFlashPaint.setStrokeWidth(5f);
        lineFlashPaint.setAntiAlias(true);
        lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
        visualizerView.addRenderer(lineRenderer);
    }

    public interface SongsClickListner {
        void onSongsClick(SongModel result, int position);

        void onSongLongClick(SongModel result, int position);

        void deleteSongs(SongModel result, boolean isChecked, int position);
    }
}