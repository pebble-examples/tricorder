//
//  AppDelegate.m
//  Tricorder
//
//  Created by Neal on 6/22/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import "AppDelegate.h"

#import <PebbleKit/PebbleKit.h>

#import "StatsTableViewController.h"
#import "Tricorder.h"

static NSUUID *TricoderAppUUID;

@interface AppDelegate ()

@end

@implementation AppDelegate

+ (void)load {
    TricoderAppUUID = [[NSUUID alloc] initWithUUIDString:@"9151a02e-5cc5-4703-8c18-299482e00317"];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // If you need more logs, uncomment the following line:
    // [PBPebbleCentral setLogLevel:PBPebbleKitLogLevelAll];

    PBPebbleCentral *central = [PBPebbleCentral defaultCentral];
    central.delegate = self;
    central.appUUID = TricoderAppUUID;
    central.dataLoggingService.delegate = [Tricorder sharedTricorder];
    [central run];
    return YES;
}

#pragma mark - PBPebbleCentralDelegate

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidConnect:(PBWatch*)watch isNew:(BOOL)isNew {
    self.connectedWatch = watch;
    self.connectedWatch.delegate = self;
    [self launchPebbleApp];
}

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidDisconnect:(PBWatch*)watch {
    if ([watch isEqual:self.connectedWatch]) {
        self.connectedWatch = nil;
    }
}

#pragma mark - Other

- (void)launchPebbleApp {
    [self.connectedWatch appMessagesLaunch:^(PBWatch *watch, NSError *error) {
        if (error) {
            NSLog(@"Error launching app on Pebble: %@", error);
        }
    }];
}

@end
