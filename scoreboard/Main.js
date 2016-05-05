"use strict";

var messenger;
var listView;

//Loads the views when the page loads.
if (window.addEventListener) window.addEventListener("load", load, false);
else if (window.attachEvent) window.attachEvent("onload", load);

/**
 * Called when the page loads, it initialises all of the views and connects to
 * the matchmaker.
 */
function load () {
  listView = new ListView("list");
  var year = new Date().getFullYear();
  var text = "Scotland Yard tournament " + year + ", University of Bristol";
  new ScrollTextView(document.getElementById("scrollingTextView"), "#19DBB6", text).repaint().start();
  messenger = new Messenger("ws:it025716.users.bris.ac.uk:8120");
}
