# Androidplot Versioning
For details on what to expect in general when updating to a new version of Androiplot, check out the
[versioning doc](versioning.md).


# 1.5.7
* (#94) Potential fix / better error logging for a crash caused by a buffered canvas resize with illegal arguments.
* (#93) Fix Android 9 compiler warnings.
* (#83) Fix NPE when attempting to recycle an already null buffered canvas instance.
* Remove unused `PlotRenderException`.
* Added `IN_ORDER` BarRenderer mode.

# 1.5.6
* Adds convenience methods for saving / restoring `PanZoom` state.
* (#80) Targets SDK 28, fixing compatibility issues.

# 1.5.5

* (#76) Fixed a bug that could cause a deadlock when grid steps are much larger than actual plot range.
* (#78) Fixed a bug where setting insets on XYGraphWidget would have no effect after the plot was drawn.
* XYGraphWidget.drawMarkerText is now marked `protected`.

# 1.5.4

* (#69) Fixed a bug in `SimpleXYPlot` preventing the resizing of `Y_VALS_ONLY` formatted series.
* (#73) Fixed a bug where dynamically resizing a Plot (by marking a sibling view as `GONE`, etc.) would not resize the graph widget.

# 1.5.3

* Minor cleanup of Widget example source.
* (#67) Fixed Javadoc link

# 1.5.2

_This version is pickier than it's predecessors about proper XML configuration.  Where
previous versions would silently ignore illegal XML attrs, a `RuntimeException` will be thrown._
* Added [sizing documentation](plot_composition.md#sizing-widgets)
* Added [custom renderer documentation](custom_renderer.md)
* Fixed (#61) Bug in `XYGraphWidget.screenToSeriesY(...)`.
* Fixed (#63) Fixed compatibility issue with Gradle 3.x.x that caused issues with XML parsing.

# 1.5.1

* (#52) Fixed minor NPE issue
* (#55) Fixed bug with `PieRenderer.getContainingSegment` not working for very large segments.

# 1.5.0

_Updates to legend functionality in this version may result in changes to the display order
of legend items in some cases.  A custom `Comparator` can be used to resolve this if necessary;
see the [legend doc](legend.md) for implementation details._

* Added [legend doc](legend.md)
* Added legend support to `PieChart`
* Added configurable legend item sorting
* (#45) Auto range boundaries calculation fix for when using a fixed domain range and a `FastXYSeries`
* Minor Performance Optimizations

# 1.4.3

* (#39) `FastLineAndPointRenderer` now renders vertices for legend items.
* Added [XML Attrs reference doc](attrs.md).

# 1.4.2

* (#32) New step mode: `INCREMENT_BY_FIT`.
* (#33) `PanZoom` support for `INCREMENT_BY_FIT`.
* (#34) Removed examples and documentation for serializing `SeriesRegistry` to preserve state.

# 1.4.1

* (#26) Fixed an NPE issue when drawing null values with a `PointLabeler`.
* Fixed a broken link in Quickstart doc.

# 1.4.0

* Moderate refactor of `PieRenderer`.  [Documentation](piechart.md) has been updated to reflect these changes.  
* Major refactor of `BarRenderer`.  [Documentation](barchart.md) has been updated to reflect these changes.
* Added `ScalingXYSeries` which wraps other instances of `XYSeries` to be dynamically scaled.  This is
particularly useful for creating animated intros using `XYSeries` data.
* Added [AnimatedXYPlotActivity](../demoapp/src/main/java/com/androidplot/demos/AnimatedXYPlotActivity.java) 
demonstrating the use of `ScalingXYSeries` to create an animated intro.
* `XYPlot.getXVal(..)` and `XYPlot.getYVal(...)` methods have been deprecated and will be removed in 1.5.0.
`XYPlot.screenToSeries(...)` and `XYPlot.seriesToScreen(...)` should be used instead.
* Domain and range cursors are now disabled by default.  To enable, set a valid cursor position using
`XYGraphWidget.setCursorPosition(float, float)`.  Cursor position values are expressed in screen coordinates;
you can convert between screen and series values using `XYPlot.screenToSeries(...)` and `XYPlot.seriesToScreen(...)`.

# 1.3.1

* Added [NormedXYSeries](advanced_xy_plot.md#normedxyseries) wrapper to simplify the process of normalizing xy series data.
* Added [DualScaleActivity](../demoapp/src/main/java/com/androidplot/demos/DualScaleActivity.java) 
demonstrating `NormedXYSeries` usage to present dual range scales.
* LineAndPointRenderer options for cases where two or mode series' of different size have been added.
* Fixed a bug causing points scrolled off-screen to occasionally accumulate and render along the left edge of the graph.
* Fixed a bug that could cause render jitter when extreme zoom levels were applied.
* Fixed a bug that prevented `PanZoom` from working properly on plots with an undefined outer limit.

# 1.3.0

* Added sampling support.  See the [Advanced XY Plot](advanced_xy_plot.md) doc for details.
* PanZoom performance enhancements & bug fixes.  If you're currently using PanZoom you'll likely need to 
update your code as the interface has slightly changed. 
* Added leakcanary to DemoApp for debug builds.
* More unit test coverage
* Fixed a bug that prevented an instance of a given series from being added more than once, even 
when a unique formatter is supplied.
* Added `Formatter.getLegendIconEnabled()` and `Formatter.setLegendIconEnabled(boolean)`, used to enable / disable drawing legend items for individual
series / formatter pairs.
* Added `XYGraphWidget.Edge.NONE` to be used with `XYGraphWidget.setLineLabelEdges(Edge...)` to disable all edges.

# 1.2.2

* BarRenderer / BarFormatter cleanup
* More documentation! 
* Bounds and XYBounds have been merged into Region and RectRegion respectively.
* ValPixConverter has been removed and it's functionality migrated to Region and RectRegion.
* Added Region.transform(...) and RectRegion.transform(...)
* Added Region.ratio(...) and RectRegion.ratio(...)
* XYPlot.getCalculatedMinX(), XYPlot.getCalculatedMaxX(), XYPlot.getCalculatedMinY() and XYPlot.getCalculatedMinY()
have been replaced with XYPlot.getBounds().
* Configurator has become it's own library - [Fig!](https://github.com/halfhp/fig)
* New constructors have been added to Formatters to simplify XML configuration via Fig.
* Added Jacoco code coverage reporting

# 1.2.1

### Pie Chart Enhancements
Pie chart has been updated with new methods and format attributes to improve segment
selection and highlighting functionality:

* Added `offset`, `radialInset`, `innerInset` and `outerInset` properties to SegmentFormatter.  
See the [pie chart documentation](piechart.md) for usage details.
* Updated PieRenderer to support the new SegmentFormatter properties.
* SimplePieChartActivity has been updated to provide an interactive demo of some of these new features.

### Misc

* Added `Plot.getListeners()` method.
* Added FastLineAndPointRenderer, updated OrientationSensorExampleActivity to use it.
* Updated to target SDK 24, removed sdkmanager dependency, and other misc. updates to project deps etc.
* Lots of additions and updates to documentation
* Added CircleCI support

# 1.1.0

* Added drawGridOnTop param to XYGraphWidget; when set to true, grid lines will be drawn on top of rendered series data.  (default is false)
* Added PanZoom class providing one-line configuration of pan/zoom behavior for instances of XYPlot.  See TouchZoomExampleActivity for a usage example.
* Removed InteractiveXYPlot as PanZoom makes it obsolete.

# 1.0.0

This is a factor of several core elements of the Androidplot lib.  The general theme was to 
make class and method names more intuitive and to make xml styling more powerful.

* Major refactor of XYGraphWidget
* Tick renamed to Line
* Extensible Label Formatters
* getXXXWidget methods renamed to simply getXXX
* Per-edge tick extensions
* Moved documentation into vcs.  Docs from 1.0 forward will be maintained here. (TODO)
* plot label xml param renamed to title