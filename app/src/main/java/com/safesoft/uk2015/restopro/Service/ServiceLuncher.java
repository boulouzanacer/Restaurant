package com.safesoft.uk2015.restopro.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.safesoft.uk2015.restopro.PostData.PostData_Bon1;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.safesoft.uk2015.restopro.PostData.PostData_Families;
import com.safesoft.uk2015.restopro.PostData.PostData_Imps;
import com.safesoft.uk2015.restopro.PostData.PostData_Menu;
import com.safesoft.uk2015.restopro.PostData.PostData_Supplement;
import com.safesoft.uk2015.restopro.PostData.PostData_Tables;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import org.firebirdsql.event.DatabaseEvent;
import org.firebirdsql.event.EventListener;
import org.firebirdsql.event.FBEventManager;

import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by UK2015 on 06/06/2016.
 */
public class ServiceLuncher extends Service {

    private final IBinder mBinder = new MyBinder();
    private String Server;
    private String Username;
    private String Password;
    private String MY_PREFS_NAME = "ConfigNetwork";
    private String Path;
    private ArrayList<PostData_Tables> tables;
    private ArrayList<PostData_Families> families;
    private ArrayList<PostData_Menu> menus;
    private ArrayList<PostData_Bon1> bon1s;
    private ArrayList<PostData_Bon2> bon2s;
    private ArrayList<PostData_Bon2> supps;
    private ArrayList<PostData_Imps> imps;
    private ArrayList<PostData_Supplement> supplements;
    private DATABASE controller;
    private Connection con = null;
    private Context _context;
    private Activity _activity;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Server = prefs.getString("ip", "192.168.1.5");
        Path = prefs.getString("path", "C:/RESTOPRO");
        Username = prefs.getString("username", "SYSDBA");
        Password = prefs.getString("password", "masterkey");
        // Path = "C:/P-VENTE/DATA/RESTO PRO/"+ Database +".FDB";
        controller = new DATABASE(this);

        //////////========== TABLES ===============////////////////////
        lunch_load_tables();

        //////////========== BON1 =================////////////////////
        lunch_load_bon1();

        //////////========== NON2 =================////////////////////
        //lunch_load_bon2();

        //////////========== FAMILLIES ============////////////////////
        new Load_Families_Task().execute();

        //////////========== MENU =================/////////////////////
        new Load_Menu_Task().execute();

        /////////=========== EVENTS =================///////////////////
        //lunch_all_events();
        new Lunch_EventsTask().execute();
        ///////// ========= SUPPELEMENT =============///////////////////
        new Supplement_task().execute();

        /////////==========  IMPRIMANTES ============///////////////////
       new Load_Imp_Task().execute();



        //Check if there is any orders hasn't been sent before last execution!
     /*   Thread.interrupted();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
               // check_temp();
            }
        });
        th.start();
*/

    }
