package com.app.musicplayer.ui.contract;

import com.app.musicplayer.entity.SongEntity;
import com.app.mvpdemo.businessframe.base.IBasePresenter;
import com.app.mvpdemo.businessframe.base.IBasePresenterView;

import java.util.List;

public interface IActivityTrashedSongsContract {
    interface IActivityTrashedSongsPresenter extends IBasePresenter<IActivityTrashedSongsView> {

        /**
         * @param songEntity
         */
        void addSong(SongEntity songEntity);

        void deleteSongById(long id);

        void selectSongsList();

    }

    interface IActivityTrashedSongsView extends IBasePresenterView<IActivityTrashedSongsPresenter> {
        void updateSongsList(List<SongEntity> list);

        void addSongs(SongEntity entity);

        void refreshView();

        void showListView();

        void showNoDataView();

        void notifyAdapter();

        void setListView();
    }
}
