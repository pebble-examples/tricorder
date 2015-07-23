//
//  DataloggingReceiver.h
//  Tricorder
//
//  Created by Neal on 7/2/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import <PebbleKit/PebbleKit.h>

extern NSString *const TricorderDataUpdatedNotification;

@interface Tricorder : NSObject <PBDataLoggingServiceDelegate>

@property (readonly) NSMutableArray *recordedData;
@property (readonly) NSInteger crcMismatches;
@property (readonly) NSInteger duplicatePackets;
@property (readonly) NSInteger outOfOrderPackets;
@property (readonly) NSInteger missingPackets;

+ (instancetype)sharedTricorder;

- (NSString *)connectionStatus;
- (uint32_t)latestPacketId;
- (NSString *)latestPacketTime;
- (NSUInteger)numberOfLogs;
- (void)resetData;

@end