/*
    private void check_temp() {
        ArrayList<PostData_Bon2> TempFinalListBon2 = new ArrayList<>();
        String selectQuery = "SELECT * FROM  TempBon2";
        TempFinalListBon2 = controller.get_bon2_from_database(selectQuery);
        if (TempFinalListBon2.size() > 0) {
            Check_Connection();
        }
    }
*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public ArrayList<PostData_Tables> getTablesList() {

        return tables;
    }

    public ArrayList<PostData_Families> getFamiliesList() {

        return families;
    }

    public ArrayList<PostData_Menu> getMenuList() {

        return menus;
    }

    public void setContext(Context context) {
        _context = context;
    }

    ///////////////////////// LUNCH EVENT /////////////////////////////////////////////////////
    public void lunch_load_tables(){
        new Load_Tables_Task().execute();
    }
//---------------------------------------------------------------------------------------------   ///////////////////////// LUNCH EVENT /////////////////////////////////////////////////////
    public void lunch_load_bon1(){
        new Load_Bon1_Task().execute();
    }
//---------------------------------------------------------------------------------------------   ///////////////////////// LUNCH EVENT /////////////////////////////////////////////////////
    public Integer lunch_load_bon2(){
        Integer flag = 0;
        Load_all_Bon2_Task LoadallBon2Task = new Load_all_Bon2_Task();
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                LoadallBon2Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                flag = LoadallBon2Task.get();
            }
            else
                flag = LoadallBon2Task.execute().get();

            LoadallBon2Task.cancel(true);
        }catch (Exception e){
            Log.e("TRACKKK", "ERROR LOADING BON2 FROM SERVER FIREBIRD " + e.getMessage());
            LoadallBon2Task.cancel(true);
        }
        return  flag;
    }
//---------------------------------------------------------------------------------------------

    ///////////////////////// LUNCH EVENT /////////////////////////////////////////////////////
    public void lunch_all_events(){
        Lunch_EventsTask lunch_event_task = new Lunch_EventsTask();
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                lunch_event_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else
                lunch_event_task.execute();
        }catch (Exception e){
        }
    }
//-----------------------------------------------------------------------------------------------

    ///////////////////////// SENT TO KITCHEN /////////////////////////////////////////////////
    public Integer SendToKitchen(String HT, ArrayList<PostData_Bon2> receipt_from_act) {
        //Boolean executed = false;
        Integer flag = 0;
        Insert_Into_ServerFireBird_DataBase InsertOrderFireBirdServer = new Insert_Into_ServerFireBird_DataBase(HT, receipt_from_act);
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                InsertOrderFireBirdServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                flag = InsertOrderFireBirdServer.get();
            }
            else
            flag = InsertOrderFireBirdServer.execute().get();


        } catch (Exception e) {
            Log.e("TRACKKK", "ERROR SENDING ORDERS TO SERVER FIREBIRD " + e.getMessage());
            InsertOrderFireBirdServer.cancel(true);
        }
        return flag;
    }
//----------------------------------------------------------------------------------------------

    /////////////////////// RESERVE RECORDID ////////////////////////////////////////////////////
    public Integer[] ReserveRecordid2() {
        Integer[] flag = new Integer[2];
        Reserve_recordid_into_server_task reserve_recordid_task = new Reserve_recordid_into_server_task();
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                reserve_recordid_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                flag = reserve_recordid_task.get();
            }
            else
            flag = reserve_recordid_task.execute().get();

            reserve_recordid_task.cancel(true);
        } catch (Exception e) {
            Log.e("TRACKKK", "ERROR SENDING ORDERS TO SERVER FIREBIRD " + e.getMessage());
        }
        return flag;
    }
//------------------------------------------------------------------------------------------------
/*
    ////////////////////// SEND LOST ORDERS TO KITCHEN /////////////////////////////////////////
    public Boolean SendTempToKitchen(ArrayList<PostData_Bon2> receipt_from_act) {
        Boolean executed = false;
        try {
            Sent_Temp_Task SendOrdersTemp = new Sent_Temp_Task(receipt_from_act);
            executed = SendOrdersTemp.execute().get();
        } catch (Exception e) {
            Log.e("TRACKKK", "ERROR SENDING ORDERS TO SERVER FIREBIRD " + e.getMessage());
        }
        return executed;
    }*/
//-------------------------------------------------------------------------------------------------

    ////////////////////////// CHANGE TABLE /////////////////////////////////////////////////////
    public Boolean ChangeTable(ArrayList<String> ARRAY_DATA) {
        Boolean executed = false;
        Change_Table_Into_ServerFireBird_DataBase ChangeTableFireBirdServer = new Change_Table_Into_ServerFireBird_DataBase(ARRAY_DATA);
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                ChangeTableFireBirdServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                executed = ChangeTableFireBirdServer.get();
            }
            else
            executed = ChangeTableFireBirdServer.execute().get();

            ChangeTableFireBirdServer.cancel(true);
        } catch (Exception e) {
            Log.e("TRACKKK", "ERROR CHANGING TABLE ONTO THE SERVER FIREBIRD " + e.getMessage());
            ChangeTableFireBirdServer.cancel(true);
        }
        return executed;
    }
//-------------------------------------------------------------------------------------------------

    /////////////////////////// RESERVE TABLE /////////////////////////////////////////////////////
    public String [] ReserveTable(ArrayList<String> ARRAY_DATA) {
       String [] _traitement = new String[2];
        Reserve_Table_Into_ServerFireBird_DataBase ReserveTableFireBirdServer = new Reserve_Table_Into_ServerFireBird_DataBase(ARRAY_DATA);
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                ReserveTableFireBirdServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                _traitement = ReserveTableFireBirdServer.get();
            }
            else
                _traitement = ReserveTableFireBirdServer.execute().get();

            ReserveTableFireBirdServer.cancel(true);
        } catch (Exception e) {
            Log.e("TRACKKK", "ERROR RESERVING TABLE ONTO THE SERVER FIREBIRD " + e.getMessage());
            ReserveTableFireBirdServer.cancel(true);
        }
        return  _traitement;
    }
