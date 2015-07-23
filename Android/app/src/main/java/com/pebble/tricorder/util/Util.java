package com.pebble.tricorder.util;


public class Util {

    public static int CRC32(byte[] data, int len) {
        int crc  = 0xFFFFFFFF;
        final int poly = 0xEDB88320;

        for (int i = 0; i < len; i++) {
            int temp = (crc ^ data[i]) & 0xff;

            for (int j = 0; j < 8; j++) {
                if ((temp & 1) == 1) temp = (temp >> 1) ^ poly;
                else                 temp = (temp >> 1);
            }
            crc = (crc >> 8) ^ temp;
        }

        return ~crc;
    }
}