package com.pebble.tricorder.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.getpebble.android.kit.PebbleKit;
import com.pebble.tricorder.R;
import com.pebble.tricorder.adapter.TricorderPagerAdapter;
import com.pebble.tricorder.fragment.LogsFragment;
import com.pebble.tricorder.fragment.OverviewFragment;
import com.pebble.tricorder.model.TricorderData;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class MainActivity extends FragmentActivity {

    private static final UUID WATCHAPP_UUID = UUID.fromString("9151a02e-5cc5-4703-8c18-299482e00317");
    private static final String EXT_FOLDERNAME = "/Tricorder/";
    private static final String FILENAME = "logs.csv";

    private ArrayList<TricorderData> mTricorderDatas = new ArrayList<TricorderData>();

    private OverviewFragment mOverviewFragment;
    private LogsFragment mLogsFragment;

    private PebbleKit.PebbleDataLogReceiver mDataloggingReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOverviewFragment = new OverviewFragment(mTricorderDatas);
        mLogsFragment = new LogsFragment(mTricorderDatas);

        Button resetButton = (Button) findViewById(R.id.toolbar_reset_button);
        Button emailLogsButton = (Button) findViewById(R.id.toolbar_email_logs_button);

        resetButton.setOnClickListener(resetDataListener);
        emailLogsButton.setOnClickListener(sendLogsListener);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TricorderPagerAdapter tricorderPagerAdapter = new TricorderPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        tricorderPagerAdapter.addFragment(mOverviewFragment, "Overview");
        tricorderPagerAdapter.addFragment(mLogsFragment, "Logs");
        viewPager.setAdapter(tricorderPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mDataloggingReceiver = new PebbleKit.PebbleDataLogReceiver(WATCHAPP_UUID) {

            @Override
            public void receiveData(Context context, UUID logUuid, Long timestamp, Long tag, byte[] data) {
                TricorderData tricorderData = new TricorderData(data);
                logTricorderData(tricorderData);

                mOverviewFragment.addDataToCharts(tricorderData);

                mTricorderDatas.add(tricorderData);
            }

            @Override
            public void onFinishSession(Context context, UUID logUuid, Long timestamp, Long tag) {
                super.onFinishSession(context, logUuid, timestamp, tag);

                mOverviewFragment.updateCharts();

                mLogsFragment.updateLogsListView();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        // this is unregistered by PebbleKit all on its own
        PebbleKit.registerDataLogReceiver(getApplicationContext(), mDataloggingReceiver);
    }

    private View.OnClickListener resetDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mOverviewFragment.clearCharts();

            mTricorderDatas.clear();

            mLogsFragment.updateLogsListView();
        }
    };

    private View.OnClickListener sendLogsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendLogsViaEmail();
        }
    };

    private void logTricorderData(TricorderData tricorderData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        Log.d("Log", "=========================================================");
        Log.d("Log", "packet_id:\t\t\t" + tricorderData.getPacketId());
        Log.d("Log", "timestamp:\t\t\t" + sdf.format(tricorderData.getTimestamp()));
        Log.d("Log", "connection_status:\t" + tricorderData.getConnectionStatus());
        Log.d("Log", "charge_percent:\t\t" + tricorderData.getChargePercent());
        Log.d("Log", "accel_x:\t\t\t\t" + tricorderData.getAccelData().getX());
        Log.d("Log", "accel_y:\t\t\t\t" + tricorderData.getAccelData().getY());
        Log.d("Log", "accel_z:\t\t\t\t" + tricorderData.getAccelData().getZ());
        Log.d("Log", "accel_did_vibrate:\t" + tricorderData.getAccelData().getDidVibrate());
        Log.d("Log", "accel_timestamp:\t\t" + sdf.format(tricorderData.getAccelData().getTimestamp()));
        Log.d("Log", "pebble_crc32:\t\t\t" + tricorderData.getPebbleCrc32());
        Log.d("Log", "android_crc32:\t\t" + tricorderData.getAndroidCrc32());
    }

    public void sendLogsViaEmail() {
        StringBuilder log = new StringBuilder();

        log.append("packetId,timestamp,connectionStatus,chargePercent,accelX,accelY,accelZ,accelDidVibrate,accelTimestamp,malformed\n");

        for(TricorderData data : mTricorderDatas) {
            int connected = data.getConnectionStatus() ? 1 : 0;
            int vibrated = data.getAccelData().getDidVibrate() ? 1 : 0;
            int malformed = data.getAndroidCrc32() == data.getPebbleCrc32() ? 0 : 1;

            log.append(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n", data.getPacketId(),
                    data.getTimestamp().getTime(), connected, data.getChargePercent(),
                    data.getAccelData().getX(), data.getAccelData().getY(), data.getAccelData().getZ(),
                    vibrated, data.getAccelData().getTimestamp().getTime(), malformed));
        }

        File file = null;
        String newPath = Environment.getExternalStorageDirectory() + EXT_FOLDERNAME;

        try {
            File f = new File(newPath);
            f.mkdirs();
            FileOutputStream fos = new FileOutputStream(newPath + FILENAME);
            fos.write(log.toString().getBytes());
            fos.close();

            String subject = "Tricorder Data Logs";
            String message = mOverviewFragment.getStatsString();

            file = new File(newPath + FILENAME);
            Uri path = Uri.fromFile(file);

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);

            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (Exception e) {
            Log.e("MainActivity", "There was an issue sending the logs.", e);
        }
    }
}