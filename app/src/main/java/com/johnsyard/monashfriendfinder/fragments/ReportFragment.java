package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanzhang on 5/05/2017.
 */

public class ReportFragment extends Fragment{
    private View vReport;
    private Button btPie;
    private Button btBar;
    private EditText etStart;
    private EditText etEnd;

    private int studentId;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vReport = inflater.inflate(R.layout.fragment_report, container, false);
        btBar = (Button) vReport.findViewById(R.id.bt_bar);
        btPie = (Button) vReport.findViewById(R.id.bt_pie);
        etStart = (EditText) vReport.findViewById(R.id.et_start);
        etEnd = (EditText) vReport.findViewById(R.id.et_end);
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        spEdit = sp.edit();

        new AsyncTask<Void, Void, JsonArray>(){
            @Override
            protected JsonArray doInBackground(Void... voids) {
                JsonArray favoriteUnits = RestClient.getFavoriteUnits();
                String myProfile = sp.getString("myProfile", null);
                JsonObject profileJson = new JsonParser().parse(myProfile).getAsJsonObject();
                studentId = profileJson.get("studentId").getAsInt();
                spEdit.putString("favoriteUnits", favoriteUnits.toString());
                spEdit.apply();
                return favoriteUnits;
            }
        }.execute();

//        //get location records of user
//        new AsyncTask<Void, Void, JsonArray>(){
//            @Override
//            protected JsonArray doInBackground(Void... voids) {
//                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//                String myProfileString = sp.getString("myProfile", null);
//                JsonObject myProfileJson = new JsonParser().parse(myProfileString).getAsJsonObject();
//                int studentId = myProfileJson.get("studentId").getAsInt();
//
//                JsonArray locations = RestClient.getLocationRecords(String.valueOf(studentId));
//                SharedPreferences.Editor spEdit = sp.edit();
//                spEdit.putString("locationRecords", locations.toString());
//                spEdit.apply();
//                return locations;
//            }
//        }.execute();

        btPie.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new PieChartFragment()).commit();
            }
        });

        btBar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //get the report data
                new AsyncTask<String, Void, Void>(){
                    @Override
                    protected Void doInBackground(String... strings) {
                        JsonArray locationFrequency = RestClient.getLocationFrequency(strings[0], strings[1], strings[2]);
                        spEdit.putString("locationFrequency", locationFrequency.toString());
                        spEdit.apply();
                        return null;
                    }
                }.execute(String.valueOf(studentId), etStart.getText().toString(), etEnd.getText().toString());
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new BarChartFragment()).commit();
            }
        });

        return vReport;
    }
}
