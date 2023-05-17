package com.app.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.AdapterSongsDeleteBinding;
import com.app.musicplayer.db.SongModel;
import com.app.musicplayer.utils.ImageUtil;

import java.util.List;

public class DeleteSongsAdapter extends RecyclerView.Adapter<DeleteSongsAdapter.MyViewHolder> {
    String TAG = DeleteSongsAdapter.class.getSimpleName();
    Context context;
    List<SongModel> songList;
    DeleteSongsListner deleteSongsListner;

    public DeleteSongsAdapter(List<SongModel> list, Context context, DeleteSongsListner deleteSongs) {
        this.context = context;
        songList = list;
        deleteSongsListner = deleteSongs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(AdapterSongsDeleteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            holder.binding.txtMsg.setText("" + (("" + result.getAlbumName()).replace("null", "").replace("Null", "")));
            if (("" + (("" + result.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                holder.binding.imageView.setImageResource(R.drawable.icv_songs);
            } else {
                holder.binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(result.getBitmapCover()));
            }
            holder.binding.cbDelete.setChecked(result.getIsChecked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final AdapterSongsDeleteBinding binding;

        public MyViewHolder(AdapterSongsDeleteBinding itemBinding) {
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

                binding.getRoot().setOnClickListener(v -> {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface DeleteSongsListner {
        void deleteSongs(SongModel result, boolean isChecked, int position);
    }
}