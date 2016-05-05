"use strict";

/**
 * A view to display a list.
 *
 * @param containerId the id of the ul in the html document.
 */
var ListView = function (containerId) {
  this.container = document.getElementById(containerId);
};

/**
 * Updates the view with a new list.
 *
 * @param games the new list with which to update the views.
 */
ListView.prototype.updateList = function (list) {
  this.list = list;
  this.container.innerHTML = "";
  var sortable = [];
  var keys = Object.keys(list);
  for (var i = 0; i < keys.length; i++) {
    sortable.push([keys[i], list[keys[i]]]);
  }
  sortable.sort(function (a, b) {
    return b[1] - a[1];
  });
  for (var i = 0; i < sortable.length; i++) {
    var item = document.createElement('li');
    item.innerHTML = this.format(sortable[i][0], sortable[i][1], i);
    this.container.appendChild(item);
  }
}

/**
 * Returns a formatted string for an item in the list.
 *
 * @param teamNames the item to be formatted.
 * @return a formatted string for an item in the list.
 */
ListView.prototype.format = function (teamName, rating, index) {
  var chars = 43;
  var charsLeftForName = chars - ("" + rating).length - (("" + index).length + 2);
  teamName = teamName.slice(0, charsLeftForName - 3);
  var dots = charsLeftForName - ("" + teamName).length;
  var string = index + ". " + teamName;
  for (var i = 0; i < dots; i++) string += ".";
  string += rating;
  return string;
};
