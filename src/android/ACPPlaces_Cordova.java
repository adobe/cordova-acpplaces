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
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Places;
import com.adobe.marketing.mobile.PlacesAuthorizationStatus;
import com.adobe.marketing.mobile.PlacesPOI;
import com.adobe.marketing.mobile.PlacesRequestError;

import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import android.location.Location;
import com.google.android.gms.location.Geofence;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACPPlaces_Cordova extends CordovaPlugin {

    final static String METHOD_PLACES_CLEAR = "clear";
    final static String METHOD_PLACES_EXTENSION_VERSION_PLACES = "extensionVersion";
    final static String METHOD_PLACES_GET_CURRENT_POINTS_OF_INTEREST = "getCurrentPointsOfInterest";
    final static String METHOD_PLACES_GET_LAST_KNOWN_LOCATION = "getLastKnownLocation";
    final static String METHOD_PLACES_GET_NEARBY_POINTS_OF_INTEREST = "getNearbyPointsOfInterest";
    final static String METHOD_PLACES_PROCESS_GEOFENCE = "processGeofence";
    final static String METHOD_PLACES_SET_AUTHORIZATION_STATUS = "setAuthorizationStatus";

    final static String LOG_TAG = "ACPPlaces_Cordova";

    final static String POI = "POI";
    final static String LATITUDE = "Latitude";
    final static String LONGITUDE = "Longitude";
    final static String LOWERCASE_LATITUDE = "latitude";
    final static String LOWERCASE_LONGITUDE = "longitude";
    final static String IDENTIFIER = "Identifier";
    final static String RADIUS = "radius";
    final static String REQUEST_ID = "requestId";
    final static String CIRCULAR_REGION = "circularRegion";
    final static String EXPIRATION_DURATION = "expirationDuration";
    final static String PROVIDER = "cordova-plugin-geolocation";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if (METHOD_PLACES_CLEAR.equals(action)) {
            clear(callbackContext);
            return true;
        } else if (METHOD_PLACES_EXTENSION_VERSION_PLACES.equals((action))) {
            extensionVersion(callbackContext);
            return true;
        } else if (METHOD_PLACES_GET_CURRENT_POINTS_OF_INTEREST.equals((action))) {
            getCurrentPointsOfInterest(callbackContext);
            return true;
        }else if (METHOD_PLACES_GET_LAST_KNOWN_LOCATION.equals((action))) {
            getLastKnownLocation(callbackContext);
            return true;
        } else if (METHOD_PLACES_GET_NEARBY_POINTS_OF_INTEREST.equals((action))) {
            getNearbyPointsOfInterest(args, callbackContext);
            return true;
        } else if (METHOD_PLACES_PROCESS_GEOFENCE.equals((action))) {
            processGeofence(args, callbackContext);
            return true;
        } else if (METHOD_PLACES_SET_AUTHORIZATION_STATUS.equals((action))) {
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
                        callbackContext.success(generatePOIString(pois));
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
                        if(location != null) {
                            JSONObject json = new JSONObject();;
                            try {
                                json.put(LATITUDE, location.getLatitude());
                                json.put(LONGITUDE, location.getLongitude());
                            } catch (JSONException e){
                                LOG.d(LOG_TAG, "Error putting data into JSON: " + e.getLocalizedMessage());
                            }
                            callbackContext.success(json.toString());
                        } else {
                            callbackContext.error("Error retrieving last known location.");
                        }
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
                Location location = new Location(PROVIDER);
                HashMap<String, String> locationMap;
                int limit;
                try {
                    locationMap = getStringMapFromJSON(args.getJSONObject(0));
                    limit = args.getInt(1);
                } catch (JSONException e) {
                    callbackContext.error("Error while parsing arguments, Error " + e.getLocalizedMessage());
                    return;
                }

                location.setLatitude(Double.parseDouble(locationMap.get(LOWERCASE_LATITUDE)));
                location.setLongitude(Double.parseDouble(locationMap.get(LOWERCASE_LONGITUDE)));
                Places.getNearbyPointsOfInterest(location, limit, new AdobeCallback<List<PlacesPOI>>() {
                    @Override
                    public void call(List<PlacesPOI> pois) {
                        callbackContext.success(generatePOIString(pois));
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
                String requestId = (String)geofenceMap.get(REQUEST_ID);
                HashMap<String, String> circularRegion = getCircularRegionData((String)geofenceMap.get(CIRCULAR_REGION));
                if(circularRegion == null) {
                    callbackContext.error("Unable to get circular region data");
                    return;
                }
                double latitude = Double.parseDouble(circularRegion.get(LOWERCASE_LATITUDE));
                double longitude = Double.parseDouble(circularRegion.get(LOWERCASE_LONGITUDE));
                float radius = Float.parseFloat(circularRegion.get(RADIUS));
                long expirationDuration = Long.parseLong((String)geofenceMap.get(EXPIRATION_DURATION));
                final Geofence geofence = new Geofence.Builder()
                        .setCircularRegion(latitude, longitude, radius)
                        .setExpirationDuration(expirationDuration)
                        .setTransitionTypes(transitionType)
                        .setRequestId(requestId)
                        .build();
                Places.processGeofence(geofence, transitionType);
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
                callbackContext.success();
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
                LOG.d(LOG_TAG, "JSON error: " + e.getLocalizedMessage());
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
                LOG.d(LOG_TAG, "JSON error: " + e.getLocalizedMessage());
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

    private HashMap<String, String> getCircularRegionData(final String regionData) {
        JSONObject json = null;

        try {
            json = new JSONObject(regionData);
            HashMap<String, String> regionDataMap = new HashMap<>();
            @SuppressWarnings("unchecked")
            Iterator<String> it = json.keys();
            while(it.hasNext()) {
                String name = it.next();
                regionDataMap.put(name, json.getString(name));
            }
            return regionDataMap;
        } catch (JSONException e) {
            LOG.d(LOG_TAG, "Error converting regionData string to Map: " + e.getLocalizedMessage());
            return null;
        }

    }

    private String generatePOIString(final List<PlacesPOI> pois) {
        JSONArray jsonArray = new JSONArray();
        JSONObject json;
        if (!pois.isEmpty()) {
            for (int index = 0; index < pois.size(); index++) {
                try {
                    PlacesPOI poi = pois.get(index);
                    json = new JSONObject();
                    json.put(POI, poi.getName());
                    json.put(LATITUDE, poi.getLatitude());
                    json.put(LONGITUDE, poi.getLongitude());
                    json.put(IDENTIFIER, poi.getIdentifier());
                    jsonArray.put(index, json);
                } catch (JSONException e) {
                    LOG.d(LOG_TAG, "Error putting data into JSON: " + e.getLocalizedMessage());
                }
            }
        }
        return jsonArray.toString();
    }
}
