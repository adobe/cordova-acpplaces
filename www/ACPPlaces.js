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
    // public enums
    // ===========================================================================
    // ACPPlacesRequestError
    ACPPlaces.ACPPlacesRequestErrorNone = 0;
    ACPPlaces.ACPPlacesRequestErrorConnectivityError = 1;
    ACPPlaces.ACPPlacesRequestErrorServerResponseError = 2;
    ACPPlaces.ACPPlacesRequestErrorInvalidLatLongError = 3;
    ACPPlaces.ACPPlacesRequestErrorConfigurationError = 4;
    ACPPlaces.ACPPlacesRequestErrorQueryServiceUnavailable = 5;
    ACPPlaces.ACPPlacesRequestErrorUnknownError = 6;

    // ACPRegionEventType
    ACPPlaces.ACPRegionEventTypeNone = 0;
    ACPPlaces.ACPRegionEventTypeEntry = 1;
    ACPPlaces.ACPRegionEventTypeExit = 2;
	// ===========================================================================
	// public APIs
	// ===========================================================================

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

        return exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
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
        return exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
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
        return exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, []);
    };

    // Requests a list of nearby Points of Interest (POI).
    ACPPlaces.getNearbyPointsOfInterest = function (location, limit, success, error) {
        var FUNCTION_NAME = "getNearbyPointsOfInterest";

        if (location && !acpIsValidLocation(location)) {
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

        return exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [location, limit]);
    };


    // Pass a Geofence and transition type to be processed by the SDK.
    // This corresponds to Android ACPPlaces.processGeofence and iOS ACPPlaces.processRegionEvent
    ACPPlaces.processGeofence = function (geofence, transitionType, success, error) {
        var FUNCTION_NAME = "processGeofence";

        if (geofence && !acpIsValidGeofence(geofence)) {
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

        return exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [geofence, transitionType]);
    };

    // Sets the authorization status in the Places extension.
    ACPPlaces.setAuthorizationStatus = function (status, success, error) {
        var FUNCTION_NAME = "setAuthorizationStatus";

        if (status && !acpIsNumber(status)) {
            acpPrintNotANumber("status", FUNCTION_NAME);
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

        return exec(success, error, 'ACPPlaces_Cordova', FUNCTION_NAME, [status]);
    };

	return ACPPlaces;
}());

// ===========================================================================
// helper functions
// ===========================================================================
function acpIsString (value) {
    return typeof value === 'string' || value instanceof String;
};

function acpPrintNotAString (paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be a String.");
};

function isFunction (value) {
    return typeof value === 'function';
}

function printNotAFunction (paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be a function.");
}

function acpIsObject (value) {
    return value && typeof value === 'object' && value.constructor === Object;
};

function acpPrintNotAnObject (paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be an Object.");
};

function acpIsNumber (value) {
    return typeof value === 'number' && isFinite(value);
};

function acpPrintNotANumber (paramName, functionName) {
    console.log("Ignoring call to '" + functionName + "'. The '" + paramName + "' parameter is required to be a Number.");
};

function acpIsValidLocation (location) {
    if (!acpIsNumber(location.latitude)) {
        console.log("location.latitude must be of type Number.");
        return false;
    }

    if (!acpIsNumber(location.longitude)) {
        console.log("location.longitude must be of type Number.");
        return false;
    }

    return true;
};

function acpIsValidGeofence (geofence) {
    if (!acpIsString(geofence.requestId)) {
        console.log("geofence.requestId must be of type String.");
        return false;
    }

    if (!acpIsValidCircularRegion(geofence.circularRegion)) {
        return false;
    }

    if (!acpIsNumber(geofence.expirationDuration)) {
        console.log("geofence.expirationDuration must be of type Number.");
        return false;
    }

    return true;
};

function acpIsValidCircularRegion (circularRegion) {
    if (!acpIsNumber(circularRegion.latitude)) {
        console.log("circularRegion.latitude must be of type Number.");
        return false;
    }

    if (!acpIsNumber(circularRegion.longitude)) {
        console.log("circularRegion.longitude must be of type Number.");
        return false;
    }

    if (!acpIsNumber(circularRegion.radius)) {
        console.log("circularRegion.radius must be of type Number.");
        return false;
    }

    return true;
};

module.exports = ACPPlaces;
