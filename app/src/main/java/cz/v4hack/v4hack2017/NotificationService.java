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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            JSONObject nearbyInfo = Connector.getNearbyInfo(location, 1);
            JSONObject lines = nearbyInfo.getJSONObject("lines");

            List<String> linesNumbers = new ArrayList<>();
            for (Iterator<String> keys = lines.keys(); keys.hasNext(); ) {
                linesNumbers.add(keys.next());
            }

            Utils.sortLineNumbersByFavorite(linesNumbers);

            List<RemoteViews> linesViews = new ArrayList<>();
            for (String lineNumber : linesNumbers) {
                JSONObject lineInfo = lines.getJSONObject(lineNumber);
                int drawable = R.drawable.ic_tram_black_24dp;
                if ("bus".equals(lineInfo.getString("type"))) {
                    drawable = R.drawable.ic_directions_bus_black_24dp;
                } else if ("trolley".equals(lineInfo.getString("type"))) {
                    drawable = R.drawable.trolley;
                }

                RemoteViews lineContentView = new RemoteViews(getPackageName(),
                        R.layout.notification_content_line);
                lineContentView.setTextViewText(R.id.line_number, lineNumber);
                lineContentView.setTextViewText(R.id.line_in_name, lineInfo.getJSONObject("in").getString("destination"));
                lineContentView.setTextViewText(R.id.line_out_name, lineInfo.getJSONObject("out").getString("destination"));
                lineContentView.setTextViewText(R.id.line_in_time, lineInfo.getJSONObject("in").getJSONArray("connections").getString(0));
                lineContentView.setTextViewText(R.id.line_out_time, lineInfo.getJSONObject("out").getJSONArray("connections").getString(0));
                lineContentView.setTextViewCompoundDrawables(R.id.line_in_time, drawable, 0, 0, 0);
                lineContentView.setTextViewCompoundDrawables(R.id.line_out_time, drawable, 0, 0, 0);
                linesViews.add(lineContentView);
            }

            if (linesViews.isEmpty()) {
                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            } else {
                RemoteViews bigContentView = new RemoteViews(getPackageName(), R.layout.notification_content_big);
                bigContentView.removeAllViews(R.id.container);

                for (int i = 0, len = Math.min(linesViews.size(), 4); i < len; i++) {
                    bigContentView.addView(R.id.container, linesViews.get(i));
                }

                Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(nearbyInfo.getString("station"))
                        .setContentText("Tap or swipe down for more info")
                        .setContent(bigContentView)
                        .setCustomBigContentView(bigContentView)
                        .setOngoing(true)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class)
                                        .putExtra(MainActivity.EXTRA_LOCATION, location)
                                        .putExtra(MainActivity.EXTRA_NEARBY_INFO, nearbyInfo.toString()),
                                PendingIntent.FLAG_CANCEL_CURRENT))
                        .build();
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
            }

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
