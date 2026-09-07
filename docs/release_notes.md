# Androidplot Versioning
For details on what to expect in general when updating to a new version of Androiplot, check out the
[versioning doc](versioning.md).

# 1.6.0
* `XYPlot.seriesToScreenY(Number)` previously returned a y pixel flipped relative to
  `seriesToScreen(XYCoords)` and `screenToSeriesY` (the largest series value landed on the bottom
  edge of the grid).  It now agrees with them, so code that compensated for the inversion should
  drop the compensation.
* Removed three unused, undocumented public classes: `SimpleLineLabelFormatter`, `RenderBundle` and
  `APTrace`.  None were referenced by the library, the demo app or the docs.
* (#96) Background-mode plots are no longer forced onto a software layer, removing a full-size CPU
  copy of the view from every frame.  `Redrawer` now tolerates plots that have been garbage
  collected instead of crashing, and exits on its own once none remain.
* (#125) Fix grid lines and labels not being drawn for an axis with inverted boundaries (min
  greater than max) when using `StepMode.INCREMENT_BY_VAL` or `INCREMENT_BY_FIT`.
* (#120) Fix background-thread plots never rendering when their first layout pass gives them a
  zero-sized dimension.  The background render loop has been reworked while fixing this; see
  **Behavior changes** below.
* (#118) Fix pie chart segments larger than half the pie not responding to clicks over part of
  their area.
* (#88) Fix crash when a formatter config or `androidPlot.` attribute references a color, dimension
  or integer resource.  Negative integer values are now accepted as well.
* Fix `LineAndPointRenderer` throwing `IndexOutOfBoundsException` on every frame for an empty
  series with `XOrder.ASCENDING`; `SeriesUtils.iBounds` no longer yields a negative index for an
  empty series.
* Fix `LineAndPointRenderer` throwing on every frame when interpolation is enabled on a series with
  fewer than 3 points or a null value; such series now fall back to straight line segments.
* Fix `GROW` / `SHRINK` boundary modes latching the placeholder `[-1, 1]` bounds when the first
  frame is drawn before any series data exists, which left `GROW` stuck at -1 and gave `SHRINK` an
  inverted axis.
* Fix `XYConstraints.contains(RectRegion)` comparing the region's minimum y value against the domain
  instead of its minimum x value.
* Fix `FillDirection.RANGE_ORIGIN` fills being closed at the wrong screen coordinate; the range
  origin was transformed through the domain region instead of the range region.
* Fix `BarOrientation.STACKED` bars being measured from the bottom of the plot instead of from the
  range origin, which produced incorrect stacks whenever the range lower boundary was not the origin.
  Negative values now stack downward from the origin.
* Fix `BubbleRenderer` producing infinite radii when any z-val is zero or negative, and NaN radii (no
  bubble drawn) when all z-vals are equal or the series has a single bubble.
* Fix a plot whose data spans a single point or a flat line rendering blank: `Region.transform` now
  maps a zero-length region onto the center of the target range, and `XYPlot` pads a calculated axis
  whose min equals max so the data is centered with a visible grid.
* Fix `BarFormatter.setFillPaint(null)` / `setBorderPaint(null)` not disabling the fill / border;
  the formatter shadowed the inherited paint fields so `hasFillPaint()` / `hasLinePaint()` kept
  returning true and `BarRenderer` drew with a null `Paint`.
* Fix `NormedXYSeries` returning NaN for a flat or single-point series, and drifting outside of the
  `[0, 1]` range when the wrapped series changes after construction.  Auto-calculated bounds are now
  refreshed before each draw (`NormedXYSeries` implements `PlotListener`) and can be refreshed
  manually via the new `normalize()` method.
* Fix the legend (along with the plot title and border) disappearing, with an exception logged on
  every frame, when a series has no title (e.g. candlestick plots), a region has no label, or there
  are more legend items than the legend's `TableModel` can hold.  Untitled items now draw without
  text, an over-full legend draws what fits, and `DynamicTableModel` rounds its dynamic axis up and
  sizes cells by the computed row/column count so a table such as `DynamicTableModel(0, 2)` fills
  its width.  `XYSeriesRenderer.getUniqueRegionFormatters()` now returns a `Map` (a
  `LinkedHashMap`) instead of a `Hashtable`.
* Fix `Widget.position(...)` re-adding the widget to the `LayoutManager` on every call, which drew
  the widget again on top of everything positioned before it (e.g. the graph covering the legend
  and titles after `plot.getGraph().position(...)`).
* Fix `FixedTableModel` wrapping immediately (or laying out nothing) for any table whose rect does
  not start at the plot origin.
* Fix the `domainTitle*` and `rangeTitle*` size, position, anchor and visibility XML attributes
  being applied to the graph widget instead of the domain and range title widgets, so e.g.
  `ap:domainTitleVisible="false"` hid the entire graph.
* The XML configuration engine (formerly the separate Fig library) is now part of androidplot-core;
  the library no longer has a dependency on `com.halfhp.fig:figlib`.  See the new
  [XML Configuration](xml_configuration.md) doc.
* Compile and target SDK 37; build updated to AGP 9.4 / Gradle 9.7 / Kotlin 2.2.
* Snapshot builds of unreleased changes are now published to the Central snapshots repository.
* Fix crash on the render thread after restoring a `PanZoom.State` that was captured before any
  pan or zoom gesture (eg. saving `getState()` in `onSaveInstanceState` and rotating the device).
  `getState()` now snapshots the plot's actual boundaries and modes for both axes, and applying a
  state skips any axis edge it holds no mode for.  A `State` serialized by an earlier version
  still deserializes (its `serialVersionUID` is pinned) and restores as a no-op.  `XYPlot` gains
  public getters for its four boundary modes.
* Fix `SampledXYSeries` crashing the render thread on its first draw: the series had no active
  data until a zoom factor was applied, and had no bounds when its data was too small to produce
  any sampled zoom level.  `ZoomEstimator` also tolerates a series without bounds.
* Fix `ConcurrentModificationException` (and silently skipped listeners) when a `PlotListener`
  or series removes itself from the plot from within `onBeforeDraw` / `onAfterDraw`.  The
  listener list is now a `CopyOnWriteArrayList`; `Plot.getListeners()` returns a `List`.
* Fix `centerOnRangeOrigin(...)` with `BoundaryMode.FIXED`, `GROW` or `SHRINK` throwing
  `UnsupportedOperationException` on every frame; the range axis now supports the same origin
  boundary modes as the domain axis.
* Fix `NullPointerException` when a `RectRegion` with a null (unbounded) edge is tested for
  intersection, eg. a fill region added to a `LineAndPointFormatter`.  Null now means infinity as
  documented, and the `Region(v1, v2)` constructor no longer swaps a null value to the wrong edge.
  `LineAndPointRenderer` draws such a region clipped to the plot's visible bounds.
* Fix `ConcurrentModificationException` on the render thread when `XYPlot` value markers are
  added or removed while the plot is drawing; the marker lists are now `CopyOnWriteArrayList`s.
* Fix `NullPointerException` from `SimpleXYSeries.setModel(...)`, `resize(...)` and `setXY(...)`
  after `useImplicitXVals()`.  `setModel` and `resize` keep the x-vals implicit, `setXY` sets
  only the y value, and `setX` throws an `IllegalStateException` explaining why.
* Fix `LayerListOrganizer.moveBeneath(...)` losing the moved element (and throwing
  `IndexOutOfBoundsException`) and `moveAbove(...)` silently moving it to the bottom when the
  reference element is not in the list; both now throw `IllegalArgumentException` and leave the
  list unchanged.
* Fix `PanZoom` clamping a horizontal pan to the range (y) outer limits when only `minY`/`maxY`
  outer limits are set, which snapped the data away on the first drag.
* Fix `PanZoom` pan and zoom losing precision on large-magnitude axes such as epoch milliseconds;
  small drags did not move the window and zoom quantized.  The value-space math is now done in
  double.  `PanZoom.calculateZoom(RectF, float, boolean)` is deprecated in favor of a `RectRegion`
  overload.
* Fix `PieRenderer.setDonutSize(0, DonutMode.PIXELS)` making the pie invisible; a donut size of 0
  pixels now means no hole.
* Fix pie segments with a zero value drawing their label on top of the neighbouring segment.
* Fix `Plot.removeSeries(series, rendererClass)` returning false when a series that is not a
  `PlotListener` was removed, and unregistering a `PlotListener` series (ex. `SimpleXYSeries`,
  which relies on it for draw-time locking) while the series was still registered with another
  renderer.
* Fix `Plot.clear()` and `SeriesRegistry.add()`/`clear()` mutating the series registry without
  synchronizing against the render thread, which could throw a `ConcurrentModificationException`
  mid-render.

**Behavior changes for `RenderMode.USE_BACKGROUND_THREAD`:**
* The plot view is now composited with hardware acceleration when the app has it enabled.  The
  rendered content is unchanged since drawing still happens on an offscreen bitmap; only the copy
  of that bitmap to the screen changes.  Rendering cost still scales with the plot's pixel area,
  so keep plots that redraw at high rates as small as the design allows.
* `redraw()` requests are no longer dropped when the render thread is busy drawing.  Requests
  made during a render are coalesced into one additional render pass, so the latest data is
  always drawn.  Apps that issue `redraw()` faster than the plot can draw will see one extra
  frame per burst compared to previous versions.
* Plots now re-render automatically when resized; previously a resized plot was blank until the
  next `redraw()` call.
* Resizing a plot while it is rendering could previously deadlock; the locks involved are now
  always taken in the same order.
* Detaching and quickly re-attaching a plot (as happens when scrolling a RecyclerView) could
  previously leave it without a render thread, or with recycled buffers, until its next resize.
  Thread handover is now explicit and the replacement thread always renders.

# 1.5.11
* Update project to latest gradle / build tools
* Fix issue with jetifier flagging an outdated dependency

# 1.5.10
* Update project to use latest gradle / build tools
* (#114) fix `setLinesPerRangeLabel` & `setLinesPerDomainLabel`

# 1.5.9
* (#107) Fix ambiguous ordinal for render mode attributes
* (#104) Fix issue with background rendering in RecyclerView.
* Adds a RecyclerView example to demo app.

# 1.5.8
* Maintenance release - update dependences, get off jcenter, etc.

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