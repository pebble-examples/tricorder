//
//  TricorderData.m
//  Tricorder
//
//  Created by Neal on 6/30/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import "TricorderData.h"

#import "Utils.h"

static NSUInteger const TricorderDataLength = 31;

@implementation TricorderData

- (instancetype)initWithBytes:(const UInt8 *const)bytes andLength:(NSUInteger)length {
    if (self = [super init]) {
        if (length != TricorderDataLength) {
            NSLog(@"TricorderData size mismatch: got %lu but expected %lu", (unsigned long)length, (unsigned long)TricorderDataLength);
            return nil;
        }

        NSMutableData *data = [NSMutableData dataWithBytes:bytes length:length];

        const void *subdataBytes;
        NSRange range = {0, 0};

        range.location += range.length;
        range.length = 4;
        subdataBytes = [[data subdataWithRange:range] bytes];

        _packetId = CFSwapInt32LittleToHost(*(uint32_t*)subdataBytes);

        range.location += range.length;
        range.length = 4;
        subdataBytes = [[data subdataWithRange:range] bytes];

        _timestamp = CFSwapInt64LittleToHost(*(uint64_t*)subdataBytes) * 1000;

        range.location += range.length;
        range.length = 2;
        subdataBytes = [[data subdataWithRange:range] bytes];

        _timestamp += CFSwapInt16LittleToHost(*(uint16_t*)subdataBytes);

        range.location += range.length;
        range.length = 1;
        subdataBytes = [[data subdataWithRange:range] bytes];

        _connectionStatus = CFSwapInt16LittleToHost(*(uint16_t*)subdataBytes);

        range.location += range.length;
        range.length = 1;
        subdataBytes = [[data subdataWithRange:range] bytes];

        _chargePercent = CFSwapInt16LittleToHost(*(uint16_t*)subdataBytes);

        range.location += range.length;
        range.length = 15;

        _accelData = [[AccelData alloc] initWithData:[data subdataWithRange:range]];

        range.location += range.length;
        range.length = 4;
        subdataBytes = [[data subdataWithRange:range] bytes];

        _crc32Pebble = CFSwapInt32LittleToHost(*(uint32_t*)subdataBytes);

        [data replaceBytesInRange:range withBytes:NULL length:range.length];

        _crc32Phone = pbl_crc32(data.bytes, length);
    }

    return self;
}

- (void)log {
    NSLog(@"=========================================================================");
    NSLog(@"packet_id:\t\t\t%d", _packetId);
    NSLog(@"timestamp:\t\t\t%llu", _timestamp);
    NSLog(@"connection_status:\t%d", _connectionStatus);
    NSLog(@"charge_percent:\t\t%d", _chargePercent);
    NSLog(@"accel:\t\t\t\t%05d\t%05d\t%05d\t%d\t%llu", _accelData.x, _accelData.y, _accelData.z, _accelData.didVibrate, _accelData.timestamp);
    NSLog(@"crc32_pebble:\t\t\t%d", _crc32Pebble);
    NSLog(@"crc32_phone:\t\t\t%d", _crc32Phone);
}

@end
