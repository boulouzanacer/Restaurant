package com.safesoft.uk2015.restopro.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.uk2015.restopro.Adapters.ListViewAdapter_Spinner;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.Pos;
import com.safesoft.uk2015.restopro.PostData.FoodsBean;
import com.safesoft.uk2015.restopro.PostData.PostData_Imps;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by UK2015 on 02/08/2016.
 */
public class PrintingFragment extends Fragment {

    private Context mContext;
    private ArrayList<PostData_Imps> list_imprements;
    private DATABASE controller;
    private String[] arraySpinner_list_imps;
    private String[] arraySpinner_ip_imps;
    private String[] arraySpinner_list_imps_ticket;
    private String[] arraySpinner_ip_imps_ticket;
    private String current_des_imp;
    private String current_des_imp_ticket;
    private String current_ip_imp;
    private String current_ip_imp_ticket;
    private String current_model_ticket;
    private String TICKET_IP_PRINTER_PREFRS = "CONFIG_TICKET_PRINTER";
    private String TICKET_MODEL_TICKETS_PREFRS = "CONFIG_TICKET_PRINTER";
    private Integer Position_printer;


    public PrintingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new DATABASE(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_printing, container, false);

        mContext = getActivity();
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ImperatorBronze.ttf");
        TextView tv = (TextView) rootView.findViewById(R.id.title_printer_configuration);
        tv.setTypeface(tf);

        TextView title_reciep_printer_configuration = (TextView) rootView.findViewById(R.id.title_reciep_printer_configuration);
        title_reciep_printer_configuration.setTypeface(tf);

        final EditText port = (EditText) rootView.findViewById(R.id.port);


        Button btnTest = (Button) rootView.findViewById(R.id.test);
        Button btnTest_ticket = (Button) rootView.findViewById(R.id.test_ticket);

        Spinner list_imp = (Spinner) rootView.findViewById(R.id.spinner_list_imp);
        final Spinner list_imp_ticket = (Spinner) rootView.findViewById(R.id.spinner_list_imp1);
        final Spinner list_model_ticket = (Spinner) rootView.findViewById(R.id.spinner_model_tickets);

        list_imprements = controller.select_imprimentes_from_sqlite_database();
        this.arraySpinner_list_imps = new String[list_imprements.size()];
        this.arraySpinner_ip_imps = new String[list_imprements.size()];
        this.arraySpinner_list_imps_ticket = new String[list_imprements.size()+1];
        this.arraySpinner_ip_imps_ticket = new String[list_imprements.size()+1];
        this.arraySpinner_ip_imps_ticket = new String[list_imprements.size()+1];
        int indice = 0;
        for(int i = 0; i < list_imprements.size();i++){
                arraySpinner_list_imps[i] = list_imprements.get(i).des_imp;
                arraySpinner_ip_imps[i] = list_imprements.get(i).ip_imp;

                arraySpinner_list_imps_ticket[i] = list_imprements.get(i).des_imp;
                arraySpinner_ip_imps_ticket[i] = list_imprements.get(i).ip_imp;

            indice++;
        }

        arraySpinner_list_imps_ticket[indice] = "Aucune";
        arraySpinner_ip_imps_ticket[indice] = "0.0.0.0";

        // Setting a Custom Adapter to the Spinner
        list_imp.setAdapter(new ListViewAdapter_Spinner(mContext, R.layout.spinner_rows, arraySpinner_list_imps, arraySpinner_ip_imps));
        list_imp_ticket.setAdapter(new ListViewAdapter_Spinner(mContext, R.layout.spinner_rows, arraySpinner_list_imps_ticket, arraySpinner_ip_imps_ticket));

        list_imp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
                // TODO Auto-generated method stub

