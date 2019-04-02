package com.safesoft.uk2015.restopro.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.formation.uk2015.login.R;

/**
 * Created by UK2015 on 18/10/2016.
 */

public class ListViewAdapter_Spinner extends ArrayAdapter {

    private Context mContext;
    private String [] item_des_imp ;
    private String [] item_ip_imp ;

    public ListViewAdapter_Spinner(Context context, int textViewResourceId, String[] objects_des , String[] objects_ip) {
        super(context, textViewResourceId, objects_des);
        mContext = context;
        item_des_imp = new String[objects_des.length];
        item_ip_imp = new String[objects_des.length];
        item_des_imp = objects_des;
        item_ip_imp = objects_ip;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

                // Inflating the layout for the custom Spinner
                        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                        View layout = inflater.inflate(R.layout.spinner_rows, parent, false);

                // Declaring and Typecasting the textview in the inflated layout
                        TextView _des_imp = (TextView) layout
                                .findViewById(R.id.des_imp);

                 // Setting the text using the array
                    _des_imp.setText(item_des_imp[position]);

                    // Setting the color of the text
                    _des_imp.setTextColor(Color.rgb(75, 180, 225));


                    // Declaring and Typecasting the textview in the inflated layout
                    TextView _ip_imp = (TextView) layout
                            .findViewById(R.id.ip_imp);

                    // Setting the text using the array
                    _ip_imp.setText(item_ip_imp[position]);
                // Setting an image using the id's in the array
                      //  img.setImageResource(images[position]);
/*
                // Setting Special atrributes for 1st element
                        if (position == 0) {
                // Removing the image view
                            img.setVisibility(View.GONE);
                // Setting the size of the text
                            tvLanguage.setTextSize(20f);
                // Setting the text Color
                            tvLanguage.setTextColor(Color.BLACK);

                        }*/

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}