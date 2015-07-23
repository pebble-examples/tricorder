//
//  AppDelegate.h
//  Tricorder
//
//  Created by Neal on 6/22/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <PebbleKit/PebbleKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, PBPebbleCentralDelegate, PBWatchDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) PBWatch *connectedWatch;

@end
