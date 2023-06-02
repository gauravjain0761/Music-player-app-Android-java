package com.app.musicplayer.ui.activity;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.musicplayer.R;
import com.app.musicplayer.adapter.FragmentPlaylistAdapter;
import com.app.musicplayer.databinding.ActivitySearchPlaylistBinding;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.ui.IActivityContract;
import com.app.musicplayer.ui.presenter.SearchPlaylistActivityPresenter;
import com.app.musicplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchPlaylistActivity extends BaseActivity<SearchPlaylistActivityPresenter> implements IActivityContract.IActivityView {

    ActivitySearchPlaylistBinding binding;
    SearchPlaylistActivityPresenter presenter;
    FragmentPlaylistAdapter adapter;
    String TAG = SearchPlaylistActivity.class.getSimpleName();
    ArrayList<SongEntity> playList = new ArrayList<>();

    @Override
    protected boolean isSupportHeadLayout() {
        return false;
    }

    @Override
    protected boolean isNeedSetStatusBar() {
        return true;
    }

    @Override
    protected ViewBinding getContentView() {
        binding = ActivitySearchPlaylistBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected SearchPlaylistActivityPresenter createPresenter(Context context) {
        presenter = new SearchPlaylistActivityPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void setStatusBar() {
        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initWidget() {
        try {
            binding.etSearch.mSearchActivity = this;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT);

            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(SearchPlaylistActivity.this, v);
                finish();
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
            menu_1.setTitle("Play love me a lot");
            menu_1.setComposer("12 songs");
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
    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(SearchPlaylistActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new FragmentPlaylistAdapter(SearchPlaylistActivity.this, (entity, position) -> {

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
            binding.txtNoData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
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