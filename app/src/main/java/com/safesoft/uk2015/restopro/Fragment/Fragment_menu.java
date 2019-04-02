package com.safesoft.uk2015.restopro.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_Menu;
import com.safesoft.uk2015.restopro.Activities.Order_Activity;
import com.safesoft.uk2015.restopro.PostData.PostData_Families;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.util.ArrayList;

/**
 * Created by UK2015 on 16/06/2016.
 */
public class Fragment_menu extends Fragment {


    private GridView Gridview;
    private GridViewAdapter_Menu plusAdapter;
    private ArrayList<PostData_Families> families;
    private DATABASE controller;


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


    public Fragment_menu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);



        families = new ArrayList<>();

        //Get Data from database then push them in the GridView
        controller = new DATABASE(getActivity());
        String selectQuery = "SELECT * FROM Famille";
        families=  controller.Select_data_famille_from_database(selectQuery);

        //Manage Data tp put each one in his gridview fragment

        Gridview = (GridView) rootView.findViewById(R.id.gridview_menu);
        plusAdapter = new GridViewAdapter_Menu(getActivity(), R.layout.gridview_rows_menu, families);
        Gridview.setAdapter(plusAdapter);
        //SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(plusAdapter);
        //swingBottomInAnimationAdapter.setAbsListView(Gridview);
        //swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        // Gridview.setAdapter(swingBottomInAnimationAdapter);

        Gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PostData_Families famille_name = (PostData_Families) parent.getAdapter().getItem(position);
                Fragment fragment = null;
                fragment = new Fragment_sub_menu();

                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                 //   Fragment_sub_menu instance = new Fragment_sub_menu(); // creating new object
                    fragmentManager.beginTransaction().replace(R.id.frame_container1, fragment).commit();
                    fragment = null;
                } else {
                    // error in creating fragment
                    Log.e("TRACKKK", "Error in creating fragment");
                }

                Order_Activity.myBundle.putString("Famille", String.valueOf(famille_name.Famille_name));
                someEventListener.someEvent("FRAGMENT_MENU",famille_name.Famille_name);
            }
        });

        return rootView;
    }
    public void RefreshFragment_Menu(ArrayList<PostData_Families> families){
        plusAdapter = new GridViewAdapter_Menu(getActivity(), R.layout.gridview_rows_menu, families);
        Gridview.setAdapter(plusAdapter);
    }
}