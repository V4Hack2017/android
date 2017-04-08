package cz.v4hack.v4hack2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LineActivity extends AppCompatActivity {

    public static final String KEY_LINE = "LINE";

    //    @BindView(R.id.swipeRefreshLayout)
//    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        overridePendingTransition(R.anim.slide_open, R.anim.slide_hide);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 8.4.17 implement
            }
        });

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
        // swipeRefreshLayout.setVisibility(View.VISIBLE);
        // swipeRefreshLayout.setRefreshing(false);
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
}
