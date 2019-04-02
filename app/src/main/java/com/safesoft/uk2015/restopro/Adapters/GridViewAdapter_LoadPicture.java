package com.safesoft.uk2015.restopro.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;
import com.squareup.picasso.Picasso;

/**
 * Created by UK2015 on 16/06/2016.
 */
public class GridViewAdapter_LoadPicture extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private String[] _pictures;
    private AssetManager assetManager;
    private Context _context;
    private String _name_cat;
    private String _indicator;
    private DATABASE controller;

    public interface onSomeEventListener {
        public void someEvent(String s, String Value);
    }
    onSomeEventListener someEventListener;

    public GridViewAdapter_LoadPicture(Context context, int textViewResourceId, String[] objects, String name_cat, String indicator) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        inflater = ((Activity) context).getLayoutInflater();
        _pictures = objects;
        assetManager = context.getAssets();
        _context = context;
        _name_cat = name_cat;
        _indicator = indicator;
        controller = new DATABASE(_context);

        try {
            someEventListener = (onSomeEventListener) _context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_context.toString() + " must implement onSomeEventListener");
        }

    }

    static class ViewHolder {
        ImageView postImageView;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_rows_loadpicture, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.postImageView = (ImageView) convertView.findViewById(R.id.imageViewLoadPicture);

        String path = "file:///android_asset/Pictures/"+ _pictures[position];
        Picasso.with(_context).load(path)
                .placeholder(R.mipmap.img_not_found)
                .error(R.mipmap.img_not_found)
                .into(viewHolder.postImageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(_context, _pictures[position],Toast.LENGTH_SHORT).show();
                //UPDATE IN DATABASE
                if(_indicator.equals("FAMILLE")){
                    controller.Update_famille(_name_cat, _pictures[position]);
                }else if(_indicator.equals("SUB_MENU")){
                    controller.Update_Menu(_name_cat, _pictures[position]);
                }
                someEventListener.someEvent("FROM_LOAD_PICTURE_ADAPTER",_pictures[position]);
            }
        });
        return convertView;
    }
}

