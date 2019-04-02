package com.safesoft.uk2015.restopro.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


import com.safesoft.uk2015.restopro.Adapters.ExpandableListAdapter_SettingMenu;
import com.safesoft.uk2015.restopro.PostData.Child;
import com.safesoft.uk2015.restopro.PostData.Parent;
import com.safesoft.uk2015.restopro.PostData.PostData_Families;
import com.safesoft.uk2015.restopro.PostData.PostData_Menu;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.util.ArrayList;

/**
 * Created by UK2015 on 21/06/2016.
 */
public class MenuFragment extends Fragment{

    private ExpandableListView mExpandableList;
    private DATABASE controller;
    private ArrayList<PostData_Families> families;
    ArrayList<PostData_Menu> menus;
    ArrayList<Parent> arrayParents;
    ArrayList<Child> arrayChildren;
    String selectQuery;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listmenu, container, false);

        controller = new DATABASE(getActivity());
        mExpandableList = (ExpandableListView) rootView.findViewById(R.id.expandable_list);

        return rootView;
    }


    private void charge_famille_menu(){
        //here we set the parents and the children
        for (int i = 0; i < families.size(); i++){
            //for each "i" create a new Parent object to set the title and the children
            Parent parent = new Parent();
            parent.setTitle(families.get(i).Famille_name);
            parent.setImage_index(families.get(i).Image_index);

            String selectQuery = "SELECT RECORDID, CODE, DESIGNATION, PRIX, DES_IMP, TVA, MENU_ACTIF, IMAGE_INDEX, EMPORTER FROM Menu WHERE " +
                    "FAMILLE = '"+ families.get(i).Famille_name +"' ORDER BY CODE DESC";
            menus = controller.Select_data_menu_from_database(selectQuery);
            arrayChildren = new ArrayList<Child>();
            for (int j = 0; j < menus.size(); j++) {
                Child obj_child = new Child();
                obj_child.setTitle(menus.get(j).Designation);
                obj_child.setChildImage_index(menus.get(j).Image_index);
                arrayChildren.add(obj_child);
            }
            parent.setArrayChildren(arrayChildren);
            //in this array we add the Parent object. We will use the arrayParents at the setAdapter
            arrayParents.add(parent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        selectQuery = "SELECT * FROM Famille";
        families =  controller.Select_data_famille_from_database(selectQuery);
        arrayParents = new ArrayList<Parent>();
        arrayChildren = new ArrayList<Child>();
        charge_famille_menu();
        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new ExpandableListAdapter_SettingMenu(getActivity(),arrayParents));
    }

}
