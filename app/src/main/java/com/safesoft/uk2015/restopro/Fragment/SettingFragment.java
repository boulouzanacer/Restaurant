package com.safesoft.uk2015.restopro.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.formation.uk2015.login.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by UK2015 on 02/08/2016.
 */
public class SettingFragment extends Fragment {

    private String PREFS_PARAMS_INFO = "PARAMS_INFO";
    private Context mContext;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_parametres, container, false);
        mContext = getActivity();
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_PARAMS_INFO, MODE_PRIVATE);

        final Switch switch_song = (Switch) rootView.findViewById(R.id.switch_song);
        final Switch switch_vibrator = (Switch) rootView.findViewById(R.id.switch_virator);

        switch_song.setChecked(prefs.getBoolean("SOUND", true));
        switch_vibrator.setChecked(prefs.getBoolean("VIBRATION", true));

        switch_song.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_PARAMS_INFO, getActivity().MODE_PRIVATE).edit();
                if(isChecked){
                    editor.putBoolean("SOUND",true);
                }else{
                    editor.putBoolean("SOUND",false);
                }
                editor.commit();
            }
        });

        switch_vibrator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_PARAMS_INFO, getActivity().MODE_PRIVATE).edit();
                if(isChecked){
                    editor.putBoolean("VIBRATION",true);
                }else{
                    editor.putBoolean("VIBRATION",false);
                }
                editor.commit();
            }
        });
        return rootView;
    }
}