//-----------------------------------------------------------------------------------------------

    /////////////////////////// LOAD BON2 FROM SERVER //////////////////////////////////////////////
    public Integer Load_Bon2_from_server(String num_bon) {
        Integer flag = 0;
        Load_Bon2_of_num_bon_Task load_bon2_of_num_bon_task = new Load_Bon2_of_num_bon_Task(num_bon);
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                load_bon2_of_num_bon_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                flag  = load_bon2_of_num_bon_task.get();
            }else
                flag = load_bon2_of_num_bon_task.execute().get();

            load_bon2_of_num_bon_task.cancel(true);
        } catch (Exception e) {
            Log.e("TRACKKK", "ERROR LOADING BON2 FROM THE SERVER FIREBIRD " + e.getMessage());
            load_bon2_of_num_bon_task.cancel(true);
        }
        return  flag;
    }
    //-----------------------------------------------------------------------------------------------

    ///////////////////////////// GET DIGIT ////////////////////////////////////////////////////
    public String Get_Digits_String(String number, Integer length) {
        String _number = number;
        while (_number.length() < length) {
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }
//------------------------------------------------------------------------------------------------

/*
    //FUNCTION RUNNING PERIODICALY WHEN THE CCONNECTION HAS BEEN GONE
    public void Check_Connection() {
        Boolean Connected = false;
        while (!Connected) {
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB";
                con = DriverManager.getConnection(sCon, Username, Password);

                //============
                ArrayList<PostData_Bon2> TempFinalListBon2 = new ArrayList<>();
                String selectQuery = "SELECT * FROM  TempBon2";
                TempFinalListBon2 = controller.get_bon2_from_database(selectQuery);
                Boolean executed = SendTempToKitchen(TempFinalListBon2);
                if (executed) {
                    controller.Delete_from_bon2("TempBon2", "0", 0);
                }
                //============
                Connected = true;

                Log.v("TRACKKK", "Les commandes sauvegarder localement ont envoyé à nouveau vers la cuisine");
                break;

            } catch (Exception ex) {
                Log.e("TRACKKK", "ENABLE TO RESOLE CONNECTION FROM SERVER, FUNCTION HAS STOPPED " + ex.getMessage());
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException Iex) {
                    Log.e("TRACKKK", "Interrupted Exception inside while " + ex.getMessage());
                }
            }
            con = null;
        }
    }
*/
    public class MyBinder extends Binder {
        public ServiceLuncher getService() {
            return ServiceLuncher.this;
        }
    }

    //==================== AsyncTask TO Load All Tables from server and store them in the local database (sqlite)
    public class Load_Tables_Task extends AsyncTask<Void, Void, ArrayList<PostData_Tables>> {

        @Override
        protected ArrayList<PostData_Tables> doInBackground(Void... params) {
            try {

                tables = new ArrayList<PostData_Tables>();;
                tables.clear();

                Intent intent;

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                String sql1 = "SELECT NUM_TABLE,SALLE,NUM_BON,DATE_HEURE,NOM_SERVEUR, MONTANT_BON FROM PLAN_SALLE ORDER BY NUM_TABLE";
              //  Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                Statement stmt = con.createStatement();
                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Tables table;
                while (rs1.next()) {

                    table = new PostData_Tables();
                    table.Table_number = rs1.getString("NUM_TABLE");
                    table.salle = rs1.getString("SALLE");
                    table.num_bon = rs1.getString("NUM_BON");
                    table.Table_date_time = rs1.getString("DATE_HEURE");
                    table.nom_serveur = rs1.getString("NOM_SERVEUR");
                    table.montant_bon = rs1.getString("MONTANT_BON");
                    tables.add(table);
                }

                stmt.close();
                Boolean executed = controller.ExecuteTransactionUpdateTables(tables);
                if (executed) {
                    //Make a notofiacation to the main_safe_doft for updating list tables
                    Log.v("TRACKKK", "==============================================");
                    intent = new Intent("PLAN_SALLE_UPDATE");
                    intent.putExtra("param", "YOUR PLAN SALLE HAS UPDATED FROM DATABASE SERVER");
                    sendBroadcast(intent);
                }

            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR WHEN EXECUTING LOAD TABLES ASYNCRON TASK " + e.getMessage());
            }
            return tables;
        }
    }


    //////////////////////////////////// LOAD BON1 ////////////////////////////////////////////
    //==================== AsyncTask TO Load Bon1 from server and store them in the local database (sqlite)
    public class Load_Bon1_Task extends AsyncTask<Void, Void, ArrayList<PostData_Bon1>> {

        @Override
        protected ArrayList<PostData_Bon1> doInBackground(Void... params) {
            try {

                bon1s = new ArrayList<>();
                bon1s.clear();

                Intent intent;

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //============================ GET BON1 ===========================================
                String sql2 = "SELECT BON1.NUM_BON, BON1.DATE_BON, BON1.NBR_P, BON1.TTC, BON1.BLOCAGE FROM BON1\n" +
                        "INNER JOIN plan_salle ON (BON1.num_bon = plan_salle.num_bon)";
                ResultSet rs2 = stmt.executeQuery(sql2);
                PostData_Bon1 bon1;
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                while (rs2.next()) {
                    bon1 = new PostData_Bon1();
                    bon1.Num_bon_bon1 = rs2.getString("NUM_BON");
                    bon1.Date_bon = rs2.getString("DATE_BON");
                    bon1.Nbr_p = rs2.getString("NBR_P");
                    String _prix =  (df.format(Double.valueOf(rs2.getString("TTC")))).replace(",",".");
                    bon1.TTC = _prix;
                    bon1.Blocage = rs2.getString("BLOCAGE");
                    bon1s.add(bon1);
                }

                stmt.close();
                Boolean executed = controller.ExecuteTransactionUpdateBon1(bon1s);
                if (executed) {
                    Log.v("TRACKKK", "==============================================");
                    intent = new Intent("BON1_UUPDATED");
                    intent.putExtra("param", "BON1 HAS UPDATED FROM DATABASE SERVER");
                    sendBroadcast(intent);
                }

            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR WHEN EXECUTING LOAD BON1 ASYNCRON TASK " + e.getMessage());
            }
            return bon1s;
        }
    }
    //===========================================================================================


    //==================== AsyncTask TO Load All Tables from server and store them in the local database (sqlite)
    public class Load_all_Bon2_Task extends AsyncTask<Void, Void, Integer> {

        Integer flag = 0;

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                bon2s = new ArrayList<>();
                bon2s.clear();
                Intent intent;
                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                //============================ GET BON2 ===========================================
                String sql3 = "SELECT BON2.RECORDID, BON2.num_bon, BON2.code, BON2.code_s, BON2.recordid2, BON2.produit, BON2.qte, BON2.tva, BON2.pv_ttc, BON2.montant_ht, BON2.emporter, BON2.imp_com FROM BON2\n" +
                        "INNER JOIN plan_salle ON (BON2.num_bon = plan_salle.num_bon)";
                ResultSet rs3 = stmt.executeQuery(sql3);
                PostData_Bon2 bon2;
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                while (rs3.next()) {
                    bon2 = new PostData_Bon2();
                    bon2.Bon2id = rs3.getString("RECORDID");
                    bon2.Num_bon_bon2 = rs3.getString("NUM_BON");
                    bon2.Code = rs3.getString("CODE");
                    bon2.Code_s = rs3.getString("CODE_S");
                    bon2.Recordid2 = rs3.getString("RECORDID2");
                    bon2.Produit = rs3.getString("PRODUIT");
                    Double d = Double.parseDouble(rs3.getString("QTE"));
                    bon2.Quantity = String.valueOf(d.intValue());
                    bon2.Tva = rs3.getString("TVA");
                    String _prix =  (df.format(Double.valueOf(rs3.getString("PV_TTC")))).replace(",",".");
                    bon2.Price = _prix;
                    bon2.Montant_ht = rs3.getString("MONTANT_HT");
                    bon2.Emporter = rs3.getString("EMPORTER");
                    bon2.Imp_com = rs3.getString("IMP_COM");
                    bon2s.add(bon2);
                }

                stmt.close();
                controller.ExecuteTransactionUpdateBon2(bon2s);
                flag = 1;

            } catch (SQLException sqlx) {
                con = null;
                if (sqlx.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
            }catch (ClassNotFoundException clsnotfound) {
                con = null;
                Log.e("TRACKKK", "ERROR WHEN EXECUTING LOAD BON2 ASYNCRON TASK " + clsnotfound.getMessage());
            }
            return flag;
        }
    }
