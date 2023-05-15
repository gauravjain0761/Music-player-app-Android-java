package com.app.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

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
    public static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MultiDex.install(this);
            Log.e(TAG, "AppController onCreate called.....");

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "musicplayer-db");
            Database db = helper.getWritableDb();
            // We will use daoSession for creating, deleting, updating, and retrieving records
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

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sp_userinfo = EncryptedSharedPreferences.create(getApplicationContext(), SPUtils.USER_INFO, new MasterKey.Builder(getApplicationContext()).setKeyGenParameterSpec(new KeyGenParameterSpec.Builder(MasterKey.DEFAULT_MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()).build(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } else {
                sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }
}