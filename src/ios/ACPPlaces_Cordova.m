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
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
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
        NSDictionary* retrievedPoisDict = [[NSMutableDictionary alloc]init];
        [ACPPlaces getCurrentPointsOfInterest:^(NSArray<ACPPlacesPoi *> * _Nullable retrievedPois) {
            if(retrievedPois != nil && retrievedPois.count != 0) {
                NSString* currentPoisString = @"[]";
                for (ACPPlacesPoi* currentPoi in retrievedPois) {
                    [retrievedPoisDict setValue:currentPoi.name forKey:@"POI"];
                    [retrievedPoisDict setValue:[NSNumber numberWithDouble:currentPoi.latitude] forKey:@"Latitude"];
                    [retrievedPoisDict setValue:[NSNumber numberWithDouble:currentPoi.longitude] forKey:@"Longitude"];
                    [retrievedPoisDict setValue:currentPoi.identifier forKey:@"Identifier"];
                }
                NSData* jsonData = [NSJSONSerialization dataWithJSONObject:retrievedPoisDict options:NSJSONWritingPrettyPrinted error:nil];
                currentPoisString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:currentPoisString];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        }];
    }];
}

- (void)getLastKnownLocation:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        __block CLLocation* retrievedLocation;
        [ACPPlaces getLastKnownLocation:^(CLLocation * _Nullable lastLocation) {
            retrievedLocation = lastLocation;
        }];
        NSString* latitude = [[NSString alloc] 
                  initWithFormat:@"%f",
                  retrievedLocation.coordinate.latitude];
        NSString* longitude = [[NSString alloc] 
                  initWithFormat:@"%f",
                  retrievedLocation.coordinate.longitude];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[NSString stringWithFormat:@"latitude: %@ longitude: %@", latitude, longitude]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)getNearbyPointsOfInterest:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSDictionary* retrievedPoisDict = [[NSMutableDictionary alloc]init];
        NSDictionary* locationDict = [self getCommandArg:command.arguments[0]];
        CLLocationDegrees latitude = [[locationDict valueForKey:@"latitude"] doubleValue];
        CLLocationDegrees longitude = [[locationDict valueForKey:@"longitude"] doubleValue];
        CLLocation* currentLocation = [[CLLocation alloc] initWithLatitude:latitude longitude:longitude];
        NSUInteger limit = [[self getCommandArg:command.arguments[1]] integerValue];
        [ACPPlaces getNearbyPointsOfInterest:currentLocation limit:limit callback:^(NSArray<ACPPlacesPoi *> * _Nullable retrievedPois) {
            NSString* currentPoisString = @"[]";
            if(retrievedPois != nil && retrievedPois.count != 0) {
                for (ACPPlacesPoi* currentPoi in retrievedPois) {
                    [retrievedPoisDict setValue:currentPoi.name forKey:@"POI"];
                    [retrievedPoisDict setValue:[NSNumber numberWithDouble:currentPoi.latitude] forKey:@"Latitude"];
                    [retrievedPoisDict setValue:[NSNumber numberWithDouble:currentPoi.longitude] forKey:@"Longitude"];
                    [retrievedPoisDict setValue:currentPoi.identifier forKey:@"Identifier"];
                }
                NSData* jsonData = [NSJSONSerialization dataWithJSONObject:retrievedPoisDict options:NSJSONWritingPrettyPrinted error:nil];
                currentPoisString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:currentPoisString];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        }
        errorCallback:^(ACPPlacesRequestError error) {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[NSString stringWithFormat:@"Places request error code: %lu", error]] callbackId:command.callbackId];
        }];
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
        NSDictionary* regionDict = [self getCommandArg:command.arguments[0]];
        NSDictionary* centerDict = [regionDict valueForKey:@"center"];
        CLLocationDegrees latitude = [[centerDict valueForKey:@"latitude"] doubleValue];
        CLLocationDegrees longitude = [[centerDict valueForKey:@"longitude"] doubleValue];
        CLLocationCoordinate2D center = CLLocationCoordinate2DMake(latitude,longitude);
        NSUInteger radius = [[regionDict valueForKey:@"radius"] integerValue];
        NSString* identifier = [regionDict valueForKey:@"identifier"];
        CLRegion* region = [[CLCircularRegion alloc] initWithCenter:center radius:radius identifier:identifier];
        ACPRegionEventType eventType = [[self getCommandArg:command.arguments[1]] integerValue];
        [ACPPlaces processRegionEvent:region forRegionEventType:eventType];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)setAuthorizationStatus:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        CLAuthorizationStatus status = [[self getCommandArg:command.arguments[0]] integerValue];
        [ACPPlaces setAuthorizationStatus:status];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

/*
 * Helper functions
 */

- (id) getCommandArg:(id) argument {
    return argument == (id)[NSNull null] ? nil : argument;
}


@end
