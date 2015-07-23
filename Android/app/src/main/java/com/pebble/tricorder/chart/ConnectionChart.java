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


public class ConnectionChart extends Chart {

    private static ArrayList<Entry> mConnectionDataEntries = new ArrayList<Entry>();

    private static LineDataSet mConnectionDataSet;

    public ConnectionChart(LineChart chart) {
        super();

        mLineChart = chart;

        mLineChart.getAxisLeft().setStartAtZero(false);

        mConnectionDataSet = new LineDataSet(mConnectionDataEntries, "Connection Values");

        mConnectionDataSet.setColor(Color.BLUE);

        mConnectionDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        mDataSets.add(mConnectionDataSet);
    }

    public void update() {
        if (mCurrentEntryIndex >= ENTRIES_ON_CHART) {
            int numOldEntries = mCurrentEntryIndex - ENTRIES_ON_CHART;

            for(int i = 0; i < numOldEntries; i++) {
                mConnectionDataSet.removeEntry(0);
                mXVals.remove(0);
            }

            for(int i = 0; i < ENTRIES_ON_CHART; i++) {
                mConnectionDataEntries.get(i).setXIndex(i);
            }

            mCurrentEntryIndex = ENTRIES_ON_CHART;
        }

        mLineChart.setData(mLineData);
        mLineChart.invalidate();
    }

    public void clear() {
        super.clear();
        mConnectionDataSet.clear();
    }

    public void add(TricorderData data) {
        float connection = data.getConnectionStatus() ? 1.0f : 0.0f;

        Entry entry_connection = new Entry(connection, mCurrentEntryIndex);

        mConnectionDataSet.addEntry(entry_connection);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        mXVals.add(sdf.format(data.getTimestamp()));

        if (mLineData == null) {
            mLineData = new LineData(mXVals, mDataSets);
        }

        mCurrentEntryIndex++;
    }
}
