package com.pebble.tricorder.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import static com.pebble.tricorder.util.Util.CRC32;


public class TricorderData {
    private int mPacketId;
    private Date mTimestamp;
    private boolean mConnectionStatus;
    private byte mChargePercent;
    private AccelData mAccelData;
    private int mPebbleCrc32;
    private int mAndroidCrc32;

    private final int TRICORDERDATA_SIZE = 31;

    public TricorderData(byte[] data) {
        if (data == null || data.length != TRICORDERDATA_SIZE) {
            throw new IllegalArgumentException("TricorderData must be 31 bytes.");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        mPacketId = buffer.getInt();

        int data_seconds = buffer.getInt();
        int data_ms = buffer.getShort();
        mTimestamp = new Date(((long)data_seconds * 1000) + data_ms);

        mConnectionStatus = buffer.get() == 0x01;
        mChargePercent = buffer.get();

        byte[] accel_buff = new byte[15];
        buffer.get(accel_buff, 0, 15);
        mAccelData = new AccelData(accel_buff);

        mPebbleCrc32 = buffer.getInt();

        data[27] = 0x00;
        data[28] = 0x00;
        data[29] = 0x00;
        data[30] = 0x00;

        mAndroidCrc32 = CRC32(data, TRICORDERDATA_SIZE);
    }

    public int getPacketId() {
        return mPacketId;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public boolean getConnectionStatus() {
        return mConnectionStatus;
    }

    public byte getChargePercent() {
        return mChargePercent;
    }

    public AccelData getAccelData() {
        return mAccelData;
    }

    public int getPebbleCrc32() {
        return mPebbleCrc32;
    }

    public int getAndroidCrc32() {
        return mAndroidCrc32;
    }
}