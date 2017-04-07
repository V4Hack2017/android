package cz.v4hack.v4hack2017;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public final class Connector {

    private static final String URL_NEARBY_INFO = "https://api.showway.xyz/nearby";

    private Connector() {
    }

    public static JSONObject getNearbyInfo(Location location) throws IOException, JSONException {
        String jsonResult = Jsoup.connect(URL_NEARBY_INFO)
                .data("lat", Double.toString(location.getLatitude()), "lng", Double.toString(location.getLongitude()))
                .ignoreContentType(true)
                .execute().body();
        return new JSONObject(jsonResult);
        /*return new JSONObject()
                .put("stop", "TestStation")
                .put("lines", new JSONObject()
                        .put("8", new JSONObject()
                                .put("type", "tram")
                                .put("in", new JSONObject()
                                        .put("dest", "FirstEndStation")
                                        .put("connections", new JSONArray(Collections
                                                .singletonList(System.currentTimeMillis())))
                                )
                                .put("out", new JSONObject()
                                        .put("dest", "SecondEndStation")
                                        .put("connections", new JSONArray(Collections
                                                .singletonList(System.currentTimeMillis() + (600000 * new Random().nextInt(10)))))
                                )
                        )
                        .put("88", new JSONObject()
                                .put("type", "bus")
                                .put("in", new JSONObject()
                                        .put("dest", "FirstEndStation")
                                        .put("connections", new JSONArray(Collections
                                                .singletonList(System.currentTimeMillis())))
                                )
                                .put("out", new JSONObject()
                                        .put("dest", "SecondEndStation")
                                        .put("connections", new JSONArray(Collections
                                                .singletonList(System.currentTimeMillis() + 600000)))
                                )
                        )
                        .put("25", new JSONObject()
                                .put("type", "trolley")
                                .put("in", new JSONObject()
                                        .put("dest", "FirstEndStation")
                                        .put("connections", new JSONArray(Collections
                                                .singletonList(System.currentTimeMillis())))
                                )
                                .put("out", new JSONObject()
                                        .put("dest", "SecondEndStation")
                                        .put("connections", new JSONArray(Collections
                                                .singletonList(System.currentTimeMillis() + 600000)))
                                )
                        )
                );*/
    }
}
