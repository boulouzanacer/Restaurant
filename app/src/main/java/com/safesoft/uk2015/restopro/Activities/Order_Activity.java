package com.safesoft.uk2015.restopro.Activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_AddSupplement;
import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_SubMenu;
import com.safesoft.uk2015.restopro.Adapters.ListViewAdapter_CheckedSupplement;
import com.safesoft.uk2015.restopro.Adapters.ListViewAdapter_Receipt;
import com.safesoft.uk2015.restopro.Adapters.PlanSalleAdapter_Change;
import com.safesoft.uk2015.restopro.Fragment.Fragment_AddSupplement;
import com.safesoft.uk2015.restopro.Fragment.Fragment_CheckSupplement;
import com.safesoft.uk2015.restopro.Fragment.Fragment_menu;
import com.safesoft.uk2015.restopro.Fragment.Fragment_receipt;
import com.safesoft.uk2015.restopro.Fragment.Fragment_sub_menu;
import com.safesoft.uk2015.restopro.Fragment.TablesFragment_Change;
import com.safesoft.uk2015.restopro.Pos;
import com.safesoft.uk2015.restopro.PostData.FoodsBean;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.PostData.PostData_Imps;
import com.safesoft.uk2015.restopro.Service.ServiceLuncher;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;
import com.github.fabtransitionactivity.SheetLayout;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dmax.dialog.SpotsDialog;

/**
 * Created by UK2015 on 10/06/2016.
 */

