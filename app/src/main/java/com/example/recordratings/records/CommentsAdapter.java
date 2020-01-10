package com.example.recordratings.records;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordratings.credentials.ProfileActivity;
import com.example.recordratings.R;
import com.example.recordratings.misc.Censor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

//Adapter to handle RV for comments
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    //Front end variables
    private TextView comsDn, comsPost, comsUp, comsVotes, comsDown;
    private View comment_view;
    private ImageView replyImageView;

    //Shared preferences, database, and auth variables
    private SharedPreferences shared;
    private SharedPreferences censorSP;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Miscellaneous variables
    private static boolean isDark;
    private boolean isCensored;
    private InputMethodManager imm;
    private static int commentCount;

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Recycler view front end variables
        private CircleImageView photoImageView;
        private TextView dnTextView;
        private TextView commentTextView;
        private TextView upArrowTextView;
        private TextView votesTextView;
        private TextView downArrowTextView;
        private TextView dateTextView;

        @SuppressLint("ResourceType")
        private ViewHolder(View itemView){
            super(itemView);
            //Initializes view holder front end variables
            photoImageView = itemView.findViewById(R.id.comment_photo);
            replyImageView = itemView.findViewById(R.id.comment_reply_photo);
            dnTextView = itemView.findViewById(R.id.comment_display_name);
            commentTextView = itemView.findViewById(R.id.comment_post);
            upArrowTextView = itemView.findViewById(R.id.comment_up_arrow);
            votesTextView = itemView.findViewById(R.id.comment_votes);
            downArrowTextView = itemView.findViewById(R.id.comment_down_arrow);
            comment_view = itemView.findViewById(R.id.comment_view);
            dateTextView = itemView.findViewById(R.id.comment_date);

            //Input managed for opening soft keyboard on button press
            imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            //Initializes auth and db
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            //Gets shared preferences
            shared = itemView.getContext().getSharedPreferences("liked", MODE_PRIVATE);
            censorSP = itemView.getContext().getSharedPreferences("censorPrefs", MODE_PRIVATE);

            if(returnDark()){
                dnTextView.setTextColor(itemView.getResources().getColor(R.color.colorWhite));
                commentTextView.setTextColor(itemView.getResources().getColor(R.color.colorWhite));
                votesTextView.setTextColor(itemView.getResources().getColor(R.color.colorWhite));
                dateTextView.setTextColor(itemView.getResources().getColor(R.color.hintDarkModeColor));
                replyImageView.setColorFilter(itemView.getResources().getColor(R.color.hintDarkModeColor), PorterDuff.Mode.SRC_ATOP);
            }
        }
        //Determines if night mode preference is enabled
        public boolean returnDark(){
            shared = itemView.getContext().getSharedPreferences("DarkMode", MODE_PRIVATE);
            isDark = shared.getBoolean("darkMode", false);
            return isDark;
        }

        //Determines if censorship is disabled
        private boolean returnCensor(){
            isCensored = censorSP.getBoolean("censorOff", false);
            return isCensored;
        }

    }

    private ArrayList<Comment> mComments;

    public CommentsAdapter(ArrayList<Comment> comments) {
        mComments = comments;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.comment_item, parent, false);

        // Return a new holder instance
        CommentsAdapter.ViewHolder viewHolder = new CommentsAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder viewHolder, final int position) {

        // Get the data model based on position
        final Comment com = mComments.get(position);
        Censor censor = new Censor();

        // Set item views based on your views and data model
        final CircleImageView imageView = viewHolder.photoImageView;
        if(imageView != null){
            db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if(doc.getString("mDisplayName").equals(com.getmDisplayName())){
                            Picasso.get().load(Uri.parse(doc.getString("mPhotoUrl"))).into(imageView);
                        }
                    }
                }
            });
        }

        final TextView dn = viewHolder.dnTextView;
        dn.setText(com.getmDisplayName());
        final TextView message = viewHolder.commentTextView;
        if(!viewHolder.returnCensor()){
            String contents = censor.censorText(com.getmContents());
            message.setText(contents);
        }else{
            message.setText(com.getmContents());
        }
        final TextView upArrow = viewHolder.upArrowTextView;
        upArrow.setText("\u2303");
        final TextView votes = viewHolder.votesTextView;
        votes.setText(Integer.toString(com.getmVotes()));
        final TextView downArrow = viewHolder.downArrowTextView;
        downArrow.setText("\u2304");
        final TextView dateText = viewHolder.dateTextView;
        dateText.setText(" - " + com.getmTimestamp());


        //Hides reply icon for all comments from current user
        if(mAuth.getCurrentUser() != null){
            if(com.getmDisplayName().equals(mAuth.getCurrentUser().getDisplayName())){
                replyImageView.setVisibility(View.INVISIBLE);
            }
        }

        //Button listener for replying to a comment
        replyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    if(!com.getmDisplayName().equals(mAuth.getCurrentUser().getDisplayName())){
                        //Gets user's  display name
                        RecordPageFragment.commentBox.setText("@" + com.getmDisplayName());

                        //Opens soft keyboard on button click
                        RecordPageFragment.commentBox.requestFocus();
                        RecordPageFragment.commentBox.setSelection(com.getmDisplayName().length() + 1);
                        RecordPageFragment.commentBox.setShowSoftInputOnFocus(true);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }else{
                    //Prevents anonymous users from commenting
                    Toast.makeText(v.getContext(), "You Must be Logged In to Comment.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button listener that opens profile page
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.uid = com.getmUserId();
                v.getContext().startActivity(new Intent(v.getContext(), ProfileActivity.class));
            }
        });


