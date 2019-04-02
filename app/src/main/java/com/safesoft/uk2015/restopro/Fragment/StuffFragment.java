package com.safesoft.uk2015.restopro.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.Activities.Main_SafeSoft;
import com.safesoft.uk2015.restopro.PostData.PostData_Users;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by UK2015 on 02/08/2016.
 */
public class StuffFragment extends Fragment {

    int TAKE_PHOTO_CODE = 0;
    String dir;
    ImageView myImage;
    private boolean mReturningWithResult = false;
    private String _waiter;
    private DATABASE controller;
    private ArrayList<PostData_Users> users = new ArrayList<>();

    public interface onSomeEventListener {
        public void someEvent(String s, String Value);
    }

    onSomeEventListener someEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    public StuffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_staff, container, false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AbandoN.ttf");
        TextView tv = (TextView) rootView.findViewById(R.id.title_user_gestion);
        tv.setTypeface(tf);

        controller = new DATABASE(getActivity());
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/waiter_picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        EditText editText = (EditText) rootView.findViewById(R.id.name);
        myImage = (ImageView) rootView.findViewById(R.id.imageView6);

        _waiter = ((Main_SafeSoft)getActivity()).WAITER;
            File imgFile = new  File(dir+_waiter+".jpg");
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myImage.setImageBitmap(myBitmap);
            editText.setText(_waiter);
        }

        Button capture = (Button) rootView.findViewById(R.id.take_photo);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // and likewise.

                String file = dir+_waiter+".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                }

                Uri outputFileUri = Uri.fromFile(newfile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == getActivity().RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
            mReturningWithResult = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReturningWithResult) {
            // Commit your transactions here.
            File imgFile = new  File(dir+_waiter+".jpg");
            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myImage.setImageBitmap(myBitmap);
                controller.Update_users(_waiter+".jpg");

                //send event to main_saifsoft
                someEventListener.someEvent("FROM_STUFF_FRAGMENT",_waiter+".jpg");
            }
        }
        // Reset the boolean flag back to false for next time.
        mReturningWithResult = false;
    }
}
