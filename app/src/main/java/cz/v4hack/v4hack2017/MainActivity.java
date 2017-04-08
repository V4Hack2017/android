package cz.v4hack.v4hack2017;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION = "EXTRA_LOCATION";
    public static final String EXTRA_NEARBY_INFO = "EXTRA_NEARBY_INFO";
    private static final String LOG_TAG = "MainActivity";
    private static final int LOCATION_PERMISSIONS_REQUEST_ID = 100;
    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler)
    public RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            fetchLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkPermissions();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_LOCATION) && intent.hasExtra(EXTRA_NEARBY_INFO)) {
            try {
                applyNearbyInfoOnRecyclerView((Location) intent.getParcelableExtra(EXTRA_LOCATION),
                        new JSONObject(intent.getStringExtra(EXTRA_NEARBY_INFO)));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Failed to parse NearbyInfo", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(R.string.app_name);
                        // TODO: show item that describes problem
                        recyclerView.setAdapter(new LineDataAdapter(new ArrayList<LineData>()));
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                checkPermissions();
            }
        } else {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        if (!Utils.hasAllLocationPermissions(this)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSIONS_REQUEST_ID);
        } else {
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_ID) {
            NotificationService.reload(this);
            if (Utils.hasAnyLocationPermission(this)) {
                requestLocation();
            } else {
                setTitle(R.string.app_name);
                // TODO: show item that describes problem
                recyclerView.setAdapter(new LineDataAdapter(new ArrayList<LineData>()));
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void requestLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //noinspection MissingPermission // method is called only if permission is provided
        locationManager.requestSingleUpdate(Utils.getLocationRequestCriteria(), locationListener, null);
    }

    private void fetchLocation(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject nearbyInfo = Connector.getNearbyInfo(location, 100);
                    applyNearbyInfoOnRecyclerView(location, nearbyInfo);
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, "Failed to load NearbyInfo", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTitle(R.string.app_name);
                            // TODO: show item that describes problem
                            recyclerView.setAdapter(new LineDataAdapter(new ArrayList<LineData>()));
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }

    private void applyNearbyInfoOnRecyclerView(Location location, JSONObject nearbyInfo) throws JSONException {
        JSONObject lines = nearbyInfo.getJSONObject("lines");
        final ArrayList<LineData> arrayList = new ArrayList<>();
        for (Iterator<String> iterator = lines.keys(); iterator.hasNext(); ) {
            String lineNumber = iterator.next();
            JSONObject line = lines.getJSONObject(lineNumber);
            LineData lineData = new LineData();
            lineData.setLocationLat(location.getLatitude());
            lineData.setLocationLng(location.getLongitude());
            lineData.setStation(nearbyInfo.getString("station"));
            lineData.setLineNumber(lineNumber);
            lineData.setType(line.getString("type"));
            lineData.setFirstDestination(line.getJSONObject("in").optString("destination"));
            lineData.setSecondDestination(line.getJSONObject("out").optString("destination"));
            JSONArray inConnections = line.getJSONObject("in").getJSONArray("connections");
            JSONArray outConnections = line.getJSONObject("out").getJSONArray("connections");
            lineData.setFirstTime(inConnections.optString(0));
            lineData.setSecondTime(line.getJSONObject("out")
                    .getJSONArray("connections").optString(0));
            ArrayList<LineData> list = new ArrayList<>();
            for (int i = 0; i < (inConnections.length() > outConnections.length()
                    ? inConnections.length() : outConnections.length()); i++) {
                LineData data = new LineData();
                data.setStation(nearbyInfo.getString("station"));
                data.setLineNumber(lineNumber);
                data.setType(line.getString("type"));
                data.setFirstDestination(line.getJSONObject("in").optString("destination"));
                data.setSecondDestination(line.getJSONObject("out").optString("destination"));
                data.setFirstTime(inConnections.optString(i));
                data.setSecondTime(outConnections.optString(i));
                list.add(data);
            }
            lineData.setList(list);
            arrayList.add(lineData);
        }

        Utils.sortLinesByFavorite(arrayList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(arrayList.get(0).getStation());
                recyclerView.setAdapter(new LineDataAdapter(arrayList));
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
