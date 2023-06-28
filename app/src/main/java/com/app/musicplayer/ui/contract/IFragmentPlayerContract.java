package com.app.musicplayer.ui.contract;

import com.app.musicplayer.entity.SongEntity;
import com.app.mvpdemo.businessframe.base.IBasePresenter;
import com.app.mvpdemo.businessframe.base.IBasePresenterView;

import java.util.List;

public interface IFragmentPlayerContract {
    interface IFragmentPlayerPresenter extends IBasePresenter<IFragmentPlayerView> {

        /**
         * @param songEntity
         */
        void addSong(SongEntity songEntity);

        void deleteSongById(long id);

        void selectSongsList();

    }

    interface IFragmentPlayerView extends IBasePresenterView<IFragmentPlayerPresenter> {
        void updateSongsList(List<SongEntity> list);

        void addSongs(SongEntity entity);
    }
}
