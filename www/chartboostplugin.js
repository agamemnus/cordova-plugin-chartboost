var argscheck = require ('cordova/argscheck')
var exec      = require ('cordova/exec')

module.exports = function () {
 var exports = {}
 
 var plugin_name = "ChartboostPlugin"
 
 exports.createInterstitialView = function (options, success, error) {
  cordova.exec (success, error, plugin_name, "createInterstitialView", [options])
 }
 
 exports.requestInterstitialAd = function (options, success, error) {
  if (typeof options == "undefined" || options == null) options = {}
  cordova.exec (success, error, plugin_name, "requestInterstitialAd", [options])
 }
 
 exports.showInterstitialAd = function (show, success, error) {
  if (typeof show == "undefined") show = true
  cordova.exec (success, error, plugin_name, 'showInterstitialAd', [show])
 }
 
 return exports
} ()