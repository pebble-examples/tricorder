package com.pebble.tricorder.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pebble.tricorder.model.TricorderData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class BatteryChart extends Chart {

    private static ArrayList<Entry> mBatteryDataEntries = new ArrayList<Entry>();

    private static LineDataSet mBatteryDataSet;

    public BatteryChart(LineChart chart) {
        super();

        mLineChart = chart;

        mLineChart.getAxisLeft().setStartAtZero(false);

        mBatteryDataSet = new LineDataSet(mBatteryDataEntries, "Connection Values");

        mBatteryDataSet.setColor(Color.BLUE);

        mBatteryDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        mDataSets.add(mBatteryDataSet);
    }

    public void update() {
        if (mCurrentEntryIndex >= ENTRIES_ON_CHART) {
            int numOldEntries = mCurrentEntryIndex - ENTRIES_ON_CHART;

            for(int i = 0; i < numOldEntries; i++) {
                mBatteryDataSet.removeEntry(0);
                mXVals.remove(0);
            }

            for(int i = 0; i < ENTRIES_ON_CHART; i++) {
                mBatteryDataEntries.get(i).setXIndex(i);
            }

            mCurrentEntryIndex = ENTRIES_ON_CHART;
        }

        mLineChart.setData(mLineData);
        mLineChart.invalidate();
    }

    public void clear() {
        super.clear();
        mBatteryDataSet.clear();
    }

    public void add(TricorderData data) {
        Entry entry_battery = new Entry(data.getChargePercent(), mCurrentEntryIndex);

        mBatteryDataSet.addEntry(entry_battery);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        mXVals.add(sdf.format(data.getTimestamp()));

        if (mLineData == null) {
            mLineData = new LineData(mXVals, mDataSets);
        }

        mCurrentEntryIndex++;
    }
}
