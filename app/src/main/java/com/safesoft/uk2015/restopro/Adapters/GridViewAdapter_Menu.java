package com.safesoft.uk2015.restopro.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.PostData.PostData_Families;
import com.formation.uk2015.login.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by UK2015 on 16/06/2016.
 */
public class GridViewAdapter_Menu extends ArrayAdapter<PostData_Families> {

    private int lastPosition = -1;
    private LayoutInflater inflater;
    private ArrayList<PostData_Families> datas;
    private AssetManager assetManager;
    private Context _context;


    public GridViewAdapter_Menu(Context context, int textViewResourceId, ArrayList<PostData_Families> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        inflater = ((Activity) context).getLayoutInflater();
        datas = objects;
        _context = context;
        assetManager = context.getAssets();
    }

    static class ViewHolder {
        ImageView postImageView;
        TextView postNameView;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_rows_menu, null);

            viewHolder = new ViewHolder();
            viewHolder.postImageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.postNameView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

         //   try {
         //     final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
           //   File imgFile = new  File(dir+"2.jpg");
             String path = "file:///android_asset/Pictures/"+ datas.get(position).Image_index;
             Picasso.with(_context).load(path)
                     .placeholder(R.mipmap.img_not_found)
                     .error(R.mipmap.img_not_found)
                     .into(viewHolder.postImageView);


           // File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pizza.png");
           // Picasso.with(_context).load(file).into(viewHolder.postImageView);
              //  InputStream ims = assetManager.open("Pictures/"+datas.get(position).Image_index);
              //  Bitmap bitmap = BitmapFactory.decodeStream(ims);
             //   viewHolder.postImageView.setImageBitmap(bitmap);
         //   }catch(IOException ex) {
          //      Log.v("TRACKKK", ex.getMessage());
        //    }


        viewHolder.postNameView.setText(datas.get(position).Famille_name);

        return convertView;
    }
}

