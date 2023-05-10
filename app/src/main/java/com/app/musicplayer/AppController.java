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

import com.app.musicplayer.utils.SPUtils;

public class AppController extends MultiDexApplication {
    static final String TAG = AppController.class.getSimpleName();
    static SharedPreferences sp_userinfo;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MultiDex.install(this);
            Log.e(TAG, "AppController onCreate called.....");

            initSharedPreferences();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sp_userinfo = EncryptedSharedPreferences.create(
                        getApplicationContext(),
                        SPUtils.USER_INFO,
                        new MasterKey.Builder(getApplicationContext())
                                .setKeyGenParameterSpec(new KeyGenParameterSpec.Builder(MasterKey.DEFAULT_MASTER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                        .setKeySize(256)
                                        .build()).build(),
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
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