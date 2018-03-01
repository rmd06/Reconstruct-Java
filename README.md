# Reconstruct Java Experiments
## This is an early Java prototype of basic functionality (February 27th, 2018):

![Early Demo](Screenshot_02272018_105320PM.png?raw=true "Early Demo")

This version uses the familiar menus from the earlier Reconstruct. Most of the menus are non-functional.

This version does support importing a single image with the **Series/Import/Images...** option.

This version supports switching between **Move Mode** and **Draw Mode** via the space bar. These modes are:

* **Move Mode:** The image can be zoomed with the mouse scrollwheel and panned with mouse click and drag.
* **Draw Mode:** The image can be zoomed with the scrollwheel. Multiple outlines can be drawn with click and drag.

## Use:

* Obtain a copy of Reconstruct.jar (either by downloading, or by building with make).
* It's most convenient to place Reconstruct.jar near the image files to be opened.
* Double click on Reconstruct.jar (or run it with: java -jar Reconstruct.jar).
* Use the menu option "**Series / Import / Images...**" to open an image file (.png or .jpg).
* The mouse wheel will zoom in and out. The image will zoom about the mouse location (that point remains fixed).
* The default mode will be **Move Mode** (cursor will show 4 arrows). Click and drag to move the image.
* The mode can be toggled between **Move Mode** and **Draw Mode** with the space bar (the mouse will change shape).
* Traces can be drawn with **Draw Mode** ("crosshair" cursor).
* Current traces are single segments with no editing capabilities.
* All traces can be printed to parent console with "**Program / Debug / Dump**".
* All traces can be cleared with "**Program / Debug / Clear**".
* The "Mode" menu item can also be used to switch modes, print traces, and clear traces.
* The "Center Drawing" mode acts like a sewing machine or jigsaw where the "work" is moved
  under the center drawing point (cross hair).

