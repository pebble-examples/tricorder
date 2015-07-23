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


public class AccelChart extends Chart {

    private ArrayList<Entry> mAccelDataXEntries = new ArrayList<Entry>();
    private ArrayList<Entry> mAccelDataYEntries = new ArrayList<Entry>();
    private ArrayList<Entry> mAccelDataZEntries = new ArrayList<Entry>();

    private LineDataSet mAccelDataXDataSet;
    private LineDataSet mAccelDataYDataSet;
    private LineDataSet mAccelDataZDataSet;

    public AccelChart(LineChart chart) {
        super();

        mLineChart = chart;

        mLineChart.getAxisLeft().setStartAtZero(false);

        mAccelDataXDataSet = new LineDataSet(mAccelDataXEntries, "Accel X Values");
        mAccelDataYDataSet = new LineDataSet(mAccelDataYEntries, "Accel Y Values");
        mAccelDataZDataSet = new LineDataSet(mAccelDataZEntries, "Accel Z Values");

        mAccelDataXDataSet.setColor(Color.BLUE);
        mAccelDataYDataSet.setColor(Color.RED);
        mAccelDataZDataSet.setColor(Color.GREEN);

        mAccelDataXDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mAccelDataYDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mAccelDataZDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        mDataSets.add(mAccelDataXDataSet);
        mDataSets.add(mAccelDataYDataSet);
        mDataSets.add(mAccelDataZDataSet);
    }

    public void update() {
        if (mCurrentEntryIndex >= ENTRIES_ON_CHART) {
            int numOldEntries = mCurrentEntryIndex - ENTRIES_ON_CHART;

            for (int i = 0; i < numOldEntries; i++) {
                mAccelDataXDataSet.removeEntry(0);
                mAccelDataYDataSet.removeEntry(0);
                mAccelDataZDataSet.removeEntry(0);
                mXVals.remove(0);
            }

            for(int i = 0; i < ENTRIES_ON_CHART; i++) {
                mAccelDataXEntries.get(i).setXIndex(i);
                mAccelDataYEntries.get(i).setXIndex(i);
                mAccelDataZEntries.get(i).setXIndex(i);
            }

            mCurrentEntryIndex = ENTRIES_ON_CHART;
        }

        mLineChart.setData(mLineData);
        mLineChart.invalidate();
    }

    public void clear() {
        super.clear();
        mAccelDataXDataSet.clear();
        mAccelDataYDataSet.clear();
        mAccelDataZDataSet.clear();
    }

    public void add(TricorderData data) {
        Entry entryX = new Entry(data.getAccelData().getX(), mCurrentEntryIndex);
        Entry entryY = new Entry(data.getAccelData().getY(), mCurrentEntryIndex);
        Entry entryZ = new Entry(data.getAccelData().getZ(), mCurrentEntryIndex);

        mAccelDataXDataSet.addEntry(entryX);
        mAccelDataYDataSet.addEntry(entryY);
        mAccelDataZDataSet.addEntry(entryZ);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        mXVals.add(sdf.format(data.getTimestamp()));

        if (mLineData == null) {
            mLineData = new LineData(mXVals, mDataSets);
        }

        mCurrentEntryIndex++;
    }
}
