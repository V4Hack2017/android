package cz.v4hack.v4hack2017;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NotificationService extends IntentService {
    private static final String ACTION_UPDATE =
            "cz.v4hack.v4hack2017.action.UPDATE";
    private static final String ACTION_ENABLE =
            "cz.v4hack.v4hack2017.action.ENABLE";
    private static final String ACTION_DISABLE =
            "cz.v4hack.v4hack2017.action.DISABLE";

    public NotificationService() {
        super("NotificationService");
    }

    public static void initialize(Context context) {
        updateRefreshing(context);
    }

    public static Intent getIntentActionUpdate(Context context) {
        return new Intent(context, NotificationService.class)
                .setAction(ACTION_UPDATE);
    }

    public static void startActionUpdate(Context context) {
        context.startService(getIntentActionUpdate(context));
    }

    public static void startActionEnable(Context context) {
        context.startService(new Intent(context, NotificationService.class)
                .setAction(ACTION_ENABLE));
    }

    public static void startActionDisable(Context context) {
        context.startService(new Intent(context, NotificationService.class)
                .setAction(ACTION_DISABLE));
    }

    private static void updateRefreshing(Context context) {
        PendingIntent updateIntent = PendingIntent.getService(context, 0,
                getIntentActionUpdate(context), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(updateIntent);
        if (isOnline(context)) {
            long TRIGGER_OFFSET = 1000 * 10;
            long UPDATE_INTERVAL = 1000 * 60;

            long firstTrigger = System.currentTimeMillis() + TRIGGER_OFFSET;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTrigger,
                    UPDATE_INTERVAL, updateIntent);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        final String action = intent.getAction();
        switch (action) {
            case ACTION_UPDATE:
                handleActionUpdate();
                break;
            case ACTION_ENABLE:
                handleActionEnable();
                break;
            case ACTION_DISABLE:
                handleActionDisable();
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                handleActionConnectivityChange();
                break;
        }
    }

    private void handleActionUpdate() {
        // TODO: 4/7/17 implement
    }

    private void handleActionEnable() {
        // TODO: 4/7/17 implement
    }

    private void handleActionDisable() {
        // TODO: 4/7/17 implement
    }

    private void handleActionConnectivityChange() {
        updateRefreshing(this);
    }
}
