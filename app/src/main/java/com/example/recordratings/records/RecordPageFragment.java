package com.example.recordratings.records;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.recordratings.MainActivity;
import com.example.recordratings.R;
import com.example.recordratings.misc.DatabaseHelper;
import com.example.recordratings.misc.MovePage;

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
    private LinearLayout layout;
    private TextView album;
    private TextView artist;
    private ImageView photo;
    private RatingBar rating;
    private TextView description;
    private View view1;

    //Front-end variables
    public static int idTemp;
    public static String albumTemp;
    public static String artistTemp;
    public static double ratingTemp;
    public static String genreTemp;
    public static String descTemp;
    public static Bitmap photoTemp;

    //Buttons
    private Button delBtn;
    private Button editBtn;
    private MovePage m;

    //Database helper
    private DatabaseHelper dbh;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static boolean isDark = false;

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
        View view = inflater.inflate(R.layout.fragment_record_page, container, false);

        dbh = new DatabaseHelper(getContext());
        delBtn = view.findViewById(R.id.records_page_delete_btn);
        editBtn = view.findViewById(R.id.records_page_edit_btn);
        layout = view.findViewById(R.id.record_page_layout);
        view1 = view.findViewById(R.id.view);


        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditRecord.class));
                getActivity().finish();
                EditRecord.idTemp = idTemp;
                EditRecord.albumTemp = albumTemp;
                EditRecord.photoTemp = photoTemp;
                EditRecord.artistTemp = artistTemp;
                EditRecord.genreTemp = genreTemp;
                EditRecord.descTemp = descTemp;
                EditRecord.ratingTemp = ratingTemp;
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbh.deleteData(Integer.toString(idTemp));
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        setDetailViewVariables(view);
        if(isDark) {
            view1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            delBtn.setBackgroundColor(getResources().getColor(R.color.colorButton));
            editBtn.setBackgroundColor(getResources().getColor(R.color.colorButton));
            album.setTextColor(getResources().getColor(R.color.colorWhite));
            artist.setTextColor(getResources().getColor(R.color.colorWhite));
            description.setTextColor(getResources().getColor(R.color.colorWhite));
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
        photo.setImageBitmap(photoTemp);

        description = view.findViewById(R.id.records_page_desc);
        description.setText(descTemp);
    }


}
