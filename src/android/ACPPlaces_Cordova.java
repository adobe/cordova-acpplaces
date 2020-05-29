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

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import android.location.Location;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

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
            processRegionEvent(callbackContext);
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
                Location location = new Location("");
                HashMap<String, String> locationMap;
                int limit;
                try {
                    locationMap = getStringMapFromJSON(args.getJSONObject(0));
                    limit = args.getInt(1);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing arguments, Error " + e.getLocalizedMessage());
                    return;
                }

                location.setLatitude(Double.parseDouble(locationMap.get("latitude")));
                location.setLongitude(Double.parseDouble(locationMap.get("longitude")));
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
                HashMap<String, Object> geofenceEventMap;
                try {
                    geofenceEventMap = getObjectMapFromJSON(args.getJSONObject(0));
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing argument, Error " + e.getLocalizedMessage());
                    return;
                }

                // TODO: see what kind of intent we get from native js
                geofencingEvent = null; // should be converted from intent (GeofencingEvent.fromIntent(intent);)
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
                int transitionType;
                HashMap<String, Object> geofenceMap;
                try {
                    geofenceMap = getObjectMapFromJSON(args.getJSONObject(0));
                    transitionType = args.getInt(1);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing argument, Error " + e.getLocalizedMessage());
                    return;
                }
                ArrayList<String> circularRegion = (ArrayList)geofenceMap.get("circularRegion");
                double latitude = Double.parseDouble(circularRegion.get(0));
                double longitude = Double.parseDouble(circularRegion.get(1));
                float radius = Float.parseFloat(circularRegion.get(2));
                long expirationDuration = Long.parseLong((String)geofenceMap.get("expirationDuration"));
                Geofence geofence = new Geofence.Builder().setCircularRegion(latitude, longitude, radius).setExpirationDuration(expirationDuration).setTransitionTypes(transitionType).build();
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
                    status = getAuthorizationStatus(args.getInt(0));
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing argument, Error " + e.getLocalizedMessage());
                    return;
                }

                Places.setAuthorizationStatus(status);
            }
        });
    }

    // ===============================================================
    // Helpers
    // ===============================================================
    private HashMap<String, String> getStringMapFromJSON(JSONObject data) {
        HashMap<String, String> map = new HashMap<String, String>();
        @SuppressWarnings("rawtypes")
        Iterator it = data.keys();
        while (it.hasNext()) {
            String n = (String) it.next();
            try {
                map.put(n, data.getString(n));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private HashMap<String, Object> getObjectMapFromJSON(JSONObject data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        @SuppressWarnings("rawtypes")
        Iterator it = data.keys();
        while (it.hasNext()) {
            String n = (String) it.next();
            try {
                map.put(n, data.getString(n));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private PlacesAuthorizationStatus getAuthorizationStatus(final int status){
        if(status == 0) {
            return  PlacesAuthorizationStatus.DENIED;
        } else if(status == 1) {
            return PlacesAuthorizationStatus.ALWAYS;
        } else if(status == 2) {
            return PlacesAuthorizationStatus.UNKNOWN;
        } else if(status == 3) {
            return PlacesAuthorizationStatus.RESTRICTED;
        } else if(status == 4) {
            return PlacesAuthorizationStatus.WHEN_IN_USE;
        } else {
            return null;
        }
    }
}
