package com.flyingsoftgames.chartboostplugin;

import android.app.Activity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBClickError;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import android.util.Log;

public class ChartboostPlugin extends CordovaPlugin {
 private static final String LOG_TAG = "ChartboostPlugin";
 
 private String adId            = "";
 private String appSignature    = "";
 private boolean alreadyStarted = false;
 
 @Override public boolean execute (String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {
  PluginResult result = null;
  if ("setOptions".equals(action)) {
   JSONObject options = inputs.optJSONObject(0);
   result = executeSetOptions(options, callbackContext);
  } else if ("createInterstitialView".equals(action)) {
   JSONObject options = inputs.optJSONObject(0);
   result = executeCreateInterstitialView(options, callbackContext);
  } else if ("requestInterstitialAd".equals(action)) {
   JSONObject options = inputs.optJSONObject(0);
   result = executeRequestInterstitialAd(options, callbackContext);
  } else if ("showInterstitialAd".equals(action)) {
   boolean show = inputs.optBoolean(0);
   result = executeShowInterstitialAd(show, callbackContext);
  } else {
   Log.d (LOG_TAG, String.format("An invalid action has been passed: %s", action));
   result = new PluginResult(Status.INVALID_ACTION);
  }
  if (result != null) callbackContext.sendPluginResult (result);
  return true;
 }
 
 private PluginResult executeSetOptions (JSONObject options, CallbackContext callbackContext) {
  setOptions (options);
  callbackContext.success ();
  return null;
 }
 
 private void setOptions (JSONObject options) {
  if (options == null) return;
  if (options.has("adId")) this.adId = options.optString ("adId");
 }
 
 private ChartboostDelegate delegate = new ChartboostDelegate () {
  @Override public boolean shouldRequestInterstitial (String location) {return true;}
  @Override public boolean shouldDisplayInterstitial(String location) {return true;}
  @Override public void didCacheInterstitial (String location) {
   webView.loadUrl ("javascript:cordova.fireDocumentEvent('onReceiveInterstitialAd', {'ad_network': 'chartboost'});");
  }
  @Override public void didFailToLoadInterstitial (String location, CBImpressionError error) {
   webView.loadUrl (String.format("javascript:cordova.fireDocumentEvent('onFailedToReceiveInterstitialAd', {'ad_network': 'chartboost', 'error': '%s'});", error.name ()));
  }
  // didDismissInterstitial fires when a user either clicks the ad or clicks the "X" button. didCloseInterstitial fires when a user click the "X" button.
  @Override public void didDismissInterstitial (String location) {}
  @Override public void didCloseInterstitial (String location) {
   webView.loadUrl ("javascript:cordova.fireDocumentEvent('onDismissInterstitialAd', {'ad_network': 'chartboost'});");
  }
  @Override public void didClickInterstitial (String location) {
   webView.loadUrl ("javascript:cordova.fireDocumentEvent('onLeaveToInterstitialAd', {'ad_network': 'chartboost'});");
  }
  @Override public void didDisplayInterstitial (String location) {
   webView.loadUrl ("javascript:cordova.fireDocumentEvent('onPresentInterstitialAd', {'ad_network': 'chartboost'});");
  }
  //@Override public boolean shouldRequestMoreApps (String location) {return true;}
  //@Override public boolean shouldDisplayMoreApps (String location) {return true;}
  @Override public void didFailToLoadMoreApps (String location, CBImpressionError error) {}
  @Override public void didCacheMoreApps (String location) {}
  @Override public void didDismissMoreApps (String location) {}
  @Override public void didCloseMoreApps (String location) {}
  @Override public void didClickMoreApps (String location) {}
  @Override public void didDisplayMoreApps (String location) {}
  @Override public void didFailToRecordClick (String uri, CBClickError error) {}
  //@Override public boolean shouldDisplayRewardedVideo (String location) {return true;}
  @Override public void didCacheRewardedVideo (String location) {}
  @Override public void didFailToLoadRewardedVideo (String location, CBImpressionError error) {}
  @Override public void didDismissRewardedVideo(String location) {}
  @Override public void didCloseRewardedVideo (String location) {}
  @Override public void didClickRewardedVideo (String location) {}
  @Override public void didCompleteRewardedVideo (String location, int reward) {}
  @Override public void didDisplayRewardedVideo(String location) {}
  @Override public void willDisplayVideo (String location) {}
 };
 
 @Override public void onResume (boolean multitasking) {Chartboost.onResume (cordova.getActivity());super.onResume(multitasking); }
 @Override public void onPause (boolean multitasking) {Chartboost.onPause (cordova.getActivity()); super.onPause(multitasking); }       
 @Override public void onDestroy () {super.onDestroy (); Chartboost.onDestroy (cordova.getActivity());}
 
 private PluginResult executeCreateInterstitialView (final JSONObject options, final CallbackContext callbackContext) {
  cordova.getActivity().runOnUiThread (new Runnable () {
   @Override public void run () {
    setOptions (options);
    if ((options.has("forceRestart") && (options.optBoolean("forceRestart") == true)) || (alreadyStarted == false)) {
     alreadyStarted = true;
     Activity activity = cordova.getActivity();
     int currentId = activity.getResources().getIdentifier ("chartboost_app_signature", "string", activity.getPackageName());
     appSignature = activity.getResources().getString(currentId);
     Chartboost.startWithAppId (activity, adId, appSignature);
     Chartboost.setDelegate (delegate);
     Chartboost.setImpressionsUseActivities (true);
     Chartboost.onCreate (activity);
     Chartboost.onStart (activity);
     executeRequestInterstitialAd (options, callbackContext);
    }
  	}
  });
  return null;
 }
 
 private PluginResult executeRequestInterstitialAd (JSONObject options, CallbackContext callbackContext) {
  setOptions (options);
  final CallbackContext delayCallback = callbackContext;
  cordova.getActivity().runOnUiThread (new Runnable() {
   @Override public void run () {
    Chartboost.cacheInterstitial (CBLocation.LOCATION_DEFAULT);
    //Chartboost.cacheRewardedVideo (CBLocation.LOCATION_GAMEOVER);
    delayCallback.success ();
   }
  });
  return null;
 }
 
 private PluginResult executeShowInterstitialAd (final boolean show, final CallbackContext callbackContext) {
  cordova.getActivity().runOnUiThread (new Runnable () {
   @Override public void run () {
    Chartboost.showInterstitial  (CBLocation.LOCATION_DEFAULT);
    // Chartboost.showRewardedVideo (CBLocation.LOCATION_GAMEOVER);
    if (callbackContext != null) callbackContext.success ();
   }
  });
  return null;
 }
}