package com.app.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.activity.PlaylistSongsActivity;
import com.app.musicplayer.databinding.AdapterFragmentPlaylistBinding;
import com.app.musicplayer.db.SongModel;

import java.util.ArrayList;

public class FragmentPlaylistAdapter extends RecyclerView.Adapter<FragmentPlaylistAdapter.MyViewHolder> {
    String TAG = FragmentPlaylistAdapter.class.getSimpleName();
    Context context;
    ArrayList<SongModel> playList;

    public FragmentPlaylistAdapter(ArrayList<SongModel> list, Context context) {
        this.context = context;
        playList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(AdapterFragmentPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            SongModel result = playList.get(position);
            holder.binding.txtTitle.setText("" + (("" + result.getTitle()).replace("null", "").replace("Null", "")));
            holder.binding.txtMsg.setText("" + (("" + result.getComposer()).replace("null", "").replace("Null", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final AdapterFragmentPlaylistBinding binding;

        public MyViewHolder(AdapterFragmentPlaylistBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        Log.v(TAG, "binding.getRoot().setOnClickListener getLayoutPosition()....." + getLayoutPosition());
                        context.startActivity(new Intent(context, PlaylistSongsActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}