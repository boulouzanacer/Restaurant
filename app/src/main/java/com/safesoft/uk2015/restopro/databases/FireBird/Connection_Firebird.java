package com.safesoft.uk2015.restopro.databases.FireBird;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * Created by UK2015 on 31/05/2016.
 */
public class Connection_Firebird extends absDB implements IMDB{

    private static final int CONNECT_DRIVERMANAGER = 1;
    private static final int CONNECT_DRIVER = 2;
    private String Server;
    private String Database;
    private String Username;
    private String Password;
    protected Connection con = null;
    Boolean etatCon = false;
    //   private UserLoginTask mAuthTask = null;

    public Connection_Firebird() {

        //Here you should get data connection from shared preferences
        Server = "192.168.1.40";
        Database = "TEST";
        Username = "SYSDBA";
        Password = "masterkey";
        con = null;
    }

    public Connection_Firebird(String server, String db, String user, String pwd) {
        Server = server;
        Database = db;
        Username = user;
        Password = pwd;
        con = null;
    }

    @Override
    public boolean CreateDB() {
        return false;
    }

    @Override
    public boolean Connect() {
        Insert_Into_FireBird_DataBase mAuthTask1 = new Insert_Into_FireBird_DataBase();
        UserLoginTask mAuthTask2 = new UserLoginTask();


        Log.v("TRACKKK", " 1 = "+ etatCon);
        try {
            mAuthTask1.execute().get();
            Log.v("TRACKKK", " Between = "+ etatCon);
            etatCon = false;
            mAuthTask2.execute().get(100, TimeUnit.MILLISECONDS);
          //  Thread.sleep(500);
        }catch(Exception e){

        }
        Log.v("TRACKKK", " 2 = "+ etatCon);
        return etatCon;
    }

    @Override
    public void Disconnect() {
        try {
            if (con == null) {
                return;
            }
            System.out.println("connection closed!!");
            con.close();
        } catch (SQLException e) {
            System.out.println("Unable to disconnect a connection.");
            showSQLException(e);
        }
    }

    @Override
    public boolean StartTransAction() {
        try {
            con.setAutoCommit(false);
            System.out.println("Auto-commit is disabled.");
        } catch (java.sql.SQLException e) {
            System.out.println("Unable to disable autocommit.");
            showSQLException(e);
            return false;
        }

        return true;
    }

    @Override
    public boolean CommitTransAction() {
        try {
            con.commit();
            System.out.println("Statement commit.");
            con.setAutoCommit(true);
            System.out.println("Auto-commit is enabled.");
        } catch (java.sql.SQLException e) {
            System.out.println("Unable to disable autocommit.");
            showSQLException(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean RollbackTransAction() {
        try {
            con.rollback();
            System.out.println("Statement rollback.");
        }
        catch (java.sql.SQLException e) {
            System.out.println("Unable to rollback statement.");
            showSQLException(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean BackupTo() {
        return false;
    }

    @Override
    public boolean IsValid() {
        boolean bool = false;
        try {
            bool=con.isValid(1000);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;
    }

    //class execute functions in background
    //====================================
    public class UserLoginTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try{
                System.setProperty("FBAdbLog", "true");
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:"+Server+":C:/"+Database+".FDB?encoding=ISO8859_1";
                if(con == null){

                }
                Connection con = DriverManager.getConnection(sCon, "SYSDBA", "masterkey");
                etatCon = true;
                String sql = "SELECT * FROM CLIENT";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while(rs.next()){
                    Log.v("TRACKKK", "Client : "+ rs.getString("RECORDID")+ " " + rs.getString("NAME") + rs.getString("PHONE") );
                }
                Log.v("TRACKKK", "==============================================");
                rs.close();

            }
            catch(Exception e){
                Log.e("FirebirdExample", e.getMessage());
                etatCon = false;
            }
            return null;
        }

    }
    //==================================================


    //class Insert Data into FireBird Database
    //====================================
    public class Insert_Into_FireBird_DataBase extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try{
                System.setProperty("FBAdbLog", "true");
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:"+Server+":C:/"+Database+".FDB?allowMultiQueries=true?encoding=ISO8859_1";
                if(con == null){
                   // Connection con = DriverManager.getConnection(sCon, "SYSDBA", "masterkey");
                }
                Connection con = DriverManager.getConnection(sCon, "SYSDBA", "masterkey");
                Statement stmt = con.createStatement();
                con.setAutoCommit(false);
                String sql = "INSERT INTO CLIENT VALUES ('1','NASSER','2222222')";
                String sql2 = "INSERT INTO CLIENT VALUES ('2','SALIM','3333333')";
                stmt.addBatch(sql);
               stmt.addBatch(sql2);
                stmt.executeBatch();
               // Statement stmt1 = con.createStatement();
                con.commit();
                //con.setAutoCommit(true);
                etatCon = true;
            }
            catch(Exception e){
                Log.e("FirebirdExample", e.getMessage());
                etatCon = false;
            }
            return null;
        }

    }
    //==================================================

}
