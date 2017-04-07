package cz.v4hack.v4hack2017;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Random;

public final class Connector {

    private Connector() {
    }

    public static JSONObject getLocationInfo(Location location) throws IOException, JSONException {
        return new JSONObject()
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
                );
    }
}
