package com.app.musicplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.R;
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

    public FragmentSongsAdapter(int selected, List<SongModel> list, Context context, SongsClickListner clickListner) {
        this.context = context;
        songList = list;
        selectedSortTypeRadio = selected;
        songsClickListner = clickListner;
    }

    public void setPlayerInfo(SongModel sModel, Boolean playing) {
        playSongModel = sModel;
        isPlaying = playing;
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
            if (selectedSortTypeRadio == 0) {
                holder.binding.txtMsg.setText("" + (("" + result.getAlbumName()).replace("null", "").replace("Null", "")));
            } else if (selectedSortTypeRadio == 1) {
                holder.binding.txtMsg.setText("" + (("" + result.getArtistName()).replace("null", "").replace("Null", "")));
            } else {
                holder.binding.txtMsg.setText("" + (("" + result.getGenreName()).replace("null", "").replace("Null", "")));
            }

            if (("" + (("" + result.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                holder.binding.imageView.setImageResource(R.drawable.icv_songs);
            } else {
                holder.binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(result.getBitmapCover()));
            }

            if (isPlaying && playSongModel != null && Objects.equals(playSongModel.getId(), result.getId())) {
                holder.binding.visualizerView.setVisibility(View.VISIBLE);
                holder.binding.imageView.setVisibility(View.GONE);
//                if (FragmentPlayer.mediaPlayer != null) {
//                    try {
//                        holder.binding.visualizerView.flash();
//                        holder.binding.visualizerView.release();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    holder.binding.visualizerView.link(FragmentPlayer.mediaPlayer);
//                }
            } else {
                holder.binding.visualizerView.setVisibility(View.GONE);
                holder.binding.imageView.setVisibility(View.VISIBLE);
            }
            addLineRenderer(holder.binding.visualizerView);
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
                        songsClickListner.onSongsClick(songList.get(getPosition()), getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.getRoot().setOnLongClickListener(v -> {
                    songsClickListner.onSongLongClick(songList.get(getPosition()), getPosition());
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
    }
}