package com.xstudio.xbrowser;

import android.app.Activity;
import android.app.Application;
import com.xstudio.xbrowser.util.Logger;
import com.google.android.gms.analytics.*;

public class ThisApplication extends Application {

    private static ThisApplication instance;
    private Activity mainActivity;
    private static GoogleAnalytics googleAnalytics;
    private Tracker defaultTracker;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        instance = this;
        googleAnalytics = GoogleAnalytics.getInstance(this);
        
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
    
    public Tracker getDefaultTracker() {
        if (defaultTracker == null) {
            synchronized (this) {
                if (defaultTracker == null) {
                    defaultTracker = googleAnalytics.newTracker("UA-131732467-1");
                }
            }
        }
        return defaultTracker;
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
