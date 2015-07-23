//
//  StatsTableViewController.m
//  Tricorder
//
//  Created by Neal on 7/1/15.
//  Copyright (c) 2015 Pebble Technology. All rights reserved.
//

#import "StatsTableViewController.h"

#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>

#import "Tricorder.h"
#import "TricorderData.h"

@interface StatsTableViewController () <ChartViewDelegate, MFMailComposeViewControllerDelegate>

@property (nonatomic, strong) IBOutlet UIBarButtonItem *sendEmailButton;

@property (nonatomic, strong) LineChartView *accelChartView;
@property (nonatomic, strong) LineChartView *connectionChartView;
@property (nonatomic, strong) LineChartView *batteryChartView;

@end

@implementation StatsTableViewController

#pragma mark - UIViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    if (![MFMailComposeViewController canSendMail]) {
        _sendEmailButton.enabled = NO;
    }

    _accelChartView = [[LineChartView alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, 256)];
    _accelChartView.descriptionText = @"";
    _accelChartView.rightAxis.enabled = NO;
    _accelChartView.drawGridBackgroundEnabled = NO;
    _accelChartView.userInteractionEnabled = NO;
    [_accelChartView setScaleEnabled:NO];

    _connectionChartView = [[LineChartView alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, 256)];
    _connectionChartView.descriptionText = @"";
    _connectionChartView.rightAxis.enabled = NO;
    _connectionChartView.userInteractionEnabled = NO;
    _connectionChartView.drawGridBackgroundEnabled = NO;
    [_connectionChartView setScaleEnabled:NO];

    _batteryChartView = [[LineChartView alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, 256)];
    _batteryChartView.descriptionText = @"";
    _batteryChartView.rightAxis.enabled = NO;
    _batteryChartView.userInteractionEnabled = NO;
    _batteryChartView.drawGridBackgroundEnabled = NO;
    [_batteryChartView setScaleEnabled:NO];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];

    [[NSNotificationCenter defaultCenter] addObserverForName:TricorderDataUpdatedNotification object:[Tricorder sharedTricorder] queue:nil
                                                  usingBlock:^(NSNotification *note) {
                                                      [self populateAccelChartData];
                                                      [self populateConnectionChartData];
                                                      [self populateBatteryChartData];
                                                      [self.tableView reloadData];
                                                  }];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];

    [[NSNotificationCenter defaultCenter] removeObserver:self name:TricorderDataUpdatedNotification object:[Tricorder sharedTricorder]];
}

#pragma mark - UITableViewController

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 4;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 32;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 8;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case 0:
            return 44;

        case 1:
        case 2:
        case 3:
            return 256;

        default:
            return 0;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0:
            return 7;

        case 1:
        case 2:
        case 3:
            return 1;

        default:
            return 0;
    }
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0:
            return @"Stats";

        case 1:
            return @"Accel Graph";

        case 2:
            return @"Connection Status Graph";

        case 3:
            return @"Battery Graph";

        default:
            return @"";
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell;

    switch (indexPath.section) {
        case 0:
            if ((cell = [tableView dequeueReusableCellWithIdentifier:@"StatsTableCell"]) == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"StatsTableCell"];
            }

            switch (indexPath.row) {
                case 0:
                    cell.textLabel.text = @"Connection Status";
                    cell.detailTextLabel.text = Tricorder.sharedTricorder.connectionStatus;
                    break;

                case 1:
                    cell.textLabel.text = @"Last Packet ID";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%u", Tricorder.sharedTricorder.latestPacketId];
                    break;

                case 2:
                    cell.textLabel.text = @"Last Packet Time";
                    cell.detailTextLabel.text = Tricorder.sharedTricorder.latestPacketTime;
                    break;

                case 3:
                    cell.textLabel.text = @"CRC Mismatches";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%ld", (long)Tricorder.sharedTricorder.crcMismatches];
                    break;

                case 4:
                    cell.textLabel.text = @"Duplicate Packets";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%ld", (long)Tricorder.sharedTricorder.duplicatePackets];
                    break;

                case 5:
                    cell.textLabel.text = @"Out of Order Packets";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%ld", (long)Tricorder.sharedTricorder.outOfOrderPackets];
                    break;

                case 6:
                    cell.textLabel.text = @"Missing Packets";
                    cell.detailTextLabel.text = [NSString stringWithFormat:@"%ld", (long)Tricorder.sharedTricorder.missingPackets];
                    break;
            }
            break;

        case 1:
            if ((cell = [tableView dequeueReusableCellWithIdentifier:@"StatsTableCellChart"]) == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"StatsTableCellChart"];
            }

            [cell addSubview:_accelChartView];
            break;

        case 2:
            if ((cell = [tableView dequeueReusableCellWithIdentifier:@"StatsTableCellChart"]) == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"StatsTableCellChart"];
            }

            [cell addSubview:_connectionChartView];
            break;

        case 3:
            if ((cell = [tableView dequeueReusableCellWithIdentifier:@"StatsTableCellChart"]) == nil) {
                cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"StatsTableCellChart"];
            }

            [cell addSubview:_batteryChartView];
            break;
    }

    cell.selectionStyle = UITableViewCellSelectionStyleNone;

    return cell;
}

