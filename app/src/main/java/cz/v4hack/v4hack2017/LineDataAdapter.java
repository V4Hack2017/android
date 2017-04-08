package cz.v4hack.v4hack2017;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LineDataAdapter extends RecyclerView.Adapter<LineDataAdapter.Holder> {

    private ArrayList<LineData> arrayList;

    public LineDataAdapter(ArrayList<LineData> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.holder_line, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final LineData lineData = arrayList.get(position);
        final int drawable = "tram".equals(lineData.getType()) ? R.drawable.ic_tram_black_24dp
                : R.drawable.ic_directions_bus_black_24dp;

        holder.number.setText(lineData.getLineNumber());
        holder.name1.setText(lineData.getFirstDestination());
        holder.name2.setText(lineData.getSecondDestination());
        holder.time1.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        holder.time1.setText(lineData.getFirstTime());
        holder.time2.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        holder.time2.setText(lineData.getSecondTime());

        if (lineData.getList() != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.itemView.getContext().startActivity(
                            new Intent(holder.itemView.getContext(), LineActivity.class)
                                    .putExtra(LineActivity.KEY_LINE, lineData));
                }
            });
            holder.rightArrow.setVisibility(View.VISIBLE);
            holder.number.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setOnClickListener(null);
            holder.rightArrow.setVisibility(View.GONE);
            holder.number.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public View itemView;
        public TextView number;
        public TextView name1;
        public TextView name2;
        public TextView time1;
        public TextView time2;
        private ImageView rightArrow;

        public Holder(final View itemView) {
            super(itemView);
            this.itemView = itemView;
            number = ((TextView) itemView.findViewById(R.id.number));
            name1 = ((TextView) itemView.findViewById(R.id.name1));
            name2 = ((TextView) itemView.findViewById(R.id.name2));
            time1 = ((TextView) itemView.findViewById(R.id.time1));
            time2 = ((TextView) itemView.findViewById(R.id.time2));
            rightArrow = ((ImageView) itemView.findViewById(R.id.rightArrow));
        }
    }
}
