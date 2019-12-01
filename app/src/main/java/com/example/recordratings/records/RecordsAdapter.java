package com.example.recordratings.records;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordratings.MainActivity;
import com.example.recordratings.misc.MovePage;
import com.example.recordratings.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordsAdapter extends
    RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    Fragment mFragment;
    FragmentManager mManager;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView albumTextView;
        public TextView artistTextView;
        public ImageView photoImageView;
        public RatingBar ratingBar;

        public ViewHolder(View itemView){
            super(itemView);

            albumTextView = itemView.findViewById(R.id.album);
            artistTextView = itemView.findViewById(R.id.artist);
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
        final TextView textView = viewHolder.albumTextView;
        textView.setText(buf.getTitle());
        final TextView textView2 = viewHolder.artistTextView;
        textView2.setText(buf.getArtist());
        final RatingBar rating = viewHolder.ratingBar;
        rating.setRating((float) (buf.getRating()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MovePage m = new MovePage();
                Log.d("Word", "Up");
                RecordPageFragment.idTemp = buf.getId();
                RecordPageFragment.albumTemp = buf.getTitle();
                RecordPageFragment.artistTemp = buf.getArtist();
                RecordPageFragment.ratingTemp = buf.getRating();
                RecordPageFragment.photoTemp = buf.getPhoto();
                RecordPageFragment.genreTemp = buf.getGenre();
                RecordPageFragment.descTemp = buf.getDesc();
                m.moveActivity(viewHolder.itemView.getContext(), RecordsPage.class);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mRecords.size();
    }


}
