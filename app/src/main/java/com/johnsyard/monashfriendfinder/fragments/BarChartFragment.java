package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;

import org.apache.commons.codec.net.BCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuanzhang on 5/05/2017.
 */

public class BarChartFragment extends Fragment {
    private View vBarView;
    private BarChart barChart;

    private ArrayList<Float> yData = new ArrayList<>();
    private String[] xData = new String[50];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vBarView = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        barChart = (BarChart) vBarView.findViewById(R.id.c_bar);
        String startingDate;
        String endingDate;

        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        JsonArray locationFrequency = new JsonParser().parse(sp.getString("locationFrequency", "[]")).getAsJsonArray();
        List<BarEntry> entries = new ArrayList<>();
        if (locationFrequency.size() > 0){
            for(int i = 0; i < locationFrequency.size(); i++){
                JsonObject location = locationFrequency.get(i).getAsJsonObject();
                entries.add(new BarEntry((float)i, (float) location.get("frequency").getAsInt()));
                xData[i] = location.get("locationName").getAsString();
            }
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xData[(int) value];
            }
        };

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate();



        return vBarView;
    }
}
