package com.example.recordratings.credentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordratings.R;
import com.example.recordratings.misc.Censor;
import com.example.recordratings.misc.ProfileEditDialog;
import com.example.recordratings.records.Records;
import com.example.recordratings.records.RecordsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

public class ProfileActivity extends AppCompatActivity {

    //Miscellaneous
    public static String uid;
    private int PICK_IMAGE_REQUEST = 1;
    private String photoToString = "";

    private LinearLayout bottom;
    private CircleImageView pic, editPic;
    private Button btnApply, btnCancel, btnChangePic;
    private TextView dn, bio, recordCount;
    private EditText editBio;
    private RecyclerView rvRecords;
    private RecordsAdapter adapter;
    private ArrayList<Records> records = new ArrayList<>();

    private SharedPreferences shared, censorSP;
    private boolean isCensored;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(returnDark()){
            setTheme(R.style.darkThemeNoBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("");

        //Initializes databases, auth, and storage variables
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Initializes front-end variables
        bottom = findViewById(R.id.profile_bottom);
        pic = findViewById(R.id.profile_page_picture);
        dn = findViewById(R.id.profile_page_dn);
        bio = findViewById(R.id.profile_bio);
        recordCount = findViewById(R.id.profile_record_counts);

        //Initializes RV w/ adapter
        rvRecords = findViewById(R.id.rvProfileRecords);
        adapter = new RecordsAdapter(records);
        if(uid.equals(mAuth.getCurrentUser().getUid())){
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvRecords);
        }
        rvRecords.setAdapter(adapter);

        //Sets up shared preferences for censor preferences
        censorSP = getSharedPreferences("censorPrefs", MODE_PRIVATE);

        //Calls toolbar xml file
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        //Additional styling changes if night mode preference is on
        if(returnDark()){
            bottom.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            rvRecords.setBackground(getResources().getDrawable(R.drawable.rv_dark_border));
        }

        //Gets all of the user's records from db
        readFromDatabase();

        //Database query to set up profile picture, display name, bio, and record count
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc: task.getResult()){
                    if(doc.getString("mId").equals(uid)){
                        Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                        Picasso.get().load(uri).into(pic);

                        dn.setText(doc.getString("mDisplayName"));

                        if(adapter.getItemCount() < 1){
                            recordCount.setText(doc.getString("mDisplayName") + " has not posted any records.");

                        }else{
                            recordCount.setText(doc.getString("mDisplayName") + "'s Total Records: " + Integer.toString(adapter.getItemCount()));
                        }

                        if(mAuth.getCurrentUser().getUid().equals(uid) && adapter.getItemCount() < 1){
                            recordCount.setText("You have not posted any records.");
                        }else if(mAuth.getCurrentUser().getUid().equals(uid) && adapter.getItemCount() >= 1){
                            recordCount.setText("Your Total Records: " + Integer.toString(adapter.getItemCount()));
                        }

                        if(doc.getString("bio").equals("Empty")){
                            bio.setText("\"This Mysterious User Has Nothing To Say...\"");
                        }else{
                            if(!returnCensor()){
                                Censor censor = new Censor();
                                bio.setText(censor.censorText(doc.getString("bio")));
                            }else{
                                bio.setText(doc.getString("bio"));
                            }
                        }

//                        break;

                    }
                }
            }
        });

    }

    //Determines if night mode preference is on
    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    //Determines if censorship is disabled
    private boolean returnCensor(){
        isCensored = censorSP.getBoolean("censorOff", false);
        return isCensored;
    }

    //Database query that loads in all records for a specific user
    public void readFromDatabase(){
        db = FirebaseFirestore.getInstance();
        records = new ArrayList<>();
        db.collection("records").orderBy("datePostedUnix", DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int i = 0;
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String id = document.get("id").toString();
                    String album = document.getString("title");
                    String artist = document.getString("artist");
                    Double rating = document.getDouble("rating");
                    String photo = document.getString("mPhotoString");
                    String genre = document.getString("genre");
                    String desc = document.getString("desc");
                    String recId = document.getString("recId");
                    long date = (long) document.get("datePostedUnix");
                    if(id.equals(uid)){
                        records.add(new Records(id, album, artist, rating, photo, genre, desc, recId, date));
                        i++;
                    }
                }

                adapter = new RecordsAdapter(records);
                rvRecords.setAdapter(adapter);
                rvRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter.notifyDataSetChanged();


            }
        });
        records = new ArrayList<>();
    }

    //Handles menu icons and actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        invalidateOptionsMenu();
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        if(returnDark()){
            for(int i = 0; i < menu.size(); i++){
                if(!mAuth.getCurrentUser().getUid().equals(uid)){
                    menu.getItem(i).setVisible(false);
                }
                Drawable drawable = menu.getItem(i).getIcon();
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }else{
            for(int i = 0; i < menu.size(); i++) {
                if (!mAuth.getCurrentUser().getUid().equals(uid)) {
                    menu.getItem(i).setVisible(false);
                }
            }
        }

        return true;
    }

    //Handles actions for selected menu icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fav) {
            openDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Opens dialog for customization of profile
    public void openDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        //Initializes front end variables for dialog
        editBio = dialogView.findViewById(R.id.edit_bio);
        editPic = dialogView.findViewById(R.id.edit_change_pic);
        btnApply = dialogView.findViewById(R.id.edit_apply);
        btnCancel = dialogView.findViewById(R.id.edit_cancel);
        btnChangePic = dialogView.findViewById(R.id.edit_change_pic_btn);

        if(returnDark()){
            editBio.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
        }

        //Loads current user's photo into image view
        db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                    Picasso.get().load(uri).into(editPic);
                }
            }
        });

        //Creates instance of alert dialog
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //Button listener for choosing new profile image
        btnChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //Determines action of apply and cancel buttons
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!photoToString.isEmpty()){
                    db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot doc: task.getResult()){
                                db.collection("users").document(doc.getId()).update("mPhotoUrl", photoToString);
                                break;
                            }
                        }
                    });

                }

                db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(final QueryDocumentSnapshot doc: task.getResult()){
                            Map<String, Object> map = new HashMap<>();
                            map.put("bio", '"' + editBio.getText().toString() + '"');
                            db.collection("users").document(doc.getId()).update(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!returnCensor()){
                                                Censor censor = new Censor();
                                                bio.setText(censor.censorText(doc.getString("bio")));
                                            }else{
                                                bio.setText(doc.getString("bio"));
                                            }
                                        }
                                    });
                        }
                    }
                });
                Toast.makeText(v.getContext(), "Changes Applied.", Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Canceled.", Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
            }
        });

    }

    //Sets up intent for choosing image from gallery
    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Handles action of choosing image.  Loads image into image on successful pick
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final StorageReference ref = mStorageRef.child(data.getDataString());
                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUri = uri;
                                Picasso.get().load(uri).into(editPic);
                                photoToString = downloadUri.toString();
                            }
                        });
                    }
                });
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "No Image has been Selected.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //Item touch helper for recycler view swipe action
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //Creates alert dialog on left swipe that prompts user for record deletion
        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(viewHolder.itemView.getContext());
            dialogBuilder.setTitle("Delete Record")
                    .setMessage("Are you Sure you Want to Delete this Record?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String id = records.get(viewHolder.getAdapterPosition()).getRecId();
                            db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                                        if(doc.getString("recId").equals(id)){
                                            db.collection("records").document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
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
                        rvRecords.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        rvRecords.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }).show();

        }

        //Handles front-end swipe box
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(ProfileActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}
