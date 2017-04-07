package cz.v4hack.v4hack2017;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.Holder> {

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
            final JSONObject object = arrayList.get(position);
            final String type = object.getString("type");

            holder.number.setText(object.getString("line"));
            holder.name1.setText(object.getJSONObject("in").getString("destination"));
            holder.name2.setText(object.getJSONObject("out").getString("destination"));

            final String time1 = object.getJSONObject("in").getJSONArray("connections").getString(0);
            final String time2 = object.getJSONObject("out").getJSONArray("connections").getString(0);
            final int drawable = "tram".equals(type) ? R.drawable.ic_tram_black_24dp
                            : R.drawable.ic_directions_bus_black_24dp;
            holder.time1.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            holder.time1.setText(time1);
            holder.time2.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            holder.time2.setText(time2);
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

        public Holder(final View itemView) {
            super(itemView);
            number = ((TextView) itemView.findViewById(R.id.number));
            name1 = ((TextView) itemView.findViewById(R.id.name1));
            name2 = ((TextView) itemView.findViewById(R.id.name2));
            time1 = ((TextView) itemView.findViewById(R.id.time1));
            time2 = ((TextView) itemView.findViewById(R.id.time2));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(
                            new Intent(itemView.getContext(), LineActivity.class));
                }
            });
        }
    }
}
