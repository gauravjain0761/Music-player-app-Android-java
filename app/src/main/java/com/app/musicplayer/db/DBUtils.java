package com.app.musicplayer.db;

import com.app.musicplayer.AppController;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.entity.SongEntityDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBUtils {

    static final int NUMBER_OF_THREADS = 10;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public interface TaskComplete {
        void onTaskComplete();
    }

    public static void insertSingleSongs(final SongEntity song, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                song.setIsChecked(false);
                song.setIsTrashed(false);
                if (!checkSongIsExistInDB(song.getSongId())) {
                    AppController.getDaoSession().getSongEntityDao().save(song);
                } else {
                    restoreSongIsExistInDB(song.getSongId());
                }
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertMultipleSongs(final List<SongEntity> songs, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                for (SongEntity song : songs) {
                    song.setIsChecked(false);
                    song.setIsTrashed(false);
                    if (!checkSongIsExistInDB(song.getSongId())) {
                        AppController.getDaoSession().getSongEntityDao().save(song);
                    } else {
                        restoreSongIsExistInDB(song.getSongId());
                    }
                }
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSingleSongs(final SongEntity song, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                AppController.getDaoSession().getSongEntityDao().update(song);
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMultipleSongs(final List<SongEntity> songs, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                for (SongEntity song : songs) {
                    AppController.getDaoSession().getSongEntityDao().update(song);
                }
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trashMultipleSongs(final List<SongEntity> songs, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                for (SongEntity song : songs) {
                    song.setIsChecked(false);
                    song.setIsTrashed(true);
                    AppController.getDaoSession().getSongEntityDao().update(song);
                }
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trashSingleSongs(final SongEntity song, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                song.setIsChecked(false);
                song.setIsTrashed(true);
                AppController.getDaoSession().getSongEntityDao().delete(song);
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreMultipleSongs(final List<SongEntity> songs, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                for (SongEntity song : songs) {
                    song.setIsChecked(false);
                    song.setIsTrashed(false);
                    AppController.getDaoSession().getSongEntityDao().update(song);
                }
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreSingleSongs(final SongEntity song, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                song.setIsChecked(false);
                song.setIsTrashed(false);
                AppController.getDaoSession().getSongEntityDao().update(song);
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleSongs(final List<SongEntity> songs, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                for (SongEntity song : songs) {
                    AppController.getDaoSession().getSongEntityDao().delete(song);
                }
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSingleSongs(final SongEntity song, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                AppController.getDaoSession().getSongEntityDao().delete(song);
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllSongs(final SongEntity song, final TaskComplete taskComplete) {
        try {
            databaseWriteExecutor.execute(() -> {
                AppController.getDaoSession().getSongEntityDao().deleteAll();
                taskComplete.onTaskComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkSongIsTrashedInDB(int songId) {
        if (AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.SongId.eq(songId), SongEntityDao.Properties.IsTrashed.eq(false)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkSongIsExistInDB(int songId) {
        if (AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.SongId.eq(songId)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void restoreSongIsExistInDB(int songId) {
        databaseWriteExecutor.execute(() -> restoreMultipleSongs(AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.SongId.eq(songId)).list(), () -> {
        }));
    }

    public static List<SongEntity> getAllSongByNameAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getAllSongs() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).list();
    }

    public static List<SongEntity> getSearchSongsByName(String searchText) {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.Title.like("%" + searchText + "%"), SongEntityDao.Properties.IsTrashed.eq(false)).list();
    }

    public static List<SongEntity> getSearchTrashSongsByName(String searchText) {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.Title.like("%" + searchText + "%"), SongEntityDao.Properties.IsTrashed.eq(true)).list();
    }

    public static List<SongEntity> getAllSongByNameDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getAllSongByDateAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getAllSongByDateDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getAllSongByDurationAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getAllSongByDurationDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getAllSongBySizeAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderAsc(SongEntityDao.Properties.Size).list();
    }

    public static List<SongEntity> getAllSongBySizeDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(false)).orderDesc(SongEntityDao.Properties.Size).list();
    }

    public static List<SongEntity> getAllTrashSongs() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).list();
    }

    public static List<SongEntity> getTrashedSongByNameAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getTrashedSongByNameDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.Title).list();
    }

    public static List<SongEntity> getTrashedSongByDateAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getTrashedSongByDateDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.DateAdded).list();
    }

    public static List<SongEntity> getTrashedSongByDurationAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getTrashedSongByDurationDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.Duration).list();
    }

    public static List<SongEntity> getTrashedSongBySizeAsc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderAsc(SongEntityDao.Properties.Size).list();
    }

    public static List<SongEntity> getTrashedSongBySizeDesc() {
        return AppController.getDaoSession().getSongEntityDao().queryBuilder().where(SongEntityDao.Properties.IsTrashed.eq(true)).orderDesc(SongEntityDao.Properties.Size).list();
    }
}