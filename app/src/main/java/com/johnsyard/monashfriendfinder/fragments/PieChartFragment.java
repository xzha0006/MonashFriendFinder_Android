package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanzhang on 5/05/2017.
 */

public class PieChartFragment extends Fragment {
    private View vPieView;
    private PieChart pieChart;

    private ArrayList<Float> yData = new ArrayList<>();
    private ArrayList<String> xData = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vPieView = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pieChart = (PieChart) vPieView.findViewById(R.id.c_pi);


        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        JsonArray favoriteUnits = new JsonParser().parse(sp.getString("favoriteUnits", "[]")).getAsJsonArray();
        if (favoriteUnits.size() > 0){
            for(int i = 0; i < favoriteUnits.size(); i++){
                xData.add(i, favoriteUnits.get(i).getAsJsonObject().get("favouriteUnitName").getAsString());
                yData.add(i, favoriteUnits.get(i).getAsJsonObject().get("Frequency").getAsFloat());
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i< yData.size(); i++){
            entries.add(new PieEntry(yData.get(i), xData.get(i)));
        }
        PieDataSet pieDataSet = new PieDataSet(entries,"Items");
        ArrayList<Integer> color = new ArrayList<>();
        for (int i : ColorTemplate.COLORFUL_COLORS)
            color.add(i);
        for (int i : ColorTemplate.JOYFUL_COLORS)
            color.add(i);
        pieDataSet.setColors(color);
        PieData pieData = new PieData(pieDataSet);
        Legend l = pieChart.getLegend();
        l.setXEntrySpace(2);
        l.setYEntrySpace(5);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        Description desc = new Description();
        desc.setText("Favorite Unit Frequency");
        pieChart.setUsePercentValues(true);
        pieChart.setDescription(desc);
        pieChart.setData(pieData);
        //refresh
        pieChart.invalidate();
        return vPieView;
    }
}
