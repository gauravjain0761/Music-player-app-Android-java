package com.app.musicplayer;

import android.database.CursorWindow;
import android.os.StrictMode;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.app.musicplayer.entity.DaoMaster;
import com.app.musicplayer.entity.DaoSession;
import com.app.mvpdemo.businessframe.BusinessLogicManager;
import com.app.mvpdemo.businessframe.config.BusinessFrameConfig;

import org.greenrobot.greendao.database.Database;

import java.lang.reflect.Field;

public class AppController extends MultiDexApplication {
    static final String TAG = AppController.class.getSimpleName();

    private static AppController mApplication;
    private static final int THREAD_COUNT = 9;
    public static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MultiDex.install(this);
            mApplication = this;

            initDataBase();
            initBusiness();
            initStrictMode();
            initCursorWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCursorWindow() {
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 500 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDataBase() {
        try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "musicplayer-db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化StrictMode 防止系统分享文件报错
     */
    private void initStrictMode() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化框架层参数
     */
    private void initBusiness() {
        BusinessFrameConfig businessFrameConfig = new BusinessFrameConfig.Builder().context(this).appName(getResources().getString(R.string.app_name)).threadCount(THREAD_COUNT).build(this);
        BusinessLogicManager.getInstance().initConstants(businessFrameConfig);
    }

    public static AppController getInstance() {
        return mApplication;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}