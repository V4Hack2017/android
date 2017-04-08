package cz.v4hack.v4hack2017;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NotificationService extends IntentService {
    private static final String LOG_TAG = "NotificationService";
    private static final int NOTIFICATION_ID = 0;
    private static final long UPDATE_INTERVAL = 1000 * 60;

    private static final String PREFERENCES_NAME = "NotificationServicePreferences";
    private static final String PREF_ENABLED_KEY = "enabled";
    private static final String PREF_LAST_REFRESH_KEY = "lastRefresh";

    private static final String ACTION_LOCATION_RECEIVED =
            "cz.v4hack.v4hack2017.action.LOCATION_RECEIVED";
    private static final String ACTION_UPDATE =
            "cz.v4hack.v4hack2017.action.UPDATE";
    private static final String ACTION_ENABLE =
            "cz.v4hack.v4hack2017.action.ENABLE";
    private static final String ACTION_DISABLE =
            "cz.v4hack.v4hack2017.action.DISABLE";

    public NotificationService() {
        super(LOG_TAG);
    }

    public static void reload(Context context) {
        updateRefreshing(context);
    }

    private static PendingIntent getPendingIntentActionLocationReceived(Context context) {
        return PendingIntent.getService(context, 1,
                new Intent(context, NotificationService.class).setAction(ACTION_LOCATION_RECEIVED),
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent getPendingIntentActionUpdate(Context context) {
        return PendingIntent.getService(context, 0,
                getIntentActionUpdate(context),
                PendingIntent.FLAG_CANCEL_CURRENT);
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
        PendingIntent updateIntent = getPendingIntentActionUpdate(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(updateIntent);
        if (canUpdate(context)) {
            long firstTrigger = getLastRefresh(context) + UPDATE_INTERVAL;
            if (firstTrigger < System.currentTimeMillis())
                firstTrigger = System.currentTimeMillis();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTrigger,
                    UPDATE_INTERVAL, updateIntent);
        } else {
            getPendingIntentActionLocationReceived(context);
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
        }
    }

    private static boolean canUpdate(Context context) {
        return isEnabled(context) && Utils.isOnline(context) && Utils.hasAnyLocationPermission(context);
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    private static long getLastRefresh(Context context) {
        return getPreferences(context).getLong(PREF_LAST_REFRESH_KEY, 0);
    }

    private static void updateLastRefresh(Context context) {
        getPreferences(context).edit().putLong(PREF_LAST_REFRESH_KEY,
                System.currentTimeMillis()).apply();
    }

    public static boolean isEnabled(Context context) {
        return getPreferences(context).getBoolean(PREF_ENABLED_KEY, true);
    }

    private static void setEnabled(Context context, boolean enable) {
        getPreferences(context).edit().putBoolean(PREF_ENABLED_KEY, enable).apply();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        final String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                reload(this);
                break;
            case ACTION_LOCATION_RECEIVED:
                Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
                handleActionLocationReceived(location);
                break;
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

    private void handleActionLocationReceived(Location location) {
        try {
            JSONObject locationInfo = Connector.getNearbyInfo(
                    location.getLatitude(), location.getLongitude(), 1);

            //RemoteViews smallContentView = new RemoteViews(getPackageName(), R.layout.holder_line);
            // TODO: 4/8/17 create

            //RemoteViews bigContentView = new RemoteViews(getPackageName(), R.layout.holder_line);
            // TODO: 4/8/17 create

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Nearest connections")
                    .setContentText(locationInfo.toString())
                    //.setContent(smallContentView)
                    //.setCustomBigContentView(bigContentView)
                    .build();
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);

            updateLastRefresh(this);
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Failed to refresh notification", e);
        }
    }

    private void handleActionUpdate() {
        if (!canUpdate(this)) {
            updateRefreshing(this);
            Log.w(LOG_TAG, "Started NotificationService update");
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestSingleUpdate(Utils.getLocationRequestCriteria(),
                getPendingIntentActionLocationReceived(this));
    }

    private void handleActionEnable() {
        setEnabled(this, true);
        updateRefreshing(this);
    }

    private void handleActionDisable() {
        setEnabled(this, false);
        updateRefreshing(this);
    }

    private void handleActionConnectivityChange() {
        updateRefreshing(this);
    }
}
