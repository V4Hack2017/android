package cz.v4hack.v4hack2017;

import android.app.Application;

public class AppInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationService.reload(this);
    }
}
