Chartboost plugin for Cordova (Android-only currently).
================================================================

Installation
--------
1) ``cordova plugin add https://github.com/agamemnus/cordova-plugin-chartboost --variable CHARTBOOST_APP_SIGNATURE="the_chartboost_app_signature"``

2) Get the chartboost version 5 library file ("chartboost.jar") and put it in e.g. ``/platforms/android/libs/``.


Usage
--------
1) Run ``createInterstitialView`` with the adId parameter -- i.e.: the chartboost "app id".

````
var chartboost_plugin = window.plugins.ChartboostPlugin
chartboost_plugin.createInterstitialView (
 {adId : "chartboostappid"},
 function () {console.log ("Success function ran.")},
 function () {console.log ("Error function ran.")}
)
````

2) Run ``showInterstitialAd`` to show the ad. This should be run when we know Chartboost is cached.
* Note that Chartboost version 5 automatically caches a new ad after the current ad is displayed.
* If an ad is not cached at the moment showInterstitialAd is run, try running ``showInterstitialAd()`` in another few seconds, or run ``executeRequestInterstitialAd ()`` to explicitly request a cache.
````
var chartboost_plugin = window.plugins.ChartboostPlugin
chartboost_plugin.showInterstitialAd ({
 function () {console.log ("Success function ran.")},
 function () {console.log ("Error function ran.")}
)
````

3) Show run ``showInterstitialAd`` again to show another ad.

4) There are several supported event listeners, all of which send an object with an ``ad_network: chartboost`` property.
````
onReceiveInterstitialAd         // Runs when an interstitial is cached.
onDismissInterstitialAd         // Runs when an interstitial is dismissed, which happens when a user clicks the "X" button or when the user clicks the ad itself.
onLeaveToInterstitialAd         // Runs when a user clicks the interstitial ad.
onPresentInterstitialAd         // Runs when an interstitial is shown to the user.
onFailedToReceiveInterstitialAd // Runs when Chartboost refuses to send an ad for some reason. The response value includes an "error" property indicating the error reason.
````

5) Example usage of callback:
````
var current_ad = {chartboost: {}}
document.addEventListener (ad_control.onPresentInterstitialAd, onReceiveInterstitialAd)
function onReceiveInterstitialAd (evt) {
 console.log ("Ad network: " + evt.ad_network)
 current_ad["chartboost"].cached = true
}
````
