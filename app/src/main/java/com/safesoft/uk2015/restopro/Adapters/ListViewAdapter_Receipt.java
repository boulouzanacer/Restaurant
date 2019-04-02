package com.safesoft.uk2015.restopro.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 24/06/2016.
 */
public class ListViewAdapter_Receipt extends BaseAdapter {

    ArrayList<PostData_Bon2> received_receipt = new ArrayList<PostData_Bon2>();
    private static LayoutInflater inflater=null;
    Context context;

    public interface onReceiptSendEventListener {
        public void ReceiptSendEvent(String s, ArrayList<PostData_Bon2> items_receip);
    }
    onReceiptSendEventListener ReceiptSendEventListener;

    public ListViewAdapter_Receipt(Context mainActivity, ArrayList<PostData_Bon2> itemList) {
        // TODO Auto-generated constructor stub
        received_receipt = itemList;
        context=mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            ReceiptSendEventListener = (onReceiptSendEventListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onReceiptSendEventListener");
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return received_receipt.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return received_receipt.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder
    {
        TextView item;
        TextView quantity;
        TextView price;
        View em_indicator;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder ;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.reciept_row, null);

                holder.item = (TextView) convertView.findViewById(R.id.item);
                holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.em_indicator = (View) convertView.findViewById(R.id.emporter_indicator);
              //  Drawable drawablePic = context.getResources().getDrawable(R.drawable.prix_bg);
              //  LinearLayout sup_suppliment = (LinearLayout) convertView.findViewById(R.id.sup_suppliment);
             //   sup_suppliment.setBackground(drawablePic);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(received_receipt.get(position).Code !=null){
            convertView.setBackgroundResource(R.drawable.selector_listview_product_row);
            holder.item.setTextSize(18);
            holder.item.setTypeface(null, Typeface.BOLD);
            holder.quantity.setTextSize(18);
            holder.quantity.setTypeface(null, Typeface.BOLD);
            holder.price.setTextSize(18);
            holder.price.setTypeface(null, Typeface.BOLD);
            holder.item.setText(received_receipt.get(position).Produit);
            holder.quantity.setText(received_receipt.get(position).Quantity);
            holder.price.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(received_receipt.get(position).Price)));
                if((received_receipt.get(position).Emporter == null)||(received_receipt.get(position).Emporter.equals("0")))
                {
                    holder.em_indicator.setBackgroundResource(R.color.transparent);
                }else{
                    holder.em_indicator.setBackgroundResource(R.color.Blue_Peter_river);
                }
        }else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.clouds));
            convertView.setClickable(false);
            //convertView.setFocusable(false);
            convertView.setFocusableInTouchMode(false);
            holder.item.setTextSize(13);
            holder.item.setTypeface(null, Typeface.NORMAL);
            holder.quantity.setTextSize(13);
            holder.quantity.setTypeface(null, Typeface.NORMAL);
            holder.price.setTextSize(13);
            holder.price.setTypeface(null, Typeface.NORMAL);
            holder.item.setText(received_receipt.get(position).Produit);
            holder.quantity.setText(received_receipt.get(position).Quantity);
            holder.price.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(received_receipt.get(position).Price)));
            holder.em_indicator.setBackgroundResource(R.color.transparent);
        }

        return convertView;
    }

    public void Refresh_list(){
        notifyDataSetChanged();
        ReceiptSendEventListener.ReceiptSendEvent("FROM_RECEIPT_ADAPTER",received_receipt);
    }

    public void Add_To_LisView(PostData_Bon2 rec){
        received_receipt.add(rec);
        notifyDataSetChanged();
        ReceiptSendEventListener.ReceiptSendEvent("FROM_RECEIPT_ADAPTER",received_receipt);
    }

    public void Add_To_LisViewInPosition(int asPosition, PostData_Bon2 rec){
        received_receipt.add(asPosition, rec);
        notifyDataSetChanged();
        ReceiptSendEventListener.ReceiptSendEvent("FROM_RECEIPT_ADAPTER",received_receipt);
    }

    public ArrayList<PostData_Bon2> Remove_from_LisView(int position){
        try {
            if(Integer.parseInt(received_receipt.get(position).Quantity) > 1 ){
                received_receipt.get(position).Quantity = String.valueOf((Integer.parseInt(received_receipt.get(position).Quantity)) - 1);
            }else{
                received_receipt.remove(position);
            }
        }catch (NumberFormatException e){
            Log.e("TRACKKK","ERREUR CAST QUANTITY RECIEPT WHEN REMOVING ITEM FROM RECEIPT : "+e.getMessage());
        }
        notifyDataSetChanged();
        ReceiptSendEventListener.ReceiptSendEvent("FROM_RECEIPT_ADAPTER",received_receipt);
        return received_receipt;
    }

}
