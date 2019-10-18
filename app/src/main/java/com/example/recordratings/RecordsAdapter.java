package com.example.recordratings;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecordsAdapter extends
    RecyclerView.Adapter<RecordsAdapter.ViewHolder> implements Filterable {

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
    public List<Records> mRecordsCopy = new ArrayList<>();

    public RecordsAdapter(ArrayList<Records> records) {
        mRecords = records;
        mRecordsCopy.addAll(mRecords);
    }

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
    public void onBindViewHolder(final RecordsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Records buf = mRecords.get(position);

        // Set item views based on your views and data model
        final TextView textView = viewHolder.titleTextView;
        textView.setText(buf.getTitle());
        TextView textView2 = viewHolder.artistTextView;
        textView2.setText(buf.getAritst());
        TextView textView3 = viewHolder.ratingTextView;
        textView3.setText(Double.toString(buf.getRating()) + "/5");
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MovePage m = new MovePage();
                Log.d("Word", "Up");
                RecordsPage.albumTemp = buf.getTitle();
                RecordsPage.artistTemp = buf.getAritst();
                RecordsPage.ratingTemp = buf.getRating();
                m.moveActivity(viewHolder.itemView.getContext(), RecordsPage.class);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public void filter(String text) {
        mRecords.clear();
        if(text.isEmpty()){
            mRecords.addAll(mRecordsCopy);
        } else{
            text = text.toLowerCase();
            for(Records record: mRecordsCopy){
                if(record.getAritst().toLowerCase().contains(text) || record.getTitle().toLowerCase().contains(text)){
                    mRecords.add(record);
                }
            }
        }
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Records> results = new ArrayList<>();
                if (mRecords == null)
                    mRecords = new ArrayList<>(mRecordsCopy);
                if (constraint != null && constraint.length() > 0) {
                    if (mRecords != null && mRecords.size() > 0) {
                        for (final Records cd : mRecords) {
                            if (cd.getAritst().toLowerCase()
                                    .contains(constraint.toString().toLowerCase()))
                                results.add(cd);
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();//newly Aded by ZA
                } else {
                    oReturn.values = mRecords;
                    oReturn.count = mRecords.size();//newly added by ZA
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(final CharSequence constraint,
                                          FilterResults results) {
                mRecordsCopy = new ArrayList<>((ArrayList<Records>) results.values);
                // FIXME: 8/16/2017 implement Comparable with sort below
                ///Collections.sort(itemList);
                notifyDataSetChanged();
            }
        };
    }

    public void filterList(ArrayList<Records> filteredList){
        mRecords = filteredList;
        notifyDataSetChanged();
    }



}
