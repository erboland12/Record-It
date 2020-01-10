package com.example.recordratings.records;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.recordratings.credentials.ProfileActivity;
import com.example.recordratings.misc.Censor;
import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.misc.MovePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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

    //Front end variables
    private LinearLayout layout, addComLayout, rvLayout;
    private TextView album;
    private TextView artist;
    private TextView commentCharCount;
    public static EditText commentBox;
    private ImageView photo;
    private CircleImageView commentPic, createrPic;
    private RatingBar rating;
    private TextView description;
    private TextView totalComs, createrName;
    public static Spinner sortCommentsBy;
    private View view1;

    //Comment section
    public static RecyclerView rvComments;
    public CommentsAdapter adapter;
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Comment> tempComments = new ArrayList<>();

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
    private Button commentBtn;

    //Database helper
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences shared, censorSP;
    private boolean isCensored;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Miscellaneous
    private boolean isAdmin = false;
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

        //Initializes all front end variables in fragment
        layout = view.findViewById(R.id.record_page_layout);
        rvLayout = view.findViewById(R.id.rvLayout);
        addComLayout = view.findViewById(R.id.add_comment_section);
        view1 = view.findViewById(R.id.view);
        description = view.findViewById(R.id.records_page_desc);
        totalComs = view.findViewById(R.id.total_comments);
        commentBox = view.findViewById(R.id.comment_box);
        commentBtn = view.findViewById(R.id.comment_btn);
        commentPic = view.findViewById(R.id.comment_pic);
        commentCharCount = view.findViewById(R.id.comment_char_count);
        sortCommentsBy = view.findViewById(R.id.sort_comments);
        rvComments = view.findViewById(R.id.rvComments);
        createrPic = view.findViewById(R.id.creater_pic);
        createrName = view.findViewById(R.id.creater_name);
        adapter = new CommentsAdapter(comments);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        //Sets up censor shared preference
        censorSP = view.getContext().getSharedPreferences("censorPrefs", MODE_PRIVATE);

        //Handles custom toolbar
        Toolbar toolbar = view.findViewById(R.id.fragment_toolbar);
        toolbar.setFitsSystemWindows(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        //Creates options and adapter for sort spinner
        String[] sorts = {"Recently Added", "Highest Rated"};
        selectionAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, sorts);

        //Additional styling if night mode preference is enabled
        if(returnDark()){
            description.setBackground(getResources().getDrawable(R.drawable.dark_layout_border));
            description.setPadding(20, 20, 20, 20);
            commentBox.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
            addComLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            rvLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            totalComs.setTextColor(getResources().getColor(R.color.colorWhite));
            rvComments.setBackground(getResources().getDrawable(R.drawable.rv_dark_border));
            selectionAdapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, sorts);
            selectionAdapter.setDropDownViewResource(R.layout.spinner_drop_box);
        }

        //Censors description text if censor is disabled
        if(!returnCensor()){
            Censor censor = new Censor();
            String desc = description.getText().toString();
            description.setText(censor.censorText(desc));
        }
        sortCommentsBy.setAdapter(selectionAdapter);

        //Database helper stuff
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser() != null){
            displayName = mAuth.getCurrentUser().getDisplayName();
            userId = mAuth.getCurrentUser().getUid();
        }

        //Button listener for opening profile of user that posted record
        createrPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            if(recId.contains(doc.getString("id"))){
                                ProfileActivity.uid = doc.getString("id");
                                v.getContext().startActivity(new Intent(v.getContext(), ProfileActivity.class));
                                break;
                            }
                        }
                    }
                });

            }
        });

        //Database query to obtain record id
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

        //Pulls all comments for a specific record
        queryComments();

        //Sets selection action on spinner item select
