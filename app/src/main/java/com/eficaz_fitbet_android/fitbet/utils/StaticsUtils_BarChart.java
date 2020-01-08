package com.eficaz_fitbet_android.fitbet.utils;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.eficaz_fitbet_android.fitbet.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class StaticsUtils_BarChart {

    private static Context mContext;

    public static void setBarChartData(Context context, BarChart chart, int size, ArrayList<String> speed, ArrayList<String> dateDes,int type) {
        mContext = context;
        chart.invalidate();
        chart.clear();
        ArrayList<String> year = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            year.add(dateDes.get(i));
        }
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            entries.add(new BarEntry(i, Float.parseFloat(speed.get(i))));
        }
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setBarBorderWidth(0.5f);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(year);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setEnabled(false);
        barDataSet.setColors(new int[]{ContextCompat.getColor(mContext, R.color.light_blue)});
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
        chart.setDrawGridBackground(false);
        chart.setData(barData);
        chart.setFitBars(true);
        /*if(type==0){
            chart.setVisibleXRangeMaximum(6);
            chart.setVisibleXRangeMinimum(6);
            //chart.setVisibleXRangeMinimum(6);
        }else{
            //chart.setVisibleXRangeMaximum(12);
            chart.setVisibleXRangeMinimum(6);
        }*/
        chart.animateXY(1000, 1000);
        chart.invalidate();
        chart.setScaleEnabled(false);


    }
}
