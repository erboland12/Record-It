package com.example.recordratings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordsAdapter extends
    RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public TextView artistTextView;
        public TextView ratingTextView;

        public ViewHolder(View itemView){
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title);
            artistTextView = itemView.findViewById(R.id.artist);
            ratingTextView = itemView.findViewById(R.id.rating);
        }
    }

    private List<Records> mRecords;

    public RecordsAdapter(List<Records> records) { mRecords = records;}

    @NonNull
    @Override
    public RecordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.record_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecordsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Records buf = mRecords.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.titleTextView;
        textView.setText(buf.getTitle());
        TextView textView2 = viewHolder.artistTextView;
        textView2.setText(buf.getAritst());
        TextView textView3 = viewHolder.ratingTextView;
        textView3.setText(Double.toString(buf.getRating()) + "/5");
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mRecords.size();
    }

}
