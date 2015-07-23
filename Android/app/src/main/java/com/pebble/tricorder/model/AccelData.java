package com.pebble.tricorder.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;


public class AccelData {
    private short mX;
    private short mY;
    private short mZ;
    private boolean mDidVibrate;
    private Date mTimestamp;

    private final int ACCELDATA_SIZE = 15;

    public AccelData(byte[] data) {
        if (data == null || data.length != ACCELDATA_SIZE) {
            throw new IllegalArgumentException("AccelData must be 15 bytes.");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        mX = buffer.getShort();
        mY = buffer.getShort();
        mZ = buffer.getShort();
        mDidVibrate = buffer.get() == 0x01;

        long accelTime = buffer.getLong();
        mTimestamp = new Date(accelTime);
    }

    public short getX() {
        return mX;
    }

    public short getY() {
        return mY;
    }

    public short getZ() {
        return mZ;
    }

    public boolean getDidVibrate() {
        return mDidVibrate;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }
}
