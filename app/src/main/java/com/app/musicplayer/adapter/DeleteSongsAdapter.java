package com.app.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.databinding.AdapterSongsDeleteBinding;
import com.app.musicplayer.db.SongModel;

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
            Log.v(TAG, "onBindViewHolder called.....");
            bindListLayout(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindListLayout(MyViewHolder holder, int position) {
        try {
            SongModel result = songList.get(position);
            holder.binding.txtTitle.setText("" + (("" + result.getTitle()).replace("null", "").replace("Null", "")));
            holder.binding.txtMsg.setText("" + (("" + result.getAlbumName()).replace("null", "").replace("Null", "")));
            holder.binding.cbDelete.setChecked(result.getIsChecked());
            holder.binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> deleteSongsListner.deleteSongs(result, isChecked, position));
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
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        Log.v(TAG, "binding.getRoot().setOnClickListener getLayoutPosition()....." + getLayoutPosition());
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