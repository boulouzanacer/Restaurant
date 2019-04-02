package com.safesoft.uk2015.restopro.Activities;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_Orders;
import com.safesoft.uk2015.restopro.Adapters.PlanSalleAdapter;
import com.safesoft.uk2015.restopro.Fragment.MenuFragment;
import com.safesoft.uk2015.restopro.Fragment.OrdersFragment;
import com.safesoft.uk2015.restopro.Fragment.PrintingFragment;
import com.safesoft.uk2015.restopro.Fragment.SettingFragment;
import com.safesoft.uk2015.restopro.Fragment.StuffFragment;
import com.safesoft.uk2015.restopro.Fragment.TablesFragment;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.Pos;
import com.safesoft.uk2015.restopro.PostData.FoodsBean;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.safesoft.uk2015.restopro.Service.ServiceLuncher;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Main_SafeSoft extends AppCompatActivity implements
        PlanSalleAdapter.onDataSendEventListener,
        StuffFragment.onSomeEventListener,
        GridViewAdapter_Orders.onSomeEventListener{

    private static final String TAG=Main_SafeSoft.class.getName();
    private static ArrayList<Activity> activities=new ArrayList<Activity>();
    protected TablesFragment tablesFragment;
    protected OrdersFragment orderFragment;
    protected MenuFragment menufragment;
    protected StuffFragment stufffragment;
    protected PrintingFragment printingfragment;
    protected SettingFragment settingFragment;
    protected LinearLayout switchFragmentLinearLayout;
    private boolean started;
    private Toolbar toolbar;
    private Activity activity;
    private int dips50;
    private DATABASE controller;
    //************************
    private Fragment fragment = null;
    private TextView tablesTextView;
    private TextView  menuTextView;
    //*************************
    private ServiceLuncher s;
    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler;
    private Thread thread;
    private String [] RED_DATA_CHTABLE = new String[2];
    private boolean mBound = false;
    public String WAITER = "UNKOWNN";
    private CircleImageView circle_image_usn;
    private String dir;
    private String PREFS_PARAMS_INFO = "PARAMS_INFO";
    private Boolean _mVibrate = true;
    private Boolean _mSound = true;
    public Pos pos;
    private List<FoodsBean> foodsBean;
    private String _num_bon;
    private String IP_PRINTER;
    private String TICKET_IP_PRINTER_PREFRS = "CONFIG_TICKET_PRINTER";
    Receiver receiver;
    IntentFilter filter_PLAN_SALLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main__safe_soft);

        activities.add(this);
        controller = new DATABASE(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.main__safe_soft);//changed
        //toolbar2 menu items CallBack listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                if(arg0.getItemId() == R.id.logout){
                    Intent launchNewIntent = new Intent(Main_SafeSoft.this,LoginActivity.class);
                    startActivityForResult(launchNewIntent, 0);
                    overridePendingTransition(R.animator.slide_out_right, R.animator.slide_in_left);
                    finishAll();
                }
                return false;
            }
        });

        TextView username = (TextView) findViewById(R.id.username);
        circle_image_usn = (CircleImageView) findViewById(R.id.profile_image);
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/waiter_picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        Bundle boundle = getIntent().getExtras();
        if(boundle != null){
            WAITER = boundle.getString("WAITER");
            username.setText(WAITER);
            File imgFile = new  File(dir+WAITER+".jpg");
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                circle_image_usn.setImageBitmap(myBitmap);
            }
        }


        activity = this;

           this.dips50 = (int) TypedValue.applyDimension(1, 50.0f, getResources().getDisplayMetrics());
           this.switchFragmentLinearLayout = (LinearLayout) findViewById(R.id.switchFragmentLinearLayout);
            tablesTextView  = (TextView) findViewById(R.id.tablesTextView);
            TextView ordersTextView  = (TextView) findViewById(R.id.ordersTextView);
            menuTextView = (TextView) findViewById(R.id.menuTextView);
            TextView staffTextView = (TextView) findViewById(R.id.staffTextView);
            TextView printingTextView = (TextView) findViewById(R.id.printingTextView);
            TextView settingTextView = (TextView) findViewById(R.id.settingTextView);


        this.started = true;
        switchFragment(tablesTextView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void switchFragment(View selectedView) {
        if (this.started) {
            int i;
            AppBarLayout.LayoutParams params;
            int selectedViewId = selectedView.getId();
            if (selectedViewId == R.id.tablesTextView) {
                vibrate();
                if (this.tablesFragment == null) {
                    this.tablesFragment = new TablesFragment();
                }
                fragment = this.tablesFragment;
                try {
                    s.lunch_load_tables();
                }catch (Exception e){

                }
            }else if (selectedViewId == R.id.ordersTextView) {
                vibrate();

                //================
                final SpotsDialog dialogmessage = new SpotsDialog(Main_SafeSoft.this, " Chargement des commandes ... ");
                handler = new Handler(){
                    public void handleMessage(Message msg) {
                        switch(msg.what) {
                            case 0:
                                dialogmessage.show();
                                break;
                            case 1:
                                dialogmessage.dismiss();
                                //Start Class Order_Activity
                                //====================================================
                                if (Main_SafeSoft.this.orderFragment == null) {
                                    Main_SafeSoft.this.orderFragment = new OrdersFragment();
                                }
                                fragment = Main_SafeSoft.this.orderFragment;
                                if (!(fragment == null)) {
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.rightFrameLayout, fragment);
                                    fragmentTransaction.commit();
                                }
                                break;
                            case 2:
                                dialogmessage.dismiss();
                                new SweetAlertDialog(Main_SafeSoft.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Il y'a un problème de connexion avec le serveur !")
                                        .show();
                                break;
                            case 3:
                                dialogmessage.dismiss();
                                break;
                            case 4:
                                dialogmessage.dismiss();
                                new SweetAlertDialog(Main_SafeSoft.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                        .show();
                                break;
                        }
                    }
                };

                comunication3();
                //=================
            }else if (selectedViewId == R.id.menuTextView) {
                vibrate();
                if (this.menufragment == null) {
                    this.menufragment = new MenuFragment();
            }
                fragment = this.menufragment;
            }else if (selectedViewId == R.id.staffTextView) {
                vibrate();
                if (this.stufffragment == null) {
                    this.stufffragment = new StuffFragment();
                }
                fragment = this.stufffragment;
            }else if (selectedViewId == R.id.printingTextView) {
                vibrate();
                if (this.printingfragment == null) {
                    this.printingfragment = new PrintingFragment();
                }
                fragment = this.printingfragment;
            }else if (selectedViewId == R.id.settingTextView) {
                vibrate();
                if (this.settingFragment == null) {
                    this.settingFragment = new SettingFragment();
                }
                fragment = this.settingFragment;
            }

            if (!(fragment == null)) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rightFrameLayout, fragment);
                fragmentTransaction.commit();

                switchFragmentLinearLayout = (LinearLayout) findViewById(R.id.switchFragmentLinearLayout);
                for (i = 0; i < switchFragmentLinearLayout.getChildCount(); i++) {
                    if (switchFragmentLinearLayout.getChildAt(i) instanceof TextView) {
                        TextView textView = (TextView) switchFragmentLinearLayout.getChildAt(i);
                        if (textView.getId() == selectedView.getId()) {
                            textView.setBackgroundResource(R.color.blue_restopro);
                            textView.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            textView.setBackgroundResource(R.color.white);
                            textView.setTextColor(getResources().getColor(R.color.gray_primary_text));
                        }
                    }
                }
            }

            int childrenCount = this.switchFragmentLinearLayout.getChildCount();
            for (i = 0; i < childrenCount; i++) {
                View view = this.switchFragmentLinearLayout.getChildAt(i);
                if (view instanceof TextView) {
                 //   if (i == childrenCount - 1) {
                //        view.setBackgroundResource(R.drawable.background_card_white);
                 //       view.setPadding(this.dips50 / 5, this.dips50 / 5, this.dips50 / 5, this.dips50 / 5);
                  //  } else {
                        view.setBackgroundResource(R.color.white);
                //    }
                    params = new AppBarLayout.LayoutParams(-1, this.dips50);
                    params.setMargins(0, 0, this.dips50 / 5, 0);
                    view.setLayoutParams(params);
                }
            }
            params = new AppBarLayout.LayoutParams(-1, this.dips50);
            params.setMargins(0, 0, 0, 0);
            selectedView.setLayoutParams(params);
            selectedView.setBackgroundResource(R.drawable.triangle_blue);
            selectedView.setPadding(this.dips50 / 5, this.dips50 / 5, this.dips50 / 5, this.dips50 / 5);
            this.tablesFragment = null;
        }
    }

    private void vibrate(){
        if(_mVibrate){
            Vibrator vb = (Vibrator)  getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1200) { // from loadpicture activity
            switchFragment(menuTextView);
        }else if(requestCode == 1100){ // from login activity
            switchFragment(tablesTextView);
        }
    }//onActivityResult

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("TRACKKK","START Main_SafeSoft");
        Intent intent= new Intent(this, ServiceLuncher.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
      //  switchFragment(tablesTextView);
    }

    @Override
    protected void onPause() {
        Log.v("TRACKKK","PAUSE Main_SafeSoft");
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TRACKKK","RESUME Main_SafeSoft");
        receiver = new Receiver();
        filter_PLAN_SALLE = new IntentFilter("PLAN_SALLE_UPDATE");
        registerReceiver(receiver, filter_PLAN_SALLE);
        try {
            s.lunch_load_tables();
        }catch (Exception e){

        }

        SharedPreferences prefs2 = getSharedPreferences(PREFS_PARAMS_INFO, MODE_PRIVATE);
        _mVibrate = prefs2.getBoolean("VIBRATION", true);
        _mSound = prefs2.getBoolean("SOUND", true);
    }

    @Override
    protected void onStop() {
        Log.v("TRACKKK","STOP Main_SafeSoft");
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
       if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Clicker deux fois pour quitter", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
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

        final ArrayList<String> _DATA_TABLE = new ArrayList<>();
        _DATA_TABLE.addAll(ARRAY_DATA);

        if(received.equals("FROM_PLAN_SALLE_ADAPTER_NON_RESERVED")){

            final ArrayList<String> ARRAY_DATA_TABLE = new ArrayList<>();
            final SpotsDialog dialogmessage = new SpotsDialog(Main_SafeSoft.this, "Reservation de table ...");
            handler = new Handler(){
                public void handleMessage(Message msg) {
                    switch(msg.what) {
                        case 0:
                            dialogmessage.show();
                            break;
                        case 1:
                            dialogmessage.dismiss();
                           // LL.setBackgroundResource(R.drawable.selector_reserved_table);
                            ARRAY_DATA_TABLE.add(RED_DATA_CHTABLE[1]);
                            ARRAY_DATA_TABLE.add(_DATA_TABLE.get(1));
                            ARRAY_DATA_TABLE.add("0.00");
                            ARRAY_DATA_TABLE.add(WAITER);
                            //Start Class Order_Activity
                            Intent postviewIntent = new Intent(Main_SafeSoft.this, Order_Activity.class);
                            Bundle boundle = new Bundle();
                            boundle.putStringArrayList("INFOS_TABLE",ARRAY_DATA_TABLE);
                            postviewIntent.putExtras(boundle); //Put
                            Main_SafeSoft.this.startActivity(postviewIntent);
                            (Main_SafeSoft.this).overridePendingTransition(R.animator.slide_out_right, R.animator.slide_in_left);
                            break;
                        case 2:
                            dialogmessage.dismiss();
                            new SweetAlertDialog(Main_SafeSoft.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Il y'a un problème de connexion avec le serveur !")
                                    .show();
                            break;
                        case 3:
                            dialogmessage.dismiss();
                    }
                }
            };
            //==========
           // ARRAY_DATA.set(2, WAITER);
            comunication(_DATA_TABLE);

        }else if(received.equals("FROM_PLAN_SALLE_ADAPTER_RESERVED")){

            final SpotsDialog dialogmessage = new SpotsDialog(Main_SafeSoft.this, "Ouverture de commande ...");
            handler = new Handler(){
                public void handleMessage(Message msg) {
                    switch(msg.what) {
                        case 0:
                            dialogmessage.show();
                            break;
                        case 1:
                            dialogmessage.dismiss();
                            //Start Class Order_Activity
                            //====================================================
                            Intent postviewIntent = new Intent(Main_SafeSoft.this, Order_Activity.class);
                            Bundle boundle = new Bundle();
                            boundle.putStringArrayList("INFOS_TABLE",_DATA_TABLE);
                            postviewIntent.putExtras(boundle); //Put
                            startActivity(postviewIntent);
                            overridePendingTransition(R.animator.slide_out_right, R.animator.slide_in_left);
                            break;
                        case 2:
                            dialogmessage.dismiss();
                            new SweetAlertDialog(Main_SafeSoft.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Il y'a un problème de connexion avec le serveur !")
                                    .show();
                            break;
                        case 3:
                            dialogmessage.dismiss();
                            break;
                        case 4:
                            dialogmessage.dismiss();
                            new SweetAlertDialog(Main_SafeSoft.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                    .show();
                            break;
                    }
                }
            };

            comunication2(_DATA_TABLE.get(0));

        }
    }

    public void comunication(final ArrayList<String> ARRAY_DATA){

        thread = new Thread(){
            public void run(){
                try {
                    handler.sendEmptyMessage(0);
                     RED_DATA_CHTABLE = s.ReserveTable(ARRAY_DATA);
                    if(RED_DATA_CHTABLE[0].equals("YES")){
                        handler.sendEmptyMessage(1);
                    }else if(RED_DATA_CHTABLE[0].equals("NO")){
                        handler.sendEmptyMessage(2);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            };
        };

        thread.start();
    }


    public void comunication2(final String num_bon){

        thread = new Thread(){
            public void run(){
                try {
                    handler.sendEmptyMessage(0);
                    Integer flag = 0 ;
                    flag = s.Load_Bon2_from_server(num_bon);
                    if(flag == 1){
                        handler.sendEmptyMessage(1);
                    }else if(flag == 2){
                        handler.sendEmptyMessage(2);
                    }else if(flag == 3){
                        handler.sendEmptyMessage(4);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            };
        };

        thread.start();
    }


    public void comunication3(){

        thread = new Thread(){
            public void run(){
                try {
                    handler.sendEmptyMessage(0);
                    Integer flag = 1 ;
                    Thread.sleep(2000);
                       flag = s.lunch_load_bon2();
                    if(flag == 1){
                        handler.sendEmptyMessage(1);
                    }else if(flag == 2){
                        handler.sendEmptyMessage(2);
                    }else if(flag == 3){
                        handler.sendEmptyMessage(4);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            };
        };

        thread.start();
    }

    @Override
    public void someEvent(String received, String Value) {
        if(received.equals("FROM_STUFF_FRAGMENT")){
            File imgFile = new  File(dir+WAITER+".jpg");
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                circle_image_usn.setImageBitmap(myBitmap);
            }
        }else if(received.equals("FROM_ORDERS_ADAPTER")){
            _num_bon = Value;
            print_ticket();
        }
    }

    private void print_ticket(){
        SharedPreferences prefs = getSharedPreferences(TICKET_IP_PRINTER_PREFRS, MODE_PRIVATE);
        IP_PRINTER = prefs.getString("IP_PRINTER", "192.168.1.5");
        ArrayList<PostData_Bon2> receipt_list = new ArrayList<>();
        receipt_list.clear();
        String selectQuery = "SELECT " +
                "    Bon2.BON2ID ," +
                "    Bon2.NUM_BON ," +
                "    Bon2.CODE ," +
                "    Bon2.CODE_S," +
                "    Bon2.RECORDID2 ," +
                "    Bon2.PRODUIT ," +
                "    Bon2.QUANTITY ," +
                "    Bon2.TVA ," +
                "    Bon2.PV_TTC ," +
                "    Bon2.MONTANT_HT ," +
                "    Bon2.EMPORTER, " +
                "    Bon2.IMP_COM, " +
                "    Menu.DES_IMP, " +
                "    Tables.NOM_SERVEUR, " +
                "    Tables.TABLE_NUMBER " +
                " FROM Bon2 LEFT JOIN Menu ON (Bon2.CODE == Menu.CODE)" +
                " JOIN Tables ON (Bon2.NUM_BON == Tables.NUM_BON)" +
                " WHERE Bon2.NUM_BON = '" + _num_bon + "' " +
                " GROUP BY " +
                "    Bon2.BON2ID ," +
                "    Bon2.NUM_BON," +
                "    Bon2.CODE," +
                "    Bon2.CODE_S," +
                "    Bon2.RECORDID2," +
                "    Bon2.PRODUIT," +
                "    Bon2.QUANTITY," +
                "    Bon2.TVA ," +
                "    Bon2.PV_TTC," +
                "    Bon2.MONTANT_HT," +
                "    Bon2.EMPORTER, " +
                "    Bon2.IMP_COM, " +
                "    Menu.DES_IMP, " +
                "    Tables.NOM_SERVEUR, " +
                "    Tables.TABLE_NUMBER " +
                "    ORDER BY 2,5,3 DESC";

        receipt_list = controller.get_bon2_from_database(selectQuery);
        if(receipt_list.size() > 0){
            initData(receipt_list);
            new Thread() {
                public void run() {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
                    final String currentDateandTime = sdf.format(new Date());
                    lunch_printer(IP_PRINTER, currentDateandTime);
                }
            }.start();
        }else{
            new SweetAlertDialog(Main_SafeSoft.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("La table n'a pas de commande !")
                    .show();
        }
    }

    //Class Receiving events from ServiceLuncher
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //Toast.makeText(context, intent.getStringExtra("param"),Toast.LENGTH_SHORT).show();
            if (intent.getAction().equals("PLAN_SALLE_UPDATE"))
            {
                Fragment currentFragment =  getFragmentManager().findFragmentById(R.id.rightFrameLayout);
                if (currentFragment instanceof TablesFragment) {
                    TablesFragment fragment2 = (TablesFragment) getFragmentManager().findFragmentById(R.id.rightFrameLayout);
                    fragment2.Refresh_Tables();
                }
                /*else if(currentFragment instanceof OrdersFragment){
                    OrdersFragment fragment2 = (OrdersFragment) getFragmentManager().findFragmentById(R.id.rightFrameLayout);
                    fragment2.Refresh_Orders();
                }*/
            }

        }

    }

    public String getWaiter(){
        return WAITER;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        activities.remove(this);
    }

    public static void finishAll()
    {
        for(Activity activity:activities)
            activity.finish();
    }

    private void lunch_printer(String ip, String date_time){
        try {
            pos = new Pos(ip, Integer.parseInt("9100"), "GBK");
            //   pos.initPos();
            pos.beep();

            //   Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.resto);
            //   pos.printImage(image);

            pos.bold(true);
            pos.printTabSpace(1);
            //pos.printWordSpace(1);

            if(_num_bon == null){
                pos.printText("BON DE COMMANDE   N : 000000");
            }else{
                pos.printText("BON DE COMMANDE   N : "+ _num_bon);
            }

            pos.bold(false);
            pos.printLocation(0);
            pos.printTextNewLine("----------------------------------------------");
            pos.bold(false);
            if(foodsBean.get(0).getNumtable() == null){
                pos.printTextNewLine("TABLE : UNKNOWN");
            }else{
                pos.printTextNewLine("TABLE : " + foodsBean.get(0).getNumtable());
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
            pos.printTextNewLine("----------------------------------------------");


            for (FoodsBean foods :foodsBean ) {
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

            pos.printTextNewLine("----------------------------------------------");

            pos.printLocation(1);
            pos.printLine(2);
            //打印二维码
            //  pos.qrCode("http://www.safesoft-dz.com/");

            //切纸
            pos.feedAndCut();
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

    private void initData(ArrayList<PostData_Bon2> reciept) {
        foodsBean = new ArrayList<>();

        for (int i = 0; i < reciept.size(); i++) {
            FoodsBean fb = new FoodsBean();

            fb.setCode(reciept.get(i).Code);
            fb.setCode_s(reciept.get(i).Code_s);
            fb.setName(reciept.get(i).Produit);
            fb.setPrice(reciept.get(i).Price);
            fb.setQuantity(reciept.get(i).Quantity);
            fb.setEmporter(reciept.get(i).Emporter);
            fb.setNumtable(reciept.get(i).num_table);
            fb.setWaiter(reciept.get(i).Waiter);

            foodsBean.add(fb);
        }
    }
}
