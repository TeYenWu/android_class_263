package com.example.simpleui.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by wudeyan on 3/21/16.
 */
public class SimpleUIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
