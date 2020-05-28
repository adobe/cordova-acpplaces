/*
 Copyright 2020 Adobe. All rights reserved.
 This file is licensed to you under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software distributed under
 the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 OF ANY KIND, either express or implied. See the License for the specific language
 governing permissions and limitations under the License.
 */

/********* cordova-acpplaces.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <ACPPlaces/ACPPlaces.h>
#import <Cordova/CDVPluginResult.h>

@interface ACPPlaces_Cordova : CDVPlugin

- (void)clear:(CDVInvokedUrlCommand*)command;
- (void)extensionVersion:(CDVInvokedUrlCommand*)command;
- (void)getCurrentPointsOfInterest:(CDVInvokedUrlCommand*)command;
- (void)getLastKnownLocation:(CDVInvokedUrlCommand*)command;
- (void)getNearbyPointsOfInterest:(CDVInvokedUrlCommand*)command;
- (void)processGeofenceEvent:(CDVInvokedUrlCommand*)comman;
- (void)processGeofence:(CDVInvokedUrlCommand*)command;
- (void)processRegionEvent:(CDVInvokedUrlCommand*)command;
- (void)setAuthorizationStatus:(CDVInvokedUrlCommand*)command;

@end

@implementation ACPPlaces_Cordova

- (void)clear:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        [ACPPlaces clear];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:extensionVersion];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)extensionVersion:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* pluginResult = nil;
        NSString* extensionVersion = [ACPPlaces extensionVersion];

        if (extensionVersion != nil && [extensionVersion length] > 0) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:extensionVersion];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)getCurrentPointsOfInterest:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CLLocation *currentLocation = [command.arguments objectAtIndex:0];
        NSUInteger limit = [command.arguments objectAtIndex:1];
        [ACPPlaces getNearbyPointsOfInterest: currentLocation limit: limit callback:^(NSArray<ACPPlacesPoi *> * _Nullable nearbyPoi) {
            NSString* placesPoiString = @"[]";
            for (ACPPlacesPoi *currentPoi in nearbyPoi) {
                placesPoiString = [placesPoiString stringByAppendingFormat:@"[POI: %@]", currentPoi];
            }
        }
        errorCallback:^(ACPPlacesRequestError error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[NSString stringWithFormat:@"Error dispatching event: %@", error.localizedDescription ?: @"unknown error"]] callbackId:command.callbackId];
        }];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:placesPoiString];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)getLastKnownLocation:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString *locationString = @"";
        [ACPPlaces getLastKnownLocation: ^(CLLocation * _Nullable lastLocation) {
            locationString = lastLocation;
        }];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:locationString];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)getNearbyPointsOfInterest:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CLLocation *currentLocation = [command.arguments objectAtIndex:0];
        NSUInteger limit = [command.arguments objectAtIndex:1];
        [ACPPlaces getNearbyPointsOfInterest: currentLocation limit: limit callback:^(NSArray<ACPPlacesPoi *> * _Nullable nearbyPoi) {
            NSString* placesPoiString = @"[]";
            for (ACPPlacesPoi *currentPoi in nearbyPoi) {
                placesPoiString = [placesPoiString stringByAppendingFormat:@"[POI: %@]", currentPoi];
            }
        }
        errorCallback:^(ACPPlacesRequestError error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[NSString stringWithFormat:@"Error dispatching event: %@", error.localizedDescription ?: @"unknown error"]] callbackId:command.callbackId];
        }];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:placesPoiString];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)processGeofenceEvent:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        // this method is not implemented in iOS
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)processGeofence:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        // this method is not implemented in iOS
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)processRegionEvent:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CLRegion *region = [command.arguments objectAtIndex:0];
        ACPRegionEventType eventType = [command.arguments objectAtIndex:1];
        [ACPPlaces processRegionEvent: region forRegionEventType:eventType];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:extensionVersion];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)setAuthorizationStatus:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CLAuthorizationStatus status = [command.arguments objectAtIndex:0];
        [ACPPlaces setAuthorizationStatus:status];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

@end
