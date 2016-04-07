package com.example.simpleui.simpleui;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;

/**
 * Created by wudeyan on 3/21/16.
 */
public class SimpleUIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("76ee57f8e5f8bd628cc9586e93d428d5")
                .clientKey(null)
                .server("http://parseserver-3imy2-env.us-west-2.elasticbeanstalk.com/parse/")
                .build()
        );
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