//=================================================================================================


    //==================== AsyncTask TO Load All Impriments from server and store them in the local database (sqlite)
    public class Load_Imp_Task extends AsyncTask<Void, Void, ArrayList<PostData_Imps>> {

        @Override
        protected ArrayList<PostData_Imps> doInBackground(Void... params) {
            try {
                // Thread.sleep(10000);
                imps = new ArrayList<PostData_Imps>();;
                imps.clear();

                Intent intent;

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);


                String sql1 = "SELECT DES_IMP, IP_IMP FROM IMPRIMANTES";
                Statement stmt = con.createStatement();
                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Imps imp;
                while (rs1.next()) {

                    imp = new PostData_Imps();
                    imp.des_imp = rs1.getString("DES_IMP");
                    imp.ip_imp = rs1.getString("IP_IMP");
                    imps.add(imp);
                }

                stmt.close();
                controller.ExecuteTransactionInsertImp(imps);

            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR WHEN EXECUTING LOAD TABLES ASYNCRON TASK " + e.getMessage());
            }
            return imps;
        }
    }

    //==================== AsyncTask TO Load Bon2 of selected table from server and store them in the local database (sqlite)
    public class Load_Bon2_of_num_bon_Task extends AsyncTask<Void, Void, Integer> {
          Integer flag = 0;
        String _num_bon;
        public Load_Bon2_of_num_bon_Task(String num_bon){
            _num_bon = num_bon;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {

                bon2s = new ArrayList<>();
                bon2s.clear();
                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                //============================ GET BON2 ===========================================
                String sql3 = "SELECT BON2.RECORDID, BON2.num_bon, BON2.code, BON2.code_s, BON2.recordid2, BON2.produit, BON2.qte, BON2.tva, BON2.pv_ttc, BON2.montant_ht, BON2.emporter, BON2.imp_com FROM BON2\n" +
                        "WHERE BON2.num_bon = "+ _num_bon;
                ResultSet rs3 = stmt.executeQuery(sql3);
                PostData_Bon2 bon2;
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                while (rs3.next()) {
                    bon2 = new PostData_Bon2();
                    bon2.Bon2id = rs3.getString("RECORDID");
                    bon2.Num_bon_bon2 = rs3.getString("NUM_BON");
                    bon2.Code = rs3.getString("CODE");
                    bon2.Code_s = rs3.getString("CODE_S");
                    bon2.Recordid2 = rs3.getString("RECORDID2");
                    bon2.Produit = rs3.getString("PRODUIT");
                    Double d = Double.parseDouble(rs3.getString("QTE"));
                    bon2.Quantity = String.valueOf(d.intValue());
                    bon2.Tva = rs3.getString("TVA");
                    String _prix =  (df.format(Double.valueOf(rs3.getString("PV_TTC")))).replace(",",".");
                    bon2.Price = _prix;
                    bon2.Montant_ht = rs3.getString("MONTANT_HT");
                    bon2.Emporter = rs3.getString("EMPORTER");
                    bon2.Imp_com = rs3.getString("IMP_COM");
                    bon2s.add(bon2);
                }

                stmt.close();
                controller.ExecuteTransactionUpdateBon2(bon2s);
                flag = 1;

            } catch (SQLException sqlx) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + sqlx.getMessage());
                if (sqlx.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
            } catch (ClassNotFoundException clsnotfound) {
                con = null;
                Log.e("TRACKKK", "ERROR WHEN EXECUTING LOAD BON2 ASYNCRON TASK " + clsnotfound.getMessage());
            }
            return flag;
        }
    }
    //=================================================================================================
    //class Load Family
    public class Load_Families_Task extends AsyncTask<Void, Void, ArrayList<PostData_Families>> {

        @Override
        protected ArrayList<PostData_Families> doInBackground(Void... params) {
            try {

                families = new ArrayList<PostData_Families>();
                families.clear();
                Intent intent;





                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                String sql = "SELECT DISTINCT * FROM FAMILLE_MENU ORDER BY FAMILLE_MENU.RECORDID";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);


                PostData_Families family;
                Boolean perfect = false;
                while (rs.next()) {

                    family = new PostData_Families();

                    try {
                        String str = "";

                        family.Famille_id = rs.getString("RECORDID");
                        //str = new String(rs.getString("FAMILLE").getBytes("ISO8859_6"), "ISO8859_6");
                        //family.Famille_name = str;
                        family.Famille_name = rs.getString("FAMILLE");
                        family.Image_index = rs.getString("IMAGE_INDEX");
                        families.add(family);
                    }catch (UnsupportedCharsetException usce){

                    }

                }

                perfect = controller.ExecuteTransactionUpdateFamily(families);

                stmt.close();
                if (perfect) {
                    Log.v("TRACKKK", "==============================================");
                    intent = new Intent("FAMILIES_LOADED");
                    intent.putExtra("param", "YOUR FAMILIES LIST HAS LOADED");
                    sendBroadcast(intent);
                }

            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR IN LOAD FAMILLE ASYNCRON TASK " + e.getMessage());
            }
            return families;
        }
    }

    //class Load Menu
    public class Load_Menu_Task extends AsyncTask<Void, Void, ArrayList<PostData_Menu>> {

        @Override
        protected ArrayList<PostData_Menu> doInBackground(Void... params) {
            try {
                // Thread.sleep(10000);
                menus = new ArrayList<PostData_Menu>();
                menus.clear();
                // list.clear();
                Intent intent;

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);


                String sql = "SELECT DISTINCT RECORDID, CODE, DESIGNATION, FAMILLE, TVA, PRIX1,DES_IMP, MENU_ACTIF, IMAGE_INDEX, EMPORTER FROM MENU ORDER BY MENU.RECORDID";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);


                PostData_Menu menu;
                Boolean perfect = false;
                while (rs.next()) {

                    menu = new PostData_Menu();
                    menu.Menuid = rs.getString("RECORDID");
                    menu.Code = rs.getString("CODE");
                    menu.Designation = rs.getString("DESIGNATION");
                    menu.Famille_Menu = rs.getString("FAMILLE");
                    menu.Tva = rs.getString("TVA");
                    menu.Prix1 = rs.getString("PRIX1");
                    menu.Des_imp = rs.getString("DES_IMP");
                    menu.Menu_actif = rs.getString("MENU_ACTIF");
                    menu.Image_index = rs.getString("IMAGE_INDEX");
                    menu.Emporter = rs.getString("EMPORTER");
                    menus.add(menu);
                }
                perfect = controller.ExecuteTransactionUpdateMenu(menus);

                stmt.close();
                if (perfect) {
                    Log.v("TRACKKK", "==============================================");
                    intent = new Intent("MENU_LOADED");
                    intent.putExtra("param", "YOUR MENU LIST HAS LOADED");
                    sendBroadcast(intent);
                }

            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR IN LOAD MENU ASYNCRON TASK " + e.getMessage());
            }
            return menus;
        }
    }

    //class LOAD SUPPLEMENT
    public class Supplement_task extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                supplements = new ArrayList<>();
                supps = new ArrayList<>();
                supplements.clear();
                supps.clear();
                Intent intent;

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                //============================ GET SOUS_PRODUIT ===========================================
                String sql4 = "SELECT SOUS_PROD1.CODE, SOUS_PROD1.CODE_S, SOUS_PROD1.LIBELLE, SOUS_PROD1.PRIX FROM SOUS_PROD1";
                ResultSet rs4 = stmt.executeQuery(sql4);
                PostData_Bon2 supp;
                while (rs4.next()) {

                    supp = new PostData_Bon2();
                    supp.Code = rs4.getString("CODE");
                    supp.Code_s = rs4.getString("CODE_S");
                    supp.Produit = rs4.getString("LIBELLE");
                    supp.Price = rs4.getString("PRIX");
                    supps.add(supp);
                }
                controller.Insert_into_sous_produit(supps);


                String sql = "SELECT * FROM SUPPLEMENT_MENU";
                ResultSet rs = stmt.executeQuery(sql);
                PostData_Supplement supplement;
                Boolean perfect = false;
                while (rs.next()) {

                    supplement = new PostData_Supplement();
                    supplement.Code_s = rs.getString("CODE_S");
                    supplement.Libelle = rs.getString("LIBELLE");
                    supplement.Sup_price = rs.getString("PRIX");
                    supplement.Tva = rs.getString("TVA");

                    supplements.add(supplement);
                }
                perfect = controller.ExecuteTransactionInsertSuppelement(supplements);

                stmt.close();
                if (perfect) {
                    Log.v("TRACKKK", "==============================================");
                    intent = new Intent("SUPPELEMENT_LOADED");
                    intent.putExtra("param", "YOUR SUPPELEMENT LIST HAS LOADED");
                    sendBroadcast(intent);
                }

            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR IN LOAD SUPPELEMENT ASYNCRON TASK " + e.getMessage());
            }
            return null;
        }
    }
    //==================================================


    //==================================================

    //Class Load Events
    public class Lunch_EventsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                FBEventManager fes = new FBEventManager();
                fes.setDatabase(Path + ".FDB");
                fes.setHost(Server);
                fes.setPort(3050);
                fes.setUser(Username);
                fes.setPassword(Password);
                try {
                    fes.connect();
                    fes.addEventListener("PLAN_SALLE_MOBILE", new EventListener() {
                        @Override
                        public void eventOccurred(DatabaseEvent databaseEvent) {
                            new Load_Tables_Task().execute();
                            Log.v("TRACKKK", "PLAN_SALLE EVENT FROM SERVER :)");
                        }
                    });

                    fes.addEventListener("FAMILLE_MENU_UPDATE", new EventListener() {
                        @Override
                        public void eventOccurred(DatabaseEvent databaseEvent) {
                            new Load_Families_Task().execute();
                            Log.v("TRACKKK", "FAMILLE EVENT FROM SERVER :)");
                        }
                    });

                    fes.addEventListener("MENU_UPDATE", new EventListener() {
                        @Override
                        public void eventOccurred(DatabaseEvent databaseEvent) {
                            new Load_Menu_Task().execute();
                            Log.v("TRACKKK", "MENU EVENT FROM SERVER :)");
                        }
                    });


                    fes.addEventListener("BON1_UPDATE", new EventListener() {
                        @Override
                        public void eventOccurred(DatabaseEvent databaseEvent) {
                            lunch_load_bon1();
                            Log.v("TRACKKK", "BON 1 EVENT FROM SERVER :)");
                        }
                    });
/*
                   fes.addEventListener("BON2_UPDATE", new EventListener() {
                        @Override
                        public void eventOccurred(DatabaseEvent databaseEvent) {
                           lunch_load_bon2();
                            Log.v("TRACKKK", "BON 2 EVENT FROM SERVER :)");
                        }
                    });
*/
                    fes.addEventListener("SUPPLEM_UPDATE", new EventListener() {
                        @Override
                        public void eventOccurred(DatabaseEvent databaseEvent) {
                            new Supplement_task().execute();
                            Log.v("TRACKKK", "SUPPELEMENT EVENT FROM SERVER :)");
                        }
                    });

                } catch (Throwable e) {
                    e.printStackTrace();
                    Log.e("TRACKKK", e.getMessage());
                }
            } catch (Exception e) {
                Log.e("FirebirdExample", e.getMessage());
            }

            return null;
        }
    }
    //==================================================

    //class Insert Data into FireBird Database
    //====================================
    public class Insert_Into_ServerFireBird_DataBase extends AsyncTask<Void, Void, Integer> {

        ArrayList<PostData_Bon2> taskreceipt = new ArrayList<>();
        String _HT;
        //Boolean executed = false;
        Integer flag = 0;

        public Insert_Into_ServerFireBird_DataBase(String HT, ArrayList<PostData_Bon2> receipt) {
            super();
            // do stuff
            taskreceipt = receipt;
            _HT = HT;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                con.setAutoCommit(false);
                //String query = "UPDATE BON1 SET NBR_P = " + taskreceipt.size() + " , HT = " + _HT + ", EMPORTER = 5 WHERE NUM_BON = '" + taskreceipt.get(0).Num_bon_bon2 + "' ";
                //stmt.addBatch(query);
                String[] buffer = new String[taskreceipt.size()];
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                for (int i = 0; i < taskreceipt.size(); i++) {
                    Double prix_calc = (Double.valueOf(taskreceipt.get(i).Price) * 100)/(Double.valueOf(taskreceipt.get(i).Tva)+100);
                    String _prix =  (df.format(prix_calc)).replace(",",".");
                    buffer[i] = "UPDATE OR INSERT INTO BON2 (RECORDID, NUM_BON, CODE, CODE_S, RECORDID2, PRODUIT, QTE, PV_HT, TVA, " +
                            " EMPORTER, IMP_COM) VALUES (" + taskreceipt.get(i).Bon2id + " , '" + taskreceipt.get(i).Num_bon_bon2 + "' , " +
                            taskreceipt.get(i).Code + " , " + taskreceipt.get(i).Code_s + " , " + taskreceipt.get(i).Recordid2 + " , '" +
                            taskreceipt.get(i).Produit.replace("'","`").replace("\"","`") + "' , " + taskreceipt.get(i).Quantity + " , " +
                            _prix + " , " + taskreceipt.get(i).Tva + " , " +  taskreceipt.get(i).Emporter + " , '1') MATCHING (RECORDID)";
                    stmt.addBatch(buffer[i]);
                }
                stmt.executeBatch();
                con.commit();
                stmt.clearBatch();
                con.setAutoCommit(true);
                flag = 1;
                if (taskreceipt.size() > 0) {
                  //  controller.ExecuteTransactionSend(_HT, taskreceipt.get(0).Num_bon_bon2, taskreceipt);
                }

            } catch (SQLException sqlx) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + sqlx.getMessage());
                if (sqlx.getMessage().contains("Unable to complete network request to host") && (taskreceipt.size() > 0)) {
                   /* controller.ExecuteTransactionSend2(_HT, taskreceipt.get(0).Num_bon_bon2, taskreceipt);
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Check_Connection();
                        }
                    });
                    th.start();*/
                    //not executed , data saved in the Tempdatabase;
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
            } catch (ClassNotFoundException clsnotfound) {
                con = null;
                Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD " + clsnotfound.getMessage());
            }

            return flag;
        }

    }

    //class Insert Data into FireBird Database
    //====================================
    public class Change_Table_Into_ServerFireBird_DataBase extends AsyncTask<Void, Void, Boolean> {

        ArrayList<String> _ARRAY_DATA = new ArrayList<>();
        PostData_Tables _table = new PostData_Tables();
        Boolean executed = false;

        public Change_Table_Into_ServerFireBird_DataBase(ArrayList<String> ARRAY_DATA) {
            super();
            // do stuff
            _ARRAY_DATA = ARRAY_DATA;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                con.setAutoCommit(false);
                String selectquery = "SELECT * FROM Tables WHERE NUM_BON = '" + _ARRAY_DATA.get(2) + "'";
                _table = controller.select_current_table(selectquery);
                String _num_bon = Get_Digits_String(_table.num_bon, 6);
                String query1 = "UPDATE PLAN_SALLE SET NUM_BON = '" + _num_bon + "', DATE_HEURE = '" + _table.Table_date_time + "' , NOM_SERVEUR = '" + _table.nom_serveur.replace("'","`").replace("\"","`") + "', MONTANT_BON = " + _table.montant_bon + "  WHERE NUM_TABLE= " + _ARRAY_DATA.get(1);
                String query2 = "UPDATE PLAN_SALLE SET NUM_BON = " + null + ", DATE_HEURE = " + null + " , NOM_SERVEUR = " + null + ", MONTANT_BON = " + null + "  WHERE NUM_TABLE= " + _ARRAY_DATA.get(0);
                stmt.addBatch(query1);
                stmt.addBatch(query2);
                stmt.executeBatch();
                con.commit();
                stmt.clearBatch();
                con.setAutoCommit(true);
                executed = true;
            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR CHANGING TABLE ONTO SERVER FirEBIRD " + e.getMessage());
            }
            return executed;
        }
    }
    //==================================================

    //class Insert Data into FireBird Database
    //====================================
    public class Reserve_Table_Into_ServerFireBird_DataBase extends AsyncTask<Void, Void, String []> {

        ArrayList<String> _ARRAY_DATA = new ArrayList<>();
        String [] _traitement = new String[2];

        public Reserve_Table_Into_ServerFireBird_DataBase(ArrayList<String> ARRAY_DATA) {
            super();
            // do stuff
            _ARRAY_DATA = ARRAY_DATA;
            _traitement [0] = "NO";
        }

        @Override
        protected String [] doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                _traitement [1] = "0";
                Statement stmt = con.createStatement();
                con.setAutoCommit(false);
                String query1 = "SELECT MAX(NUM_BON) AS NEW_NUM_BON FROM BON1";
                ResultSet rs = stmt.executeQuery(query1);
                while (rs.next()) {
                    _traitement [1] = rs.getString("NEW_NUM_BON");
                }
                _traitement [1]= String.valueOf(Integer.parseInt(_traitement [1]) + 1);
                _traitement [1] = Get_Digits_String(String.valueOf(_traitement [1]), 6);
                String query2 = "INSERT INTO BON1 (NUM_BON, DATE_BON, BLOCAGE, NOM_SERVEUR) VALUES" +
                        " ('" + _traitement [1] + "',cast ('" + _ARRAY_DATA.get(4) + "'as timestamp), '', '" + _ARRAY_DATA.get(3).replace("'","`").replace("\"","`") + "')";
                stmt.addBatch(query2);
                String query3 = "UPDATE PLAN_SALLE SET NUM_BON = '" + _traitement [1] + "', DATE_HEURE = cast ('" + _ARRAY_DATA.get(4) + "' as timestamp), NOM_SERVEUR = '" + _ARRAY_DATA.get(3).replace("'","`").replace("\"","`") + "' WHERE NUM_TABLE = " + _ARRAY_DATA.get(1) + "";
                stmt.addBatch(query3);
                stmt.executeBatch();
                stmt.clearBatch();
                con.commit();
                con.setAutoCommit(true);
                _traitement [0] = "YES";
            } catch (Exception e) {
                con = null;
                Log.e("TRACKKK", "ERROR RESERVING TABLE ONTO SERVER FirEBIRD " + e.getMessage());
                _traitement [0] = "NO";
            }
            return  _traitement;
        }

    }

    //class Insert Data into FireBird Database
    //====================================
    public class Sent_Temp_Task extends AsyncTask<Void, Void, Boolean> {

        ArrayList<PostData_Bon2> taskreceipt = new ArrayList<>();
        Boolean executed = false;

        public Sent_Temp_Task(ArrayList<PostData_Bon2> receipt) {
            super();
            // do stuff
            taskreceipt = receipt;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                //stmt.setQueryTimeout(4);
                con.setAutoCommit(false);
                //String query = "UPDATE BON1 SET NBR_P = " + taskreceipt.size() + " , HT = " + _HT + ", EMPORTER = 5 WHERE NUM_BON = '" + taskreceipt.get(0).Num_bon_bon2 + "' ";
                //stmt.addBatch(query);
                String[] buffer = new String[taskreceipt.size()];
                for (int i = 0; i < taskreceipt.size(); i++) {
                    buffer[i] = "INSERT INTO BON2 (NUM_BON, CODE, CODE_S, PRODUIT, QTE, PV_HT," +
                            " EMPORTER, IMP_COM) VALUES ('" + taskreceipt.get(i).Num_bon_bon2 + "'," +
                            taskreceipt.get(i).Code + "," + taskreceipt.get(i).Code_s + ",'" +
                            taskreceipt.get(i).Produit.replace("'","`").replace("\"","`") + "'," + taskreceipt.get(i).Quantity + "," +
                            taskreceipt.get(i).Price + ", " + taskreceipt.get(i).Emporter + ", " + taskreceipt.get(i).Imp_com + " )";
                    stmt.addBatch(buffer[i]);
                }
                stmt.executeBatch();
                con.commit();
                stmt.clearBatch();
                con.setAutoCommit(true);
                executed = true;
                //controller.ExecuteTransactionSend("3000", taskreceipt.get(0).Num_bon_bon2, taskreceipt);

            } catch (SQLException sqlx) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + sqlx.getMessage());
            } catch (ClassNotFoundException clsnotfound) {
                con = null;
                Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD AGAIN " + clsnotfound.getMessage());
            }

            return executed;
        }

    }

    //class Insert Data into FireBird Database
    //====================================
    public class Reserve_recordid_into_server_task extends AsyncTask<Void, Void, Integer[]> {
        Integer[] flag = new Integer[2];

        @Override
        protected Integer[] doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                flag[0] = 0;
                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB";
                con = DriverManager.getConnection(sCon, Username, Password);

                String ggg = "SELECT distinct gen_id(gen_bon2_id,1) as RECORDID FROM rdb$database";
                Statement stmt = con.createStatement();
                ResultSet rs1 = stmt.executeQuery(ggg);
                while (rs1.next()) {
                    flag[1] = rs1.getInt("RECORDID");
                }

                flag[0] = 1;

            } catch (SQLException sqlx) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + sqlx.getMessage());
                if (sqlx.getMessage().contains("Unable to complete network request to host")) {
                    //not executed , problem connection
                    flag[0] = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    //not executed with problem in the sql statement
                    flag[0] = 3;
                }
            } catch (ClassNotFoundException clsnotfound) {
                con = null;
                Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD " + clsnotfound.getMessage());
            }
            return flag;
        }

    }
}