package com.app.musicplayer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.ui.activity.SearchPlaylistActivity;
import com.app.musicplayer.adapter.FragmentPlaylistAdapter;
import com.app.musicplayer.databinding.FragmentPlaylistBinding;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.contract.IFragmentPlaylistContract;
import com.app.musicplayer.ui.presenter.FragmentPlaylistPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentPlaylist extends BaseFragment<FragmentPlaylistPresenter> implements IFragmentPlaylistContract.IFragmentPlaylistView {
    String TAG = FragmentPlaylist.class.getSimpleName();
    FragmentPlaylistBinding binding;
    FragmentPlaylistAdapter adapter;
    ArrayList<SongEntity> playList = new ArrayList<>();
    int selectedSortByRadio = 0;

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentPlaylistBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentPlaylistPresenter createPresenter(Context context) {
        return new FragmentPlaylistPresenter(context, this);
    }

    @Override
    protected void initWidget(View root) {
        try {
            getPresenter().setBinding(binding);

            binding.txtSort.setOnClickListener(v -> {
                if (playList != null && playList.size() > 0) {
                    new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                        Log.e("TAG", "selected" + selected);
                        selectedSortByRadio = selected;
                        Log.e("TAG", "selected" + selectedSortByRadio);
                        refreshView();
                    }).show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                }
            });

            binding.imageViewSearch.setOnClickListener(v -> requireActivity().startActivity(new Intent(requireActivity(), SearchPlaylistActivity.class)));

            binding.fab.setOnClickListener(v -> showAddPlayListDialog());

            HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
            HomeActivity.bindingHome.playScreenFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp), (int) getResources().getDimension(com.intuit.sdp.R.dimen._65sdp));
                        binding.listView.setPadding(0,0,0,(int) getResources().getDimension(com.intuit.sdp.R.dimen._100sdp));
                    } else {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp), (int) getResources().getDimension(com.intuit.sdp.R.dimen._7sdp));
                        binding.listView.setPadding(0,0,0,(int) getResources().getDimension(com.intuit.sdp.R.dimen._50sdp));
                    }
                    int newVis = HomeActivity.bindingHome.playScreenFrameLayout.getVisibility();
                    if ((int) HomeActivity.bindingHome.playScreenFrameLayout.getTag() != newVis) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void registerEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshView() {
        try {
            if (playList != null) playList.clear();
            SongEntity menu_1 = new SongEntity();
            menu_1.setTitle("Last Queue music");
            menu_1.setComposer("12 songs");
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            playList.add(menu_1);
            if (playList != null && playList.size() > 0) {
                showListView();
            } else {
                showNoDataView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.layoutNoData.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new FragmentPlaylistAdapter(requireActivity(), (entity, position) -> {

            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(playList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.layoutNoData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddPlayListDialog() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
            alertDialog.setTitle(getResources().getString(R.string.create_playlist));
            final AppCompatEditText input = new AppCompatEditText(requireActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(15, 10, 15, 10);
            input.setLines(1);
            input.setMaxLines(1);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setLayoutParams(params);
            alertDialog.setView(input);

            alertDialog.setPositiveButton(getResources().getString(R.string.txt_confirm), null);
            alertDialog.setNegativeButton(getResources().getString(R.string.txt_cancel), null);

            final AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();

            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                try {
                    if (input.getText().toString().isEmpty()) {
                        input.setError(getResources().getString(R.string.error_playlistname));
                        input.requestFocus();
                    } else {
                        alert.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                try {
                    alert.dismiss();
                    AppUtils.hideKeyboardOnClick(requireActivity(), v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSongsList(List<SongEntity> list) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSongs(SongEntity entity) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}