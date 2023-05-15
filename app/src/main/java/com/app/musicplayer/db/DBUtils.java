package com.app.musicplayer.db;

import android.util.Log;

import com.app.musicplayer.AppController;

import java.util.List;

public class DBUtils {

    public static void insertSingleSongs(SongModel song) {
        //if(song.getId()!=null && song.getTitle()!=null)
        if (!checkSongIsExistInDB(song.getSongId()))
            AppController.getDaoSession().getSongModelDao().save(song);
    }

    public static void insertMultipleSongs(List<SongModel> songs) {
        for (SongModel song : songs) {
            if (!checkSongIsExistInDB(song.getSongId()))
                AppController.getDaoSession().getSongModelDao().save(song);
        }
    }

    public static void updateSingleSongs(SongModel song) {
        AppController.getDaoSession().getSongModelDao().update(song);
    }

    public static void updateMultipleSongs(List<SongModel> songs) {
        for (SongModel song : songs) {
            AppController.getDaoSession().getSongModelDao().update(song);
        }
    }

    public static void deleteMultipleSongs(List<SongModel> songs) {
        for (SongModel song : songs) {
            AppController.getDaoSession().getSongModelDao().delete(song);
        }
    }

    public static void deleteSingleSongs(SongModel song) {
        AppController.getDaoSession().getSongModelDao().delete(song);
    }

    public static void deleteAllSongs(SongModel song) {
        AppController.getDaoSession().getSongModelDao().deleteAll();
    }

    public static List<SongModel> getAllSongs() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().list();
    }

    public static List<SongModel> getSearchSongsByName(String searchText) {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.Title.like("%" + searchText + "%")).list();
    }

    public static boolean checkSongIsExistInDB(int songId) {
        if (AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.SongId.eq(songId)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static List<SongModel> getAllSongByNameAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderAsc(SongModelDao.Properties.Title).list();
    }

    public static List<SongModel> getAllSongByNameDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderDesc(SongModelDao.Properties.Title).list();
    }

    public static List<SongModel> getAllSongByDateAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderAsc(SongModelDao.Properties.DateAdded).list();
    }

    public static List<SongModel> getAllSongByDateDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderDesc(SongModelDao.Properties.DateAdded).list();
    }

    public static List<SongModel> getAllSongByDurationAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderAsc(SongModelDao.Properties.Duration).list();
    }

    public static List<SongModel> getAllSongByDurationDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderDesc(SongModelDao.Properties.Duration).list();
    }

    public static List<SongModel> getAllSongBySizeAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderAsc(SongModelDao.Properties.Size).list();
    }

    public static List<SongModel> getAllSongBySizeDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().orderDesc(SongModelDao.Properties.Size).list();
    }
}
