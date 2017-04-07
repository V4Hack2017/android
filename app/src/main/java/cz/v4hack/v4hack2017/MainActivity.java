package cz.v4hack.v4hack2017;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<JSONObject> arrayList = new ArrayList<>();

                    try {
                        final JSONObject locationInfo = Connector.getLocationInfo(null);
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
                            }
                        });
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<JSONObject> arrayList = new ArrayList<>();

                    try {
                        final JSONObject locationInfo = Connector.getLocationInfo(null);
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
                            }
                        });
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
    @BindView(R.id.recycler)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                return;
            }
        }
    }

    private void checkPermissions() {
        LocationManager locationManager = ((LocationManager) getSystemService(LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }
}
