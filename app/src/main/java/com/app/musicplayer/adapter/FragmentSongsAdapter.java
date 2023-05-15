package com.app.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.databinding.AdapterFragmentSongsBinding;
import com.app.musicplayer.db.SongModel;

import java.util.List;

public class FragmentSongsAdapter extends RecyclerView.Adapter<FragmentSongsAdapter.MyViewHolder> {
    String TAG = FragmentSongsAdapter.class.getSimpleName();
    Context context;
    List<SongModel> songList;
    int selectedSortTypeRadio;
    SongsClickListner songsClickListner;

    public FragmentSongsAdapter(int selected, List<SongModel> list, Context context, SongsClickListner clickListner) {
        this.context = context;
        songList = list;
        selectedSortTypeRadio = selected;
        songsClickListner = clickListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(AdapterFragmentSongsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            if (selectedSortTypeRadio == 0) {
                holder.binding.txtMsg.setText("" + (("" + result.getAlbumName()).replace("null", "").replace("Null", "")));
            } else if (selectedSortTypeRadio == 1) {
                holder.binding.txtMsg.setText("" + (("" + result.getArtistName()).replace("null", "").replace("Null", "")));
            } else {
                holder.binding.txtMsg.setText("" + (("" + result.getGenreName()).replace("null", "").replace("Null", "")));
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
                        Log.v(TAG, "binding.getRoot().setOnClickListener getLayoutPosition()....." + getPosition());
                        songsClickListner.onSongsClick(songList.get(getPosition()), getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.v(TAG, "binding.getRoot().setOnLongClickListener getLayoutPosition()....." + getPosition());
                        songsClickListner.onSongLongClick(songList.get(getPosition()), getPosition());
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface SongsClickListner {
        void onSongsClick(SongModel result, int position);

        void onSongLongClick(SongModel result, int position);
    }
}