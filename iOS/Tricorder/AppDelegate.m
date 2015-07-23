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

static NSString *TricorderAppUuid = @"9151a02e-5cc5-4703-8c18-299482e00317";

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [[PBPebbleCentral defaultCentral] setDelegate:self];
    [self configureForUUID:TricorderAppUuid];

    [[[PBPebbleCentral defaultCentral] dataLoggingService] setDelegate:[Tricorder sharedTricorder]];

    [self watchDidConnect:[PBPebbleCentral defaultCentral].lastConnectedWatch];

    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

#pragma mark - PBWatchDelegate

- (void)watchDidConnect:(PBWatch *)watch {
    self.connectedWatch = watch;
    [self.connectedWatch setDelegate:self];
    [self launchPebbleApp];
    [[[PBPebbleCentral defaultCentral] dataLoggingService] pollForData];
}

#pragma mark - PBPebbleCentralDelegate

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidConnect:(PBWatch*)watch isNew:(BOOL)isNew {
    [self watchDidConnect:watch];
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

- (void)configureForUUID:(NSString *)uuidString {
    uuid_t appUUIDbytes;
    NSUUID *appUUID = [[NSUUID alloc]initWithUUIDString:uuidString];
    [appUUID getUUIDBytes:appUUIDbytes];
    [[PBPebbleCentral defaultCentral] setAppUUID:[NSData dataWithBytes:appUUIDbytes length:16]];
}

@end
