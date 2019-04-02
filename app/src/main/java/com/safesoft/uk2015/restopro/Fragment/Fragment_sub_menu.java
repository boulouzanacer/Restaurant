package com.safesoft.uk2015.restopro.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_SubMenu;
import com.safesoft.uk2015.restopro.Activities.Order_Activity;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;
import com.safesoft.uk2015.restopro.PostData.PostData_Menu;

import java.util.ArrayList;

/**
 * Created by UK2015 on 10/06/2016.
 */
public class Fragment_sub_menu extends Fragment{

    private GridView Gridview;
    private GridViewAdapter_SubMenu plusAdapter;
    private ArrayList<PostData_Menu> menus;
    private String famille;
    private DATABASE contoller;
    private String selectQuery;

    public Fragment_sub_menu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sub_menu, container, false);
        try{
            famille = (String) Order_Activity.myBundle.get("Famille");
            Log.v("TRACKKK", "----------------------------------------------------------" + famille);
        }catch (Exception e){
            Log.e("TRACKKK", "ERROR READ famille NAME FROM Fragment_Menu " + e.getMessage());
            famille = "NOTHING";
        }

        //Get Data from database then push them in the GridView
        menus = new ArrayList<PostData_Menu>();
        contoller = new DATABASE(getActivity());
        selectQuery = "SELECT RECORDID, CODE,DESIGNATION, PRIX, DES_IMP, TVA, MENU_ACTIF, IMAGE_INDEX, EMPORTER FROM Menu WHERE " +
                "FAMILLE = '"+ famille +"' ORDER BY CODE DESC";
        menus = contoller.Select_data_menu_from_database(selectQuery);

        //Manage Data tp put each one in his gridview fragment

        Gridview = (GridView) rootView.findViewById(R.id.gridView_sub_menu);
        plusAdapter = new GridViewAdapter_SubMenu(getActivity(), R.layout.gridview_rows_sub_menu, menus);
        Gridview.setAdapter(plusAdapter);
        //SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(plusAdapter);
        //swingBottomInAnimationAdapter.setAbsListView(Gridview);
        //swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        // Gridview.setAdapter(swingBottomInAnimationAdapter);


//        someMethod(list);
        return rootView;
    }
    public void RefreshFragment_Sub_Menu(ArrayList<PostData_Menu> menus){
        menus = contoller.Select_data_menu_from_database(selectQuery);
        plusAdapter = new GridViewAdapter_SubMenu(getActivity(), R.layout.gridview_rows_sub_menu, menus);
        Gridview.setAdapter(plusAdapter);
    }
}