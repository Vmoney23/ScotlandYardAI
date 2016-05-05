"use strict";

/**
 * A class to interact with the matchmaker and get the list of scores every minute.
 *
 * @param url the url of the matchmaker.
 */
var Messenger = function (url) {
  this.url = url;
  var self = this;
  this.socket = new WebSocket(url);
  var updateList = function () {
    var message = {
      type: "REQUEST_SCORES"
    };
    console.log("OUT:", message.type);
    self.socket.send(JSON.stringify(message));
  };
  this.socket.onopen = function () {
    updateList();
  };
  this.socket.onmessage = function (message) {
    var decodedMessage = JSON.parse(message.data);
    console.log("IN:", decodedMessage.type);
    if (decodedMessage.type == "SCORES") {
      listView.updateList(decodedMessage.scores);
    }
  };
};
