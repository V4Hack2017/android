package cz.v4hack.v4hack2017;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        try {
            JSONObject locationInfo = Connector.getLocationInfo(null);
            String name = locationInfo.getString("stop");
            setTitle(name);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("test");
        arrayList.add("test2");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DataAdapter(arrayList));
    }
}
