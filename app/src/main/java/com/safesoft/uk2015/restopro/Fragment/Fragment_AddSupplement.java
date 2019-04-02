package com.safesoft.uk2015.restopro.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_AddSupplement;
import com.safesoft.uk2015.restopro.PostData.PostData_Supplement;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;
import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;

import java.util.ArrayList;

/**
 * Created by UK2015 on 21/07/2016.
 */
public class Fragment_AddSupplement extends SwipeAwayDialogFragment {

    private Context context;
    private GridView gridview_add_supplement;
    private GridViewAdapter_AddSupplement plusAdapter;
    private ArrayList<PostData_Supplement> supplements;
    private DATABASE contoller;

    public Fragment_AddSupplement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        contoller = new DATABASE(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_addsupplement, container, false);

        getDialog().setTitle("Ajouter suppelement");
        supplements = new  ArrayList<>();
        String selectQuery = "SELECT * FROM Suppelement_menu";
        supplements = contoller.select_supplement_from_sqlite_database(selectQuery);

        context = getActivity();
        gridview_add_supplement = (GridView) rootView.findViewById(R.id.gridview_supplement);
        plusAdapter = new GridViewAdapter_AddSupplement(context, R.layout.gridview_rows_add_supplement, supplements);
        gridview_add_supplement.setAdapter(plusAdapter);

        return rootView;
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity().overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
        super.onActivityResult(requestCode, resultCode, data);
    }
    */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
