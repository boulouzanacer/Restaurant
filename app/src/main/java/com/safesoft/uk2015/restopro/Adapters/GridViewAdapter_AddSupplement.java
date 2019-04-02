package com.safesoft.uk2015.restopro.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.Fragment.Fragment_AddSupplement;
import com.safesoft.uk2015.restopro.Activities.Order_Activity;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.safesoft.uk2015.restopro.PostData.PostData_Supplement;
import com.formation.uk2015.login.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 16/06/2016.
 */
public class GridViewAdapter_AddSupplement extends ArrayAdapter<PostData_Supplement> {


    private LayoutInflater inflater;
    private ArrayList<PostData_Supplement> datas;
    private Context _context;

    public interface onDataSendEventListener {
        public void DataSendEvent(String s, PostData_Bon2 receipt);
    }

    onDataSendEventListener DataSendEventListener;

    public GridViewAdapter_AddSupplement(Context context, int textViewResourceId, ArrayList<PostData_Supplement> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        inflater = ((Activity) context).getLayoutInflater();
        datas = objects;
        _context = context;
        try {
            DataSendEventListener = (onDataSendEventListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onDataSendEventListener");
        }
    }

    static class ViewHolder {
        ImageView postImageView;
        TextView postSuppName;
        TextView postSuppPrice;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_rows_add_supplement, null);

            viewHolder = new ViewHolder();
            viewHolder.postImageView = (ImageView) convertView.findViewById(R.id.post_menu_image);
            viewHolder.postSuppName = (TextView) convertView.findViewById(R.id.name_supplement);
            viewHolder.postSuppPrice = (TextView) convertView.findViewById(R.id.price_supplement);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //   viewHolder.postImageView.setBackgroundResource(R.mipmap.ic_messaging_posttype_chat);
        viewHolder.postSuppName.setText(datas.get(position).Libelle);
        viewHolder.postSuppPrice.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(datas.get(position).Sup_price)));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData_Bon2 suppelement_added = new PostData_Bon2();
                suppelement_added.Bon2id = null;
                suppelement_added.Code = null;
                suppelement_added.Code_s = datas.get(position).Code_s;
                suppelement_added.Produit = datas.get(position).Libelle;
                suppelement_added.Tva = "0";
                suppelement_added.Price = datas.get(position).Sup_price;
                suppelement_added.Quantity = "1";

                DataSendEventListener.DataSendEvent("FROM_ADD_SUPPELEMENT_ADAPTER", suppelement_added);

                Fragment prev = ((Order_Activity)_context).getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    Fragment_AddSupplement df = (Fragment_AddSupplement) prev;
                    df.dismiss();
                }
            }
        });
     //   Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? android.R.anim.slide_in_left : android.R.anim.slide_out_right);
     //   convertView.startAnimation(animation);
     //   lastPosition = position;
        return convertView;
    }
}

