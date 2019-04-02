package com.safesoft.uk2015.restopro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 21/07/2016.
 */
public class ListViewAdapter_CheckedSupplement extends BaseAdapter {

    ArrayList<PostData_Bon2> _supplements = new ArrayList<>();
    final ArrayList<PostData_Bon2> listcheck_spp = new ArrayList<>();
    private static LayoutInflater inflater = null;
    Context context;


    public interface onReceiptSendEventListener {
        public void ReceiptSendEvent(String s, ArrayList<PostData_Bon2> items_receip);
    }
    onReceiptSendEventListener ReceiptSendEventListener;

    public ListViewAdapter_CheckedSupplement(Context mainActivity, ArrayList<PostData_Bon2> itemList) {
        // TODO Auto-generated constructor stub
        _supplements = itemList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            ReceiptSendEventListener = (onReceiptSendEventListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onReceiptSendEventListener");
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return _supplements.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class ViewHolder {
        ImageView image_check;
        TextView text_check;
        AnimCheckBox checkBox;
        TextView sup_price_check;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.suppliment_check_row, null);
               // holder.image_check = (ImageView) convertView.findViewById(R.id.item);
                holder.text_check = (TextView) convertView.findViewById(R.id.check_sup_name);
                holder.checkBox = (AnimCheckBox) convertView.findViewById(R.id.check_sup_item);
                holder.sup_price_check = (TextView) convertView.findViewById(R.id.check_sup_price);


            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
            holder.text_check.setText(_supplements.get(position).Produit);
            holder.sup_price_check.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(_supplements.get(position).Price)));
            holder.checkBox.setChecked(false,false);
            holder.checkBox.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
                @Override
                public void onChange(boolean checked) {
                    _supplements.get(position).Checked = checked;
                    ReceiptSendEventListener.ReceiptSendEvent("FROM_CHECK_SUPPLEMENT_ADAPTER",_supplements);
                }
            });

        return convertView;
    }
}