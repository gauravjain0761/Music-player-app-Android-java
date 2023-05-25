package com.app.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.AdapterSongsTrashedBinding;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.ImageUtil;

import java.util.List;

public class TrashedSongsAdapter extends RecyclerView.Adapter<TrashedSongsAdapter.MyViewHolder> {
    String TAG = TrashedSongsAdapter.class.getSimpleName();
    Context context;
    List<SongModel> songList;
    DeleteSongsListner deleteSongsListner;
    boolean isAnyLongPress = false;

    public TrashedSongsAdapter(List<SongModel> list, Context context, DeleteSongsListner deleteSongs) {
        this.context = context;
        songList = list;
        deleteSongsListner = deleteSongs;
    }

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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(AdapterSongsTrashedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            final SongModel result = songList.get(position);
            holder.binding.txtTitle.setText("" + (("" + result.getTitle()).replace("null", "").replace("Null", "")));
            holder.binding.txtMsg.setText("" + (("" + result.getArtistName()).replace("null", "").replace("Null", "")));
            if (("" + (("" + result.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                holder.binding.imageView.setImageResource(R.drawable.icv_songs);
            } else {
                holder.binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(result.getBitmapCover()));
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
        final AdapterSongsTrashedBinding binding;

        public MyViewHolder(AdapterSongsTrashedBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    try {
                        SongModel songModel = songList.get(getPosition());
                        songModel.setIsChecked(isChecked);
                        deleteSongsListner.deleteSongs(songModel, isChecked, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.getRoot().setOnLongClickListener(v -> {
                    try {
                        isAnyLongPress = true;
                        deleteSongsListner.longPressSongs(isAnyLongPress);
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

    public interface DeleteSongsListner {
        void deleteSongs(SongModel result, boolean isChecked, int position);

        void longPressSongs(boolean isAnyLongPress);
    }
}