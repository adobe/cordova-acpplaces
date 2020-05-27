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

var ACPPlaces = (function() {
    var exec = require('cordova/exec');
	var ACPPlaces = (typeof exports !== 'undefined') && exports || {};
	var PLUGIN_NAME = "ACPPlacesCordova";
	// ===========================================================================
	// public APIs
	// ===========================================================================

    // Gets the current Places extension version.
    ACPPlaces.extensionVersion = function (success, error) {
        var FUNCTION_NAME = "extensionVersion";

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }

        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
    };

    // Requests a list of nearby Points of Interest (POI).
    ACPPlaces.getNearbyPointsOfInterest = function (location, limit, success, error) {
        var FUNCTION_NAME = "getNearbyPointsOfInterest";

        if (location && !acpIsObject(location)) {
            acpPrintNotAnObject("location", FUNCTION_NAME);
            return;
        }

        if (limit && !acpIsNumber(limit)) {
            acpPrintNotANumber("limit", FUNCTION_NAME);
            return;
        }

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }

        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [location, limit]);
    };

    // Pass a GeofencingEvent to be processed by the SDK.
    ACPPlaces.processGeofenceEvent = function (geofencingEvent, success, error) {
        var FUNCTION_NAME = "processGeofenceEvent";

        if (geofencingEvent && !acpIsObject(geofencingEvent)) {
            acpPrintNotAnObject("geofencingEvent", FUNCTION_NAME);
            return;
        }

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }
        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [geofencingEvent]);
    };

    // Pass a Geofence and transition type to be processed by the SDK.
    ACPPlaces.processGeofence = function (geofence, transitionType, success, error) {
        var FUNCTION_NAME = "processGeofence";

        if (geofence && !acpIsObject(geofence)) {
            acpPrintNotAnObject("geofence", FUNCTION_NAME);
            return;
        }

        if (transitionType && !acpIsNumber(transitionType)) {
            acpPrintNotANumber("transitionType", FUNCTION_NAME);
            return;
        }

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }

        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [geofence, transitionType]);
    };

    // Returns all Points of Interest (POI) in which the device is currently known to be within.
    ACPPlaces.getCurrentPointsOfInterest = function (success, error) {
        var FUNCTION_NAME = "getCurrentPointsOfInterest";

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }
        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
    };

    // Returns the last latitude and longitude provided to the ACPPlaces Extension.
    ACPPlaces.getLastKnownLocation = function (success, error) {
        var FUNCTION_NAME = "getLastKnownLocation";

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }
        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
    };

    // Clears out the client-side data for Places in shared state, local storage, and in-memory.
    ACPPlaces.clear = function (success, error) {
        var FUNCTION_NAME = "clear";

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }

        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
    };

    // Sets the authorization status in the Places extension.
    ACPPlaces.setAuthorizationStatus = function (status, success, error) {
        var FUNCTION_NAME = "setAuthorizationStatus";

        if (status && !acpIsObject(status)) {
            acpPrintNotAnObject("status", FUNCTION_NAME);
            return;
        }

        if (success && !isFunction(success)) {
            printNotAFunction("success", FUNCTION_NAME);
            return;
        }

        if (error && !isFunction(error)) {
            printNotAFunction("error", FUNCTION_NAME);
            return;
        }

        exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [status]);
    };

	return ACPPlaces;
}());

// ===========================================================================
// helper functions
// ===========================================================================
function isFunction (value) {
    return typeof value === 'function';
}

function printNotAFunction(paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be a function.");
}

function acpIsObject = function (value) {
    return value && typeof value === 'object' && value.constructor === Object;
};

function acpPrintNotAnObject = function (paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be an Object.");
};

function acpIsNumber = function (value) {
    return typeof value === 'number' && isFinite(value);
};

function acpPrintNotANumber = function (paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be a Number.");
};

module.exports = ACPPlaces;
