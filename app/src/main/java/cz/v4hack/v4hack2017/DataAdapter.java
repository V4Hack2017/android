package cz.v4hack.v4hack2017;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.Holder> {

    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private ArrayList<JSONObject> arrayList;

    public DataAdapter(ArrayList<JSONObject> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_line, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        try {
            JSONObject object = arrayList.get(position);
            holder.number.setText(object.getString("line"));
            holder.name1.setText(object.getJSONObject("in").getString("dest"));
            holder.name2.setText(object.getJSONObject("out").getString("dest"));
            long time1 = object.getJSONObject("in").getJSONArray("connections").getLong(0);
            long time2 = object.getJSONObject("out").getJSONArray("connections").getLong(0);
            holder.time1.setText(format.format(new Date(time1)));
            holder.time2.setText(format.format(new Date(time2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView number;
        public TextView name1;
        public TextView name2;
        public TextView time1;
        public TextView time2;

        public Holder(View itemView) {
            super(itemView);
            number = ((TextView) itemView.findViewById(R.id.number));
            name1 = ((TextView) itemView.findViewById(R.id.name1));
            name2 = ((TextView) itemView.findViewById(R.id.name2));
            time1 = ((TextView) itemView.findViewById(R.id.time1));
            time2 = ((TextView) itemView.findViewById(R.id.time2));
        }
    }
}
