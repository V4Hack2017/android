package cz.v4hack.v4hack2017;

import android.Manifest;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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
            fetchLocation();
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

        checkPermissions();
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
            }
        }
    }

    private void requestLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //noinspection MissingPermission // method is called only if permission is provided
        locationManager.requestSingleUpdate(Utils.getLocationRequestCriteria(), locationListener, null);
    }

    private void fetchLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<JSONObject> arrayList = new ArrayList<>();

                try {
                    final JSONObject locationInfo = Connector.getNearbyInfo(null);
                    final String title = locationInfo.getString("stop");
                    JSONObject lines = locationInfo.getJSONObject("lines");
                    for (Iterator<String> iterator = lines.keys(); iterator.hasNext(); ) {
                        String lineNumber = iterator.next();
                        JSONObject line = lines.getJSONObject(lineNumber);
                        line.put("line", lineNumber);
                        arrayList.add(line);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTitle(title);
                            recyclerView.setAdapter(new DataAdapter(arrayList));
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, "Failed to load NearbyInfo", e);
                }
            }
        }).start();
    }
}
