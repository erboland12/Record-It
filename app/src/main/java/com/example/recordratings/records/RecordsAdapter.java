package com.example.recordratings.records;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import com.example.recordratings.credentials.ProfileActivity;
import com.example.recordratings.misc.Censor;
import com.example.recordratings.misc.MovePage;
import com.example.recordratings.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

//Adapter that handles record items in recycler view
public class RecordsAdapter extends
    RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private SharedPreferences censorSP, shared;
    private boolean isCensored;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView albumTextView, artistTextView, userTextView;
        private ImageView photoImageView;
        private RatingBar ratingBar;
        public View view;

        private ViewHolder(View itemView){
            super(itemView);

            //Links front end variables to item view
            albumTextView = itemView.findViewById(R.id.album);
            artistTextView = itemView.findViewById(R.id.artist);
            ratingBar = itemView.findViewById(R.id.rating);
            photoImageView = itemView.findViewById(R.id.photo);
            view = itemView.findViewById(R.id.record_item_view);
            userTextView = itemView.findViewById(R.id.nameAndDate);

            //Call for censorship shared preference
            censorSP = itemView.getContext().getSharedPreferences("censorPrefs", MODE_PRIVATE);

            //Sets up firebase and auth
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
        }

        //Determines if censor preference is disabled
        private boolean returnCensor(){
            isCensored = censorSP.getBoolean("censorOff", false);
            return isCensored;
        }

        //Determines if night mode preference is enabled
        public boolean returnDark(){
            shared = itemView.getContext().getSharedPreferences("DarkMode", MODE_PRIVATE);
            return shared.getBoolean("darkMode", false);
        }

        //Converts unix time to date stamp
        private String unixToDate(long unix){
            Date date = new java.util.Date(unix*1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd-yy");
            return sdf.format(date);
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
        Picasso.get().load(uri).fit().centerCrop().into(imageView);

        final TextView textView = viewHolder.albumTextView;
        if(buf.getTitle().length() > 21){
            String shortened = buf.getTitle().substring(0, 18);
            shortened += "...";
            textView.setText(shortened);
        }else{
            textView.setText(buf.getTitle());
        }

        final TextView textView2 = viewHolder.artistTextView;
        if(buf.getArtist().length() > 28){
            String shortened = buf.getArtist().substring(0, 25);
            shortened += "...";
            textView2.setText(shortened);
        }else{
            textView2.setText(buf.getArtist());
        }

        final RatingBar rating = viewHolder.ratingBar;
        rating.setRating((float) (buf.getRating()));

        final TextView textView3 = viewHolder.userTextView;
        textView3.setTypeface(null, Typeface.ITALIC);
        if(viewHolder.returnDark()){
            textView3.setTextColor(viewHolder.itemView.getResources().getColor(R.color.hintDarkModeColor));
        }
        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    if(buf.getRecId().equals(doc.getString("recId"))){
                        textView3.setText(doc.getString("displayName") + ", " + viewHolder.unixToDate(doc.getLong("datePostedUnix")));
                    }
                }
            }
        });


        //Item click listener for each entry in rv
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MovePage m = new MovePage();

                //Stores values from item into record page fragment
                RecordPageFragment.idTemp = buf.getId();
                RecordPageFragment.albumTemp = buf.getTitle();
                RecordPageFragment.artistTemp = buf.getArtist();
                RecordPageFragment.ratingTemp = buf.getRating();
                RecordPageFragment.genreTemp = buf.getGenre();

                //Censors description text
                if(!viewHolder.returnCensor()){
                    Censor censor = new Censor();
                    RecordPageFragment.descTemp = censor.censorText(buf.getDesc());
                }else{
                    RecordPageFragment.descTemp = buf.getDesc();
                }
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
