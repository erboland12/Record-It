package com.example.recordratings.records;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordratings.MainActivity;
import com.example.recordratings.misc.Censor;
import com.example.recordratings.misc.MovePage;
import com.example.recordratings.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RecordsAdapter extends
    RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    Fragment mFragment;
    FragmentManager mManager;

    private SharedPreferences censorSP;

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

        public boolean returnCensor(){
            censorSP = itemView.getContext().getSharedPreferences("censorPrefs", MODE_PRIVATE);
            return censorSP.getBoolean("censorOff", false);
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
        Uri uri = Uri.parse(buf.getmPhotoString());
        Picasso.get().load(uri).into(imageView);

        final TextView textView = viewHolder.albumTextView;
        if(buf.getTitle().length() > 21){
            String shortened = buf.getTitle().substring(0, 18);
            shortened += "...";
            textView.setText(shortened);
        }else{
            textView.setText(buf.getTitle());
        }
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
                RecordPageFragment.photoStringTemp = buf.getmPhotoString();
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
