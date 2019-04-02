package com.safesoft.uk2015.restopro.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.safesoft.uk2015.restopro.Adapters.PlanSalleAdapter_Change;
import com.safesoft.uk2015.restopro.PostData.PostData_Tables;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;
import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;

import java.util.ArrayList;

/**
 * Created by UK2015 on 20/06/2016.
 */
public class TablesFragment_Change extends SwipeAwayDialogFragment {

    private Context context;
    private GridView gridview_plan_salle;
    private PlanSalleAdapter_Change plusAdapter;
    private ArrayList<PostData_Tables> tables;
    private DATABASE contoller;
    private String _num_bon;
    private String _numtable;


    public TablesFragment_Change() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        _num_bon = args.getString("num_bon");
        _numtable = args.getString("num_table");
        contoller = new DATABASE(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.plan_salle_gridview, container, false);
        getDialog().setTitle("Changement de table ");
        tables = new  ArrayList<>();
        String selectQuery = "SELECT * FROM Tables WHERE NUM_BON IS NULL";
        tables = contoller.Select_tables_from_sqlite_database(selectQuery);
        context = getActivity();
        if(tables.size()<=0){
            getDialog().setTitle("Aucune table est libre");
        }else{
            gridview_plan_salle = (GridView) rootView.findViewById(R.id.gridView_plan_salle);
            plusAdapter = new PlanSalleAdapter_Change(context, R.layout.plan_salle_row, tables, _num_bon, _numtable);
            gridview_plan_salle.setAdapter(plusAdapter);
        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}