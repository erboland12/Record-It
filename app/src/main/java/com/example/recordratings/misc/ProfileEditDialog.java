package com.example.recordratings.misc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.recordratings.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProfileEditDialog extends AppCompatDialogFragment {
    private EditText editBio;
    private CircleImageView editPic;
    private SharedPreferences shared;
    private View dialogView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getActivity().getLayoutInflater().inflate(R.layout.edit_custom_dialog, null));


        AlertDialog alertDialog = builder.create();

//        editBio = (EditText) alertDialog.findViewById(R.id.edit_bio);
//        editPic = (CircleImageView) alertDialog.findViewById(R.id.edit_change_pic);
//
//        if(returnDark()){
//            editBio.setHintTextColor(getResources().getColor(R.color.hintDarkModeColor));
//        }
//
//        db.collection("users").whereEqualTo("mId", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
//                    Uri uri = Uri.parse(doc.getString("mPhotoUrl"));
//                    Picasso.get().load(uri).into(editPic);
//                }
//            }
//        });

        return alertDialog;
    }

    private boolean returnDark(){
        shared = getActivity().getSharedPreferences("DarkMode", MODE_PRIVATE);
        return shared.getBoolean("darkMode", false);
    }
}
