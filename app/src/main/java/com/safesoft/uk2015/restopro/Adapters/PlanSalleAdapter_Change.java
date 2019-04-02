package com.safesoft.uk2015.restopro.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.Fragment.TablesFragment_Change;
import com.safesoft.uk2015.restopro.Activities.Order_Activity;
import com.safesoft.uk2015.restopro.PostData.PostData_Tables;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by UK2015 on 21/06/2016.
 */
public class PlanSalleAdapter_Change extends ArrayAdapter<PostData_Tables>{

    private LayoutInflater inflater;
    private ArrayList<PostData_Tables> datas;
    private Context _context;
    private DATABASE contoller;
    private String current_numbon;
    private String current_numtable;

    public interface onDataSendEventListener {
        public void DataSendEvent(String s,  ArrayList<String> ARRAY_DATA);
    }
    onDataSendEventListener DataSendEventListener;

    public PlanSalleAdapter_Change(Context context,
                                   int textViewResourceId,
                                   ArrayList<PostData_Tables> objects,
                                   String numbon,
                                   String numtable) {

        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        inflater = ((Activity) context).getLayoutInflater();
        datas = objects;
        _context = context;
        contoller = new DATABASE(_context);
        current_numbon = numbon;
        current_numtable = numtable;

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
                    datas.get(position).num_bon, position);

        convertView.setTag(viewHolder);

        return convertView;
    }

    public void createL(View convertView, final String numTable, String Time, String num_Bon, final Integer position) {

        final String _num_bon = num_Bon;
        final String _numtable = numTable;
        final String _time = Time;
        final RelativeLayout LL = new RelativeLayout(_context);
        LL.setClickable(true);
        LL.setBackgroundResource(R.drawable.selector_normal_table);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(100, 100);
        LL.setLayoutParams(LLParams);

            // Creating a new TextView
            TextView title = new TextView(_context);
            title.setText("Table " + _numtable);
            title.setTextSize(15);
            title.setId(Integer.parseInt(_numtable));
            // Defining the layout parameters of the TextView
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
            title.setLayoutParams(lp1);
            LL.addView(title);

        RelativeLayout rl = ((RelativeLayout) convertView.findViewById(R.id.root));
        rl.addView(LL);

        LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Changement table!")
                        .setContentText("Etes-vous sure de changer cette table vers la table "+ numTable + " ?.")
                        .setCancelText("Annuler")
                        .setConfirmText("Changer")
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
                                ArrayList<String> ARRAY_DATA = new ArrayList<>();

                                ARRAY_DATA.add(current_numtable);
                                ARRAY_DATA.add(_numtable);
                                ARRAY_DATA.add(current_numbon);
                                DataSendEventListener.DataSendEvent("FROM_CHANGE_TABLE_ADAPTER",ARRAY_DATA);

                                Fragment prev = ((Order_Activity)_context).getFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    TablesFragment_Change df = (TablesFragment_Change) prev;
                                    df.dismiss();
                                }
                                //===========
                                sDialog.dismiss();
                            }
                        }).show();

            }
        });
    }
}