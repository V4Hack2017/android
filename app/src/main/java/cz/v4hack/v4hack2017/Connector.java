package cz.v4hack.v4hack2017;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.TimeZone;

public final class Connector {

    private static final String URL_NEARBY_INFO = "https://api.showway.xyz/nearby";

    private Connector() {
    }

    public static JSONObject getNearbyInfo(Location location, int limit) throws IOException, JSONException {
        return getNearbyInfo(location.getLatitude(), location.getLongitude(), limit);
    }

    public static JSONObject getNearbyInfo(double lat, double lng, int limit) throws IOException, JSONException {
        long time = System.currentTimeMillis();
        int offset = TimeZone.getDefault().getOffset(time);
        time += offset;

        String jsonResult = Jsoup.connect(URL_NEARBY_INFO)
                .data("lat", String.valueOf(lat),
                        "lng", String.valueOf(lng),
                        "timestamp", String.valueOf(time / 1000),
                        "limit", String.valueOf(limit))
                .ignoreContentType(true)
                .execute().body();
        return new JSONObject(jsonResult);
    }
}
