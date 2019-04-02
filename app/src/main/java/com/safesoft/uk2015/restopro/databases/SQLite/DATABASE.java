package com.safesoft.uk2015.restopro.databases.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.safesoft.uk2015.restopro.PostData.PostData_Bon1;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.safesoft.uk2015.restopro.PostData.PostData_Families;
import com.safesoft.uk2015.restopro.PostData.PostData_Imps;
import com.safesoft.uk2015.restopro.PostData.PostData_Menu;
import com.safesoft.uk2015.restopro.PostData.PostData_Supplement;
import com.safesoft.uk2015.restopro.PostData.PostData_Tables;
import com.safesoft.uk2015.restopro.PostData.PostData_Users;
import com.safesoft.uk2015.restopro.PostData.PostData_orders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by UK2015 on 31/05/2016.
 */
public class DATABASE extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1; // Database version
    private static final String DATABASE_NAME = "RESTOPRO"; // Database name
    private Context _context;  // create new context
    private SQLiteDatabase db = null;


    //Constructor DATABASE
    public DATABASE(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
        _context = context;
     //  _context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.v("TRACKKK","================>  ON CREATE EXECUTED");

        db.execSQL("CREATE TABLE IF NOT EXISTS Users(USERID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME VARCHAR, PASSWORD VARCHAR, ADRESS VARCHAR, PHONE VARCHAR, LASTCONNECT VARCHAR, IMAGE_PRO VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Tables(TABLEID VARCHAR PRIMARY KEY, SALLE VARCHAR, NUM_BON VARCHAR , TABLE_NUMBER VARCHAR, DATE_TIME_RESERVE VARCHAR, NOM_SERVEUR VARCHAR, MONTANT_BON VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Bon1(BON1ID INTEGER, NUM_BON VARCHAR PRIMARY KEY, DATE_TIME_BON VARCHAR, NBR_P VARCHAR, TOTAL_TTC VARCHAR, BLOCAGE VARCHAR, EMPORTER VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Bon2(BON2ID INTEGER PRIMARY KEY  AUTOINCREMENT, NUM_BON VARCHAR , CODE VARCHAR, CODE_S VARCHAR, RECORDID2 VARCHAR, PRODUIT VARCHAR, QUANTITY VARCHAR, TVA VARCHAR, PV_TTC VARCHAR, MONTANT_HT VARCHAR, EMPORTER VARCHAR, IMP_COM VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Menu(RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, CODE VARCHAR, DESIGNATION VARCHAR, FAMILLE VARCHAR, TVA VARCHAR, PRIX VARCHAR, DES_IMP VARCHAR, IMAGE_INDEX VARCHAR, MENU_ACTIF VARCHAR, EMPORTER VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Famille(FAMILLEID INTEGER PRIMARY KEY AUTOINCREMENT, FAMILLE VARCHAR, IMAGE_INDEX VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS TempBon1(BON1ID INTEGER, NUM_BON VARCHAR PRIMARY KEY, DATE_TIME_BON VARCHAR, NBR_P VARCHAR, TOTAL_HT VARCHAR, BLOCAGE VARCHAR, EMPORTER VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS TempBon2(BON2ID INTEGER PRIMARY KEY  AUTOINCREMENT, NUM_BON VARCHAR , CODE VARCHAR, CODE_S VARCHAR, RECORDID2 VARCHAR, PRODUIT VARCHAR, QUANTITY VARCHAR, PV_TTC VARCHAR, MONTANT_HT VARCHAR, EMPORTER VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Sous_produit(RECORDID INTEGER primary key, CODE VARCHAR, CODE_S VARCHAR, LIBELLE VARCHAR, PRICE VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Suppelement_menu(RECORDID VARCHAR primary key, CODE_S VARCHAR, LIBELLE VARCHAR, PRICE VARCHAR, IMAGE_INDEX INTEGER, TVA VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Imps(IMP_ID VARCHAR primary key, DES_IMP VARCHAR, IP_IMP VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.v("TRACKKK","================>  ON UPGRADE EXECUTED");

    }
    //Function create tables
 /*   public void create_table(){
        SQLiteDatabase db = this.getWritableDatabase();

      try{
          db.execSQL("DROP TABLE IF EXISTS Users");
          db.execSQL("DROP TABLE IF EXISTS Tables");
          db.execSQL("DROP TABLE IF EXISTS Menu");
          db.execSQL("DROP TABLE IF EXISTS Bon1");
          db.execSQL("DROP TABLE IF EXISTS Bon2");
          db.execSQL("DROP TABLE IF EXISTS Famille");
          db.execSQL("DROP TABLE IF EXISTS TempBon2");
          db.execSQL("DROP TABLE IF EXISTS Sous_produit");
          db.execSQL("DROP TABLE IF EXISTS Suppelement_menu");
          db.execSQL("DROP TABLE IF EXISTS Imps");
      }catch(Exception e){

      */
    ///////////////////////////////////////////////////////////////////////////////
    //////                             Functions                             //////
    ///////////////////////////////////////////////////////////////////////////////
    //================================= FUNCTION RESERVE TABLE =====================================

    //=================== FUNCTION UPDATE Tables FROM SERVER ===========================
    public Boolean ExecuteTransactionUpdateTables(ArrayList<PostData_Tables> tables){
        Boolean executed = false;
        try {
            db = this.getWritableDatabase();
             db.beginTransaction();
            try {
                //SAVE List Tables in the local database
                Insert_into_tables(tables);
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    //=================== FUNCTION UPDATE Bon1 FROM SERVER ===========================
    public Boolean ExecuteTransactionUpdateBon1(ArrayList<PostData_Bon1> bon1s){
        Boolean executed = false;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            db.delete("Bon1",null,null);
            try {
                for(int i = 0;i< bon1s.size(); i++){
                    Insert_into_bon1(db,bon1s.get(i).Num_bon_bon1, bon1s.get(i).Date_bon, bon1s.get(i).Nbr_p, bon1s.get(i).TTC, bon1s.get(i).Blocage, bon1s.get(i).Emporter);
                }
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //=================== FUNCTION UPDATE Bon2 FROM SERVER ===========================
    public Boolean ExecuteTransactionUpdateBon2(ArrayList<PostData_Bon2> bon2s){
        Boolean executed = false;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                Delete_from_bon2("Bon2","0", 0);
                for(int i = 0;i< bon2s.size(); i++){
                    Insert_into_bon2(db, "Bon2", bon2s.get(i).Bon2id, bon2s.get(i).Num_bon_bon2, bon2s.get(i).Code, bon2s.get(i).Code_s, bon2s.get(i).Recordid2, bon2s.get(i).Produit, bon2s.get(i).Quantity, bon2s.get(i).Tva, bon2s.get(i).Price, bon2s.get(i).Montant_ht, bon2s.get(i).Emporter, bon2s.get(i).Imp_com);
                }
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    //============================= FUNCTION INSERT OR UPDATE FAMILLY ==============================
    public Boolean ExecuteTransactionUpdateFamily(ArrayList<PostData_Families> newfamilies){
        Boolean executed = false;
        try {
           // db.delete("Famille",null,null);
            db = this.getWritableDatabase();
            ArrayList<PostData_Families>  oldfamille = new ArrayList<>();
            String querry = "SELECT * FROM Famille";
            oldfamille = Select_data_famille_from_database(querry);
            db.beginTransaction();


            Boolean check = false;
            if( oldfamille.size() > 0){
                for(int s = 0; s < oldfamille.size(); s++){
                    for(int p =0; p < newfamilies.size();p++){
                        if((newfamilies.get(p).Famille_id.equals(oldfamille.get(s).Famille_id))){
                            check = true;
                            break;
                        }else{
                            check = false;
                        }
                    }
                    if(!check){
                        String whereClause = "FAMILLEID=?";
                        String[] whereArgs = new String[] { String.valueOf(oldfamille.get(s).Famille_id) };
                        db.delete("Famille", whereClause, whereArgs);
                    }
                }
            }
            oldfamille.clear();
            oldfamille = Select_data_famille_from_database(querry);
            Boolean exist = false;

            for(int i = 0; i< newfamilies.size(); i++){
                for(int j = 0; j < oldfamille.size();j++){
                    if((newfamilies.get(i).Famille_id.equals(oldfamille.get(j).Famille_id))){
                        if(newfamilies.get(i).Famille_name.equals(oldfamille.get(j).Famille_name)){
                            exist = true;
                            break;
                        }else{
                            exist = true;
                            ContentValues args = new ContentValues();
                            args.put("FAMILLE", newfamilies.get(i).Famille_name);
                            String selection = "FAMILLEID=?";
                            String[] selectionArgs = {oldfamille.get(j).Famille_id};
                            db.update("Famille", args, selection, selectionArgs);
                            break;
                        }
                    }else{
                        exist = false;
                    }
                }
                if(!exist){
                    Insert_into_family(newfamilies.get(i).Famille_id, newfamilies.get(i).Famille_name, newfamilies.get(i).Image_index);
                }

            }

            db.setTransactionSuccessful();
            executed = true;
        } finally {
            db.endTransaction();
        }
        return executed;
    }

    //================================= FUNCTION INSERT OR UPDATE MENU =====================================
    public Boolean ExecuteTransactionUpdateMenu(ArrayList<PostData_Menu> newmenus){

        Boolean executed = false;
        try {
            db = this.getWritableDatabase();
            ArrayList<PostData_Menu>  oldmenu = new ArrayList<>();
            String querry = "SELECT * FROM Menu";
            oldmenu = Select_data_menu_from_database(querry);
            db.beginTransaction();

            Boolean check = false;
            if( oldmenu.size() > 0){
                for(int s = 0; s < oldmenu.size(); s++){
                    for(int p =0; p < newmenus.size();p++){
                        if((newmenus.get(p).Menuid.equals(oldmenu.get(s).Menuid))){
                            check = true;
                            break;
                        }else{
                            check = false;
                        }
                    }
                    if(!check){
                        String whereClause = "RECORDID=?";
                        String[] whereArgs = new String[] { String.valueOf(oldmenu.get(s).Menuid) };
                        db.delete("Menu", whereClause, whereArgs);
                    }
                }
            }

            oldmenu.clear();
            oldmenu = Select_data_menu_from_database(querry);
            Boolean exist = false;


            for(int i = 0; i< newmenus.size(); i++){
                for(int j = 0; j < oldmenu.size();j++){
                    if((newmenus.get(i).Menuid.equals(oldmenu.get(j).Menuid))){
                        if(newmenus.get(i).Designation.equals(oldmenu.get(j).Designation)){
                            exist = true;
                            ContentValues args = new ContentValues();
                            args.put("FAMILLE", newmenus.get(i).Famille_Menu);
                            args.put("PRIX", newmenus.get(i).Prix1);
                            args.put("DES_IMP", newmenus.get(i).Des_imp);
                            args.put("TVA", newmenus.get(i).Tva);
                            String selection = "RECORDID=?";
                            String[] selectionArgs = {oldmenu.get(j).Menuid};
                            db.update("Menu", args, selection, selectionArgs);
                            break;
                        }else{
                            exist = true;
                            ContentValues args = new ContentValues();
                            args.put("CODE", newmenus.get(i).Code);
                            args.put("DESIGNATION", newmenus.get(i).Designation);
                            args.put("FAMILLE", newmenus.get(i).Famille_Menu);
                            args.put("PRIX", newmenus.get(i).Prix1);
                            args.put("DES_IMP", newmenus.get(i).Des_imp);
                            args.put("TVA", newmenus.get(i).Tva);
                            String selection = "RECORDID=?";
                            String[] selectionArgs = {oldmenu.get(j).Menuid};
                            db.update("Menu", args, selection, selectionArgs);
                            break;
                        }
                    }else{
                        exist = false;
                    }
                }
                if(!exist){
                    Insert_into_menu(newmenus.get(i).Menuid, newmenus.get(i).Code, newmenus.get(i).Designation,newmenus.get(i).Famille_Menu, newmenus.get(i).Tva ,newmenus.get(i).Prix1, newmenus.get(i).Des_imp, newmenus.get(i).Image_index, newmenus.get(i).Menu_actif, newmenus.get(i).Emporter );
                }

            }

            db.setTransactionSuccessful();
            executed = true;
        } finally {
            db.endTransaction();
        }

        return executed;
    }

    //================================= FUNCTION RESERVE TABLE =====================================
    public Boolean ExecuteTransactionInsertSuppelement(ArrayList<PostData_Supplement> suppelement){
        Boolean executed = false;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            db.delete("Suppelement_menu",null,null);
            for(int i = 0; i< suppelement.size(); i++){
                Insert_into_suppelement(suppelement.get(i).Code_s, suppelement.get(i).Libelle, suppelement.get(i).Sup_price, suppelement.get(i).Tva);
            }

            db.setTransactionSuccessful();
            executed = true;
        } finally {
            db.endTransaction();

        }

        return executed;
    }

    //============================= FUNCTION TO INSERT INTO Users TABLE ============================
    public void Insert_into_users(String username, String password, String adress, String phone, String lastconnect) {
        try {
            db = this.getWritableDatabase();
            Boolean exist = false;
            String selectQuery = "SELECT USERNAME, PASSWORD FROM Users";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    if ((cursor.getString(cursor.getColumnIndex("USERNAME")).equals(username)) && (cursor.getString(cursor.getColumnIndex("PASSWORD")).equals(password))) {
                        exist = true;
                    }
                } while (cursor.moveToNext());
            }

            if (exist) {
                //update
                ContentValues args = new ContentValues();
                args.put("USERNAME", username);
                args.put("PASSWORD", password);
                args.put("ADRESS", adress);
                args.put("LASTCONNECT", lastconnect);
                db.update("Users", args, null, null);
            } else {
                //insert
                db.delete("Users", null, null);
                ContentValues values = new ContentValues();
                values.put("USERNAME", username);
                values.put("PASSWORD", password);
                values.put("ADRESS", adress);
                values.put("PHONE", phone);
                values.put("LASTCONNECT", lastconnect);
                values.putNull("IMAGE_PRO");
                db.insert("Users", null, values);
                Log.v("TRACKKK", "GOOOD INSERTION");
            }

        } catch (Exception e) {

        }

    }

//============================= FUNCTION TO INSERT INTO Tables TABLE ================================
public void Insert_into_tables(ArrayList<PostData_Tables> tables)
    {
        try{
            db = this.getWritableDatabase();
            db.delete("Tables",null,null);
        ContentValues values = new ContentValues();
        for(int i = 0; i< tables.size() ; i++){
            if(tables.get(i).salle == null){values.putNull("SALLE");}else{
            values.put("SALLE",tables.get(i).salle);}
            if(tables.get(i).Table_number == null){values.putNull("TABLE_NUMBER");}else{
            values.put("TABLE_NUMBER", tables.get(i).Table_number);}
            if(tables.get(i).Table_date_time == null){values.putNull("DATE_TIME_RESERVE");}else{
            values.put("DATE_TIME_RESERVE",tables.get(i).Table_date_time);}
            if(tables.get(i).num_bon == null){values.putNull("NUM_BON");}else{
            values.put("NUM_BON", tables.get(i).num_bon);}
            if(tables.get(i).nom_serveur == null){values.putNull("NOM_SERVEUR");}else{
            values.put("NOM_SERVEUR", tables.get(i).nom_serveur);}
            if(tables.get(i).montant_bon == null){values.putNull("MONTANT_BON");}else{
            values.put("MONTANT_BON",tables.get(i).montant_bon);}
            db.insert("Tables", null, values);
        }
        }catch(Exception e){

        }
    }

    //========================== FUNCTION TO INSERT INTO Familly TABLE ====================================
    //Function to Insert Into Familly Table
    public Boolean Insert_into_family(String id, String famille, String image_index){
        Boolean perfect = false;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FAMILLEID", id);
            values.put("FAMILLE", famille);
            values.putNull("IMAGE_INDEX");
            db.insert("Famille", null, values);
            perfect = true;
        }catch (Exception e){
            Log.e("TRACKKK", "ERREOR ADDING FAMILLE TO SQLite DATABASE " + e.getMessage());
            perfect = false;
        }
       return perfect;
    }

//========================== FUNCTION TO INSERT INTO Menu TABLE ====================================
    //Function to Insert Into Menu Table
    public void Insert_into_menu(String menuid, String Code, String Designation, String Famille, String tva, String prix, String des_imp, String menu_actif, String image_index, String emporter){
        try{
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RECORDID", menuid);
        values.put("CODE", Code);
        values.put("DESIGNATION", Designation);
        values.put("FAMILLE", Famille);
        values.put("TVA", tva);
        values.put("PRIX", prix);
        values.put("DES_IMP", des_imp);
        values.put("MENU_ACTIF", menu_actif);
        values.put("IMAGE_INDEX", image_index);
        values.put("EMPORTER", emporter);
        db.insert("Menu", null, values);
        }catch(Exception e){

        }

    }

    //========================== FUNCTION TO INSERT INTO Suppelement TABLE ====================================
    //Function to Insert Into Suppelement Table
    public void Insert_into_suppelement(String Code_s, String libelle, String price, String prix){
        try{
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CODE_S", Code_s);
        values.put("LIBELLE", libelle);
        values.put("PRICE", price);
        values.put("TVA", prix);
        db.insert("Suppelement_menu", null, values);
    }catch(Exception e){

    }

    }

    //========================== FUNCTION TO INSERT INTO Imps TABLE ====================================
    //Function to Insert Into Imprementes Table
    public void Insert_into_imprement(String des_imp, String ip_imp){
        try{
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DES_IMP", des_imp);
        values.put("IP_IMP", ip_imp);
        db.insert("Imps", null, values);
        }catch(Exception e){
        }
    }
 //================================ FUNCTION TO INSERT INTO Bon1 Table ============================

    public void Insert_into_bon1( SQLiteDatabase db, String num_bon, String date_time_bon, String nbr_p, String total_ttc, String blocage, String emporter){
        Boolean exist_bon1 = false;
        try{
            db = this.getWritableDatabase();
        exist_bon1 = check_if_bon1_exist(db, num_bon);

        if(exist_bon1){
            //UPDATE
            Update_bon(db,"Bon1", nbr_p,total_ttc,num_bon,emporter);
        }else {
            //INSERT
            ContentValues values = new ContentValues();
            if (num_bon == null) {
                values.putNull("NUM_BON");
            } else {
                values.put("NUM_BON", num_bon);
            }
            if (date_time_bon == null) {
                values.putNull("DATE_TIME_BON");
            } else {
                values.put("DATE_TIME_BON", date_time_bon);
            }
            if (nbr_p == null) {
                values.putNull("NBR_P");
            } else {
                values.put("NBR_P", nbr_p);
            }
            if (total_ttc == null) {
                values.putNull("TOTAL_TTC");
            } else {
                values.put("TOTAL_TTC", total_ttc);
            }
            if (blocage == null) {
                values.putNull("BLOCAGE");
            } else {
                values.put("BLOCAGE", blocage);
            }
            if (emporter == null) {
                values.putNull("EMPORTER");
            } else {
                values.put("EMPORTER", emporter);
            }

            db.insert("Bon1", null, values);
        }
        }catch(Exception e){
        }
        }
   //================================== CHECK IF BON1 EXIST ========================================

    public Boolean check_if_bon1_exist( SQLiteDatabase db, String num_bon){
        try{
            db = this.getWritableDatabase();
        String sql = "SELECT NUM_BON FROM Bon1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String nnnn = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                if(cursor.getString(cursor.getColumnIndex("NUM_BON")).equals(num_bon)){
                    return true;
                }
            } while (cursor.moveToNext());
        }
        }catch(Exception e){

        }
        return false;
    }
