package com.safesoft.uk2015.restopro.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.safesoft.uk2015.restopro.Adapters.ListViewAdapter_Receipt;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import br.com.forusers.heinsinputdialogs.HeinsInputDialog;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputDoubleListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dmax.dialog.SpotsDialog;

/**
 * Created by UK2015 on 24/06/2016.
 */
public class Fragment_receipt extends Fragment{

    private SwipeMenuListView listview_receipt;
    private ListViewAdapter_Receipt Adapter_listview;
    private ArrayList<PostData_Bon2> mItemList;
    private PostData_Bon2 array_data;
    private DATABASE contoller;
    private String num_bon;
    private Context _context;
    private Boolean Indicator;
    private Integer _position = 0;
    private String Server;
    private String Path;
    private String MY_PREFS_NAME = "ConfigNetwork";
    private String PREFS_PARAMS_INFO = "PARAMS_INFO";
    private Boolean _mVibrate = true;
    private Boolean _mSound = true;
    private Handler handler;
    private Thread thread;
    private  Delete_data_from_ServerFireBird_DataBase delete_from_server;

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

    public Fragment_receipt() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        num_bon = args.getString("num_bon");
        contoller = new DATABASE(getActivity());

        _context = getActivity();
        SharedPreferences prefs = _context.getSharedPreferences(MY_PREFS_NAME, _context.MODE_PRIVATE);
        Server = prefs.getString("ip", "192.168.1.5");
        Path = prefs.getString("path", "C:/RESTOPRO");

        SharedPreferences prefs2 = _context.getSharedPreferences(PREFS_PARAMS_INFO, _context.MODE_PRIVATE);
       _mVibrate = prefs2.getBoolean("VIBRATION", true);
       _mSound = prefs2.getBoolean("SOUND", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Indicator = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.x_x, container, false);

        mItemList = new ArrayList<>();
              String sql = "SELECT " +
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
                      " WHERE Bon2.NUM_BON = '" + num_bon + "' " +
                "GROUP BY " +
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

        mItemList = contoller.get_bon2_from_database(sql);
        String selectQuery1 = "SELECT TOTAL_TTC FROM Bon1 WHERE NUM_BON = '" + num_bon + "' ";
        String TTC = contoller.select_total_ttc_from_bon1(selectQuery1);

        //If the table has opened before you should receive and load data of the old order
        listview_receipt = (SwipeMenuListView) rootView.findViewById(R.id.listView22);
        Adapter_listview = new ListViewAdapter_Receipt(getActivity(), mItemList);
        listview_receipt.setAdapter(Adapter_listview);

        if(mItemList.size() > 0){
            someEventListener.someEvent("FROM_RECEIPT_ADAPTER", TTC);
        }

