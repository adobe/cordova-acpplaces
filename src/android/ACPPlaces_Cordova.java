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
                        int index = 0;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject json;
                        if (!pois.isEmpty()) {
                            for (PlacesPOI poi : pois) {
                                try {
                                    json = new JSONObject();
                                    json.put("POI", poi.getName());
                                    json.put("Latitude", poi.getLatitude());
                                    json.put("Longitude", poi.getLongitude());
                                    json.put("Identifier", poi.getIdentifier());
                                    jsonArray.put(index, json);
                                    index++;
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        callbackContext.success(jsonArray.toString());
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
                        JSONObject json = new JSONObject();;
                        try {
                            json.put("Latitude", location.getLatitude());
                            json.put("Longitude", location.getLongitude());
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        callbackContext.success(json.toString());
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
                        int index = 0;
                        JSONArray jsonArray = new JSONArray();
                        JSONObject json;
                        if (!pois.isEmpty()) {
                            for (PlacesPOI poi : pois) {
                                try {
                                    json = new JSONObject();
                                    json.put("POI", poi.getName());
                                    json.put("Latitude", poi.getLatitude());
                                    json.put("Longitude", poi.getLongitude());
                                    json.put("Identifier", poi.getIdentifier());
                                    jsonArray.put(index, json);
                                    index++;
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        callbackContext.success(jsonArray.toString());
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
                // this method is not implemented. please use processGeofence.
                callbackContext.success();
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
                String requestId = (String)geofenceMap.get("requestId");
                HashMap<String, String> circularRegion = getCircularRegionData((String)geofenceMap.get("circularRegion"));
                double latitude = Double.parseDouble(circularRegion.get("latitude"));
                double longitude = Double.parseDouble(circularRegion.get("longitude"));
                float radius = Float.parseFloat(circularRegion.get("radius"));
                long expirationDuration = Long.parseLong((String)geofenceMap.get("expirationDuration"));
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

    private HashMap<String, String> getCircularRegionData(final String regionData) {
        String regionDataString = regionData.replace("{","").replace("}","").replace("\"","");
        String[] dataPairs = regionDataString.split(",");
        HashMap<String, String> regionDataMap = new HashMap<>();
        for(int i=0; i < dataPairs.length; i++){
            String pair = dataPairs[i];
            String[] keys = pair.split(":");
            regionDataMap.put(keys[0], keys[1]);
        }
        return regionDataMap;
    }
}
