"use strict";

/**
 * A view to display a rolling animation of text.
 *
 * @param canvas the canvas to draw to.
 * @param color the color of the text and lines.
 * @param text the text to be displayed.
 */
var ScrollTextView = function (canvas, color, text) {
  this.canvas = canvas;
  this.color = color;
  this.text = text;
  return this;
};

/**
 * Draws the view to the specified canvas.
 */
ScrollTextView.prototype.paintCanvas = function () {
  var context = this.canvas.getContext("2d");
  //Draw the lines across the view
  context.strokeStyle = this.color;
  context.lineWidth = 6;
  context.beginPath();
  context.moveTo(0, 0);
  context.lineTo(this.canvas.width, 0);
  context.moveTo(0, this.canvas.height);
  context.lineTo(this.canvas.width, this.canvas.height);
  context.stroke();
  //Draw the text
  context.fillStyle = this.color;
  context.textAlign = "left";
  context.font = "19.33px Joystix";
  context.fillText(this.text, this.x1, this.canvas.height - 18);
  context.fillText(this.text, this.x2, this.canvas.height - 18);
};

/**
 * Re-draws the whole view.
 */
ScrollTextView.prototype.repaint = function () {
  this.canvas.width = this.canvas.clientWidth;
  this.canvas.height = this.canvas.clientHeight;
  this.paintCanvas();
  return this;
};

/**
 * Starts animating the text.
 */
ScrollTextView.prototype.start = function () {
  if (!this.animating) {
    this.animating = true;
    var dec = this.measureText("A").width;
    this.x1 = this.canvas.clientWidth;
    this.x2 = this.canvas.clientWidth + this.measureText(this.text).width + (dec * 7);
    var self = this;
    var step = function () {
      if (self.x1 <= -self.measureText(self.text).width) {
        self.x1 = self.x2;
        self.x2 += self.measureText(self.text).width + (dec * 7);
      }
      self.x1 -= dec;
      self.x2 -= dec;
      self.repaint();
    };
    window.setInterval(step, 300);
  }
};

/**
 * Returns an object containing the width of the text on the canvas.
 *
 * @param text the text to find the width for.
 * @return an object containing the width of the text on the canvas.
 */
ScrollTextView.prototype.measureText = function (text) {
  var context = this.canvas.getContext("2d");
  context.font = "19.33px Joystix";
  return context.measureText(text);
};
