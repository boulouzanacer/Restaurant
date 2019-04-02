package com.safesoft.uk2015.restopro.Adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.safesoft.uk2015.restopro.Fragment.DialogFragement_Details;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.PostData.PostData_Menu;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 16/06/2016.
 */
public class GridViewAdapter_SubMenu extends ArrayAdapter<PostData_Menu> {

    private int lastPosition = -1;
    private LayoutInflater inflater;
    private ArrayList<PostData_Menu> datas;
    private Context _context;

    public interface onDataSendEventListener {
        public void DataSendEvent(String s, PostData_Bon2 receipt);
    }

    onDataSendEventListener DataSendEventListener;

    public GridViewAdapter_SubMenu(Context context, int textViewResourceId, ArrayList<PostData_Menu> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        _context = context;
        inflater = ((Activity) context).getLayoutInflater();
        datas = objects;

        try {
            DataSendEventListener = (onDataSendEventListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onDataSendEventListener");
        }
    }

    static class ViewHolder {
        ImageView postImageView;
        TextView postNameView;
        SlantedTextView postPriceView;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_rows_sub_menu, null);

            viewHolder = new ViewHolder();
            viewHolder.postImageView = (ImageView) convertView.findViewById(R.id.post_menu_image);
            viewHolder.postNameView = (TextView) convertView.findViewById(R.id.name_sub_menu);
            viewHolder.postPriceView = (SlantedTextView ) convertView.findViewById(R.id.price_sub_menu);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

            String path = "file:///android_asset/Pictures/"+ datas.get(position).Image_index;
            Picasso.with(_context).load(path)
                    .placeholder(R.mipmap.img_not_found)
                    .error(R.mipmap.img_not_found)
                    .into(viewHolder.postImageView);

        viewHolder.postNameView.setText(datas.get(position).Designation);
        //viewHolder.postPriceView.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(datas.get(position).Prix1)));
        viewHolder.postPriceView.setText(new DecimalFormat("##,##0.00").format(Double.valueOf(datas.get(position).Prix1)))
                .setTextColor(Color.WHITE)
                .setSlantedBackgroundColor(_context.getResources().getColor(R.color.Blue_Peter_river))
                .setTextSize(18)
                .setSlantedLength(40)
                .setMode(SlantedTextView.MODE_RIGHT);
        convertView.setId(position);

        // When Sub Menu Item Clicked
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData_Bon2 receipt = new PostData_Bon2();
                receipt.Produit  = datas.get(position).Designation;
                receipt.Quantity = "1";
                receipt.Code = datas.get(position).Code;
                receipt.Tva = datas.get(position).Tva;
                receipt.Price = datas.get(position).Prix1;
                receipt.Des_imp = datas.get(position).Des_imp;
                DataSendEventListener.DataSendEvent("FROM_SUB_MENU_ADAPTER",receipt);
                //=======
            }
        });

        // When Sub Menu Item Long Clicked
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShowDetailsDialog();
                return true;
            }
        });


        // Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // convertView.startAnimation(animation);
        //lastPosition = position;
        return convertView;
    }


    private void ShowDetailsDialog() {

        FragmentActivity activity = (FragmentActivity) (_context);
        FragmentManager fm = activity.getFragmentManager();
        DialogFragment dialog = new DialogFragement_Details().newInstance("Description de produit"); // creating new object
        dialog.show(fm, "dialog");
    }

}