//        sortCommentsBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                selection = CommentsAdapter.commentSelection;
//                if(sortCommentsBy.getSelectedItem().toString().equals("Highest Rated")){
//                    selection = "mVotes";
//                }
//                else if (sortCommentsBy.getSelectedItem().toString().equals("Recently Added")){
//                    selection = "mTimestamp";
//                }
//                rvComments.scrollToPosition(0);
//                queryComments();
//                selection = " ";
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                queryComments();
//            }
//        });


        //Text change listener for comment edit text
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

        //Button listener for creating a comment
        commentBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Prevents anonymous users from commenting
                if(mAuth.getCurrentUser() == null){
                    Toast.makeText(v.getContext(), "You Must be Logged In to Comment", Toast.LENGTH_SHORT).show();
                }else{

                    String contents = commentBox.getText().toString();
                    Random rand1 = new Random();
                    Random rand2 = new Random();
                    int randomNum1 = rand1.nextInt(99999999);
                    int randomNum2 = rand2.nextInt(99999999);

                    //Creates date format to record timestamp
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = new Date();
                    long timestamp = Instant.now().getEpochSecond();

                    //Generates unique comment id
                    commentId = mAuth.getUid()  + Integer.toString(randomNum1) + mAuth.getCurrentUser().getDisplayName() + Integer.toString(randomNum2);

                    //Database call to add new comment
                    db.collection("comments").add(new Comment(displayName, contents, df.format(date), userId, recId, commentId, photoString, 0, timestamp)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(), "Comment Added.", Toast.LENGTH_SHORT).show();
                            commentBox.setText("");
                        }
                    });
                }
            }
        });

        //Loads in picture for comment
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

        //Additional styling if night mode is enabled
        if(isDark) {
            view1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            album.setTextColor(getResources().getColor(R.color.colorWhite));
            artist.setTextColor(getResources().getColor(R.color.hintDarkModeColor));
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
        Picasso.get().load(uri).fit().centerCrop().into(photo);

        description = view.findViewById(R.id.records_page_desc);
        description.setText(descTemp);
    }

    //Determines if night mode preference is enabled
    private boolean returnDark(){
        shared = getActivity().getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    //Determines if censor preference is disabled
    private boolean returnCensor(){
        isCensored = censorSP.getBoolean("censorOff", false);
        return isCensored;
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

    //Various database calls. See comments above each query
    public void queryComments(){
        //DB call to determine admin status
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    if(mAuth.getCurrentUser() != null){
                        if(mAuth.getCurrentUser().getUid().equals(doc.getString("mId"))){
                            if(doc.getBoolean("admin")){
                                isAdmin = true;
                            }
                        }    
                    }
                }
            }
        });

        //DB call to load in comment image, id, and timestamp
        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    if(recId.contains(doc.getString("id"))){
                        db.collection("users").whereEqualTo("mId", doc.getString("id")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(final QueryDocumentSnapshot doc: task.getResult()){
                                    Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                                    Picasso.get().load(uri).into(createrPic);
                                    ProfileActivity.uid = doc.getString("mId");

                                    createrName.setText("Posted by " + doc.getString("mDisplayName"));
                                }
                            }
                        });
                    }
                }
            }
        });

        //DB call to add new comment
        db.collection("comments").orderBy("unixTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                doc.getLong("mVotes").intValue(),
                                doc.getLong("unixTime"));

                        comments.add(newComment);
                        tempComments.add(newComment);
                    }

                }

                adapter = new CommentsAdapter(comments);
                if(isAdmin){
                    new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvComments);
                }
                rvComments.setAdapter(adapter);
                rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter.notifyDataSetChanged();
                totalComs.setText("Total Comments: " + adapter.getItemCount());
                comments = new ArrayList<>();

            }
        });
    }

    //Handles custom menu icons and actions
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_record_fragment, menu);
        if(returnDark()){
            for(int i = 0; i < menu.size(); i++){
                Drawable drawable = menu.getItem(i).getIcon();
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }
        if(mAuth.getCurrentUser() != null){
            db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                        if(doc.getString("mId").equals(mAuth.getCurrentUser().getUid())){
                            if(doc.getBoolean("admin")){
                                menu.getItem(0).setVisible(true);
                            }
                        }
                    }
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    //Handles custom menu item slection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            startActivity(new Intent(getContext(), MainActivity.class));
            return true;
        }
        else if (id == R.id.action_admin_del_record){
            createDeleteRecordAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    //Item touch helper that allows swipe function for recycler view
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            createDeleteCommentAlertDialog(viewHolder);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    //Creates alert dialog for deleting a record
    private void createDeleteRecordAlertDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Admin Delete Record")
                .setMessage("Are you Sure you Want to Delete this Record?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                    if(doc.getString("recId").equals(recId)){
                                        db.collection("records").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Record Deleted By Admin", Toast.LENGTH_SHORT).show();
                                                getActivity().finish();
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), RecordsPage.class));
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), RecordsPage.class));
                    }
                }).show();
    }

    //Creates alert dialog for deleting a comment
    private void createDeleteCommentAlertDialog(final RecyclerView.ViewHolder viewHolder){
        //Interface for Admin to delete a comment
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Admin Delete Comment")
                .setMessage("Are you Sure you Want to Delete this Comment?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String id = tempComments.get(viewHolder.getAdapterPosition()).getmCommentId();
                        db.collection("comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(QueryDocumentSnapshot doc: Objects.requireNonNull(task.getResult())){
                                    if(id.equals(doc.getString("mCommentId"))){
                                        db.collection("comments").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Comment Deleted By Admin", Toast.LENGTH_SHORT).show();
                                                rvComments.scrollToPosition(viewHolder.getAdapterPosition());
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comments.addAll(tempComments);
                        rvComments.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        comments.addAll(tempComments);
                        rvComments.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }
}