                current_des_imp =  arraySpinner_list_imps[position];
                current_ip_imp = arraySpinner_ip_imps[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


        list_imp_ticket.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
                // TODO Auto-generated method stub
                current_des_imp_ticket =  arraySpinner_list_imps_ticket[position];
                current_ip_imp_ticket = arraySpinner_ip_imps_ticket[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


        list_model_ticket.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
                // TODO Auto-generated method stub
                current_model_ticket =  (String) arg0.getSelectedItem();
                SharedPreferences.Editor editor = mContext.getSharedPreferences(TICKET_MODEL_TICKETS_PREFRS, MODE_PRIVATE).edit();
                editor.putString("MODEL_TICKET", current_model_ticket);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean executed = false;
                try {
                    CheckConnexionTask checkconnexion =   new CheckConnexionTask(current_ip_imp.toString(), Integer.parseInt(port.getText().toString()));
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                        checkconnexion.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        executed  = checkconnexion.get();
                    }else
                        executed = checkconnexion.execute().get();

                    if(executed){

                        Toast.makeText(getActivity(), current_model_ticket,Toast.LENGTH_LONG).show();
                        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Connexion success")
                                .setContentText("Voulez-vous Imprimer une copie de test sur l'imprimente ( "+ current_des_imp +" ) ?!")
                                .setCancelText("Annuler")
                                .setConfirmText("Imprimer!")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // re-use previous dialog instance, keep widget user state, reset them if you need
                                        sDialog.dismiss();

                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        PrintTestTask printtast = new PrintTestTask(current_ip_imp.toString(), Integer.parseInt(port.getText().toString()));
                                        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                                            printtast.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        }else
                                           printtast.execute();
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oups !")
                                .setContentText("Il y'a un problème de connexion avec l'imprimente ( " + current_des_imp+" )")
                                .setConfirmText("OK")
                                .show();
                    }
                }catch(Exception e){
                    Log.v("TRACKKK","Problem when executed the test : " + e.getMessage());
                }
            }
        });


        btnTest_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean executed = false;
                try {
                    if(!current_des_imp_ticket.equals("Aucune")){
                        CheckConnexionTask checkconnexion =   new CheckConnexionTask(current_ip_imp.toString(), Integer.parseInt(port.getText().toString()));
                        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                            checkconnexion.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            executed  = checkconnexion.get();
                        }else
                            executed = checkconnexion.execute().get();
                    }else{
                        executed = true;
                    }


                    if(executed){
                        SharedPreferences.Editor editor = mContext.getSharedPreferences(TICKET_IP_PRINTER_PREFRS, MODE_PRIVATE).edit();
                        editor.putString("IP_PRINTER", current_ip_imp_ticket);
                        editor.putInt("POSITION_PRINTER", list_imp_ticket.getSelectedItemPosition());
                        editor.commit();
                        if(current_des_imp_ticket.equals("Aucune")){
                            new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Réussi !")
                                    .setContentText("Parametres sauvegarder")
                                    .setConfirmText("OK")
                                    .show();
                        }else {
                            new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Connexion success")
                                    .setContentText("Voulez-vous Imprimer une copie de test sur l'imprimente ( "+ current_des_imp +" ) ?!")
                                    .setCancelText("Annuler")
                                    .setConfirmText("Imprimer!")
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            // re-use previous dialog instance, keep widget user state, reset them if you need
                                            sDialog.dismiss();
                                        }
                                    })
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            PrintTestTask printtast = new PrintTestTask(current_ip_imp_ticket, Integer.parseInt(port.getText().toString()));
                                            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                                                printtast.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            }else
                                                printtast.execute();
                                            sDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }

                    }else{
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oups !")
                                .setContentText("Il y'a un problème de connexion avec l'imprimente ( " + current_des_imp+" )")
                                .setConfirmText("OK")
                                .show();
                    }
                }catch(Exception e){
                    Log.v("TRACKKK","Problem when executed the test : " + e.getMessage());
                }
            }
        });

        SharedPreferences prefs = mContext.getSharedPreferences(TICKET_IP_PRINTER_PREFRS, MODE_PRIVATE);
        Position_printer = prefs.getInt("POSITION_PRINTER", 0);
        list_imp_ticket.setSelection(Position_printer);


        return rootView;
    }


    private class CheckConnexionTask extends AsyncTask<Void, Void, Boolean> {

        private String _Server;
        private Integer _Port;
        private Boolean executed = false;
        public CheckConnexionTask(String ip, Integer port){
            _Server = ip;
            _Port = port;
        }
        protected Boolean doInBackground(Void... urls) {

            try {
                Socket socket = new Socket(_Server, _Port);
                socket.setSoTimeout(300);
                final OutputStream socketOutputStream = socket.getOutputStream();
                executed = true;

                socketOutputStream.flush();
                socketOutputStream.close();
                socket.close();

            }catch (Exception e){
                executed = false;
                Log.v("TRACKKK","Can't connect to the printer : " + e.getMessage());
            }

            return executed;
        }
    }

    private class PrintTestTask extends AsyncTask<Void, Void, Boolean> {

        private String _Server;
        private Integer _Port;
        private Boolean executed = false;
        public PrintTestTask(String ip, Integer port){
            _Server = ip;
            _Port = port;
        }
        protected Boolean doInBackground(Void... urls) {

            try {
                Socket socket = new Socket(_Server, _Port);
                socket.setSoTimeout(300);
                final OutputStream socketOutputStream = socket.getOutputStream();
                executed = true;

                SharedPreferences prefs = mContext.getSharedPreferences(TICKET_MODEL_TICKETS_PREFRS, MODE_PRIVATE);
                final String model_ticket = prefs.getString("MODEL_TICKET", "Petit model");
                if(model_ticket.equals("Petit model")){
                    String title = "THIS IS JUST A COPY FROM "+ current_des_imp +" PRINTER\n";
                    socketOutputStream.write(title.getBytes("iso-8859-1"));
                    socketOutputStream.write("  THIS IS JUST A TEST OF THE CONNEXION\n".getBytes("iso-8859-1"));
                    socketOutputStream.write(" ***** BHAGATRAM SWEETS RESTAURANT *****\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    socketOutputStream.write(("DATE : ----> "+currentDateandTime+"\n").getBytes("iso-8859-1"));
                    socketOutputStream.write("POWRED BY SARL SAFE SOFT\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("-------------------------------------------\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("SANDWISH ESCALOPE            2    200.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("PAPAYA JUICE                 1    250.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("MANGO JUICE                  3    250.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("MSHKAKI                      1    350.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("GOLA KEBAB                   1    400.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("COCA                         1    100.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("-------------------------------------------\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("Purchased item total :           1550.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("Sign Up and Save !\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("With Preferred Saving Card\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("-------------------------------------------\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("  ALL RIGHT RESERVED FOR SAFE SOFT COMPANY \n".getBytes("iso-8859-1"));


                    socketOutputStream.write("  الحمد لله \n".getBytes("PC864"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("CS_CP720"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("CP1256"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("Cp858"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("iso-8859-5"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("iso-8859-6"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("iso-8859-7"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("iso-8859-8"));
                    socketOutputStream.write("  الحمد لله \n".getBytes("iso-8859-1"));

                    socketOutputStream.write("  ALL RIGHT RESERVED FOR SAFE SOFT COMPANY \n".getBytes("iso-8859-1"));

                }else if(model_ticket.equals("Large model")){
                    String title = "THIS IS JUST A COPY FROM "+ current_des_imp +" PRINTER\n";
                    socketOutputStream.write(title.getBytes("iso-8859-1"));
                    socketOutputStream.write("      THIS IS JUST A TEST OF THE CONNEXION\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("     ***** BHAGATRAM SWEETS RESTAURANT *****\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    socketOutputStream.write(("DATE : ----> "+currentDateandTime+"\n").getBytes("iso-8859-1"));
                    socketOutputStream.write("POWRED BY SARL SAFE SOFT\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("-----------------------------------------------\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("SANDWISH ESCALOPE                2    200.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("PAPAYA JUICE                     1    250.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("MANGO JUICE                      3    250.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("MSHKAKI                          1    350.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("GOLA KEBAB                       1    400.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("COCA                             1    100.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("-----------------------------------------------\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("Purchased item total :               1550.00 DA\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("Sign Up and Save !\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("With Preferred Saving Card\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("-----------------------------------------------\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("\n".getBytes("iso-8859-1"));
                    socketOutputStream.write("     ALL RIGHT RESERVED FOR SAFE SOFT COMPANY \n".getBytes("iso-8859-1"));
                }



                    socketOutputStream.write(new byte[]{0x0A});
                    socketOutputStream.write(new byte[]{0x0A});
                    socketOutputStream.write(new byte[]{0x0A});
                    socketOutputStream.write(new byte[]{0x0A});

                    socketOutputStream.write(0x1D);
                    socketOutputStream.write(86);
                    socketOutputStream.write(48);
                    socketOutputStream.write(0);



                socketOutputStream.flush();
                socketOutputStream.close();
                socket.close();

            }catch (Exception e){
                executed = false;
                Log.v("TRACKKK","Can't connect to the printer : " + e.getMessage());
            }

            return executed;
        }
    }



}
