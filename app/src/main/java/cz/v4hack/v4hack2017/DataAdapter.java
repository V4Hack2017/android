package cz.v4hack.v4hack2017;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.Holder> {

    private ArrayList<String> arrayList;

    public DataAdapter(ArrayList<String> arrayList) {
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
        holder.textView.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView textView;

        public Holder(View itemView) {
            super(itemView);
            textView = ((TextView) itemView.findViewById(R.id.name));
        }
    }
}