//        comsUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                shared = v.getContext().getSharedPreferences("liked", MODE_PRIVATE);
//                final SharedPreferences.Editor editor = v.getContext().getSharedPreferences("liked", MODE_PRIVATE).edit();
//                db.collection("comments").whereEqualTo("mCommentId", com.getmCommentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for(QueryDocumentSnapshot snap: task.getResult()){
//                           if(!shared.getBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "liked", false)){
//                               int votes = snap.getLong("mVotes").intValue();
//                               votes += 1;
//                               Toast.makeText(v.getContext(), "Comment Upvoted", Toast.LENGTH_SHORT).show();
//                               db.collection("comments").document(snap.getId()).update("mVotes", votes);
//
//                               editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "liked", true);
//                               editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "disliked", false);
//
//                               if(RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Recently Added")){
//                                   commentSelection = "mTimestamp";
//                                   RecordPageFragment.selection = "mTimestamp";
//                               }else if (RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Highest Rated")){
//                                   commentSelection = "mVotes";
//                                   RecordPageFragment.selection = "mVotes";
//                               }
//
//                            }else{
//                               Toast.makeText(v.getContext(), "You Have Already Liked This Comment", Toast.LENGTH_SHORT).show();
//                           }
//                        }
//
//                        editor.apply();
//                        editor.commit();
//
//                        RecordPageFragment.rvComments.scrollToPosition(rvPosition);
//
//                    }
//                });
//            }
//        });
//
//        comsDown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                shared = v.getContext().getSharedPreferences("liked", MODE_PRIVATE);
//                final SharedPreferences.Editor editor = v.getContext().getSharedPreferences("liked", MODE_PRIVATE).edit();
//                db.collection("comments").whereEqualTo("mCommentId", com.getmCommentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for(QueryDocumentSnapshot snap: task.getResult()){
//                            if(!shared.getBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "disliked", false)){
//                                int votes = snap.getLong("mVotes").intValue();
//                                votes -= 1;
//                                Toast.makeText(v.getContext(), "Comment Downvoted.", Toast.LENGTH_SHORT).show();
//                                db.collection("comments").document(snap.getId()).update("mVotes", votes);
//
//                                editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "liked", false);
//                                editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "disliked", true);
//                                RecordPageFragment.rvComments.scrollToPosition(position - 1);
//
//                                if(RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Recently Added")){
//                                    commentSelection = "mTimestamp";
//                                }else if (RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Highest Rated")){
//                                    commentSelection = "mVotes";
//                                }
//
//                            }else{
//                                Toast.makeText(v.getContext(), "You Have Already Disliked This Comment.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        editor.apply();
//                        editor.commit();
//                        RecordPageFragment.rvComments.scrollToPosition(rvPosition);
//
//                    }
//                });
//            }
//        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        commentCount = mComments.size();
        return mComments.size();
    }

}