public class Order_Activity extends AppCompatActivity implements
        SheetLayout.OnFabAnimationEndListener,
        Fragment_menu.onSomeEventListener,
        GridViewAdapter_SubMenu.onDataSendEventListener,
        GridViewAdapter_AddSupplement.onDataSendEventListener,
        ListViewAdapter_Receipt.onReceiptSendEventListener,
        ListViewAdapter_CheckedSupplement.onReceiptSendEventListener,
        Fragment_receipt.onSomeEventListener,
        Fragment_CheckSupplement.onSomeEventListener,
        PlanSalleAdapter_Change.onDataSendEventListener {

    private boolean mBound = false;
    private ServiceLuncher s;
    private Toolbar toolbar;
    private TextView total_TTC;
    private String waiter_name;
    private ArrayList<PostData_Bon2> FinalItemList;
    private ArrayList<String> DATA_ARRAY_TABLE;
    private ArrayList<String> _DATA_ARRAY;
    private ArrayList<PostData_Bon2> ReceivedListSupplement;
    private Integer [] datareturned = new Integer[2];
    private DATABASE contoller;
    private TextView num_bon;
    private TextView date;
    private TextView num_table;

    private TextView show_progress;
    private Receiver receiver;
    private IntentFilter filter_FAMILLE;
    private IntentFilter filter_ORDERS;
    private IntentFilter filter_NOT_ORDERS;
    public static Bundle myBundle = new Bundle();
    private Button comboView;
    private Handler handler;
    private Thread thread;
    private Boolean stat_execution = true;
    private ThreeBounce mThreeBounceDrawable;
    public Pos pos;
    private List<FoodsBean> foodsBean;
    private String TICKET_MODEL_TICKETS_PREFRS = "CONFIG_TICKET_PRINTER";
    private String _ServerPrinter;
    private String _PortPrinter = "9100";
    private ArrayList<PostData_Imps> list_imprements;

    ///////////////////////////////////////////////
    private SheetLayout mSheetLayout;
    private FloatingActionButton mFab;                // Declaration variables of Sheet Bottom and fab buttom
    private static final int REQUEST_CODE = 1;
    ///////////////////////////////////////////////

    private String PREFS_PARAMS_INFO = "PARAMS_INFO";
    private Boolean _mVibrate;
    private Boolean _mSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_order);

        button_comboview();  //Create a ComboView Button // (Send To kitchen)
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.restopro_header);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mSheetLayout = (SheetLayout) findViewById(R.id.bottom_sheet);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                mSheetLayout.expandFab();
            }
        });
        ButterKnife.bind(this);

        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);

        Fragment fragment = null;
        fragment = new Fragment_menu();
        Lunch_fragment(fragment);


        mFab.hide();
        // In your application class
        contoller = new DATABASE(this);
        FinalItemList = new ArrayList<>();
        ReceivedListSupplement = new ArrayList<>();

    }

    private void vibrate(){
        if(_mVibrate) {
            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(100);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        num_bon = (TextView) findViewById(R.id.num_bon1);
        date = (TextView) findViewById(R.id.date1);
        num_table = (TextView) findViewById(R.id.name_table1);
        total_TTC = (TextView) findViewById(R.id.total);

        Bundle boundle = getIntent().getExtras();
        DATA_ARRAY_TABLE = new ArrayList<>();
        if (boundle != null) {
            DATA_ARRAY_TABLE = boundle.getStringArrayList("INFOS_TABLE");
            num_bon.setText(DATA_ARRAY_TABLE.get(0));
            //===============// Set Current Date //===================//
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat current_date = new SimpleDateFormat("EEE MMM d, yyyy");
            date.setText(current_date.format(cal.getTime()));
            //========================================================//
            num_table.setText("Table " + DATA_ARRAY_TABLE.get(1));
            total_TTC.setText(new DecimalFormat("##,##0.00").format(Double.parseDouble(DATA_ARRAY_TABLE.get(2).replace(",","."))) + " DA");

            waiter_name =  DATA_ARRAY_TABLE.get(3);
        }

        Fragment fragment = null;
        fragment = new Fragment_receipt();
        if (fragment != null) {
            Bundle args = new Bundle();
            args.putString("num_bon", num_bon.getText().toString());
            fragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.listview_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
        Intent intent = new Intent(this, ServiceLuncher.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

       // SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
       // _ServerPrinter = prefs.getString("ip", "192.168.1.171");
       // _PortPrinter = prefs.getString("port", "9100");
    }

    @Override
    protected void onStop() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    private  String getTotal(){
        String TTC = total_TTC.getText().toString();
        int index = TTC.indexOf(" ");
        TTC = TTC.substring(0, index);
        return  TTC;
    }
    private void Lunch_fragment(Fragment fragment) {

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container1, fragment).commit();
            fragment = null;
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();

        }
    }

    @Override
    public void onFabAnimationEnd() {
        Fragment fragment = null;
        fragment = new Fragment_menu();
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container1, fragment).commit();
            fragment = null;
        }
        mSheetLayout.contractFab();
        mFab.hide();
    }

    @Override
    public void someEvent(String received, String value) {
        if (received.equals("FRAGMENT_MENU")) {
            mFab.show();
        }else if(received.equals("FROM_RECEIPT_ADAPTER")){
            String  s = value.toString();
            s = s.replace(" ", "");
            double SalePotential = 0.00;
            try
            {
                SalePotential = Double.parseDouble(s);
            }catch(NumberFormatException e)
            {
                e.printStackTrace();
            }
            total_TTC.setText(new DecimalFormat("##,##0.00").format(SalePotential) + " DA");
        }else if(received.equals("FROM_FRAGMENT_CHECK_SUPPELEMENT")){
            final SpotsDialog dialogmessage = new SpotsDialog(Order_Activity.this, "Affectation de suppelement ...!");
            handler = new Handler(){
                public void handleMessage(Message msg) {
                    try{
                        //=====================
                        switch(msg.what) {
                            case 0:
                                dialogmessage.show();
                                break;
                            case 1:
                                dialogmessage.dismiss();
                                final Fragment_receipt fragment = (Fragment_receipt) getSupportFragmentManager().findFragmentById(R.id.listview_container);
                                fragment.AddSupplementToDataListView(ReceivedListSupplement);
                                ReceivedListSupplement.clear();

                                break;
                            case 2:
                                dialogmessage.dismiss();
                                Crouton.showText(Order_Activity.this, "Probleme de connexion! vous ne pouvez pas ajouter le suppelement !", Style.ALERT);
                                break;
                            case 3:
                                dialogmessage.dismiss();
                                break;
                            case 4:
                                dialogmessage.dismiss();
                                new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                        .show();
                                break;
                        }

                    }catch(Exception ex){

                    }
                }
            };

            comunication(3);
        }
    }

    @Override
    public void DataSendEvent(String received, final PostData_Bon2 _receipt) {
        if (received.equals("FROM_SUB_MENU_ADAPTER")) {
            if(_mSound){
                play_sound_add();
            }
            vibrate();
            final SpotsDialog dialogmessage = new SpotsDialog(Order_Activity.this, "Affectation de produit ...!");
            final Fragment_receipt fragment = (Fragment_receipt) getSupportFragmentManager().findFragmentById(R.id.listview_container);
            _receipt.Num_bon_bon2 = num_bon.getText().toString();
            //==============
            handler = new Handler(){
                public void handleMessage(Message msg) {
                    try{
                        //=====================
                        switch(msg.what) {
                            case 0:
                                dialogmessage.show();
                                break;
                            case 1:
                                dialogmessage.dismiss();
                                ArrayList<PostData_Bon2> list_supp;
                                _receipt.Recordid2 = String.valueOf(datareturned[1]);
                                _receipt.Bon2id = String.valueOf(datareturned[1]);
                                _receipt.Waiter = waiter_name;
                                fragment.updateDataListView(_receipt);
                                list_supp = contoller.get_row_supplement_from_database(_receipt.Code);
                                if(list_supp.size() > 0){
                                    //================= Open the checkable DialogFragment ========
                                    android.app.FragmentManager fm = getFragmentManager();
                                    DialogFragment dialog = new Fragment_CheckSupplement(); // creating new object
                                    Bundle args = new Bundle();
                                    args.putString("CODE", _receipt.Code);
                                    args.putString("RECORDID2", _receipt.Recordid2);
                                    args.putString("NUM_BON", num_bon.getText().toString());
                                    dialog.setArguments(args);
                                    //dialog.getDialog().setCanceledOnTouchOutside(false);
                                    dialog.show(fm, "dialog");
                                }

                                break;
                            case 2:
                                dialogmessage.dismiss();
                                Crouton.showText(Order_Activity.this, "Probleme de connexion! vous ne pouvez pas ajouter le produit !", Style.ALERT);
                                break;
                            case 3:
                                dialogmessage.dismiss();
                                break;
                            case 4:
                                dialogmessage.dismiss();
                                new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                        .show();
                                break;
                        }

                    }catch(Exception ex){

                    }
                }
            };

            comunication(2);

        } else if (received.equals("FROM_CHANGE_TABLE_ADAPTER")) {
            num_table.setText("Table " + _receipt.Num_bon_bon2);

        }else if(received.equals("FROM_ADD_SUPPELEMENT_ADAPTER")){
            final SpotsDialog dialogmessage = new SpotsDialog(Order_Activity.this, "Affectation de suppelement ...! ");
            handler = new Handler(){
                public void handleMessage(Message msg) {
                    try{
                        //=====================
                        switch(msg.what) {
                            case 0:
                                dialogmessage.show();
                                break;
                            case 1:
                                dialogmessage.dismiss();
                                final Fragment_receipt fragment = (Fragment_receipt) getSupportFragmentManager().findFragmentById(R.id.listview_container);
                                _receipt.Bon2id = String.valueOf(datareturned[1]);
                                fragment.updateDataListViewInPosition(_receipt);

                                break;
                            case 2:
                                dialogmessage.dismiss();
                                Crouton.showText(Order_Activity.this, "Probleme de connexion! vous ne pouvez pas ajouter le suppelement!", Style.ALERT);
                                break;
                            case 3:
                                dialogmessage.dismiss();
                                break;
                            case 4:
                                dialogmessage.dismiss();
                                new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                        .show();
                                break;
                        }

                    }catch(Exception ex){

                    }
                }
            };

            comunication(4);
        }
    }

    private void play_sound_add(){
        final MediaPlayer mp = new MediaPlayer();
        if(mp.isPlaying())
        {
            mp.stop();
        }

        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd("sounds/FuzzyBeep.mp3");
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.change_table) {
            // Option to change table to another table
            ShowChangeTableFragmentDialog();
        }else if(id == R.id.add_supplement){
            ShowAddSupplementFragmentDialog();
        }
        else if (id == android.R.id.home) {
            Fragment currentFragment = getSupportFragmentManager().
                    findFragmentById(R.id.frame_container1);
            if (currentFragment instanceof Fragment_sub_menu) {
                Fragment fragment = null;
                fragment = new Fragment_menu();
                Lunch_fragment(fragment);
                mFab.hide();
            }else{
                vibrate();
                overridePendingTransition(R.animator.slide_out_right, R.animator.slide_in_left);
                finish(); // close this activity and return to preview activity (if there is any)
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowChangeTableFragmentDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new TablesFragment_Change(); // creating new object
        Bundle args = new Bundle();
        args.putString("num_bon", num_bon.getText().toString());
        String c_numT = num_table.getText().toString();
        c_numT = c_numT.substring(c_numT.indexOf(" ") + 1, c_numT.length());
        args.putString("num_table", c_numT);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");
    }

    private void ShowAddSupplementFragmentDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new Fragment_AddSupplement(); // creating new object
        Bundle args = new Bundle();
        args.putString("num_bon", num_bon.getText().toString());
        String c_numT = num_table.getText().toString();
        c_numT = c_numT.substring(c_numT.indexOf(" ") + 1, c_numT.length());
        args.putString("num_table", c_numT);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");
    }

    private void button_comboview() {
        comboView = (Button) findViewById(R.id.comboview);
        show_progress = (TextView) findViewById(R.id.show_progress);


        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        DoubleBounce doubleBounce = new DoubleBounce();
        doubleBounce.setBounds(0, 0, 100, 100);
        doubleBounce.setColor(R.color.color_prix_gridview);
        progressBar.setIndeterminateDrawable(doubleBounce);

        mThreeBounceDrawable = new ThreeBounce();
        mThreeBounceDrawable.setBounds(0, 0, 100, 100);
        //noinspection deprecation
        mThreeBounceDrawable.setColor(getResources().getColor(R.color.colorAccent));
        show_progress.setCompoundDrawables(mThreeBounceDrawable, null, null, null);


        comboView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                if(FinalItemList.size() > 0 ){
                    handler = new Handler(){
                        public void handleMessage(Message msg) {
                            try{
                                //=====================
                                switch(msg.what) {
                                    case 0:
                                        mThreeBounceDrawable.start();
                                        comboView.setVisibility(View.GONE);
                                        show_progress.setVisibility(View.VISIBLE);
                                        break;
                                    case 1:
                                        comboView.setVisibility(View.VISIBLE);
                                        show_progress.setVisibility(View.GONE);
                                        mThreeBounceDrawable.stop();
                                        if(!(Order_Activity.this).isFinishing()) {
                                            new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Réussi!")
                                                    .setContentText("la commande a été envoyé vers la caisse !")
                                                    .show();
                                            print_receipt();
                                        }
                                        break;
                                    case 2:
                                        show_progress.setVisibility(View.GONE);
                                        comboView.setVisibility(View.VISIBLE);
                                        mThreeBounceDrawable.stop();
                                        Crouton.showText(Order_Activity.this, "Probleme de connexion! vous ne pouvez pas ajouter cette commande !", Style.ALERT);
                                        break;
                                    case 3:
                                        show_progress.setVisibility(View.GONE);
                                        comboView.setVisibility(View.VISIBLE);
                                        mThreeBounceDrawable.stop();
                                        break;
                                    case 4:
                                        show_progress.setVisibility(View.GONE);
                                        comboView.setVisibility(View.VISIBLE);
                                        mThreeBounceDrawable.stop();
                                        new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                                .show();
                                        break;
                                }

                            }catch(Exception ex){

                            }
                        }
                    };

                    comunication(0);
                }else{
                    new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Info...")
                            .setContentText("Vous devez ajouter au moins un produit !")
                            .show();
                }
            }
        });

    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }


    @Override
    public void ReceiptSendEvent(String received, ArrayList<PostData_Bon2> items_receip) {
        if (received.equals("FROM_RECEIPT_ADAPTER")) {
            FinalItemList = items_receip;
            if(FinalItemList.size() > 0){
                FinalItemList.get(0).Num_bon_bon2 = num_bon.getText().toString();
                Double Total = 0.00;
                try {
                    for (int i = 0; i < items_receip.size(); i++) {
                        if(items_receip.get(i).Price != null) {
                            Total = Total + ((Double.parseDouble(items_receip.get(i).Price)) *
                                    (Double.parseDouble(items_receip.get(i).Quantity)));
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.e("TRACKKK", "ERREUR CAST TOTAL (Order_Activity) WHEN TRYING TO CALCULATE " +
                            "TOTAL ITEMS : " + e.getMessage());
                }catch (NullPointerException nullex){
                    Log.e("TRACKKK", "ERREUR PARSE DOUBLE (Order_Activity) WHEN TRYING TO CALCULATE" +
                            " TOTAL ITEMS : " + nullex.getMessage());
                }
                total_TTC.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(Total)) + " DA");
            }


        }else if(received.equals("FROM_CHECK_SUPPLEMENT_ADAPTER")) {
            ReceivedListSupplement.clear();
            for(int i = 0 ; i < items_receip.size();i++){
                if(items_receip.get(i).Checked){
                    ReceivedListSupplement.add(items_receip.get(i));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new Receiver();
        filter_FAMILLE = new IntentFilter("FAMILIES_LOADED");
        registerReceiver(receiver, filter_FAMILLE);
        filter_ORDERS = new IntentFilter("ORDERS_SENT");
        registerReceiver(receiver, filter_ORDERS);
        filter_NOT_ORDERS = new IntentFilter("ORDERS_NOT_SENT");
        registerReceiver(receiver, filter_NOT_ORDERS);

        SharedPreferences prefs2 = getSharedPreferences(PREFS_PARAMS_INFO, MODE_PRIVATE);
        _mVibrate = prefs2.getBoolean("VIBRATION", true);
        _mSound = prefs2.getBoolean("SOUND", true);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            ServiceLuncher.MyBinder b = (ServiceLuncher.MyBinder) binder;
            s = b.getService();
            mBound = true;
            s.setContext(getBaseContext());
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
            mBound = false;
        }
    };


    @Override
    public void DataSendEvent(String received, final ArrayList<String> ARRAY_DATA) {
        if(received.equals("FROM_CHANGE_TABLE_ADAPTER")){
            final SpotsDialog dialogmessage = new SpotsDialog(Order_Activity.this, "Changement de table en cours ...");

            handler = new Handler(){
                public void handleMessage(Message msg) {
                    switch(msg.what) {
                        case 0:
                            dialogmessage.show();
                            break;
                        case 1:
                            num_table.setText("Table "+ARRAY_DATA.get(1));
                            dialogmessage.dismiss();
                            new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Réussit!")
                                    .setContentText("La table est bien changé !")
                                    .show();
                            break;
                        case 2:
                            dialogmessage.dismiss();
                            Crouton.showText(Order_Activity.this, "Probleme de connexion! vous ne pouvez pas changer cette table !", Style.ALERT);

                            break;
                        case 3:
                            dialogmessage.dismiss();
                    }
                }
            };
            //==========

            _DATA_ARRAY = new ArrayList<>();
            _DATA_ARRAY = ARRAY_DATA;
            comunication(1);
        }
    }

    //==================== Class Receiving events from ServiceLuncher ==============================
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("FAMILIES_LOADED")) {

                Fragment currentFragment = getSupportFragmentManager().
                        findFragmentById(R.id.frame_container1);

                if (currentFragment instanceof Fragment_menu) {
                    Fragment_menu fragment2 = (Fragment_menu) getSupportFragmentManager().
                            findFragmentById(R.id.frame_container1);

                    fragment2.RefreshFragment_Menu(s.getFamiliesList());
                } else if (currentFragment instanceof Fragment_sub_menu) {
                    Fragment_sub_menu fragment2 = (Fragment_sub_menu) getSupportFragmentManager().
                            findFragmentById(R.id.frame_container1);

                    fragment2.RefreshFragment_Sub_Menu(s.getMenuList());

                }
            }
        } //fin method onReceive
    }//fin class receiver

    public void comunication(final Integer index){

        thread = new Thread(){
            public void run(){
                try {
                    handler.sendEmptyMessage(0);
                    int flag;
                    if(index == 0 ){
                       // stat_execution =   s.SendToKitchen(getTotal(),FinalItemList);
                        flag = s.SendToKitchen(getTotal(),FinalItemList);
                        if(flag == 0){
                            //failed
                            handler.sendEmptyMessage(3);
                        }else if(flag == 1){
                            //success
                            handler.sendEmptyMessage(1);
                        }else if(flag == 2){
                            //problem connection, data saved in temp
                            handler.sendEmptyMessage(2);
                        }else if(flag == 3){
                            //problem sql
                            handler.sendEmptyMessage(4);
                        }

                    }else if(index == 1){
                        stat_execution = s.ChangeTable(_DATA_ARRAY);
                        if(stat_execution == true){
                            handler.sendEmptyMessage(1);
                        }else if(stat_execution == false){
                            handler.sendEmptyMessage(2);
                        }
                    }else if(index == 2 ){
                        datareturned = s.ReserveRecordid2();
                        if(datareturned [0] == 0){
                            //failed
                            handler.sendEmptyMessage(3);
                        }else if(datareturned[0] == 1){
                            //success recordid2 returned ok
                            handler.sendEmptyMessage(1);
                        }else if(datareturned[0] == 2){
                            //problem connection with server
                            handler.sendEmptyMessage(2);
                        }else if(datareturned[0] == 3){
                            //problem in sql
                            handler.sendEmptyMessage(4);
                        }
                        handler.sendEmptyMessage(3);
                    }else if(index == 3 ){
                        for(int i = 0; i< ReceivedListSupplement.size();i++){
                            datareturned = s.ReserveRecordid2();
                            if(datareturned[0] == 1){
                                ReceivedListSupplement.get(i).Bon2id = String.valueOf(datareturned[1]);
                            }else{
                                break;
                            }
                        }
                        if(datareturned [0] == 0){
                            //failed
                            handler.sendEmptyMessage(3);
                        }else if(datareturned[0] == 1){
                            //success recordid2 returned ok
                            handler.sendEmptyMessage(1);
                        }else if(datareturned[0] == 2){
                            //problem connection with server
                            handler.sendEmptyMessage(2);
                        }else if(datareturned[0] == 3){
                            //problem in sql
                            handler.sendEmptyMessage(4);
                        }
                        handler.sendEmptyMessage(3);
                    }else if(index == 4 ){
                          datareturned = s.ReserveRecordid2();
                        if(datareturned [0] == 0){
                            //failed
                            handler.sendEmptyMessage(3);
                        }else if(datareturned[0] == 1){
                            //success recordid2 returned ok
                            handler.sendEmptyMessage(1);
                        }else if(datareturned[0] == 2){
                            //problem connection with server
                            handler.sendEmptyMessage(2);
                        }else if(datareturned[0] == 3){
                            //problem in sql
                            handler.sendEmptyMessage(4);
                        }
                        handler.sendEmptyMessage(3);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            };
        };

        thread.start();
    }

    public void print_receipt(){

        handler = new Handler(){
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                    break;
                    case 2:
                        if(!(Order_Activity.this).isFinishing())
                        {
                            new SweetAlertDialog(Order_Activity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention...")
                                .setContentText("Probleme de connexion avec L'impriment ! Le ticket n'a pas été imprimer au niveau de la cuisine !")
                                .show();
                        }
                    break;
                }
            }
        };

        ///////////////////// traitement of all receipt with all printer ///////////////////////////
        // Load all printer list from database
        list_imprements = contoller.select_imprimentes_from_sqlite_database();
        SharedPreferences prefs = getSharedPreferences(TICKET_MODEL_TICKETS_PREFRS, MODE_PRIVATE);
        final String model_ticket = prefs.getString("MODEL_TICKET", "Petit model");
        //final HashMap<String, ArrayList<FoodsBean>> list_all_reciept = new HashMap<>();
        final ArrayList<FoodsBean> temp = new  ArrayList<>();
        initData();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());
        for(int i = 0;i <list_imprements.size();i++ ){
            temp.clear();
            for(int j = 0;j< foodsBean.size(); j++){
                if(foodsBean.get(j).getDes_imp() != null && foodsBean.get(j).getCode() != null){

                    if(foodsBean.get(j).getDes_imp().equals(list_imprements.get(i).des_imp)){
                        temp.add(foodsBean.get(j));
                        for(int s = j+1; s<foodsBean.size();s++){
                            if(foodsBean.get(s).getCode_s()!= null){
                                temp.add(foodsBean.get(s));
                            }else {
                                break;
                            }
                        }
                    }
                }
            }

            _ServerPrinter = list_imprements.get(i).ip_imp;
            //list_all_reciept.put(list_imprements.get(i).des_imp, temp);
            /////////// lunch a print ticket for each existing printer on the list /////////////////
            if(temp.size() > 0 && _ServerPrinter != null){
                new Thread() {
                    public void run() {
                        if(model_ticket.equals("Petit model")){
                            lunch_printer_model_small(_ServerPrinter, temp, currentDateandTime);
                        }else if(model_ticket.equals("Large model")){
                            lunch_printer_model_large(_ServerPrinter, temp, currentDateandTime);
                        }else{
                            Toast.makeText(Order_Activity.this, "Aucun model ticket selectionné", Toast.LENGTH_LONG).show();
                        }

                    }
                }.start();
            }

            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }

        handler.sendEmptyMessage(1);
    }

    private void lunch_printer_model_large(String ip, ArrayList<FoodsBean> reciept, String date_time){
        try {
            pos = new Pos(ip, Integer.parseInt(_PortPrinter), "GBK");
            //   pos.initPos();
            pos.beep();

            //   Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.resto);
            //   pos.printImage(image);

            pos.bold(true);
            pos.printTabSpace(1);
            //pos.printWordSpace(1);

            if(num_bon == null){
                pos.printText("BON DE COMMANDE   N : 000000");
            }else{
                pos.printText("BON DE COMMANDE   N : "+ num_bon.getText());
            }

            pos.bold(false);
            pos.printLocation(0);
            pos.printTextNewLine("-----------------------------------------------");
            pos.bold(false);
            if(num_table == null){
                pos.printTextNewLine("TABLE : UNKNOWN");
            }else{
                pos.printTextNewLine("TABLE : " + num_table.getText());
            }
            if(foodsBean.get(0).getWaiter() == null){
                pos.printTextNewLine("NOM SERVEUR : UNKNOWN");
            }else{
                pos.printTextNewLine("NOM SERVEUR : " + foodsBean.get(0).getWaiter());
            }

            pos.printTextNewLine("DATE : "+ date_time);
            pos.printLine(2);

            pos.printText("PRODUIT");
            pos.printLocation(99, 1);
            pos.printWordSpace(7);
            pos.printText("QTE");
            pos.printTextNewLine("-----------------------------------------------");


            for (FoodsBean foods : reciept) {
                if(foods.getCode() != null){
                    if((foods.getEmporter() == null) || (foods.getEmporter().equals("0"))){
                        pos.printTextNewLine(foods.getName());
                    }else{
                        pos.printTextNewLine(foods.getName() + (" (Emporter)"));
                    }
                    pos.printLocation(99, 1);
                    pos.printWordSpace(7);
                    pos.printText(foods.getQuantity());

                    //
                }else{
                    pos.printTextNewLine("--- "+foods.getName());
                    pos.printLocation(99, 1);
                    pos.printWordSpace(7);
                    pos.printText(foods.getQuantity());
                }

            }

            pos.printTextNewLine("-----------------------------------------------");
            pos.printLocation(1);
            pos.printLine(2);
            //打印二维码
            //  pos.qrCode("http://www.safesoft-dz.com/");

            //切纸
            pos.feedAndCut();
           // pos.cutPaper2();
            pos.beep();

            pos.closeIOAndSocket();
            pos = null;

            handler.sendEmptyMessage(1);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(2);
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(2);
        }
    }


    private void lunch_printer_model_small(String ip, ArrayList<FoodsBean> reciept, String date_time){
        try {
            pos = new Pos(ip, Integer.parseInt(_PortPrinter), "GBK");
            //   pos.initPos();
            pos.beep();

            //   Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.resto);
            //   pos.printImage(image);

            pos.bold(true);
            pos.printTabSpace(1);
            //pos.printWordSpace(1);

            if(num_bon == null){
                pos.printText("BON DE COMMANDE   N : 000000");
            }else{
                pos.printText("BON DE COMMANDE   N : "+ num_bon.getText());
            }

            pos.bold(false);
            pos.printLocation(0);
            pos.printTextNewLine("-------------------------------------------");
            pos.bold(false);
            if(num_table == null){
                pos.printTextNewLine("TABLE : UNKNOWN");
            }else{
                pos.printTextNewLine("TABLE : " + num_table.getText());
            }
            if(foodsBean.get(0).getWaiter() == null){
                pos.printTextNewLine("NOM SERVEUR : UNKNOWN");
            }else{
                pos.printTextNewLine("NOM SERVEUR : " + foodsBean.get(0).getWaiter());
            }

            pos.printTextNewLine("DATE : "+ date_time);
            pos.printLine(2);

            pos.printText("PRODUIT");
            pos.printLocation(99, 1);
            pos.printWordSpace(6);
            pos.printText("QTE");
            pos.printTextNewLine("-------------------------------------------");


            for (FoodsBean foods : reciept) {
                if(foods.getCode() != null){
                    if((foods.getEmporter() == null) || (foods.getEmporter().equals("0"))){
                        pos.printTextNewLine(foods.getName());
                    }else{
                        pos.printTextNewLine(foods.getName() + (" (Emporter)"));
                    }
                    pos.printLocation(99, 1);
                    pos.printWordSpace(6);
                    pos.printText(foods.getQuantity());

                    //
                }else{
                    pos.printTextNewLine("--- "+foods.getName());
                    pos.printLocation(99, 1);
                    pos.printWordSpace(6);
                    pos.printText(foods.getQuantity());
                }

            }

            pos.printTextNewLine("-------------------------------------------");

            pos.printLocation(1);
            pos.printLine(2);
            //打印二维码
            //  pos.qrCode("http://www.safesoft-dz.com/");

            //切纸
            pos.feedAndCut();
            // pos.cutPaper2();
            pos.beep();

            pos.closeIOAndSocket();
            pos = null;

            handler.sendEmptyMessage(1);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(2);
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(2);
        }
    }
    private void initData() {
        foodsBean = new ArrayList<>();

        for (int i = 0; i < FinalItemList.size(); i++) {
            FoodsBean fb = new FoodsBean();
            if(FinalItemList.get(i).Imp_com == null){
                fb.setCode(FinalItemList.get(i).Code);
                fb.setCode_s(FinalItemList.get(i).Code_s);
                fb.setName(FinalItemList.get(i).Produit);
                fb.setPrice(FinalItemList.get(i).Price);
                fb.setDes_imp(FinalItemList.get(i).Des_imp);
                fb.setQuantity(FinalItemList.get(i).Quantity);
                fb.setEmporter(FinalItemList.get(i).Emporter);
                fb.setWaiter(FinalItemList.get(i).Waiter);
                FinalItemList.get(i).Imp_com = "1";
                foodsBean.add(fb);
            }
        }
    }

}//fin Class main
