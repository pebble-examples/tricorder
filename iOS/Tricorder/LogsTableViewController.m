//
//  LogsTableViewController.m
//  Tricorder
//
//  Created by Neal on 7/2/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import "LogsTableViewController.h"

#import "Tricorder.h"
#import "TricorderData.h"

@implementation LogsTableViewController

#pragma mark - UIViewController

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];

    [[NSNotificationCenter defaultCenter] addObserverForName:TricorderDataUpdatedNotification object:[Tricorder sharedTricorder] queue:nil
                                                  usingBlock:^(NSNotification *note) {
                                                      [self.tableView reloadData];
                                                  }];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];

    [[NSNotificationCenter defaultCenter] removeObserver:self name:TricorderDataUpdatedNotification object:[Tricorder sharedTricorder]];
}

#pragma mark - UITableViewController

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return Tricorder.sharedTricorder.numberOfLogs;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *simpleTableIdentifier = @"LogsTableCell";

    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];

    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:simpleTableIdentifier];
    }

    cell.selectionStyle = UITableViewCellSelectionStyleNone;

    TricorderData *data = Tricorder.sharedTricorder.recordedData[indexPath.row];

    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MMM dd, h:mm:ss:SSS"];

    cell.textLabel.text = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:data.timestamp / 1000]];

    cell.detailTextLabel.text = [NSString stringWithFormat:@"Packet %u", data.packetId];

    return cell;
}

#pragma mark - Reset Data

- (IBAction)resetDataButton:(id)sender {
    [Tricorder.sharedTricorder resetData];
    [self.tableView reloadData];
}

@end
