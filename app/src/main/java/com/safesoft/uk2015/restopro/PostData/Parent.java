package com.safesoft.uk2015.restopro.PostData;

import java.util.ArrayList;

/**
 * Created by UK2015 on 02/08/2016.
 */
public class Parent {
    private String mTitle;
    private String mImage_index;
    private ArrayList<Child> mArrayChildren;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getImage_index() {
        return mImage_index;
    }

    public void setImage_index(String image_index) {
        mImage_index = image_index;
    }
    public ArrayList<Child> getArrayChildren() {
        return mArrayChildren;
    }

    public void setArrayChildren(ArrayList<Child> arrayChildren) {
        mArrayChildren = arrayChildren;
    }
}