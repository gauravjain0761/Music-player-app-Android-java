package com.app.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.musicplayer.databinding.AdapterFragmentSongsBinding;
import com.app.musicplayer.databinding.AdapterSongsDeleteBinding;
import com.app.musicplayer.pojo.HomeModel;

import java.util.ArrayList;

public class DeleteSongsAdapter extends RecyclerView.Adapter<DeleteSongsAdapter.MyViewHolder> {
    String TAG = DeleteSongsAdapter.class.getSimpleName();
    Context context;
    ArrayList<HomeModel> bankList;

    public DeleteSongsAdapter(ArrayList<HomeModel> list, Context context) {
        this.context = context;
        bankList = list;
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
            HomeModel result = bankList.get(position);
            holder.binding.txtTitle.setText("" + (("" + result.getTitle()).replace("null", "").replace("Null", "")));
            holder.binding.txtMsg.setText("" + (("" + result.getMessage()).replace("null", "").replace("Null", "")));
            holder.binding.cbDelete.setChecked(result.getIsChecked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return bankList.size();
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
}