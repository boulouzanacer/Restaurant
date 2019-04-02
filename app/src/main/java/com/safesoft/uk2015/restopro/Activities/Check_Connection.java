package com.safesoft.uk2015.restopro.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.FireBird.BizApp;
import com.safesoft.uk2015.restopro.databases.FireBird.function_FB;

/**
 * Created by UK2015 on 31/05/2016.
 */
public class Check_Connection extends AppCompatActivity {
    Context cntx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_connection);

        cntx = this.getApplicationContext();
        Button check =(Button) findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BizApp bizApp =  new function_FB();
               // IMDB imdb = new Connection_Firebird();
               // imdb.StartTransAction();
               // imdb.CommitTransAction();

                EditText edt1 = (EditText) findViewById(R.id.ipadress);
                EditText edt2 = (EditText) findViewById(R.id.database);
                TextView etat = (TextView) findViewById(R.id.textView3);
                String adress = edt1.getText().toString();
                String database = edt2.getText().toString();
                String usename = "SYSDBA";
                String password = "masterkey";

                boolean isConnect = false;
                try {  //isConnect = function_firebird.

                    isConnect = bizApp.GetFBDB(adress, database, usename, password);

                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Check_Connection.this);
                    builder.setMessage("Can't connect to server").show();
                    return;
                }
                if (!isConnect) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Check_Connection.this);
                    builder.setMessage("Can't connect to database ");
                    builder.show();
                } else {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Check_Connection.this);
                        builder.setMessage("Success");
                        builder.show();
                        //bizApp.DownloadParams();
                        // bizApp.DownloadPrinters();
                        //bizApp.DownloadFormules();
                    } catch (Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Check_Connection.this);
                        builder.setMessage("Parametre and download printer Exception").show();
                        return;
                    }
                }
            }
        });
    }
}
