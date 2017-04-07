package cz.v4hack.v4hack2017;

import android.os.Bundle;
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

    @BindView(R.id.recycler)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<JSONObject> arrayList = new ArrayList<>();

        try {
            JSONObject locationInfo = Connector.getLocationInfo(null);
            setTitle(locationInfo.getString("stop"));

            JSONObject lines = locationInfo.getJSONObject("lines");
            for (Iterator<String> iterator = lines.keys(); iterator.hasNext(); ) {
                String lineNumber = iterator.next();
                JSONObject line = lines.getJSONObject(lineNumber);
                line.put("line", lineNumber);
                arrayList.add(line);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DataAdapter(arrayList));
    }
}
