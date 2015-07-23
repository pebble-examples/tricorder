//
//  AccelData.h
//  Tricorder
//
//  Created by Neal on 6/30/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AccelData : NSObject

@property (readonly) int16_t x;
@property (readonly) int16_t y;
@property (readonly) int16_t z;
@property (readonly) BOOL didVibrate;
@property (readonly) uint64_t timestamp;

- (instancetype)initWithData:(NSData *)data;

@end
