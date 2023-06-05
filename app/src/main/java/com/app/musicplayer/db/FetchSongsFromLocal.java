package com.app.musicplayer.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.app.musicplayer.entity.SongEntity;

import java.util.ArrayList;
import java.util.List;

public class FetchSongsFromLocal {

    public static List<SongEntity> getAllSongs(Context context) {
        List<SongEntity> songs = new ArrayList();
        SongCursorWrapper cursor = queryAllSong(context, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SongEntity song = cursor.getSong(context);
                    song.setAlbumArt("" + getAlbumUri(song.getAlbumId()).toString());
                    songs.add(song);
                } while (cursor.moveToNext());
            } else {
                Log.e("TAG", "cursor else called ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return songs;
    }

    static SongCursorWrapper queryAllSong(Context context, String whereClause, String[] whereArgs) {

        String selection;
        if (whereClause != null) {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 AND " + whereClause;
        } else {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        }
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, // where clause
                whereArgs,       //whereargs
                sortOrder);

//        MergeCursor cursor = new MergeCursor(new Cursor[]{context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, // where clause
//                whereArgs,       //whereargs
//                sortOrder),
//                context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, selection, // where clause
//                whereArgs,       //whereargs
//                sortOrder)});


        if (cursor == null) Log.e("TAG", "cursor is null called ");

        return new SongCursorWrapper(cursor);
    }

    static Uri getAlbumUri(int albumId) {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(albumArtUri, albumId);
    }


    public static List<SongEntity> getSelectedSongs(Context context, String path) {
        List<SongEntity> songs = new ArrayList();
        SongCursorWrapper cursor = querySelectedSong(context, path);
        try {
            if (cursor.moveToFirst()) {
                do {
                    SongEntity song = cursor.getSong(context);
                    song.setAlbumArt("" + getAlbumUri(song.getAlbumId()).toString());
                    songs.add(song);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return songs;
    }

    static SongCursorWrapper querySelectedSong(Context context, String path) {
        //Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{path}, "");
        MergeCursor cursor = new MergeCursor(new Cursor[]{
                context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{path}, ""),
                context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + " = ?", new String[]{path}, "")});
        return new SongCursorWrapper(cursor);
    }
}