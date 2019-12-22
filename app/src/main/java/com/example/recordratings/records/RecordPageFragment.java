package com.example.recordratings.records;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.MainActivity;
import com.example.recordratings.R;
import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.misc.MovePage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Text View for album and artist names
    private LinearLayout layout, addComLayout, rvLayout;
    private TextView album;
    private TextView artist;
    private TextView commentCharCount;
    public static EditText commentBox;
    private ImageView photo;
    private CircleImageView commentPic;
    private RatingBar rating;
    private TextView description;
    private TextView totalComs, comsDn, comsPost, comsUp, comsVotes, comsDown;
    public static Spinner sortCommentsBy;
    private View view1;
    private View view2;

    //Comment section
    public static RecyclerView rvComments;
    public CommentsAdapter adapter;
    private ArrayList<Comment> comments = new ArrayList<>();

    //Front-end variables
    public static String idTemp;
    public static String albumTemp;
    public static String artistTemp;
    public static double ratingTemp;
    public static String genreTemp;
    public static String descTemp;
    public static Bitmap photoTemp;
    public static String photoStringTemp;
    private String displayName;
    private String userId;
    private String photoString;
    private String recId = "";
    private String commentId = "";
    private ArrayAdapter<String> selectionAdapter;
    public static String selection = CommentsAdapter.commentSelection;
    private Query.Direction direction = Query.Direction.DESCENDING;

    //Buttons
    private Button delBtn;
    private Button editBtn;
    private Button commentBtn;
    private MovePage m;

    //Database helper
    private DatabaseHelper dbh;
    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private com.google.firebase.database.DatabaseReference dbRef;
    private SharedPreferences shared;
    private int layoutManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static boolean isDark = false;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    public RecordPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordPageFragment newInstance(String param1, String param2) {
        RecordPageFragment fragment = new RecordPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_record_page, container, false);

        dbh = new DatabaseHelper(getContext());
        layout = view.findViewById(R.id.record_page_layout);
        rvLayout = view.findViewById(R.id.rvLayout);
        addComLayout = view.findViewById(R.id.add_comment_section);
        view1 = view.findViewById(R.id.view);
        view2 = view.findViewById(R.id.view2);
        description = view.findViewById(R.id.records_page_desc);
        totalComs = view.findViewById(R.id.total_comments);
        comsDown = view.findViewById(R.id.comment_down_arrow);
        comsUp = view.findViewById(R.id.comment_up_arrow);
        comsDn = view.findViewById(R.id.comment_display_name);
        comsVotes = view.findViewById(R.id.comment_votes);
        comsPost = view.findViewById(R.id.comment_post);
        commentBox = view.findViewById(R.id.comment_box);
        commentBtn = view.findViewById(R.id.comment_btn);
        commentPic = view.findViewById(R.id.comment_pic);
        commentCharCount = view.findViewById(R.id.comment_char_count);
        sortCommentsBy = view.findViewById(R.id.sort_comments);
        rvComments = view.findViewById(R.id.rvComments);
        adapter = new CommentsAdapter(comments);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        String[] sorts = {"Recently Added", "Highest Rated"};
        selectionAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, sorts);

        if(returnDark()){
            commentBox.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            addComLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            rvLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            totalComs.setTextColor(getResources().getColor(R.color.colorWhite));
            rvComments.setBackground(getResources().getDrawable(R.drawable.rv_dark_border));
            selectionAdapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, sorts);
            selectionAdapter.setDropDownViewResource(R.layout.spinner_drop_box);
        }
        sortCommentsBy.setAdapter(selectionAdapter);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        if(mAuth.getCurrentUser() != null){
            displayName = mAuth.getCurrentUser().getDisplayName();
            userId = mAuth.getCurrentUser().getUid();
        }

        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot doc: queryDocumentSnapshots){
                    if(doc.getString("artist").equals(artist.getText().toString()) &&
                            doc.getString("title").equals(album.getText().toString()) &&
                            doc.getString("mPhotoString").equals(photoStringTemp)){
                        recId = doc.getString("recId");
                    }
                }
            }
        });

        if(sortCommentsBy.getSelectedItem().toString().equals("Highest Rated") ||
           CommentsAdapter.commentSelection.equals("mVotes")){
            selection = "mVotes";
        } else if(sortCommentsBy.getSelectedItem().toString().equals("Recently Added") ||
            CommentsAdapter.commentSelection.equals("mTimestamp")){
            selection = "mTimestamp";
        }

        queryComments();

        sortCommentsBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selection = CommentsAdapter.commentSelection;
                if(sortCommentsBy.getSelectedItem().toString().equals("Highest Rated")){
                    selection = "mVotes";
                }
                else if (sortCommentsBy.getSelectedItem().toString().equals("Recently Added")){
                    selection = "mTimestamp";
                }
                rvComments.scrollToPosition(0);
                queryComments();
                selection = " ";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                queryComments();
            }
        });


        commentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0 && s.length() <= 250){
                    commentBtn.setClickable(true);
                    commentBtn.setEnabled(true);
                    commentCharCount.setVisibility(View.VISIBLE);
                    commentCharCount.setText(s.length() + "/250");

                }
                if(s.length() > 249){
                    Toast.makeText(getContext(), "Max Character Count Reached", Toast.LENGTH_SHORT).show();
                }
                if(s.length() == 0){
                    commentBtn.setClickable(false);
                    commentBtn.setEnabled(false);
                    commentCharCount.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        commentBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null){
                    Toast.makeText(v.getContext(), "You Must be Logged In to Comment", Toast.LENGTH_SHORT).show();
                }else{
                    String contents = commentBox.getText().toString();
                    Random rand1 = new Random();
                    Random rand2 = new Random();

                    int randomNum1 = rand1.nextInt(99999999);
                    int randomNum2 = rand2.nextInt(99999999);
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = new Date();
                    commentId = mAuth.getUid()  + Integer.toString(randomNum1) + mAuth.getCurrentUser().getDisplayName() + Integer.toString(randomNum2);
                    db.collection("comments").add(new Comment(displayName, contents, df.format(date), userId, recId, commentId, photoString, 0)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(), "Comment Added.", Toast.LENGTH_SHORT).show();
                            commentBox.setText("");
                        }
                    });
                }
            }
        });

        if(mAuth.getCurrentUser() != null){
            final FirebaseUser user = mAuth.getCurrentUser();
            db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot doc: queryDocumentSnapshots){
                        if(user != null){
                            if(doc.getString("mId").equals(user.getUid())){
                                Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                                Picasso.get().load(uri).into(commentPic);
                                photoString = doc.getString("mPhotoUrl");
                                return;
                            }
                        }
                    }
                }
            });
        }
        setDetailViewVariables(view);
        if(isDark) {
            view1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            album.setTextColor(getResources().getColor(R.color.colorWhite));
            artist.setTextColor(getResources().getColor(R.color.colorWhite));
            commentBox.getBackground().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            commentBox.setTextColor(getResources().getColor(R.color.colorWhite));
            commentCharCount.setTextColor(getResources().getColor(R.color.colorWhite));
            layout.setBackgroundColor(getResources().getColor(R.color.darkModeRealBack));
        }

        return view;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Inserts DB values into fragment to be displayed
    private void setDetailViewVariables(View view){
        album = view.findViewById(R.id.records_page_Album);
        album.setText(albumTemp);

        rating = view.findViewById(R.id.records_page_rating);
        rating.setRating((float) ratingTemp);

        artist = view.findViewById(R.id.records_page_artist);
        artist.setText(artistTemp);

        photo = view.findViewById(R.id.records_page_image);
        Uri uri = Uri.parse(photoStringTemp);
        Picasso.get().load(uri).into(photo);

        description = view.findViewById(R.id.records_page_desc);
        description.setText(descTemp);
    }

    private boolean returnDark(){
        shared = getActivity().getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    @Override
    public void onStart(){
        super.onStart();

    }
    

    /**
     * This is a method for Fragment.
     * You can do the same in onCreate or onRestoreInstanceState
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            rvComments.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, rvComments.getLayoutManager().onSaveInstanceState());
    }

    public void queryComments(){
        if(selection.equals("mTimestamp")){
            db.collection("comments").orderBy("mTimestamp", direction).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot doc: queryDocumentSnapshots){
                        if(doc.getString("mRecordId").equals(recId)){
                            Comment newComment = new Comment(doc.getString("mDisplayName"),
                                    doc.getString("mContents"),
                                    doc.getString("mTimestamp"),
                                    doc.getString("mUserId"),
                                    doc.getString("mRecordId"),
                                    doc.getString("mCommentId"),
                                    doc.getString("photoString"),
                                    doc.getLong("mVotes").intValue());

                            comments.add(newComment);
                        }
                    }

                    adapter = new CommentsAdapter(comments);
                    rvComments.setAdapter(adapter);
                    rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.notifyDataSetChanged();
                    totalComs.setText("Total Comments: " + adapter.getItemCount());
                    comments = new ArrayList<>();
                }
            });
        }
        else if (selection.equals("mVotes")){
            db.collection("comments").orderBy("mVotes", direction).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot doc: queryDocumentSnapshots){
                        if(doc.getString("mRecordId").equals(recId)){
                            Comment newComment = new Comment(doc.getString("mDisplayName"),
                                    doc.getString("mContents"),
                                    doc.getString("mTimestamp"),
                                    doc.getString("mUserId"),
                                    doc.getString("mRecordId"),
                                    doc.getString("mCommentId"),
                                    doc.getString("photoString"),
                                    doc.getLong("mVotes").intValue());

                            comments.add(newComment);
                        }
                    }

                    adapter = new CommentsAdapter(comments);
                    rvComments.setAdapter(adapter);
                    rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.notifyDataSetChanged();
                    totalComs.setText("Total Comments: " + adapter.getItemCount());
                    comments = new ArrayList<>();
                }
            });
        }
    }


}
