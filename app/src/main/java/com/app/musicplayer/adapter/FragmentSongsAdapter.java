package com.app.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.R;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.databinding.AdapterFragmentSongsBinding;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.ui.fragment.FragmentPlayer;
import com.app.musicplayer.utils.ImageUtil;

import java.util.Objects;

public class FragmentSongsAdapter extends ListAdapter<SongEntity, FragmentSongsAdapter.ItemViewHolder> {

    String TAG = FragmentSongsAdapter.class.getSimpleName();
    SongsClickListener songsClickListener;
    Context context;
//    int selectedSortTypeRadio;

//    SongEntity playSongEntity = null;
//    Boolean isPlaying = false;
//    FragmentPlayer fragmentPlayer = null;

    boolean isAnyLongPress = false;

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

    public FragmentSongsAdapter(int selected, Context con, SongsClickListener listener) {
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
        songsClickListener = listener;
        context = con;
//        selectedSortTypeRadio = selected;
        //getFragment();
        //getPlayerInfo();
    }

//    private void getFragment() {
//        try {
//            fragmentPlayer = HomeActivity.fragmentPlayer;
//            //fragmentPlayer = (FragmentPlayer) ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("FragmentPlayer");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void getPlayerInfo() {
//        if (fragmentPlayer != null && fragmentPlayer.mediaPlayer != null && fragmentPlayer.playSongEntity != null && fragmentPlayer.mediaPlayer.isPlaying()) {
//            playSongEntity = fragmentPlayer.playSongEntity;
//            isPlaying = true;
//        }
//    }
//
//    public void setPlayerInfo(SongEntity sModel, Boolean playing) {
//        playSongEntity = sModel;
//        isPlaying = playing;
//        getFragment();
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(AdapterFragmentSongsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        final AdapterFragmentSongsBinding binding;

        public ItemViewHolder(@NonNull AdapterFragmentSongsBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        SongEntity songEntity = getItem(getPosition());
                        if (isAnyLongPress) {
                            songsClickListener.deleteSongs(songEntity, !songEntity.getIsChecked(), getPosition());
                            songEntity.setIsChecked(!songEntity.getIsChecked());
                            notifyDataSetChanged();
                        } else {
                            songsClickListener.onSongsClick(songEntity, getPosition());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    try {
                        SongEntity songEntity = getItem(getPosition());
                        songEntity.setIsChecked(isChecked);
                        songsClickListener.deleteSongs(songEntity, isChecked, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.getRoot().setOnLongClickListener(v -> {
                    try {
                        isAnyLongPress = true;
                        songsClickListener.onSongLongClick(getItem(getPosition()), getPosition());
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

        public void bindHolder(final SongEntity entity, final int position) {
            try {
                if (binding != null) {
                    binding.txtTitle.setText("" + (("" + entity.getTitle()).replace("null", "").replace("Null", "")));
                    binding.txtMsg.setText("" + (("" + entity.getArtistName()).replace("null", "").replace("Null", "")));
//            if (selectedSortTypeRadio == 0) {
//                binding.txtMsg.setText("" + (("" + entity.getAlbumName()).replace("null", "").replace("Null", "")));
//            } else if (selectedSortTypeRadio == 1) {
//                binding.txtMsg.setText("" + (("" + entity.getArtistName()).replace("null", "").replace("Null", "")));
//            } else {
//                binding.txtMsg.setText("" + (("" + entity.getGenreName()).replace("null", "").replace("Null", "")));
//            }

                    if (("" + (("" + entity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                        binding.imageView.setImageResource(R.drawable.icv_songs);
                    } else {
                        binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(entity.getBitmapCover()));
                    }

                    binding.imageView.setVisibility(View.VISIBLE);
                    binding.gifView.setVisibility(View.GONE);
                    binding.txtTitle.setTextColor(context.getResources().getColor(android.R.color.white));

                    try {
                        if (!isAnyLongPress && HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.mediaPlayer != null && HomeActivity.fragmentPlayer.playSongEntity != null && Objects.equals(HomeActivity.fragmentPlayer.playSongEntity.getId(), entity.getId()) && HomeActivity.fragmentPlayer.mediaPlayer.isPlaying()) {
                            binding.gifView.setVisibility(View.VISIBLE);
                            binding.imageView.setVisibility(View.VISIBLE);
                            binding.txtTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    binding.cbDelete.setChecked(entity.getIsChecked());
                    if (isAnyLongPress) {
                        binding.cbDelete.setVisibility(View.VISIBLE);
                        binding.layoutOptions.setVisibility(View.GONE);
                    } else {
                        binding.cbDelete.setVisibility(View.GONE);
                        binding.layoutOptions.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface SongsClickListener {
        void onSongsClick(SongEntity result, int position);

        void onSongLongClick(SongEntity result, int position);

        void deleteSongs(SongEntity result, boolean isChecked, int position);
    }
}