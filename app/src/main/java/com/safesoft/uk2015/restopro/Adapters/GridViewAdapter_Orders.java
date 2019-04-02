package com.safesoft.uk2015.restopro.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.Activities.Main_SafeSoft;
import com.safesoft.uk2015.restopro.PostData.PostData_orders;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.LoadDataFromSQLite;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by UK2015 on 22/06/2016.
 */
public class GridViewAdapter_Orders extends RecyclerView.Adapter<GridViewAdapter_Orders.MyViewHolder> {

    private Context _context;

    private ArrayList<PostData_orders> _dataorders;

    private ArrayList<PostData_Bon2> _datareceipt;

    LoadDataFromSQLite data;

    private String _currentwaiter;
    private LayoutInflater inflater;

    private int previousPosition = 0;
    public interface onSomeEventListener {
        public void someEvent(String s, String Value);
    }

    onSomeEventListener someEventListener;


    public GridViewAdapter_Orders(Context context, ArrayList<PostData_orders> dataOrders, String waiter) {

        this._context = context;
        this._dataorders = dataOrders;
        data = new LoadDataFromSQLite(_context);
        _datareceipt =new ArrayList<>();
        inflater = LayoutInflater.from(context);
        _currentwaiter = waiter;
        try {
            someEventListener = (onSomeEventListener) _context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.orders_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {

        myViewHolder.num_tab.setText("Table # " + _dataorders.get(position).id);
        myViewHolder.server.setText(_dataorders.get(position).server);
        myViewHolder.orderN.setText(_dataorders.get(position).num_bon);
        myViewHolder.date_time.setText(_dataorders.get(position).date_time);
        if(_dataorders.get(position).Total != null) {
            myViewHolder.total.setText(" " + new DecimalFormat("##,##0.00").format(Double.valueOf(_dataorders.get(position).Total.replace(",",".").replace(" ",""))));
        }else{
            myViewHolder.total.setText(" " + 0.00);
        }
            _datareceipt.clear();
            _datareceipt = data.getDataReceipt(_dataorders.get(position).num_bon);
            create_tablelayout(myViewHolder.itemView, _datareceipt);
        //==================================================

        previousPosition = position;
        final int currentPosition = position;
        final PostData_orders infoData = _dataorders.get(position);
    }

    @Override
    public int getItemCount() {
        return _dataorders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView num_tab;
        TextView server;
        TextView orderN;
        TextView date_time;
        TextView total;
        LinearLayout linea_panel_one_order;

        public MyViewHolder(View itemView) {
            super(itemView);

            num_tab = (TextView) itemView.findViewById(R.id.num_table);
            server = (TextView) itemView.findViewById(R.id.server);
            orderN = (TextView) itemView.findViewById(R.id.num_bon);
            date_time = (TextView) itemView.findViewById(R.id.date_time);
            total = (TextView) itemView.findViewById(R.id.total);
            linea_panel_one_order = (LinearLayout) itemView.findViewById(R.id.linea_panel_one_order);
            linea_panel_one_order.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!server.getText().equals("")){
                        if(_currentwaiter.equals(server.getText().toString())){
                            new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Imprimer ticket")
                                    .setContentText("voulez-vous imprimer l'addition de cette table ?!")
                                    .setCancelText("Annuler")
                                    .setConfirmText("Imprimer!")
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
                                            String num_bon = orderN.getText().toString();
                                            someEventListener.someEvent("FROM_ORDERS_ADAPTER", num_bon);
                                            //===========
                                            sDialog.dismiss();
                                        }
                                    }).show();
                        }else{
                            Crouton.showText(((Main_SafeSoft)_context), "Accès réfusé ! droit réserver au serveur " + server.getText().toString(), Style.ALERT);

                        }
                    }else{
                        new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Imprimer ticket")
                                .setContentText("voulez-vous imprimer l'addition de cette table ?!")
                                .setCancelText("Annuler")
                                .setConfirmText("Imprimer!")
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
                                        String num_bon = orderN.getText().toString();
                                        someEventListener.someEvent("FROM_ORDERS_ADAPTER", num_bon);
                                        //===========
                                        sDialog.dismiss();
                                    }
                                }).show();
                    }

                    return false;
                }
            });
        }
    }

    // This removes the data from our Dataset and Updates the Recycler View.
    private void removeItem(PostData_orders infoData) {
        int currPosition = _dataorders.indexOf(infoData);
        _dataorders.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, PostData_orders infoData) {
        _dataorders.add(position, infoData);
        notifyItemInserted(position);
    }


 public void create_tablelayout(View convertView, ArrayList<PostData_Bon2> receipt){


        // Find Tablelayout defined in main.xml
        TableLayout tablelayout = (TableLayout) convertView.findViewById(R.id.itemtablelayout);
        tablelayout.setBackgroundResource(android.R.color.transparent);
        tablelayout.removeAllViews();
        for(int position_r = 0; position_r < receipt.size(); position_r++) {

            //Prepare TableRow to be inserted
            TableRow row = new TableRow(_context);
            TableRow.LayoutParams layoutParams_item = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams_item.weight = 0.9f;
            layoutParams_item.setMargins(15, 0, 0, 0);

            TableRow.LayoutParams layoutParams_quantity = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams_quantity.weight = 0.05f;
            layoutParams_quantity.setMargins(0, 0, 25, 0);

            TextView Order1 = new TextView(_context);
            TextView Order2 = new TextView(_context);

            if(receipt.get(position_r).Code != null){
                // Creating a new TextView
                Order1.setText(receipt.get(position_r).Produit);
                Order1.setTextColor(Color.parseColor("#bdc3c7"));
                Order1.setTextSize(14);

                Order2.setText(new DecimalFormat("##,##0").format(Double.valueOf(receipt.get(position_r).Quantity)));
                Order2.setGravity(Gravity.RIGHT);
                Order2.setTextColor(Color.parseColor("#bdc3c7"));
                Order2.setTextSize(14);
                //===================
            }else{
                Order1.setText("====> " + receipt.get(position_r).Produit);
                Order1.setTextColor(Color.BLUE);
                Order1.setTextSize(10);

                Order2.setText(new DecimalFormat("##,##0").format(Double.valueOf(receipt.get(position_r).Quantity)));
                Order2.setGravity(Gravity.RIGHT);
                Order2.setTextColor(Color.BLUE);
                Order2.setTextSize(10);
            }


            row.addView(Order1,layoutParams_item);
            row.addView(Order2,layoutParams_quantity);

            tablelayout.addView(row);
        }


    }


}


