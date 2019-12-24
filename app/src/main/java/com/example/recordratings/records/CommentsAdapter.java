package com.example.recordratings.records;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {


    private TextView comsDn, comsPost, comsUp, comsVotes, comsDown;
    private ImageView replyImageView;

    private SharedPreferences shared;
    private SharedPreferences censorSP;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public static int rvPosition;
    public static String commentSelection = " ";
    public static boolean isDark;
    private boolean isCensored;

    private InputMethodManager imm;


    public class ViewHolder extends RecyclerView.ViewHolder{


        public CircleImageView photoImageView;
        public TextView dnTextView;
        public TextView commentTextView;
        public TextView upArrowTextView;
        public TextView votesTextView;
        public TextView downArrowTextView;
        public TextView dateTextView;

        @SuppressLint("ResourceType")
        public ViewHolder(View itemView){
            super(itemView);
            photoImageView = itemView.findViewById(R.id.comment_photo);
            replyImageView = itemView.findViewById(R.id.comment_reply_photo);
            dnTextView = itemView.findViewById(R.id.comment_display_name);
            commentTextView = itemView.findViewById(R.id.comment_post);
            upArrowTextView = itemView.findViewById(R.id.comment_up_arrow);
            votesTextView = itemView.findViewById(R.id.comment_votes);
            downArrowTextView = itemView.findViewById(R.id.comment_down_arrow);

            comsDown = itemView.findViewById(R.id.comment_down_arrow);
            comsUp = itemView.findViewById(R.id.comment_up_arrow);
            comsDn = itemView.findViewById(R.id.comment_display_name);
            comsVotes = itemView.findViewById(R.id.comment_votes);
            comsPost = itemView.findViewById(R.id.comment_post);
            dateTextView = itemView.findViewById(R.id.comment_date);

            imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            shared = itemView.getContext().getSharedPreferences("liked", MODE_PRIVATE);
            censorSP = itemView.getContext().getSharedPreferences("censorPrefs", MODE_PRIVATE);

            comsDown.setTextColor(itemView.getResources().getColor(R.color.red));
            comsUp.setTextColor(itemView.getResources().getColor(R.color.colorGreenBack));


            if(returnDark()){
                comsDn.setTextColor(itemView.getResources().getColor(R.color.colorWhite));
                comsPost.setTextColor(itemView.getResources().getColor(R.color.colorWhite));
                comsVotes.setTextColor(itemView.getResources().getColor(R.color.colorWhite));
                dateTextView.setTextColor(itemView.getResources().getColor(R.color.hintDarkModeColor));
            }

        }
        public boolean returnDark(){
            shared = itemView.getContext().getSharedPreferences("DarkMode", MODE_PRIVATE);
            isDark = shared.getBoolean("darkMode", false);
            return isDark;
        }

        public boolean returnCensor(){
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

        rvPosition = position;

        // Set item views based on your views and data model
        CircleImageView imageView = viewHolder.photoImageView;
        if(imageView != null){
            Uri uri = Uri.parse(com.getPhotoString());
            Picasso.get().load(uri).into(imageView);
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

        if(mAuth.getCurrentUser() != null){
            if(com.getmDisplayName().equals(mAuth.getCurrentUser().getDisplayName())){
                replyImageView.setVisibility(View.INVISIBLE);
            }
        }
        replyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    if(!com.getmDisplayName().equals(mAuth.getCurrentUser().getDisplayName())){
                        RecordPageFragment.commentBox.setText("@" + com.getmDisplayName());
                        RecordPageFragment.commentBox.requestFocus();
                        RecordPageFragment.commentBox.setSelection(com.getmDisplayName().length() + 1);
                        RecordPageFragment.commentBox.setShowSoftInputOnFocus(true);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }else{
                    Toast.makeText(v.getContext(), "You Must be Logged In to Comment.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.uid = com.getmUserId();
                v.getContext().startActivity(new Intent(v.getContext(), ProfileActivity.class));
            }
        });


        comsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                shared = v.getContext().getSharedPreferences("liked", MODE_PRIVATE);
                final SharedPreferences.Editor editor = v.getContext().getSharedPreferences("liked", MODE_PRIVATE).edit();
                db.collection("comments").whereEqualTo("mCommentId", com.getmCommentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot snap: task.getResult()){
                           if(!shared.getBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "liked", false)){
                               int votes = snap.getLong("mVotes").intValue();
                               votes += 1;
                               Toast.makeText(v.getContext(), "Comment Upvoted", Toast.LENGTH_SHORT).show();
                               db.collection("comments").document(snap.getId()).update("mVotes", votes);

                               editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "liked", true);
                               editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "disliked", false);

                               if(RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Recently Added")){
                                   commentSelection = "mTimestamp";
                                   RecordPageFragment.selection = "mTimestamp";
                               }else if (RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Highest Rated")){
                                   commentSelection = "mVotes";
                                   RecordPageFragment.selection = "mVotes";
                               }

                               RecordPageFragment.rvComments.scrollToPosition(rvPosition);

                               RecordPageFragment.selection = commentSelection;

                            }else{
                               Toast.makeText(v.getContext(), "You Have Already Liked This Comment", Toast.LENGTH_SHORT).show();
                           }
                        }

                        editor.apply();
                        editor.commit();

                        RecordPageFragment.rvComments.scrollToPosition(rvPosition);

                    }
                });
            }
        });

        comsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                shared = v.getContext().getSharedPreferences("liked", MODE_PRIVATE);
                final SharedPreferences.Editor editor = v.getContext().getSharedPreferences("liked", MODE_PRIVATE).edit();
                db.collection("comments").whereEqualTo("mCommentId", com.getmCommentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot snap: task.getResult()){
                            if(!shared.getBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "disliked", false)){
                                int votes = snap.getLong("mVotes").intValue();
                                votes -= 1;
                                Toast.makeText(v.getContext(), "Comment Downvoted.", Toast.LENGTH_SHORT).show();
                                db.collection("comments").document(snap.getId()).update("mVotes", votes);

                                editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "liked", false);
                                editor.putBoolean(snap.getString("mCommentId") + snap.get("mUserId") + "disliked", true);
                                RecordPageFragment.rvComments.scrollToPosition(position - 1);

                                if(RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Recently Added")){
                                    commentSelection = "mTimestamp";
                                }else if (RecordPageFragment.sortCommentsBy.getSelectedItem().toString().equals("Highest Rated")){
                                    commentSelection = "mVotes";
                                }

                                RecordPageFragment.rvComments.scrollToPosition(rvPosition);

                                RecordPageFragment.selection = commentSelection;



                            }else{
                                Toast.makeText(v.getContext(), "You Have Already Disliked This Comment.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        editor.apply();
                        editor.commit();


                    }
                });
            }
        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mComments.size();
    }

}
