package com.safesoft.uk2015.restopro.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.safesoft.uk2015.restopro.Adapters.PlanSalleAdapter;
import com.safesoft.uk2015.restopro.Activities.Main_SafeSoft;
import com.safesoft.uk2015.restopro.PostData.PostData_Tables;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.util.ArrayList;

/**
 * Created by UK2015 on 20/06/2016.
 */
public class TablesFragment extends Fragment {

    private Context context;
    private GridView gridview_plan_salle;
    private PlanSalleAdapter plusAdapter;
    ArrayList<PostData_Tables> tables;
    private String _waiter;

    DATABASE contoller;

    public TablesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.plan_salle_gridview, container, false);

        Main_SafeSoft activity = (Main_SafeSoft) getActivity();
        _waiter = activity.getWaiter();

        tables = new  ArrayList<>();
        contoller = new DATABASE(getActivity());
        String selectQuery = "SELECT * FROM Tables";
        tables = contoller.Select_tables_from_sqlite_database(selectQuery);


        context = getActivity();
        gridview_plan_salle = (GridView) rootView.findViewById(R.id.gridView_plan_salle);
        plusAdapter = new PlanSalleAdapter(context, R.layout.plan_salle_row, tables, _waiter);
        gridview_plan_salle.setAdapter(plusAdapter);
        return rootView;
    }


    public void Refresh_Tables(){
        String selectQuery = "SELECT * FROM Tables";
        tables = contoller.Select_tables_from_sqlite_database(selectQuery);
        plusAdapter.refreshplansall(tables);
    }

}















































/*
   public  void create_circle_shape(){

        final LinearLayout layout4 = (LinearLayout) findViewById(R.id.layout4);

        ShapeDrawable smallerCircle= new ShapeDrawable( new OvalShape());
        smallerCircle.setIntrinsicHeight( 10 );
        smallerCircle.setIntrinsicWidth( 10);
        smallerCircle.setBounds(new Rect(0, 0, 10, 10));
        smallerCircle.getPaint().setColor(Color.BLACK);
        smallerCircle.setPadding(50,50,50,50);

        Drawable[] d = {smallerCircle};
        LayerDrawable composite1 = new LayerDrawable(d);
        //LayerDrawable composite1 = new LayerDrawable(new Drawable[]{ smallerCircle, biggerCircle});
        layout4.setBackground(composite1);
    }


    public void createLayout(String numTable){
        // Creating a new RelativeLayout
        RelativeLayout relativeLayout = new RelativeLayout(this);

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(100, 100);

        // Creating a new TextView
        TextView tv = new TextView(this);
        tv.setText("Table " + numTable);
        tv.setTextSize(25);

        // Defining the layout parameters of the TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        // Setting the parameters on the TextView
        tv.setLayoutParams(lp);

        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(tv);

        // Setting the RelativeLayout as our content view
        setContentView(relativeLayout, rlp);
    }


    public void createL(){

        LinearLayout LL = new LinearLayout(this);
        LL.setBackgroundColor(Color.CYAN);
        LL.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LL.setWeightSum(6f);
        LL.setLayoutParams(LLParams);


        ImageView ladder = new ImageView(this);
        ladder.setImageResource(R.mipmap.ic_launcher);
        FrameLayout.LayoutParams ladderParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        ladder.setLayoutParams(ladderParams);
        FrameLayout ladderFL = new FrameLayout(this);
        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        ladderFLParams.weight = 5f;
        ladderFL.setLayoutParams(ladderFLParams);
        ladderFL.setBackgroundColor(Color.GREEN);
        View dummyView = new View(this);


        LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
        dummyParams.weight = 1f;
        dummyView.setLayoutParams(dummyParams);
        dummyView.setBackgroundColor(Color.RED);


        // ladderFL.addView(ladder);
        //LL.addView(ladderFL);
        // LL.addView(dummyView);

        RelativeLayout rl=((RelativeLayout) findViewById(R.id.relativeLayout));
        rl.addView(LL);
    }

    public void layout(){
        LinearLayout myRoot = (LinearLayout) findViewById(R.id.root);
        TextView tv1 = new TextView(this);
        tv1.setText("Table " + 1);
        tv1.setTextSize(25);
        //=============
        TextView tv2 = new TextView(this);
        tv2.setText("Table " + 3);
        tv2.setTextSize(25);
        //==================
        TextView tv3 = new TextView(this);
        tv3.setText("Table " + 8);
        tv3.setTextSize(25);
        //=====================

        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.HORIZONTAL);
        a.addView(tv1);
        a.addView(tv2);
        a.addView(tv3);
        myRoot.addView(a);
    }
*/

