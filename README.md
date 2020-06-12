
# Adobe Experience Platform - Places plugin for Cordova apps

[![CI](https://github.com/adobe/cordova-acpplaces/workflows/CI/badge.svg)](https://github.com/adobe/cordova-acpplaces/actions)
[![npm](https://img.shields.io/npm/v/@adobe/cordova-acpplaces)](https://www.npmjs.com/package/@adobe/cordova-acpplaces)
[![GitHub](https://img.shields.io/github/license/adobe/cordova-acpplaces)](https://github.com/adobe/cordova-acpplaces/blob/master/LICENSE)

- [Prerequisites](#prerequisites)  
- [Installation](#installation)
- [Usage](#usage)  
- [Running Tests](#running-tests)
- [Sample App](#sample-app)  
- [Additional Cordova Plugins](#additional-cordova-plugins)
- [Contributing](#contributing)  
- [Licensing](#licensing)  

## Prerequisites  

Cordova is distributed via [Node Package Management](https://www.npmjs.com/) (aka - `npm`).  

In order to install and build Cordova applications you will need to have `Node.js` installed. [Install Node.js](https://nodejs.org/en/).  

Once Node.js is installed, you can install the Cordova framework from terminal:  

```  
sudo npm install -g cordova  
```

## Installation

To start using the Places plugin for Cordova, navigate to the directory of your Cordova app and install the plugin:
```
cordova plugin add https://github.com/adobe/cordova-acpplaces.git
```
Check out the documentation for help with APIs

## Usage

##### Getting the SDK version:
```js
ACPPlaces.extensionVersion(function(version){  
    console.log(version);
}, function(error){  
    console.log(error);  
});
```
##### Registering the extension with ACPCore:  

 > Note: It is required to initialize the SDK via native code inside your AppDelegate and MainApplication for iOS and Android respectively. For more information see how to initialize [Core](https://aep-sdks.gitbook.io/docs/getting-started/initialize-the-sdk).  
  
  ##### **iOS**  
```objective-c
#import "ACPPlaces.h"  
[ACPPlaces registerExtension];  
```
  ##### **Android:**  
```java
import com.adobe.marketing.mobile.Places;  
Places.registerExtension();
```
##### Clear client side Places plugin data:
```js
ACPPlaces.clear(function(response) {  
    console.log("Successfully cleared Places data.");
}, function(error){  
    console.log(error);  
});
```
##### Get the current POI's that the device is currently known to be within:
```js
ACPPlaces.getCurrentPointsOfInterest(function(response){  
    console.log("Current POI's: ", response);  
}, function(error){  
    console.log(error);  
});  
```
##### Get the last latitude and longitude stored in the Places plugin:
```js
ACPPlaces.getLastKnownLocation(function(response) {  
    console.log("Last known location: ", response);
}, function(error){  
    console.log(error);  
});
```
##### Get a list of nearby POI's:
```js
var location = {latitude:37.3309422, longitude:-121.8939077};
var limit = 10; // max number of POI's to return
ACPPlaces.getNearbyPointsOfInterest(location, limit, function(response){  
    console.log("Nearby POI's: ", response);  
}, function(error){  
    console.log(error);  
});
```
##### Pass a Geofence and transition type to be processed by the Places plugin:

```js
var region = {latitude:37.3309422, longitude:-121.8939077, radius:1000};
var geofence = {requestId:"geofence_id", circularRegion:region, expirationDuration:-1};
ACPPlaces.processGeofence(geofence, geo.transitionType, function(response) {  
    console.log("Successfully processed geofence: ", geofence); 
}, function(error){  
    console.log(error);  
});
```
##### Set the authorization status:
```js
ACPPlaces.setAuthorizationStatus(ACPPlaces.AuthorizationStatusAlways, function(response) {  
    console.log("Successfully set the authorization status."); 
}, function(error){  
    console.log(error);  
});
```

## Running Tests
Install cordova-paramedic `https://github.com/apache/cordova-paramedic`
```bash
npm install -g cordova-paramedic
```

Run the tests
```
cordova-paramedic --platform ios --plugin . --verbose
```
```
cordova-paramedic --platform android --plugin . --verbose
```

## Sample App

A Cordova app for testing the Adobe SDK plugins is located at [https://github.com/adobe/cordova-acpsample](https://github.com/adobe/cordova-acpsample). The app is configured for both iOS and Android platforms.  

## Additional Cordova Plugins

Below is a list of additional Cordova plugins from the AEP SDK suite:

| Extension | GitHub | npm |
|-----------|--------|-----|
| Core SDK | https://github.com/adobe/cordova-acpcore | [![npm](https://img.shields.io/npm/v/@adobe/cordova-acpcore)](https://www.npmjs.com/package/@adobe/cordova-acpcore)
| Adobe Analytics | https://github.com/adobe/cordova-acpanalytics | [![npm](https://img.shields.io/npm/v/@adobe/cordova-acpanalytics)](https://www.npmjs.com/package/@adobe/cordova-acpanalytics)
| Project Griffon (Beta) | https://github.com/adobe/cordova-acpgriffon | [![npm](https://img.shields.io/npm/v/@adobe/cordova-acpgriffon)](https://www.npmjs.com/package/@adobe/cordova-acpgriffon)

## Contributing
Looking to contribute to this project? Please review our [Contributing guidelines](.github/CONTRIBUTING.md) prior to opening a pull request.

We look forward to working with you!

## Licensing  
This project is licensed under the Apache V2 License. See [LICENSE](LICENSE) for more information.
