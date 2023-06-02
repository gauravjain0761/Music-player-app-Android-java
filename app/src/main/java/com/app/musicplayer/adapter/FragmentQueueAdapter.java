package com.app.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.AdapterFragmentQueueBinding;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.ui.fragment.FragmentPlayer;
import com.app.musicplayer.utils.ImageUtil;

import java.util.Objects;

public class FragmentQueueAdapter extends ListAdapter<SongEntity, FragmentQueueAdapter.ItemViewHolder> {

    String TAG = FragmentQueueAdapter.class.getSimpleName();
    QueueClickListener songsClickListener;
    Context context;
    Boolean isDelete;

    public FragmentQueueAdapter(Boolean delete, Context con, QueueClickListener listener) {
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
        isDelete = delete;
        songsClickListener = listener;
        context = con;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(AdapterFragmentQueueBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        final AdapterFragmentQueueBinding binding;

        public ItemViewHolder(@NonNull AdapterFragmentQueueBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                if (isDelete) {
                    binding.imageViewClose.setVisibility(View.VISIBLE);
                } else {
                    binding.imageViewClose.setVisibility(View.GONE);
                }

                binding.getRoot().setOnClickListener(v -> {
                    try {
                        SongEntity songEntity = getItem(getPosition());
                        songsClickListener.onSongsClick(songEntity, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.imageViewClose.setOnClickListener(v -> {
                    try {
                        Log.e("TAG", "image close clicked called...");
                        SongEntity songEntity = getItem(getPosition());
                        songsClickListener.deleteSongs(songEntity, getPosition());
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
                    binding.txtMsg.setText("" + (("" + entity.getArtistName()).replace("null", "").replace("Null", "")));

                    if (("" + (("" + entity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                        binding.imageView.setImageResource(R.drawable.icv_songs);
                    } else {
                        binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(entity.getBitmapCover()));
                    }

                    binding.imageView.setVisibility(View.VISIBLE);
                    binding.gifView.setVisibility(View.GONE);
                    binding.txtTitle.setTextColor(context.getResources().getColor(android.R.color.white));

                    try {
                        if (HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.mediaPlayer != null && HomeActivity.fragmentPlayer.playSongEntity != null && Objects.equals(HomeActivity.fragmentPlayer.playSongEntity.getId(), entity.getId()) && HomeActivity.fragmentPlayer.mediaPlayer.isPlaying()) {
                            binding.gifView.setVisibility(View.VISIBLE);
                            binding.imageView.setVisibility(View.VISIBLE);
                            binding.txtTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface QueueClickListener {
        void onSongsClick(SongEntity result, int position);

        void deleteSongs(SongEntity result, int position);
    }
}