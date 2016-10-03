# 1.2.3

* Added leakcanary
* More unit test coverage

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