package com.safesoft.uk2015.restopro.Activities;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;

import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_LoadPicture;

import java.io.IOException;

public class LoadPictures extends AppCompatActivity implements GridViewAdapter_LoadPicture.onSomeEventListener{

    private Toolbar toolbar;
    private String[] picturesList;
    private GridView gridview;
    private GridViewAdapter_LoadPicture plusAdapter;
    private String myFamille;
    private String subMenu;
    private String indicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pictures);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.restopro_header);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Bundle extras = getIntent().getExtras();
        AssetManager assetManager = getAssets();
        // To get names of all files inside the "Files" folder
        try {
            picturesList = assetManager.list("Pictures");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        gridview = (GridView) findViewById(R.id.gridview_loadpicture);
        if (extras != null) {
            indicator = extras.getString("INDICATOR");
            if(indicator.equals("FAMILLE")){
                myFamille = extras.getString("FAMILLE_NAME");
                plusAdapter = new GridViewAdapter_LoadPicture(this, R.layout.gridview_rows_loadpicture, picturesList, myFamille, indicator);
            }else if(indicator.equals("SUB_MENU")){
                subMenu = extras.getString("SUB_MENU_NAME");
                plusAdapter = new GridViewAdapter_LoadPicture(this, R.layout.gridview_rows_loadpicture, picturesList, subMenu, indicator);
            }
        }
        gridview.setAdapter(plusAdapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void someEvent(String s, String Value) {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
