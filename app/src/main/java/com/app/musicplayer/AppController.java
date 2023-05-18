package com.app.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.StrictMode;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.app.musicplayer.db.DaoMaster;
import com.app.musicplayer.db.DaoSession;
import com.app.musicplayer.utils.SPUtils;

import org.greenrobot.greendao.database.Database;

public class AppController extends MultiDexApplication {
    static final String TAG = AppController.class.getSimpleName();
    static SharedPreferences sp_userinfo;
    static SharedPreferences sp_songInfo;
    public static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MultiDex.install(this);

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "musicplayer-db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();

            initSharedPreferences();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    private void initSharedPreferences() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sp_userinfo = EncryptedSharedPreferences.create(getApplicationContext(), SPUtils.USER_INFO, new MasterKey.Builder(getApplicationContext()).setKeyGenParameterSpec(new KeyGenParameterSpec.Builder(MasterKey.DEFAULT_MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()).build(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } else {
                sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sp_songInfo = EncryptedSharedPreferences.create(getApplicationContext(), SPUtils.SONG_INFO, new MasterKey.Builder(getApplicationContext()).setKeyGenParameterSpec(new KeyGenParameterSpec.Builder(MasterKey.DEFAULT_MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()).build(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } else {
                sp_songInfo = getApplicationContext().getSharedPreferences(SPUtils.SONG_INFO, Context.MODE_PRIVATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }

    public static synchronized SharedPreferences getSpSongInfo() {
        return sp_songInfo;
    }
}