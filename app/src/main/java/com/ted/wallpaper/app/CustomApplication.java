package com.ted.wallpaper.app;

import android.app.Application;
import android.content.Context;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.ted.wallpaper.app.other.Constants;

public class CustomApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AVOSCloud.initialize(this, Constants.LEAN_CLOUD_ID, Constants.LEAN_CLOUD_KEY);
        AVAnalytics.enableCrashReport(this, true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
