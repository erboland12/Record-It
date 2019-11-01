package com.example.recordratings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Rating;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordsAdapter extends
    RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public ImageView photoImageView;
        public RatingBar ratingBar;
        public TextView descTextView;
        public TextView genreTextView;

        public ViewHolder(View itemView){
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title);
            ratingBar = itemView.findViewById(R.id.rating);
            photoImageView = itemView.findViewById(R.id.photo);
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
        final ImageView imageView = viewHolder.photoImageView;
        Bitmap image = buf.getPhoto();
        imageView.setImageBitmap(image);
        final TextView textView = viewHolder.titleTextView;
        textView.setText(buf.getTitle() + " - " + buf.getArtist());
        final RatingBar rating = viewHolder.ratingBar;
        rating.setRating((float) (buf.getRating()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MovePage m = new MovePage();
                Log.d("Word", "Up");
                RecordsPage.idTemp = buf.getId();
                RecordsPage.albumTemp = buf.getTitle();
                RecordsPage.artistTemp = buf.getArtist();
                RecordsPage.ratingTemp = buf.getRating();
                RecordsPage.photoTemp = buf.getPhoto();
                RecordsPage.genreTemp = buf.getGenre();
                RecordsPage.descTemp = buf.getDesc();
                m.moveActivity(viewHolder.itemView.getContext(), RecordsPage.class);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
