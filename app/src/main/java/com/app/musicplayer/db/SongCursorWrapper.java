package com.app.musicplayer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.app.musicplayer.R;
import com.app.musicplayer.entity.SongEntity;
import com.app.musicplayer.utils.ImageUtil;

public class SongCursorWrapper extends CursorWrapper {
    public SongCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public SongEntity getSong(Context context) {
        SongEntity songEntity = new SongEntity();
        try {
            int id = getInt(getColumnIndex(MediaStore.Audio.Media._ID));
            String title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artistName = getString(getColumnIndex(MediaStore.Audio.Media.ARTIST));
            int artistId = getInt(getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
            String albumName = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM));
            int albumId = getInt(getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String composer = getString(getColumnIndex(MediaStore.Audio.Media.COMPOSER));
            String data = getString(getColumnIndex(MediaStore.Audio.Media.DATA));
            int trackNumber = getInt(getColumnIndex(MediaStore.Audio.Media.TRACK));
            int year = getInt(getColumnIndex(MediaStore.Audio.Media.YEAR));
            long duration = getLong(getColumnIndex(MediaStore.Audio.Media.DURATION));
            long dateModified = getLong(getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
            long dateAdded = getLong(getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
            long bookmark = getLong(getColumnIndex(MediaStore.Audio.Media.BOOKMARK));
            long size = getLong(getColumnIndex(MediaStore.Audio.Media.SIZE));
            String author = "", genreName = "";
            int genreId = 0;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    author = getString(getColumnIndex(MediaStore.Audio.Media.AUTHOR));
                    genreName = getString(getColumnIndex(MediaStore.Audio.Media.GENRE));
                    genreId = getInt(getColumnIndex(MediaStore.Audio.Media.GENRE_ID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.e("TAG", "id " + id);
            Log.e("TAG", "Data : " + data);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap songCoverBitmap;
            byte[] coverBytes = null;
            try {
                retriever.setDataSource(data);
                coverBytes = retriever.getEmbeddedPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (coverBytes != null && coverBytes.length > 0)
                songCoverBitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
            else
                songCoverBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_medieview);

            songEntity.setGenreId(genreId);
            songEntity.setGenreName(genreName);
            songEntity.setSongId(id);
            songEntity.setTrackNumber(trackNumber);
            songEntity.setYear(year);
            songEntity.setAlbumId(albumId);
            songEntity.setArtistId(artistId);
            songEntity.setDuration(duration);
            songEntity.setDateModified(dateModified);
            songEntity.setDateAdded(dateAdded);
            songEntity.setBookmark(bookmark);
            songEntity.setTitle("" + title);
            songEntity.setArtistName("" + artistName);
            songEntity.setComposer("" + composer);
            songEntity.setAlbumName("" + albumName);
            songEntity.setData("" + data);
            songEntity.setSize(size);
            songEntity.setBitmapCover(ImageUtil.convertToString(songCoverBitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songEntity;
    }
}
