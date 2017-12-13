package br.ufpe.cin.if710.podcast.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Pedro Vieira on 13-Dec-17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);
    }

}
