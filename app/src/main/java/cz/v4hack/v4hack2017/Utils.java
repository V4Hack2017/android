package cz.v4hack.v4hack2017;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Utils {

    private Utils() {
    }

    public static boolean hasAnyLocationPermission(Context context) {
        return Utils.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                || Utils.hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean hasAllLocationPermissions(Context context) {
        return Utils.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                && Utils.hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static Criteria getLocationRequestCriteria() {
        Criteria criteria = new Criteria();
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setSpeedAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(false);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);
        return criteria;
    }

    public static void sortLinesByFavorite(List<LineData> arrayList) {
        final ArrayList<String> favoriteLines = PreferenceManager.getFavoriteLines();
        Collections.sort(arrayList, new Comparator<LineData>() {
            @Override
            public int compare(LineData o1, LineData o2) {
                if (favoriteLines.contains(o1.getLineNumber())) {
                    if (!favoriteLines.contains(o2.getLineNumber())) {
                        return -1;
                    }
                    return 0;
                } else if (favoriteLines.contains(o2.getLineNumber())) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public static void sortLineNumbersByFavorite(List<String> arrayList) {
        final ArrayList<String> favoriteLines = PreferenceManager.getFavoriteLines();
        Collections.sort(arrayList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (favoriteLines.contains(o1)) {
                    if (!favoriteLines.contains(o2)) {
                        return -1;
                    }
                    return 0;
                }
                if (favoriteLines.contains(o2)) {
                    return 1;
                }
                return 0;
            }
        });
    }
}