//=============================== FUNCTION TO INSERT INTO Bon2 TABLE ===============================

    public void Insert_into_bon2(SQLiteDatabase db, String table, String recordid, String num_bon, String code, String code_s, String recordid2, String produit, String quantity,String tva, String pv_ttc, String montat_ht, String emporter, String imp_com){
        try{
            db = this.getWritableDatabase();
        String _table = table;
        if(db == null){
            db = this.getWritableDatabase();
        }
            ContentValues values = new ContentValues();
            values.put("BON2ID", recordid);
            values.put("NUM_BON", num_bon);
            values.put("CODE", code);
            values.put("CODE_S", code_s);
            values.put("RECORDID2", recordid2);
            values.put("PRODUIT", produit);
            values.put("QUANTITY", quantity);
            values.put("TVA", tva);
            values.put("PV_TTC", pv_ttc);
            values.put("MONTANT_HT", montat_ht);
            values.put("EMPORTER", emporter);
            values.put("IMP_COM", imp_com);
            db.insert(_table, null, values);
        }catch(Exception e){

        }

    }


    //==============================================================================================
    //================================= FUNCTION RESERVE TABLE =====================================
    public void ExecuteTransactionInsertImp(ArrayList<PostData_Imps> imprements){

        Boolean executed = false;

        try {
            db.beginTransaction();
            db.delete("Imps",null,null);
            for(int i = 0; i< imprements.size(); i++){
                Insert_into_imprement(imprements.get(i).des_imp, imprements.get(i).ip_imp);
            }

            db.setTransactionSuccessful();
            executed = true;
        } finally {
            db.endTransaction();
        }
    }

    //=============================== FUNCTION TO INSERT INTO TempBon2 TABLE =======================

    public void Insert_into_Tempbon2(SQLiteDatabase db, String recordid, String num_bon, String code, String code_s, String recordid2, String produit, String quantity, String pv_ttc, String montat_ht, String emporter){
        Boolean etat_db = false;
        Boolean exist_bon2 = false;
        try {
        if(db == null){
            db = this.getWritableDatabase();
            etat_db = true;
        }
        ContentValues values = new ContentValues();
        values.put("BON2ID", recordid);
        values.put("NUM_BON", num_bon);
        values.put("CODE", code);
        values.put("CODE_S", code_s);
        values.put("RECORDID2", recordid2);
        values.put("PRODUIT", produit);
        values.put("QUANTITY", quantity);
        values.put("MONTANT_HT", montat_ht);
        values.put("EMPORTER", emporter);
        db.insert("TempBon2", null, values);
        }catch(Exception e){
        }
    }



    //===========================FUNCTION INSERT INTO SOUS_PRODUI ==================================
    public void Insert_into_sous_produit(ArrayList<PostData_Bon2> supplements){
        if(db == null){
            db = this.getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        try {
            db.beginTransaction();
            db.delete("Sous_produit",null, null);
            for(int i = 0;i< supplements.size();i++){
                values.put("CODE", supplements.get(i).Code);
                values.put("CODE_S", supplements.get(i).Code_s);
                values.put("LIBELLE", supplements.get(i).Produit);
                values.put("PRICE", supplements.get(i).Price);
                db.insert("Sous_produit", null, values);
            }
            db.setTransactionSuccessful();
        }catch (Exception e){

        }finally {
        db.endTransaction();
    }

    }
    //=============================================================================================

    // DELETING .../////////////////////////////////////////////////////////////////////////////////
    //=============================== FUNCTION TO DELETE FROM Bon2 =================================
    public  void Delete_from_bon2(String table, String num_bon, Integer bin){
        if(db == null){
            db = this.getWritableDatabase();
        }
        String _table = table;
        String whereClause = "NUM_BON" + "=?";
        String[] whereArgs = new String[] { String.valueOf(num_bon) };
        if(bin == 0){
            db.delete(_table, null, null);
        }else{
            db.delete(_table, whereClause, whereArgs);
        }
    }
//==================================================================================================



    // SELECTING ...////////////////////////////////////////////////////////////////////////////////
//============================== FUNCTION SELECT FROM Users TTABLE =================================
    public  ArrayList<PostData_Users> get_users_from_sqlite_database(String us, String pas){
        ArrayList<PostData_Users> all_users = new ArrayList<PostData_Users>();
        // Select All Query
        String selectQuery = "SELECT USERNAME, PASSWORD, IMAGE_PRO FROM Users WHERE USERNAME = '" + us + "' AND PASSWORD = '" + pas+"'";
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PostData_Users user = new PostData_Users();
                user.Username = cursor.getString(cursor.getColumnIndex("USERNAME"));
                user.Password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
                user.Image = cursor.getString(cursor.getColumnIndex("IMAGE_PRO"));
                all_users.add(user);
            } while (cursor.moveToNext());
        }
        // return users list
        if(cursor != null){
            cursor.close();
        }
        return all_users;
    }

    //========================== FUNCTION SELECT FROM Bon2 TABLE ===================================
    public ArrayList<PostData_Bon2> get_bon2_from_database(String selectquery) {
        ArrayList<PostData_Bon2> _receipts = new ArrayList<PostData_Bon2>();
        PostData_Bon2 rec;
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(selectquery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                rec = new PostData_Bon2();
                rec.Bon2id = cursor.getString(cursor.getColumnIndexOrThrow("BON2ID"));
                rec.Num_bon_bon2 = cursor.getString(cursor.getColumnIndexOrThrow("NUM_BON"));
                rec.Code = cursor.getString(cursor.getColumnIndex("CODE"));
                rec.Code_s = cursor.getString(cursor.getColumnIndex("CODE_S"));
                rec.Recordid2 = cursor.getString(cursor.getColumnIndex("RECORDID2"));
                rec.Produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                rec.Quantity = cursor.getString(cursor.getColumnIndex("QUANTITY"));
                rec.Tva = cursor.getString(cursor.getColumnIndex("TVA"));
                rec.Price = cursor.getString(cursor.getColumnIndex("PV_TTC"));
                rec.Montant_ht = cursor.getString(cursor.getColumnIndex("MONTANT_HT"));
                rec.Emporter = cursor.getString(cursor.getColumnIndex("EMPORTER"));
                rec.Imp_com = cursor.getString(cursor.getColumnIndex("IMP_COM"));
                rec.Des_imp = cursor.getString(cursor.getColumnIndex("DES_IMP"));
                rec.Waiter = cursor.getString(cursor.getColumnIndex("NOM_SERVEUR"));
                rec.num_table = cursor.getString(cursor.getColumnIndex("TABLE_NUMBER"));
                _receipts.add(rec);
                //=====
            } while (cursor.moveToNext());
        }

        return _receipts;
    }

    //============================ FUNCTION SELECT FROM Tables TABLE ===============================
    public ArrayList<PostData_Tables> Select_tables_from_sqlite_database(String selectQuery){
        ArrayList<PostData_Tables> tables = new ArrayList<>();
        // Select All Query
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Tables table = new PostData_Tables();
                table.Tableid = String.valueOf(cursor.getString(cursor.getColumnIndex("TABLEID")));
                table.salle = cursor.getString(cursor.getColumnIndex("SALLE"));
                table.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                table.Table_number = cursor.getString(cursor.getColumnIndex("TABLE_NUMBER"));
                table.Table_date_time = cursor.getString(cursor.getColumnIndex("DATE_TIME_RESERVE"));
                table.nom_serveur = cursor.getString(cursor.getColumnIndex("NOM_SERVEUR"));
                table.montant_bon = cursor.getString(cursor.getColumnIndex("MONTANT_BON"));
                // Adding tables to list
                tables.add(table);
            } while (cursor.moveToNext());
        }

        // return tables list
        return tables;
    }

    //============================ FUNCTION SELECT FROM Tables TABLE ===============================
    public PostData_Tables select_current_table(String selectQuery){
        PostData_Tables table = new PostData_Tables();
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                table.Tableid = String.valueOf(cursor.getString(cursor.getColumnIndex("TABLEID")));
                table.salle = cursor.getString(cursor.getColumnIndex("SALLE"));
                table.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                table.Table_number = cursor.getString(cursor.getColumnIndex("TABLE_NUMBER"));
                table.Table_date_time = cursor.getString(cursor.getColumnIndex("DATE_TIME_RESERVE"));
                table.nom_serveur = cursor.getString(cursor.getColumnIndex("NOM_SERVEUR"));
                table.montant_bon = cursor.getString(cursor.getColumnIndex("MONTANT_BON"));
            } while (cursor.moveToNext());
        }
        return table;
    }

    //============================== FUNCTION SELECT FROM Menu TABLE ===============================

    public ArrayList<PostData_Menu> Select_data_menu_from_database(String querry){
        ArrayList<PostData_Menu> all_menu = new ArrayList<PostData_Menu>();
        // Select All Query
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Menu menu = new PostData_Menu();

                menu.Menuid = cursor.getString(cursor.getColumnIndex("RECORDID"));
                menu.Code = cursor.getString(cursor.getColumnIndex("CODE"));
                menu.Designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
                menu.Tva = cursor.getString(cursor.getColumnIndex("TVA"));
                menu.Prix1 = cursor.getString(cursor.getColumnIndex("PRIX"));
                menu.Des_imp = cursor.getString(cursor.getColumnIndex("DES_IMP"));
                menu.Menu_actif = cursor.getString(cursor.getColumnIndex("MENU_ACTIF"));
                menu.Image_index = cursor.getString(cursor.getColumnIndex("IMAGE_INDEX"));
                menu.Emporter = cursor.getString(cursor.getColumnIndex("EMPORTER"));

                // Adding contact to list
                all_menu.add(menu);
            } while (cursor.moveToNext());
        }

        // return contact list
        return all_menu;
    }

    //============================== FUNCTION SELECT FROM Famille TABLE ===================================

    public ArrayList<PostData_Families> Select_data_famille_from_database(String selectQuery){
        ArrayList<PostData_Families> all_famille = new ArrayList<PostData_Families>();
        try {
            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PostData_Families famille = new PostData_Families();

                    famille.Famille_id = cursor.getString(cursor.getColumnIndex("FAMILLEID"));
                    famille.Famille_name = cursor.getString(cursor.getColumnIndex("FAMILLE"));
                    famille.Image_index = cursor.getString(cursor.getColumnIndex("IMAGE_INDEX"));
                    // Adding contact to list
                    all_famille.add(famille);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){

        }

        return all_famille;
    }

    //=============================== FUNCTION SELECT FROM Bon1 TABLE ==============================
    public String Select_data_from_bon1(String num_bon){
    String total_ttc = "0";
    String selectQuery = "SELECT TOTAL_TTC FROM Bon1 WHERE NUM_BON = '"+ num_bon +"' ";
        if(db == null){
            db = this.getWritableDatabase();
        }
    Cursor cursor = db.rawQuery(selectQuery, null);
    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
        do {
           if(cursor.getString(cursor.getColumnIndex("TOTAL_TTC")) != null){
               total_ttc = cursor.getString(cursor.getColumnIndex("TOTAL_TTC"));
            }
        } while (cursor.moveToNext());
    }
    return total_ttc;
    }

    //=============================== SELECT ALL DATA OF ALL ORDERS ================================
    public ArrayList<PostData_orders> select_all_data_of_all_orders(){
        ArrayList<PostData_orders> orders_list = new ArrayList<>();
        String selectQuerry = "SELECT Tables.TABLE_NUMBER, Tables.NOM_SERVEUR, " +
                "Bon1.NUM_BON, Bon1.DATE_TIME_BON, Bon1.TOTAL_TTC " +
                "FROM Tables INNER JOIN Bon1 ON Tables.NUM_BON = Bon1.NUM_BON";
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuerry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_orders orders = new PostData_orders();
                orders.id = cursor.getInt(0);
                orders.server = cursor.getString(1);
                orders.num_bon = cursor.getString(2);
                orders.date_time = cursor.getString(3);
                orders.Total = cursor.getString(4);
                orders_list.add(orders);
            } while (cursor.moveToNext());
        }
        //db.close();
        return orders_list;
    }
    //==============================================================================================

    //============================ FUNCTION SELECT FROM Sous_produit TABLE ===============================
    public ArrayList<PostData_Bon2> get_row_supplement_from_database(String code){
        ArrayList<PostData_Bon2> supps = new ArrayList<>();
        if(db == null){
            db = this.getWritableDatabase();
        }
        String selectQuery = "SELECT * FROM Sous_produit WHERE CODE = " + code;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Bon2 supp = new PostData_Bon2();
                supp.Code = null;
                supp.Quantity = "1";
                supp.Code_s = cursor.getString(cursor.getColumnIndex("CODE_S"));
                supp.Produit = cursor.getString(cursor.getColumnIndex("LIBELLE"));
                supp.Price = cursor.getString(cursor.getColumnIndex("PRICE"));
                supps.add(supp);
            } while (cursor.moveToNext());
        }

        // return tables list
        return supps;
    }

    //============================ FUNCTION SELECT FROM SUPPLEMENT_MENU TABLE ===============================
    public ArrayList<PostData_Supplement> select_supplement_from_sqlite_database(String query){
        ArrayList<PostData_Supplement> supps = new ArrayList<>();
        if(db == null){
            db = this.getWritableDatabase();
        }

        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Supplement supp = new PostData_Supplement();
                supp.Code_s = cursor.getString(cursor.getColumnIndex("CODE_S"));
                supp.Libelle = cursor.getString(cursor.getColumnIndex("LIBELLE"));
                supp.Sup_price = cursor.getString(cursor.getColumnIndex("PRICE"));
                supps.add(supp);
            } while (cursor.moveToNext());
        }

        // return supplement list
        return supps;
    }


    //============================ FUNCTION SELECT FROM IMPRIMENTES TABLE ===============================
    public ArrayList<PostData_Imps> select_imprimentes_from_sqlite_database(){
        ArrayList<PostData_Imps> imps = new ArrayList<>();
        if(db == null){
            db = this.getWritableDatabase();
        }
        String querry = "SELECT DES_IMP, IP_IMP FROM Imps WHERE IP_IMP IS NOT NULL";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Imps imp = new PostData_Imps();
                imp.des_imp = cursor.getString(cursor.getColumnIndex("DES_IMP"));
                imp.ip_imp = cursor.getString(cursor.getColumnIndex("IP_IMP"));
                imps.add(imp);
            } while (cursor.moveToNext());
        }

        // return supplement list
        return imps;
    }

    //UPDATING ... /////////////////////////////////////////////////////////////////////////////////
    //================================== UPDATE TABLE (Bon1) =======================================
    public void Update_bon(SQLiteDatabase db, String table, String nbr_p, String total_ht, String num_bon, String emporter){
        if(db == null){
            db = this.getWritableDatabase();
        }
        String _table = table;
        ContentValues args = new ContentValues();
        args.put("NBR_P", nbr_p);
        args.put("TOTAL_TTC", total_ht);
        args.put("EMPORTER", emporter);
        String selection = "NUM_BON=?";
        String[] selectionArgs = {num_bon.toString()};
        db.update(_table, args, selection, selectionArgs);
    }

    //=========== UPDATE TABLE (Tables) FOR NEW RESERVATION ======================
    public String Update_reserved_table(SQLiteDatabase db, String numtable, String date_time_now){
        if(db == null){
            db = this.getWritableDatabase();
        }
        ContentValues args = new ContentValues();
        String  max = String.valueOf(Integer.parseInt(Select_max_num_bon(db)) + 1);
        max = Get_Digits_String(max,6);
        args.put("NUM_BON",max);
        args.put("DATE_TIME_RESERVE",date_time_now);
        String selection = "TABLE_NUMBER=?";
        String[] selectionArgs = {numtable.toString()};
        int i = db.update("Tables", args, selection, selectionArgs);
        String new_numbon = String.valueOf(max);
        return new_numbon;
    }

    public String Get_Digits_String(String number, Integer length){
        String _number = number;
        while(_number.length() < length){
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }

    //=============== UPDATE TABLE (Tables) FOR CHANGE TABLE =======================
    public void Update_change_table(String numbon, String old_numtable, String new_numtable){
        ArrayList<PostData_Tables> data_table = new ArrayList<>();
        try {
            String querry_sql = "SELECT * FROM Tables WHERE NUM_BON = '" + numbon + "'";
            data_table = Select_tables_from_sqlite_database(querry_sql);
        }catch (Exception e){
            Log.e("TRACKKK","ERROR GETTING TABLES INFOS - CHANGE TABLE // " + "/ Message : " + e.getMessage());
        }

        if(db == null){
            db = this.getWritableDatabase();
        }
        try {
            db.beginTransaction();
            ContentValues args = new ContentValues();
            args.putNull("NUM_BON");
            args.putNull("DATE_TIME_RESERVE");
            args.putNull("NOM_SERVEUR");
            String selection = "TABLE_NUMBER=?";
            String[] selectionArgs = {old_numtable.toString()};
            db.update("Tables", args, selection, selectionArgs);

            ContentValues args1 = new ContentValues();
            args1.put("NUM_BON",data_table.get(0).num_bon);
            args1.put("DATE_TIME_RESERVE",data_table.get(0).Table_date_time);
            args1.put("NOM_SERVEUR",data_table.get(0).nom_serveur);
            String selection1 = "TABLE_NUMBER=?";
            String[] selectionArgs1 = {new_numtable.toString()};
            db.update("Tables", args1, selection1, selectionArgs1);
            db.setTransactionSuccessful();

        } catch (Exception e){
            Log.e("TRACKKK","ERROR UPDATE TABLES - CHANGE TABLE // " + "/ Message : " + e.getMessage());
        }finally {
            db.endTransaction();
        }

    }

    //================== GET MAX NUM_BON INTO TABLE Tables ===========================
    public String Select_max_num_bon(SQLiteDatabase db){
        String max = "0";
        String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM Tables WHERE NUM_BON IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.v("TRACKKK", "size " + cursor.getCount());
        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0) != null){
                    max   = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        return max;
    }

    //================================== UPDATE TABLE (Famille) ====================================
    public void Update_famille(String famille_name, String image_name){
        if(db == null){
            db = this.getWritableDatabase();
        }
        ContentValues args = new ContentValues();
        args.put("IMAGE_INDEX", image_name);
        String selection = "FAMILLE=?";
        String[] selectionArgs = {famille_name.toString()};
        db.update("Famille", args, selection, selectionArgs);
    }


    //================================== UPDATE TABLE (Menu) ====================================
    public void Update_Menu(String menu_name, String image_name){
        if(db == null){
            db = this.getWritableDatabase();
        }
        ContentValues args = new ContentValues();
        args.put("IMAGE_INDEX", image_name);
        String selection = "DESIGNATION=?";
        String[] selectionArgs = {menu_name.toString()};
        db.update("Menu", args, selection, selectionArgs);
    }

    //================================== UPDATE TABLE (Users) ====================================
    public void Update_users(String image_name){
        if(db == null){
            db = this.getWritableDatabase();
        }
        ContentValues args = new ContentValues();
        args.put("IMAGE_PRO", image_name);
        db.update("Users", args, null, null);
    }
    //======================================================================================

    //================================== UPDATE TABLE (Users) ====================================
    public void Update_list_imprimentes(String des_imp, String ip_imp){
        if(db == null){
            db = this.getWritableDatabase();
        }
        ContentValues args = new ContentValues();
        args.put("IP_IMP", ip_imp);
        String selection = "DES_IMP=?";
        String[] selectionArgs = {ip_imp.toString()};
        db.update("Imps", args, selection, selectionArgs);
    }
    //======================================================================================

    public String select_total_ttc_from_bon1(String query){
        String _TTC = "0";
        if(db == null){
            db = this.getWritableDatabase();
        }
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                _TTC = cursor.getString(cursor.getColumnIndex("TOTAL_TTC"));
                break;
            } while (cursor.moveToNext());
        }
        return  _TTC;
    }
}