#pragma mark - Other Chart Stuff

- (void)populateAccelChartData {
    NSMutableArray *packetIds = [[NSMutableArray alloc] init];
    NSMutableArray *xVals = [[NSMutableArray alloc] init];
    NSMutableArray *yVals = [[NSMutableArray alloc] init];
    NSMutableArray *zVals = [[NSMutableArray alloc] init];

    NSArray *graphData = [[[Tricorder.sharedTricorder.recordedData subarrayWithRange:NSMakeRange(0, MIN(100, Tricorder.sharedTricorder.recordedData.count))] reverseObjectEnumerator] allObjects];

    float leftAxisMax = 0, leftAxisMin = 0;

    for (NSUInteger i = 0; i < graphData.count; i++) {
        TricorderData *data = [graphData objectAtIndex:i];

        [packetIds insertObject:[@(data.packetId) stringValue] atIndex:i];

        [xVals addObject:[[ChartDataEntry alloc] initWithValue:data.accelData.x xIndex:i]];
        [yVals addObject:[[ChartDataEntry alloc] initWithValue:data.accelData.y xIndex:i]];
        [zVals addObject:[[ChartDataEntry alloc] initWithValue:data.accelData.z xIndex:i]];

        if (data.accelData.x > leftAxisMax) leftAxisMax = data.accelData.x;
        if (data.accelData.x < leftAxisMin) leftAxisMin = data.accelData.x;
        if (data.accelData.y > leftAxisMax) leftAxisMax = data.accelData.y;
        if (data.accelData.y < leftAxisMin) leftAxisMin = data.accelData.y;
        if (data.accelData.z > leftAxisMax) leftAxisMax = data.accelData.z;
        if (data.accelData.z < leftAxisMin) leftAxisMin = data.accelData.z;
    }

    ChartYAxis *leftAxis = _accelChartView.leftAxis;
    leftAxis.customAxisMax = leftAxisMax + 200;
    leftAxis.customAxisMin = leftAxisMin - 200;
    leftAxis.startAtZeroEnabled = NO;

    NSMutableArray *dataSets = [[NSMutableArray alloc] init];

    LineChartDataSet *accel_x = [[LineChartDataSet alloc] initWithYVals:xVals label:@"Accel X"];

    [accel_x setColor:UIColor.redColor];
    [accel_x setCircleColor:UIColor.redColor];
    accel_x.circleRadius = 3.0;
    accel_x.drawFilledEnabled = NO;
    accel_x.drawCircleHoleEnabled = YES;

    [dataSets addObject:accel_x];

    LineChartDataSet *accel_y = [[LineChartDataSet alloc] initWithYVals:yVals label:@"Accel Y"];

    [accel_y setColor:UIColor.greenColor];
    [accel_y setCircleColor:UIColor.greenColor];
    accel_y.circleRadius = 3.0;
    accel_y.drawFilledEnabled = NO;
    accel_y.drawCircleHoleEnabled = YES;

    [dataSets addObject:accel_y];

    LineChartDataSet *accel_z = [[LineChartDataSet alloc] initWithYVals:zVals label:@"Accel Z"];

    [accel_z setColor:UIColor.blueColor];
    [accel_z setCircleColor:UIColor.blueColor];
    accel_z.circleRadius = 3.0;
    accel_z.drawFilledEnabled = NO;
    accel_z.drawCircleHoleEnabled = YES;

    [dataSets addObject:accel_z];

    LineChartData *data = [[LineChartData alloc] initWithXVals:packetIds dataSets:dataSets];

    _accelChartView.data = data;
}