        listview_receipt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Indicator = true;
                if(mItemList.get(position).Code == null){
                    for(int i = position - 1; i>=0; i--){
                     if(mItemList.get(i).Code != null){
                         _position = i;
                      //   parent.getChildAt(new_position).setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                         break;
                     }

                    }
                }else {
                    _position = position;
                }
                Object object_data = parent.getItemAtPosition(_position);
                array_data = new PostData_Bon2();
                array_data = (PostData_Bon2) object_data;
            }
        });

        listview_receipt.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                HeinsInputDialog dialog = new HeinsInputDialog(getActivity() );
               // dialog.setValue(""+mItemList.get(position).Quantity);
               // dialog.getEditTextLayout().getEditText().setText(""+mItemList.get(position).Quantity);
                dialog.setPositiveButton(new OnInputDoubleListener() {
                    @Override
                    public boolean onInputDouble(AlertDialog dialog, Double value) {
                        //Do something
                        mItemList.get(position).Quantity = String.valueOf(value.intValue());
                        Adapter_listview = new ListViewAdapter_Receipt(getActivity(), mItemList);
                        listview_receipt.setAdapter(Adapter_listview);
                        calculc_total();
                        someEventListener.someEvent("FROM_RECEIPT_ADAPTER", String.valueOf(calculc_total()));
                        return false;//return if consume event
                    }
                });
                dialog.setTitle(""+mItemList.get(position).Produit);
                dialog.setHint("Quantité");
                dialog.show();
                return true;
            }
        });
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem emporterItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                emporterItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                emporterItem.setWidth(dp2px(90));
                // set item title
                emporterItem.setTitle("Emporter");
                // set item title fontsize
                emporterItem.setTitleSize(12);
                // set item title font color
                emporterItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(emporterItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete_forever);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listview_receipt.setMenuCreator(creator);
        listview_receipt.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // step 2. listener item click event
        listview_receipt.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // Emporter
                        // Emporter(item);
                        if(mItemList.get(position).Code != null){
                            if((mItemList.get(position).Emporter == null)||(mItemList.get(position).Emporter.equals("0")))
                            {
                                mItemList.get(position).Emporter = "1";
                            }else{
                                mItemList.get(position).Emporter = "0";
                            }

                        }
                        Adapter_listview = new ListViewAdapter_Receipt(getActivity(), mItemList);
                        listview_receipt.setAdapter(Adapter_listview);
                        Adapter_listview.Refresh_list();
                        break;
                    case 1:
                         // delete
                         //delete(item);
                        if(mItemList.get(position).Imp_com == null){
                            String requete_delete_product;
                            if(mItemList.get(position).Code != null){
                                String recordid2 = mItemList.get(position).Recordid2;
                                requete_delete_product = "DELETE FROM BON2 WHERE RECORDID2 = " + recordid2 + " AND RECORDID2 IS NOT NULL";
                                delete(position, requete_delete_product);
                            }else{
                                String recordid = mItemList.get(position).Bon2id;
                                requete_delete_product = "DELETE FROM BON2 WHERE RECORDID = "+recordid;
                                delete(position, requete_delete_product);
                            }
                        }else{
                            Crouton.showText(getActivity() , "Vous n'avez pas le droit de supprimer ce produit !", Style.ALERT);

                        }
                        break;
                }
                return false;
            }
        });

        return rootView;
    }

    protected Double calculc_total(){
        Double total = 0.00;
        for(int k = 0; k < mItemList.size(); k++){
            total = total + (Double.valueOf(mItemList.get(k).Quantity)* Double.valueOf(mItemList.get(k).Price));
        }
        return total;
    }

    private void play_sound_delete(){
        final MediaPlayer mp = new MediaPlayer();
        if(mp.isPlaying())
        {
            mp.stop();
        }

        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = _context.getAssets().openFd("sounds/FingerBreaking.mp3");
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    public void updateDataListViewInPosition(PostData_Bon2 item_receipt){
        if(Indicator){
            item_receipt.Recordid2 = array_data.Recordid2;
            item_receipt.Num_bon_bon2 = array_data.Num_bon_bon2;
            Adapter_listview.Add_To_LisViewInPosition((_position + 1), item_receipt);
        }else
        {
           // Crouton.(_context, "Please Select a product!",null);
            Toast.makeText(_context, "SVP sélectionner un produit!", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateDataListView(PostData_Bon2 data_listview) {
        Adapter_listview.Add_To_LisView(data_listview);
    }

    public void AddSupplementToDataListView(ArrayList<PostData_Bon2> supplement_produit) {
        for(int i =  0; i< supplement_produit.size(); i++){
            Adapter_listview.Add_To_LisView(supplement_produit.get(i));
        }
    }


    private void delete(final Integer position, String requete){
        final SpotsDialog dialogmessage = new SpotsDialog(_context, "Supprission de produit ...! ");
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
                            String recordid2 = mItemList.get(position).Recordid2;
                            if(mItemList.get(position).Code != null){
                                for(int i = position; i< mItemList.size();i++){
                                    if(mItemList.get(i).Recordid2.equals(recordid2)){
                                        mItemList = Adapter_listview.Remove_from_LisView(i);
                                        i--;
                                    }
                                }
                            }else{
                                mItemList = Adapter_listview.Remove_from_LisView(position);
                            }
                            //
                            Adapter_listview = new ListViewAdapter_Receipt(getActivity(), mItemList);
                            listview_receipt.setAdapter(Adapter_listview);
                            if(_mSound){
                                play_sound_delete();
                            }
                            if(_mVibrate){
                                Vibrator vb = (Vibrator)  _context.getSystemService(Context.VIBRATOR_SERVICE);
                                vb.vibrate(100);
                            }
                            break;
                        case 2:
                            dialogmessage.dismiss();
                            new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention...")
                                    .setContentText("Problem de connexion! Vous ne pouvez pas supprimer ce produit !")
                                    .show();
                            break;
                        case 3:
                            dialogmessage.dismiss();
                            break;
                        case 4:
                            dialogmessage.dismiss();
                            new SweetAlertDialog(_context, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                                    .show();
                            break;
                    }

                }catch(Exception ex){

                }
            }
        };

        comunication(requete);
    }

    public void comunication(final String query){

        thread = new Thread(){
            public void run(){
                try {
                    handler.sendEmptyMessage(0);
                        int flag = 0;
                        delete_from_server = new Delete_data_from_ServerFireBird_DataBase(query);
                        flag =  delete_from_server.execute().get();
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
                    handler.sendEmptyMessage(3);
                } catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            };
        };

        thread.start();
    }
    //class delete Data into FireBird Database
    //====================================
    public class Delete_data_from_ServerFireBird_DataBase extends AsyncTask<Void, Void, Integer> {
        Integer flag =0;
        String _requete;
        Connection con = null;
        public Delete_data_from_ServerFireBird_DataBase(String sql) {
            super();
            _requete = sql;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(3);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, "SYSDBA", "masterkey");

                Statement stmt = con.createStatement();
                stmt.executeUpdate(_requete);
                flag = 1;
            } catch (Exception e) {

                if (e.getMessage().contains("Unable to complete network request to host")) {
                    //not executed , data saved in the Tempdatabase;
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD (DELETE PRODUCT ) " + e.getMessage());
                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                    Log.e("TRACKKK", "ERROR WITH SQL STATEMENT ( DELETE PPRODUCT )  " + e.getMessage());
                }
                con = null;
            }
            return flag;
        }

    }
    //==================================================
}