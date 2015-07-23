package com.pebble.tricorder.chart;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pebble.tricorder.model.TricorderData;

import java.util.ArrayList;


public abstract class Chart {

    protected LineChart mLineChart;

    protected ArrayList<String> mXVals = new ArrayList<String>();
    protected ArrayList<LineDataSet> mDataSets = new ArrayList<LineDataSet>();
    protected LineData mLineData;

    protected int mCurrentEntryIndex;

    protected static final int ENTRIES_ON_CHART = 10;

    public Chart() {}

    public Chart(LineChart chart) {
        mLineChart = chart;

        mLineChart.getAxisLeft().setStartAtZero(false);

        mCurrentEntryIndex = 0;
    }

    public void update() {}

    public void clear() {
        mLineChart.clear();
        mXVals.clear();
        mCurrentEntryIndex = 0;
    }

    public void add(TricorderData data) {}
}
