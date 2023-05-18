package com.app.musicplayer.db;

import com.app.musicplayer.AppController;

import java.util.List;

public class DBUtils {

    public static void insertSingleSongs(SongModel song) {
        try {
            song.setIsChecked(false);
            song.setIsTrashed(false);
            if (!checkSongIsExistInDB(song.getSongId())) {
                AppController.getDaoSession().getSongModelDao().save(song);
            } else {
                restoreSongIsExistInDB(song.getSongId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertMultipleSongs(List<SongModel> songs) {
        try {
            for (SongModel song : songs) {
                song.setIsChecked(false);
                song.setIsTrashed(false);
                if (!checkSongIsExistInDB(song.getSongId())) {
                    AppController.getDaoSession().getSongModelDao().save(song);
                } else {
                    restoreSongIsExistInDB(song.getSongId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSingleSongs(SongModel song) {
        try {
            AppController.getDaoSession().getSongModelDao().update(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMultipleSongs(List<SongModel> songs) {
        try {
            for (SongModel song : songs) {
                AppController.getDaoSession().getSongModelDao().update(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trashMultipleSongs(List<SongModel> songs) {
        try {
            for (SongModel song : songs) {
                song.setIsChecked(false);
                song.setIsTrashed(true);
                AppController.getDaoSession().getSongModelDao().update(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trashSingleSongs(SongModel song) {
        try {
            song.setIsChecked(false);
            song.setIsTrashed(true);
            AppController.getDaoSession().getSongModelDao().delete(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreMultipleSongs(List<SongModel> songs) {
        try {
            for (SongModel song : songs) {
                song.setIsChecked(false);
                song.setIsTrashed(false);
                AppController.getDaoSession().getSongModelDao().update(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreSingleSongs(SongModel song) {
        try {
            song.setIsChecked(false);
            song.setIsTrashed(false);
            AppController.getDaoSession().getSongModelDao().update(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleSongs(List<SongModel> songs) {
        try {
            for (SongModel song : songs) {
                AppController.getDaoSession().getSongModelDao().delete(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSingleSongs(SongModel song) {
        try {
            AppController.getDaoSession().getSongModelDao().delete(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllSongs(SongModel song) {
        try {
            AppController.getDaoSession().getSongModelDao().deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<SongModel> getAllSongs() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).list();
    }

    public static List<SongModel> getSearchSongsByName(String searchText) {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.Title.like("%" + searchText + "%"), SongModelDao.Properties.IsTrashed.eq(false)).list();
    }

    public static List<SongModel> getSearchTrashSongsByName(String searchText) {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.Title.like("%" + searchText + "%"), SongModelDao.Properties.IsTrashed.eq(true)).list();
    }

    public static boolean checkSongIsExistInDB(int songId) {
        if (AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.SongId.eq(songId)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void restoreSongIsExistInDB(int songId) {
        restoreMultipleSongs(AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.SongId.eq(songId)).list());
    }

    public static List<SongModel> getAllSongByNameAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderAsc(SongModelDao.Properties.Title).list();
    }

    public static List<SongModel> getAllSongByNameDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderDesc(SongModelDao.Properties.Title).list();
    }

    public static List<SongModel> getAllSongByDateAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderAsc(SongModelDao.Properties.DateAdded).list();
    }

    public static List<SongModel> getAllSongByDateDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderDesc(SongModelDao.Properties.DateAdded).list();
    }

    public static List<SongModel> getAllSongByDurationAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderAsc(SongModelDao.Properties.Duration).list();
    }

    public static List<SongModel> getAllSongByDurationDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderDesc(SongModelDao.Properties.Duration).list();
    }

    public static List<SongModel> getAllSongBySizeAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderAsc(SongModelDao.Properties.Size).list();
    }

    public static List<SongModel> getAllSongBySizeDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(false)).orderDesc(SongModelDao.Properties.Size).list();
    }

    public static List<SongModel> getAllTrashSongs() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).list();
    }

    public static List<SongModel> getTrashedSongByNameAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderAsc(SongModelDao.Properties.Title).list();
    }

    public static List<SongModel> getTrashedSongByNameDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderDesc(SongModelDao.Properties.Title).list();
    }

    public static List<SongModel> getTrashedSongByDateAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderAsc(SongModelDao.Properties.DateAdded).list();
    }

    public static List<SongModel> getTrashedSongByDateDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderDesc(SongModelDao.Properties.DateAdded).list();
    }

    public static List<SongModel> getTrashedSongByDurationAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderAsc(SongModelDao.Properties.Duration).list();
    }

    public static List<SongModel> getTrashedSongByDurationDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderDesc(SongModelDao.Properties.Duration).list();
    }

    public static List<SongModel> getTrashedSongBySizeAsc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderAsc(SongModelDao.Properties.Size).list();
    }

    public static List<SongModel> getTrashedSongBySizeDesc() {
        return AppController.getDaoSession().getSongModelDao().queryBuilder().where(SongModelDao.Properties.IsTrashed.eq(true)).orderDesc(SongModelDao.Properties.Size).list();
    }
}