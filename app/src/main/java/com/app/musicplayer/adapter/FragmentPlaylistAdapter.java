package com.app.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.databinding.AdapterFragmentPlaylistBinding;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.activity.PlaylistSongsActivity;

import java.util.Objects;

public class FragmentPlaylistAdapter extends ListAdapter<SongEntity, FragmentPlaylistAdapter.ItemViewHolder> {

    String TAG = FragmentPlaylistAdapter.class.getSimpleName();
    FragmentPlaylistListener fragmentPlaylistListener;
    Context context;

    public FragmentPlaylistAdapter(Context con, FragmentPlaylistListener listener) {
        super(new DiffUtil.ItemCallback<SongEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull SongEntity oldItem, @NonNull SongEntity newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SongEntity oldItem, @NonNull SongEntity newItem) {
                return oldItem == newItem;
            }
        });
        fragmentPlaylistListener = listener;
        context = con;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(AdapterFragmentPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        try {
            holder.bindHolder(getItem(position), position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        final AdapterFragmentPlaylistBinding binding;

        public ItemViewHolder(@NonNull AdapterFragmentPlaylistBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        context.startActivity(new Intent(context, PlaylistSongsActivity.class));
                        fragmentPlaylistListener.setOnItemClick(getItem(getPosition()), getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void bindHolder(final SongEntity entity, final int position) {
            try {
                if (binding != null) {
                    binding.txtTitle.setText("" + (("" + entity.getTitle()).replace("null", "").replace("Null", "")));
                    binding.txtMsg.setText("" + (("" + entity.getComposer()).replace("null", "").replace("Null", "")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface FragmentPlaylistListener {
        void setOnItemClick(SongEntity entity, int position);
    }
}