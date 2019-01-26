package com.xstudio.xbrowser;

import android.app.Activity;
import android.app.Application;
import com.xstudio.xbrowser.util.Logger;

public class ThisApplication extends Application {

    private static ThisApplication instance;
    private Activity mainActivity;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        instance = this;
        
        Logger.setLogLevel(Logger.DEBUG);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Logger.crash(e);
                if (ThisApplication.this.mainActivity != null) {
                     ThisApplication.this.mainActivity.finish();
                }
                System.exit(1);
            }
        });
    }
    
    public void setMainActivity(Activity activity) {
        mainActivity = activity;
    }

    public Activity getMainActivity() {
        return mainActivity;
    }

    public static ThisApplication getInstance() {
        return instance;
    }

}
