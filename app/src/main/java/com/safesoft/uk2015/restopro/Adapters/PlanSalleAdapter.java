package com.safesoft.uk2015.restopro.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.Activities.Main_SafeSoft;
import com.safesoft.uk2015.restopro.PostData.PostData_Tables;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.UpdateTime.RelativeTimeTextView;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by UK2015 on 21/06/2016.
 */
public class PlanSalleAdapter extends ArrayAdapter<PostData_Tables>{

    private LayoutInflater inflater;
    private ArrayList<PostData_Tables> datas;
    private Context _context;
    private DATABASE contoller;
    private String _currentwaiter;
    private Date date = null;

    public interface onDataSendEventListener {
        public void DataSendEvent(String s,  ArrayList<String> ARRAY_DATA);
    }
    onDataSendEventListener DataSendEventListener;

    public PlanSalleAdapter(Context context, int textViewResourceId, ArrayList<PostData_Tables> objects, String waiter) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        inflater = ((Activity) context).getLayoutInflater();
        datas = objects;
        _context = context;
        contoller = new DATABASE(_context);
        _currentwaiter = waiter;

        try {
            DataSendEventListener = (onDataSendEventListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onDataSendEventListener");
        }
    }

    static class ViewHolder {
        ImageView postImageView;
        TextView postNameView;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.plan_salle_row, null);
            viewHolder = new ViewHolder();
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        createL(convertView, datas.get(position).Table_number,
                datas.get(position).Table_date_time,
                datas.get(position).num_bon, datas.get(position).montant_bon , position);
        convertView.setTag(viewHolder);

        return convertView;
    }



    public void createL(View convertView, final String numTable, String date_time, final String num_Bon, final String montant_bon, final Integer position) {

        final String _num_bon = num_Bon;
        final String _numtable = numTable;
        final String _date_time = date_time;
        final String _total_ttc = montant_bon;

        final RelativeLayout LL = new RelativeLayout(_context);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(120, 120);
        LL.setLayoutParams(LLParams);

        if(!(_num_bon == null)){
            LL.setBackgroundResource(R.drawable.selector_reserved_table);
            // Creating a new TextView
            TextView title = new TextView(_context);
            title.setText("Table " + _numtable);
            title.setTextSize(15);



            RelativeTimeTextView timme = new RelativeTimeTextView(_context);
            timme.setTextSize(10);
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = (Date)df.parse(_date_time);
                timme.setReferenceTime( date.getTime());
            }catch (Exception e){

            }

            //TextView total = new TextView(_context);
            //title.setText(" " + _total_ttc);
            //title.setTextSize(10);

            // Defining the layout parameters of the TextView
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.CENTER_IN_PARENT);

            // Setting the parameters on the TextView
            title.setLayoutParams(lp1);

            // Defining the layout parameters of the TextView
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_BOTTOM | RelativeLayout.CENTER_HORIZONTAL);
            lp2.setMargins(0,20,0,10);
            timme.setLayoutParams(lp2);

            // Defining the layout parameters of the TextView
           /* LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp3.addRule(LinearLayout.ALIGN_BOTTOM);
            lp3.setMargins(0,0,0,0);
            total.setLayoutParams(lp3);
*/
            LL.addView(title);
            LL.addView(timme);

         //   LL.addView(total);

        }else{
            LL.setBackgroundResource(R.drawable.selector_normal_table);
            // Creating a new TextView
            TextView title = new TextView(_context);
            title.setText("Table " + _numtable);
            title.setTextSize(15);
            // Defining the layout parameters of the TextView
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
            title.setLayoutParams(lp1);
            LL.addView(title);
        }

        RelativeLayout rl = ((RelativeLayout) convertView.findViewById(R.id.root));
        rl.addView(LL);

        LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> ARRAY_DATA = new ArrayList<>();
                if(!(_num_bon == null)){
                    if(datas.get(position).nom_serveur != null){
                        if(_currentwaiter.equals(datas.get(position).nom_serveur)){
                            Vibrator vb = (Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE);
                            vb.vibrate(100);

                         //   String total_ht = contoller.Select_data_from_bon1(_num_bon);
                            ARRAY_DATA.add(_num_bon);
                            ARRAY_DATA.add(_numtable);
                            if(_total_ttc != null){
                                ARRAY_DATA.add(_total_ttc);
                            }else
                                ARRAY_DATA.add("0.00");
                            ARRAY_DATA.add(_currentwaiter);
                            //=============
                            DataSendEventListener.DataSendEvent("FROM_PLAN_SALLE_ADAPTER_RESERVED", ARRAY_DATA);
                        }else{
                            Crouton.showText(((Main_SafeSoft)_context), "Accès réfusé ! droit réserver au serveur " + datas.get(position).nom_serveur, Style.ALERT);
                        }
                    }else{
                        Vibrator vb = (Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(100);

                       // String total_ht = contoller.Select_data_from_bon1(_num_bon);
                        ARRAY_DATA.add(_num_bon);
                        ARRAY_DATA.add(_numtable);
                        if(_total_ttc != null){
                            ARRAY_DATA.add(_total_ttc);
                        }else
                            ARRAY_DATA.add("0.00");
                        ARRAY_DATA.add(_currentwaiter);
                        //=============
                        DataSendEventListener.DataSendEvent("FROM_PLAN_SALLE_ADAPTER_RESERVED", ARRAY_DATA);
                    }
                }else{

                    new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Reserver la table")
                            .setContentText("voulez-vous reserver cette table ?!")
                            .setCancelText("Annuler")
                            .setConfirmText("Reserver!")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    Date date = new Date();
                                    String date_time_now = df.format(date);

                                   // final String new_numbon = contoller.ExecuteTransactionReserve(_numtable,date_time_now);
                                    ARRAY_DATA.add("000001");
                                    ARRAY_DATA.add(_numtable);
                                    ARRAY_DATA.add("0.00");
                                    ARRAY_DATA.add(_currentwaiter);
                                    ARRAY_DATA.add(date_time_now);
                                    DataSendEventListener.DataSendEvent("FROM_PLAN_SALLE_ADAPTER_NON_RESERVED",ARRAY_DATA);
                                    //===========
                                    sDialog.dismiss();
                                }
                            }).show();
                }
                ARRAY_DATA.clear();
            }
        });
    }

    public void refreshplansall(ArrayList<PostData_Tables> tables){
        datas.clear();
        datas.addAll(tables);
        notifyDataSetChanged();
    }

}