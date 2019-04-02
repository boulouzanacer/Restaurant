package com.safesoft.uk2015.restopro.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.safesoft.uk2015.restopro.Adapters.ListViewAdapter_CheckedSupplement;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.util.ArrayList;

/**
 * Created by UK2015 on 21/07/2016.
 */
public class Fragment_CheckSupplement extends DialogFragment {

    private ListView listview_supplement_check;
    private ListViewAdapter_CheckedSupplement Adapter_listview;
    private String CODE;
    private String NUM_BON;
    private String RECORDID2;
    private DATABASE contoller;

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

    public Fragment_CheckSupplement(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        CODE = args.getString("CODE");
        NUM_BON = args.getString("NUM_BON");
        RECORDID2 = args.getString("RECORDID2");
        contoller = new DATABASE(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.suppliment_check, container, false);

        getDialog().setTitle("Cochez un supplement ");
        ArrayList<PostData_Bon2> supps;
        supps = contoller.get_row_supplement_from_database(CODE);
        for(int i=0;i<supps.size();i++){
            supps.get(i).Recordid2 = RECORDID2;
            supps.get(i).Num_bon_bon2 = NUM_BON;
            supps.get(i).Tva = "0";
            supps.get(i).Checked = false;
        }

        listview_supplement_check = (ListView) rootView.findViewById(R.id.listView22);
        Adapter_listview = new ListViewAdapter_CheckedSupplement(getActivity(), supps);
        listview_supplement_check.setAdapter(Adapter_listview);

        getDialog().setCanceledOnTouchOutside(false);
        Button button_validate_check = (Button) rootView.findViewById(R.id.button_validate_check);
        button_validate_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someEventListener.someEvent("FROM_FRAGMENT_CHECK_SUPPELEMENT","ADD");
                dismiss();
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
