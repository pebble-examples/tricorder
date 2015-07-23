//
//  Utils.m
//  Tricorder
//
//  Created by Neal on 6/30/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import <Foundation/Foundation.h>

uint32_t pbl_crc32(const void *buf, size_t len) {
    int crc  = 0xFFFFFFFF;
    int poly = 0xEDB88320;

    uint32_t i = 0;
    while (i < len) {
        int temp = (crc ^ ((char*)buf)[i]) & 0xff;

        for (int j = 0; j < 8; j++) {
            if ((temp & 1) == 1) temp = (temp >> 1) ^ poly;
            else                 temp = (temp >> 1);
        }
        crc = (crc >> 8) ^ temp;
        i++;
    }

    return crc ^ 0xffffffff;
}
