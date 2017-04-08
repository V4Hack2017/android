package cz.v4hack.v4hack2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LineActivity extends AppCompatActivity {

    public static final String KEY_LINE = "LINE";
    private static final String TAG = "LineActivity";
    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler)
    public RecyclerView recyclerView;
    @BindView(R.id.fab)
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        overridePendingTransition(R.anim.slide_open, R.anim.slide_hide);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(KEY_LINE)) {
            finish();
            return;
        }

        final LineData lineData = ((LineData) intent.getSerializableExtra(KEY_LINE));
        setTitle("Line " + lineData.getLineNumber() + " - " + lineData.getStation());
        recyclerView.setAdapter(new LineDataAdapter(lineData.getList()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final JSONObject locationInfo = Connector.getNearbyInfo(
                                    lineData.getLocationLat(), lineData.getLocationLng(), 100);
                            JSONObject line = locationInfo.getJSONObject("lines").getJSONObject(lineData.getLineNumber());
                            JSONArray inConnections = line.getJSONObject("in").getJSONArray("connections");
                            JSONArray outConnections = line.getJSONObject("out").getJSONArray("connections");
                            final ArrayList<LineData> list = new ArrayList<>();
                            for (int i = 0; i < (inConnections.length() > outConnections.length()
                                    ? inConnections.length() : outConnections.length()); i++) {
                                LineData data = new LineData();
                                data.setStation(locationInfo.getString("station"));
                                data.setLineNumber(lineData.getLineNumber());
                                data.setType(line.getString("type"));
                                data.setFirstDestination(line.getJSONObject("in").optString("destination"));
                                data.setSecondDestination(line.getJSONObject("out").optString("destination"));
                                data.setFirstTime(inConnections.optString(i));
                                data.setSecondTime(outConnections.optString(i));
                                list.add(data);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(new LineDataAdapter(list));
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } catch (IOException | JSONException e) {
                            Log.e(TAG, "Failed to load NearbyInfo", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO: show item that describes problem
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> lines = PreferenceManager.getFavoriteLines();
                if (lines.contains(lineData.getLineNumber())) {
                    lines.remove(lineData.getLineNumber());
                    Snackbar.make(view, "Line was removed from favorites", 2500).show();
                } else {
                    lines.add(lineData.getLineNumber());
                    Snackbar.make(view, "Line was added to favorites", 2500).show();
                }
                PreferenceManager.setFavoriteLines(lines);
                refreshFab(lineData);
            }
        });
        refreshFab(lineData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_show, R.anim.slide_close);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_show, R.anim.slide_close);
    }

    private void refreshFab(LineData lineData) {
        if (PreferenceManager.getFavoriteLines().contains(lineData.getLineNumber())) {
            fab.setImageResource(R.drawable.ic_star_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_star_border_white_24dp);
        }
    }
}
