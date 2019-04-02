package com.safesoft.uk2015.restopro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.uk2015.restopro.Activities.LoadPictures;
import com.safesoft.uk2015.restopro.Activities.Main_SafeSoft;
import com.safesoft.uk2015.restopro.PostData.Parent;
import com.formation.uk2015.login.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by UK2015 on 02/08/2016.
 */
public class ExpandableListAdapter_SettingMenu extends BaseExpandableListAdapter{


    private LayoutInflater inflater;
    private ArrayList<Parent> mParent;
    private Context _context;

    public ExpandableListAdapter_SettingMenu(Context context, ArrayList<Parent> parent){
        mParent = parent;
        _context = context;
        inflater = LayoutInflater.from(context);
    }



    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {
        return mParent.size();
    }

    @Override
    //counts the number of children items so the list knows how many times calls getChildView() method
    public int getChildrenCount(int i) {
        return mParent.get(i).getArrayChildren().size();
    }

    @Override
    //gets the title of each parent/group
    public Object getGroup(int i) {
        return mParent.get(i).getTitle();
    }

    //gets the image_index of each parent/group
    public Object getGroupImage(int i) {
        return mParent.get(i).getImage_index();
    }

    //gets the image_index of each child
    public Object getChildImage(int group_p, int child_p) {
        return mParent.get(group_p).getArrayChildren().get(child_p).getChildImage_index();
    }

    @Override
    //gets the name of each item
    public Object getChild(int i, int i1) {
        return mParent.get(i).getArrayChildren().get(i1).getTitle();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    //in this method you must set the text to see the parent/group on the list
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        holder.groupPosition = groupPosition;

        if (view == null) {
            view = inflater.inflate(R.layout.setting_menu_parent_row, viewGroup,false);
            view.setBackgroundResource(R.drawable.selector_listview_product_row);
        }

        TextView textView = (TextView) view.findViewById(R.id.list_item_text_view);
        textView.setText(getGroup(holder.groupPosition).toString());

        ImageView image = (ImageView) view.findViewById(R.id.imageview_group_setting);
        String path = "file:///android_asset/Pictures/"+ getGroupImage(holder.groupPosition);
        Picasso.with(_context).load(path)
                .placeholder(R.mipmap.img_not_found)
                .error(R.mipmap.img_not_found)
                .into(image);

        holder.button = (Button) view.findViewById(R.id.Personalize);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String famille_name;
                famille_name = ((String) getGroup(holder.groupPosition));
                //Toast.makeText(_context,famille_name, Toast.LENGTH_SHORT).show();

                Intent postviewIntent = new Intent(_context, LoadPictures.class);
                Bundle bundleinfo = new Bundle();
                bundleinfo.putString("INDICATOR","FAMILLE");
                bundleinfo.putString("FAMILLE_NAME",famille_name);
                postviewIntent.putExtras(bundleinfo);
                ((Main_SafeSoft)_context).startActivityForResult(postviewIntent, 1200);
                ((Main_SafeSoft)_context).overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
            }
        });

        view.setTag(holder);
        //return the entire view
        return view;
    }

    public void openFolder()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/restopro_pic/");
        intent.setDataAndType(uri, "image/png");
        _context.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        holder.childPosition = childPosition;
        holder.groupPosition = groupPosition;

        if (view == null) {
            view = inflater.inflate(R.layout.settingmenu_child_row, viewGroup,false);
            view.setBackgroundResource(R.drawable.selector_listview_product_row);
        }

        ImageView child_image = (ImageView) view.findViewById(R.id.child_menu_image);
        String path = "file:///android_asset/Pictures/"+ getChildImage(holder.groupPosition,holder.childPosition);
        Picasso.with(_context).load(path)
                .placeholder(R.mipmap.img_not_found)
                .error(R.mipmap.img_not_found)
                .into(child_image);

        TextView textView = (TextView) view.findViewById(R.id.list_item_text_child);
        String sub_famille_name;
        sub_famille_name = ((String) getChild(groupPosition, childPosition));
        textView.setText(sub_famille_name);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sub_menu_name;
                sub_menu_name = ((String) getChild(holder.groupPosition,holder.childPosition));
                //Toast.makeText(_context,sub_menu_name, Toast.LENGTH_SHORT).show();

                Intent postviewIntent = new Intent(_context, LoadPictures.class);
                Bundle bundleinfo = new Bundle();
                bundleinfo.putString("INDICATOR","SUB_MENU");
                bundleinfo.putString("SUB_MENU_NAME",sub_menu_name);
                postviewIntent.putExtras(bundleinfo);
                ((Main_SafeSoft)_context).startActivityForResult(postviewIntent, 1200);
                ((Main_SafeSoft)_context).overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
            }
        });

        view.setTag(holder);

        //return the entire view
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
        super.registerDataSetObserver(observer);
    }


// Intentionally put on comment, if you need on click deactivate it
/*  @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (view.getId() == holder.button.getId()){

           // DO YOUR ACTION
        }
    }*/


    protected class ViewHolder {
        protected int childPosition;
        protected int groupPosition;
        protected Button button;
    }
}