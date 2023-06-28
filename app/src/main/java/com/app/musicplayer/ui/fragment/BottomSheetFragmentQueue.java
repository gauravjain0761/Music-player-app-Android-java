package com.app.musicplayer.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.musicplayer.R;
import com.app.musicplayer.ui.activity.HomeActivity;
import com.app.musicplayer.utils.pageindicator.PageIndicatorView;
import com.app.musicplayer.utils.pageindicator.animation.type.AnimationType;
import com.app.musicplayer.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetFragmentQueue extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    BottomSheetClick bottomSheetClick;

    ViewPager viewPager;
    PageIndicatorView pageIndicatorView;
    ArrayList<String> tabsList = new ArrayList<>();

    public static Dialog dialogBottomSheet;

    public BottomSheetFragmentQueue(BottomSheetClick click) {
        this.bottomSheetClick = click;
        createTabsList();
    }

    private void createTabsList() {
        try {
            if (tabsList != null) tabsList.clear();
            if (tabsList != null) {
                tabsList.add(Constants.TAB_CURRENT_PLAYED);
                if (HomeActivity.fragmentPlayer != null && HomeActivity.fragmentPlayer.lastQueueSongsList != null && HomeActivity.fragmentPlayer.lastQueueSongsList.size() > 0) {
                    tabsList.add(Constants.TAB_LAST_PLAYED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull final Dialog dialog, int style) {
        try {
            View contentView = View.inflate(getContext(), R.layout.bottomsheet_queue, null);
            dialog.setContentView(contentView);
            ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

            dialogBottomSheet = dialog;
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) contentView.getParent()));
            bottomSheetBehavior.setDraggable(false);

            viewPager = contentView.findViewById(R.id.view_pager);
            pageIndicatorView = contentView.findViewById(R.id.pageIndicatorView);

            pageIndicatorView.setCount(tabsList.size());
            pageIndicatorView.setSelection(1);
            pageIndicatorView.setAnimationType(AnimationType.THIN_WORM);
            final float density = getResources().getDisplayMetrics().density;
            pageIndicatorView.setStrokeWidth(1);
            pageIndicatorView.setBackgroundColor(getResources().getColor(R.color.et_background));
            pageIndicatorView.setRadius(3 * density);
            pageIndicatorView.setViewPager(viewPager);
            pageIndicatorView.setVisibility(View.VISIBLE);

            viewPager.setAdapter(new TabsAdapter(getChildFragmentManager(), tabsList.size()));
            pageIndicatorView.setViewPager(viewPager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class TabsAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public TabsAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.mNumOfTabs = tabCount;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentQueueCurrentPlayed();
                case 1:
                    return new FragmentQueueLastPlayed();
                default:
                    return null;
            }
        }
    }

    public interface BottomSheetClick {
        void onRadioClick(int selectedRadio);
    }
}