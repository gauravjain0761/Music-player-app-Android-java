package com.app.musicplayer.db;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ExecuteTask {
    private static final Executor THREAD_POOL_EXECUTOR =
            new ThreadPoolExecutor(5, 128, 1,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void execute() {
        try {
            THREAD_POOL_EXECUTOR.execute(() -> {
                try {
                    //Before Background work
                    handler.post(() -> {
                        try {
                            onPreExecute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    //Background work here
                    doInBackground();
                    handler.post(() -> {
                        try {
                            //UI Thread work here
                            onPostExecute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void doInBackground();

    public abstract void onPostExecute();

    public abstract void onPreExecute();
}
