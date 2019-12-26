package com.example.recordratings.credentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ProfileActivity extends AppCompatActivity {

    public static String uid;
    private int PICK_IMAGE_REQUEST = 1;
    private String photoToString = "";

    private LinearLayout header, bottom;
    private CircleImageView pic, editPic;
    private Button btnApply, btnCancel, btnChangePic;
    private TextView dn, bio, recordCount;
    private EditText editBio;
    private RecyclerView rvRecords;
    private RecordsAdapter adapter;
    private ArrayList<Records> records = new ArrayList<>();
    private ArrayList<Records> tempRecords = new ArrayList<>();
    private TextView emptyRv;

    private AlertDialog dialog;

    private SharedPreferences shared;
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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        header = findViewById(R.id.profile_header);
        bottom = findViewById(R.id.profile_bottom);
        pic = findViewById(R.id.profile_page_picture);
        dn = findViewById(R.id.profile_page_dn);
        bio = findViewById(R.id.profile_bio);
        recordCount = findViewById(R.id.profile_record_counts);

        //Initializes RV w/ adapter
        rvRecords = findViewById(R.id.rvProfileRecords);
        adapter = new RecordsAdapter(records);
        rvRecords.setAdapter(adapter);
        emptyRv = findViewById(R.id.no_records);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        dialog = builder.create();

        //Calls toolbar xml file
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if(returnDark()){
            bottom.setBackgroundColor(getResources().getColor(R.color.darkModeBack));
            rvRecords.setBackground(getResources().getDrawable(R.drawable.rv_dark_border));
        }
        //Gets all of the user's records from db
        readFromDatabase();

        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
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
                            bio.setText(doc.getString("bio"));
                        }

                        break;

                    }
                }
            }
        });

    }


    private boolean returnDark(){
        shared = getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }

    public void readFromDatabase(){
        db = FirebaseFirestore.getInstance();
        records = new ArrayList<>();
        db.collection("records").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    if(id.equals(uid)){
                        records.add(new Records(id, album, artist, rating, photo, genre, desc, recId));
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
        tempRecords = new ArrayList<>();
    }

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

    @Override
    public void onStart(){
        super.onStart();
    }


    public void openDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        editBio = dialogView.findViewById(R.id.edit_bio);
        editPic = dialogView.findViewById(R.id.edit_change_pic);
        btnApply = dialogView.findViewById(R.id.edit_apply);
        btnCancel = dialogView.findViewById(R.id.edit_cancel);
        btnChangePic = dialogView.findViewById(R.id.edit_change_pic_btn);

        if(returnDark()){
            editBio.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
        }
        db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
                    Picasso.get().load(uri).into(editPic);
                }
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

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
                    db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for(DocumentSnapshot doc: queryDocumentSnapshots){
                                db.collection("users").document(doc.getId()).update("mPhotoUrl", photoToString);
                                break;
                            }
                        }
                    });                }

                db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot doc: task.getResult()){
                            Map<String, Object> map = new HashMap<>();
                            map.put("bio", '"' + editBio.getText().toString() + '"');
                            db.collection("users").document(doc.getId()).update(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            editBio.setText("");
                                        }
                                    });
                            break;
                        }
                    }
                });

                Toast.makeText(v.getContext(), "Changes Applied.", Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
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

    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

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
}
