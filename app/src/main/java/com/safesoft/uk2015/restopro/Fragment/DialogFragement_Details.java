package com.safesoft.uk2015.restopro.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formation.uk2015.login.R;
import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;

/**
 * Created by UK2015 on 21/06/2016.
 */
public class DialogFragement_Details extends SwipeAwayDialogFragment {

    public DialogFragement_Details() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static DialogFragement_Details newInstance(String title) {
        DialogFragement_Details frag = new DialogFragement_Details();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogfragmet, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        //String title = "Description of product";
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
    //    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}