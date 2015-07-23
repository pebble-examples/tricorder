package com.pebble.tricorder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.pebble.tricorder.R;
import com.pebble.tricorder.chart.AccelChart;
import com.pebble.tricorder.chart.BatteryChart;
import com.pebble.tricorder.chart.ConnectionChart;
import com.pebble.tricorder.model.TricorderData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class OverviewFragment extends Fragment {

    private ArrayList<TricorderData> mTricorderDatas;

    private HashMap<Integer, Integer> mTricorderDataCounts = new HashMap<>();

    private AccelChart mAccelChart;
    private ConnectionChart mConnectionChart;
    private BatteryChart mBatteryChart;

    private TextView mConnectionStatusView;
    private TextView mLastPacketIdView;
    private TextView mLastPacketTimeView;
    private TextView mCrcMismatchesView;
    private TextView mDuplicatePacketsView;
    private TextView mOutOfOrderPacketsView;
    private TextView mDroppedPacketsView;

    private int mCrcMismatches = 0;
    private int mDuplicatePackets = 0;
    private int mOutOfOrderPackets = 0;
    private int mDroppedPackets = 0;

    private int mLargestPacketId = 0;

    public OverviewFragment(ArrayList<TricorderData> mTricorderDatas) {
        super();
        this.mTricorderDatas = mTricorderDatas;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaled = inflater.inflate(R.layout.overview_page, container, false);

        mAccelChart = new AccelChart((LineChart) inflaled.findViewById(R.id.accelChart));
        mConnectionChart = new ConnectionChart((LineChart) inflaled.findViewById(R.id.connectionChart));
        mBatteryChart = new BatteryChart((LineChart) inflaled.findViewById(R.id.batteryChart));

        mConnectionStatusView = (TextView) inflaled.findViewById(R.id.connectionStatus);
        mLastPacketIdView = (TextView) inflaled.findViewById(R.id.lastPacketId);
        mLastPacketTimeView = (TextView) inflaled.findViewById(R.id.lastPacketTime);
        mCrcMismatchesView = (TextView) inflaled.findViewById(R.id.crcMismatches);
        mDuplicatePacketsView = (TextView) inflaled.findViewById(R.id.duplicatePackets);
        mOutOfOrderPacketsView = (TextView) inflaled.findViewById(R.id.outOfOrderPackets);
        mDroppedPacketsView = (TextView) inflaled.findViewById(R.id.droppedPackets);

        return inflaled;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addDataToCharts(TricorderData tricorderData) {
        mAccelChart.add(tricorderData);
        mConnectionChart.add(tricorderData);
        mBatteryChart.add(tricorderData);

        updateStats(tricorderData);
    }

    public void updateCharts() {
        mAccelChart.update();
        mConnectionChart.update();
        mBatteryChart.update();
    }

    public void clearCharts() {
        mAccelChart.clear();
        mConnectionChart.clear();
        mBatteryChart.clear();

        mCrcMismatches = 0;
        mDuplicatePackets = 0;
        mOutOfOrderPackets = 0;
        mDroppedPackets = 0;

        mLargestPacketId = 0;

        updateCharts();
    }

    private void updateStats(TricorderData tricorderData) {
        mConnectionStatusView.setText(getConnectionStatus(tricorderData));
        mLastPacketIdView.setText(getLastPacketId(tricorderData));
        mLastPacketTimeView.setText(getLastPacketTime(tricorderData));
        mCrcMismatchesView.setText(getCrcMismatches(tricorderData));
        mDuplicatePacketsView.setText(getDuplicatePackets(tricorderData));
        mOutOfOrderPacketsView.setText(getOutOfOrderPackets(tricorderData));
        mDroppedPacketsView.setText(getDroppedPackets());
    }

    private String getConnectionStatus(TricorderData data) {
        return data.getConnectionStatus() ? "Connected" : "Disconnected";
    }

    private String getLastPacketId(TricorderData data) {
        return "" + data.getPacketId();
    }

    private String getLastPacketTime(TricorderData data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        return sdf.format(data.getTimestamp());
    }

    private String getCrcMismatches(TricorderData data) {
        if (data.getAndroidCrc32() != data.getPebbleCrc32()) {
            mCrcMismatches++;
        }

        return "" + mCrcMismatches;
    }

    private String getDuplicatePackets(TricorderData data) {
        if (mTricorderDataCounts.containsKey(data.getPacketId())) {
            mTricorderDataCounts.put(data.getPacketId(), mTricorderDataCounts.get(data.getPacketId()) + 1);
            mDuplicatePackets++;
        } else {
            mTricorderDataCounts.put(data.getPacketId(), 1);
        }

        return "" + mDuplicatePackets;
    }

    private String getOutOfOrderPackets(TricorderData data) {
        if (data.getPacketId() > mLargestPacketId) {
            mLargestPacketId = data.getPacketId();
        }

        if (data.getPacketId() < mLargestPacketId) {
            mOutOfOrderPackets++;
        }

        return "" + mOutOfOrderPackets;
    }

    private String getDroppedPackets() {
        mDroppedPackets = mLargestPacketId - mTricorderDatas.size() - mDuplicatePackets;

        return "" + mDroppedPackets;
    }

    public String getStatsString() {
        return "Connection Status: " + mConnectionStatusView.getText() + "\n"
                + "Last Packet Id: " + mLastPacketIdView.getText() + "\n"
                + "Last Packet Time: " + mLastPacketTimeView.getText() + "\n"
                + "CRC Mismatches: " + mCrcMismatchesView.getText() + "\n"
                + "Duplicate Packets: " + mDuplicatePacketsView.getText() + "\n"
                + "Out of Order Packets: " + mOutOfOrderPacketsView.getText() + "\n"
                + "Dropped Packets: " + mDroppedPacketsView.getText() + "\n";

    }
}