- (void)populateConnectionChartData {
    NSMutableArray *packetIds = [[NSMutableArray alloc] init];
    NSMutableArray *xVals = [[NSMutableArray alloc] init];

    NSArray *graphData = [[[Tricorder.sharedTricorder.recordedData subarrayWithRange:NSMakeRange(0, MIN(100, Tricorder.sharedTricorder.recordedData.count))] reverseObjectEnumerator] allObjects];

    for (NSUInteger i = 0; i < graphData.count; i++) {
        TricorderData *data = [graphData objectAtIndex:i];

        [packetIds insertObject:[@(data.packetId) stringValue] atIndex:i];

        [xVals addObject:[[ChartDataEntry alloc] initWithValue:data.connectionStatus xIndex:i]];
    }

    ChartYAxis *leftAxis = _connectionChartView.leftAxis;
    leftAxis.customAxisMax = 2;
    leftAxis.customAxisMin = -2;
    leftAxis.startAtZeroEnabled = NO;

    NSMutableArray *dataSets = [[NSMutableArray alloc] init];

    LineChartDataSet *connectionStatus = [[LineChartDataSet alloc] initWithYVals:xVals label:@"Connection Status"];

    [connectionStatus setColor:UIColor.redColor];
    [connectionStatus setCircleColor:UIColor.redColor];
    connectionStatus.circleRadius = 3.0;
    connectionStatus.drawFilledEnabled = NO;
    connectionStatus.drawCircleHoleEnabled = YES;

    [dataSets addObject:connectionStatus];

    LineChartData *data = [[LineChartData alloc] initWithXVals:packetIds dataSets:dataSets];

    _connectionChartView.data = data;
}

- (void)populateBatteryChartData {
    NSMutableArray *packetIds = [[NSMutableArray alloc] init];
    NSMutableArray *xVals = [[NSMutableArray alloc] init];

    NSArray *graphData = [[Tricorder.sharedTricorder.recordedData reverseObjectEnumerator] allObjects];

    for (NSUInteger i = 0; i < graphData.count; i++) {
        TricorderData *data = [graphData objectAtIndex:i];

        [packetIds insertObject:[@(data.packetId) stringValue] atIndex:i];

        [xVals addObject:[[ChartDataEntry alloc] initWithValue:data.chargePercent xIndex:i]];
    }

    ChartYAxis *leftAxis = _batteryChartView.leftAxis;
    leftAxis.customAxisMax = 100;
    leftAxis.customAxisMin = 0;
    leftAxis.startAtZeroEnabled = NO;

    NSMutableArray *dataSets = [[NSMutableArray alloc] init];

    LineChartDataSet *batteryPercent = [[LineChartDataSet alloc] initWithYVals:xVals label:@"Battery Percent"];

    [batteryPercent setColor:UIColor.greenColor];
    [batteryPercent setCircleColor:UIColor.greenColor];
    batteryPercent.circleRadius = 3.0;
    batteryPercent.drawFilledEnabled = NO;
    batteryPercent.drawCircleHoleEnabled = YES;

    [dataSets addObject:batteryPercent];

    LineChartData *data = [[LineChartData alloc] initWithXVals:packetIds dataSets:dataSets];

    _batteryChartView.data = data;
}

#pragma mark - Reset Data

- (IBAction)resetDataButton:(id)sender {
    [Tricorder.sharedTricorder resetData];

    [self.tableView reloadData];
}

#pragma mark - Mail

- (IBAction)sendEmailButton:(id)sender {
    if (![MFMailComposeViewController canSendMail]) {
        _sendEmailButton.enabled = NO;
        return;
    }

    MFMailComposeViewController *mailController = [[MFMailComposeViewController alloc] init];
    mailController.mailComposeDelegate = self;
    [mailController setSubject:@"Pebble Tricorder Logs"];

    NSMutableString *body = [@"Here be the tricorder logs.\n\n" mutableCopy];
    [body appendFormat:@"CRC Mismatches: %ld\n", (long)Tricorder.sharedTricorder.crcMismatches];
    [body appendFormat:@"Duplicate Packets: %ld\n", (long)Tricorder.sharedTricorder.duplicatePackets];
    [body appendFormat:@"Out of Order Packets: %ld\n", (long)Tricorder.sharedTricorder.outOfOrderPackets];
    [body appendFormat:@"Missing Packets: %ld\n", (long)Tricorder.sharedTricorder.missingPackets];

    [mailController setMessageBody:body isHTML:NO];

    NSMutableString *csv = [@"packetId,timestamp,connectionStatus,chargePercent,accelX,accelY,accelZ,accelDidVibrate,accelTimestamp,malformed\n" mutableCopy];
    for (TricorderData *data in Tricorder.sharedTricorder.recordedData) {
        [csv appendFormat:@"%u,%llu,%d,%hhu,%hd,%hd,%hd,%d,%llu,%d\n", data.packetId, data.timestamp, data.connectionStatus, data.chargePercent, data.accelData.x, data.accelData.y, data.accelData.z, data.accelData.didVibrate, data.accelData.timestamp, (data.crc32Pebble != data.crc32Phone)];
    }
    NSData *attachmentData = [csv dataUsingEncoding:NSUTF8StringEncoding];

    [mailController addAttachmentData:attachmentData mimeType:@"text/cvs" fileName:@"tricorder-logs.csv"];

    [self presentViewController:mailController animated:YES completion:nil];
}

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error {
    [controller dismissViewControllerAnimated:YES completion:nil];
}

@end
