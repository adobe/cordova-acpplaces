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

package com.adobe.marketing.mobile.cordova;

import android.os.Handler;
import android.os.Looper;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Places;
import com.adobe.marketing.mobile.PlacesAuthorizationStatus;
import com.adobe.marketing.mobile.PlacesPOI;
import com.adobe.marketing.mobile.PlacesRequestError;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACPPlaces_Cordova extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if ("clear".equals(action)) {
            clear(callbackContext);
            return true;
        } else if ("extensionVersion".equals((action))) {
            extensionVersion(callbackContext);
            return true;
        } else if ("getCurrentPointsOfInterest".equals((action))) {
            getCurrentPointsOfInterest(callbackContext);
            return true;
        }else if ("getLastKnownLocation".equals((action))) {
            getLastKnownLocation(callbackContext);
            return true;
        } else if ("getNearbyPointsOfInterest".equals((action))) {
            getNearbyPointsOfInterest(args, callbackContext);
            return true;
        } else if ("processGeofenceEvent".equals((action))) {
            processGeofenceEvent(args, callbackContext);
            return true;
        } else if ("processGeofence".equals((action))) {
            processGeofence(args, callbackContext);
            return true;
        } else if ("processRegionEvent".equals((action))) {
            processRegionEvent(args, callbackContext);
            return true;
        } else if ("setAuthorizationStatus".equals((action))) {
            setAuthorizationStatus(args, callbackContext);
            return true;
        }

        return false;
    }

    private void clear(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Places.clear();
                callbackContext.success();
            }
        });
    }

    private void extensionVersion(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                String extensionVersion = Places.extensionVersion();
                if (extensionVersion.length() > 0) {
                    callbackContext.success(extensionVersion);
                } else {
                    callbackContext.error("Extension version is null or empty");
                }
            }
        });
    }

    private void getCurrentPointsOfInterest(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Places.getCurrentPointsOfInterest(new AdobeCallback<List<PlacesPOI>>() {
                    @Override
                    public void call(List<PlacesPOI> pois) {
                        String placesPoiString = "";
                        if (pois.isEmpty()) {
                            placesPoiString = "[]";
                        } else {
                            for (PlacesPOI poi : pois) {
                                placesPoiString = placesPoiString.concat(String.format("[POI: %s] ", poi.toString()));
                            }
                        }
                        callbackContext.success(placesPoiString);
                    }
                });
            }
        });
    }

    private void getLastKnownLocation(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Places.getLastKnownLocation(new AdobeCallback<Location>() {
                    @Override
                    public void call(Location location) {
                        callbackContext.success(location.toString());
                    }
                });
            }
        });
    }

    private void getNearbyPointsOfInterest(final JSONArray args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (args == null || args.length() != 2) {
                    callbackContext.error("Invalid argument count, expected 2 (location and limit).");
                    return;
                }
                Location location;
                int limit;
                try {
                    location = (Location)args.getJSONObject(0);
                    limit = args.getInt(1);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing arguments, Error " + e.getLocalizedMessage());
                    return;
                }
                Places.getNearbyPointsOfInterest(location, limit, new AdobeCallback<List<PlacesPOI>>() {
                    @Override
                    public void call(List<PlacesPOI> pois) {
                        String placesPoiString = "";
                        if (pois.isEmpty()) {
                            placesPoiString = "[]";
                        } else {
                            for (PlacesPOI poi : pois) {
                                placesPoiString = placesPoiString.concat(String.format("[POI: %s] ", poi.toString()));
                            }
                        }
                        callbackContext.success(placesPoiString);
                    }
                }, new AdobeCallback<PlacesRequestError>() {
                    @Override
                    public void call(PlacesRequestError placesRequestError) {
                        callbackContext.error(placesRequestError.toString());
                    }
                });
            }
        });
    }

    private void processGeofenceEvent(final JSONArray args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (args == null || args.length() != 1) {
                    callbackContext.error("Invalid argument count, expected 1 (geofence event).");
                    return;
                }
                GeofencingEvent geofencingEvent;
                try {
                    geofencingEvent = (GeofencingEvent)args.getJSONObject(0);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing argument, Error " + e.getLocalizedMessage());
                    return;
                }
                Places.processGeofenceEvent(geofencingEvent);
            }
        });
    }

    private void processGeofence(final JSONArray args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (args == null || args.length() != 2) {
                    callbackContext.error("Invalid argument count, expected 2 (geofence, transition type).");
                    return;
                }
                Geofence geofence;
                int transitionType;
                try {
                    geofence = (Geofence)args.getJSONObject(0);
                    transitionType = args.getInt(1);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing argument, Error " + e.getLocalizedMessage());
                    return;
                }
                Places.processGeofence(geofence, transitionType);
            }
        });
    }

     private void processRegionEvent(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // this method is not implemented in Android
                callbackContext.success();
            }
        });
    }

    private void setAuthorizationStatus(final JSONArray args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (args == null || args.length() != 1) {
                    callbackContext.error("Invalid argument count, expected 1 (status).");
                    return;
                }
                PlacesAuthorizationStatus status;
                try {
                    status = (PlacesAuthorizationStatus)args.getJSONObject(0);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing argument, Error " + e.getLocalizedMessage());
                    return;
                }
                Places.setAuthorizationStatus(status);
            }
        });
    }
}
