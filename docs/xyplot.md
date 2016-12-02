# XYPlot
An XYPlot renders one or more XYSeries onto an instance of XYGraphWidget.  It also includes two instances
of TextLabelWidget used to display an optional title for the domain and range axis, and an instance
of [XYLegendWidget](#legend) which by default will automatically display legend items for each XYSeries added to the plot.

![image](images/plot_anatomy.png)

# XYSeries
XYSeries is the interface Androidplot uses to retrieve your numeric data.  You may either create your own
implementation of XYSeries or use SimpleXYSeries if you don't have tight performance requirements or
if your numeric data is easily accessed as an array or list of values.

## SimpleXYSeries
As a convenience, Androidplot provides an all-purpose implementation of the XYSeries interface called 
SimpleXYSeries.  SimpleXYSeries is used to wrap your raw data with an implementation of the XYSeries interface.  

You can supply your data in several ways:

As a list of y-vals (x = i for each supplied y-val):
```java
Number[] yVals = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
XYSeries series1 = new SimpleXYSeries(
    Arrays.asList(yVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "my series");
```               

An interleaved list of x/y value pairs (x[0] = 1, y[0] = 4, x[1] = 2, y[1] = 8, ...):
```java
Number[] yVals = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
XYSeries series1 = new SimpleXYSeries(
    Arrays.asList(yVals), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "my series");
```  
                
Separate lists of x-vals and y-vals:
```java
Number[] xVals = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
Number[] yVals = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};
XYSeries series = new SimpleXYSeries(xVals, yVals, "my series");
```             

Keep in mind that SimpleXYSeries is designed to be easy to use for a broad number of applications; it's not 
optimized for any specific scenario; if you are dynamically displaying data that needs to be refreshed more than several
times a second, consider building your own implementation of XYSeries designed for your app's
specific needs.

# The Graph
XYGraphWidget encapsulates XYPlot's graphing functionality.  Given an instance of XYPlot, a reference
to XYGraphWidget can be retrieve via `XYPlot.getGraph()`.

## Domain & Range Boundaries
By default, Androidplot will analyze all XYSeries instances registered with the Plot, determine the
min/max values for domain and range and adjust the Plot's boundaries to match those values.  If your
plot contains dynamic data, especially if your plot can periodically contain either no series data
or data with no resolution on one or both axis (all identical values for either x or y) then you may
want to manually set your XYPlot's domain and range boundaries.

To set your plot's boundaries use:

* `XYPlot.setDomainBoundaries(Number value, BoundaryMode mode)`
* `XYPlot.setRangeBoundaries(Number value, BoundaryMode mode)`

Note that the value argument is only used when setting `BoundaryMode.FIXED`.  For all other
modes, pass in null.

### BoundaryMode
Androidplot provides four boundary modes:

#### Fixed
The plot's boundaries on the specified axis are fixed to user defined values.

#### Auto (default)
The plot's boundaries auto adjust to the min/max values for the defined axis.

#### Grow
The plot's boundaries automatically increase to the max value encountered by the plot.  The initial
determines the starting boundaries from which the Grow behavior will be based.

#### Shrink
The plot's boundaries automatically shrink to the min value encountered by the plot.  The initial
determines the starting boundaries from which the Shrink behavior will be based.

## Domain & Range Lines
These are the horizontal lines drawn on a graph.  These lines are configured via:

* `XYPlot.setDomainStep(StepMode mode, Number value)`
* `XYPlot.setRangeStep(StepMode, Number value)`

Androidplot provides these step modes:

### Subdivide
When using `BoundaryMode.SUBDIVIDE`, the graph is subdivided into the specified number of sections.

### IncrementByValue
`BoundaryMode.INCREMENT_BY_VALUE` instructs Androidplot draw grid lines at the specified interval.  This
is the most commonly used modes as is produces an easy to read result.

### IncrementByPixels
`BoundaryMode.INCREMENT_BY_PIXELS` behaves identically to `BoundaryMode.INCREMENT_BY_VALUE` except that 
the increment quantity is expressed in pixels.

## Domain & Range Labels
Androidplot supports labeling domain values on either or both the top and bottom  graph edges 
and range values on either or both the left and right graph edges.  Most default styles show labels
only on the left and bottom edges.

### Line Label Insets
Insets are used to control where line labels are drawn in relation to the graph space.  The Insets instance
can be obtained via `XYPlot.getGraph().getLineLabelInsets()`.  For example, to
move the range labels on the left of the graph further to the left by 5dp:

xml
```xml
ap:lineLabelInsetLeft="-5dp"
```

java
```java
plot.getGraph().getLineLabelInsets().setLeft(PixelUtils.dpToPix(-5));
```

### Dual Axis Labels
Androidplot provides methods for enabling / disabling axis labels along all edges of the graph.  By
default, only the left and bottom edge labels are enabled.  To enable labels on the right edge:

xml
```xml
ap:lineLabels="left|bottom|right"
```

java
```java
plot.getGraph().setLineLabelEdges(
    XYGraphWidget.Edge.BOTTOM, 
    XYGraphWidget.Edge.LEFT, 
    XYGraphWidget.Edge.RIGHT);
```

Once the edge has been enabled, text formatting can be controlled by enabling a custom formatter
for the desired edge:

```java
plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.RIGHT).setFormat(new Format() {
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        // obj contains the raw Number value representing the position of the label being drawn.
        // customize the labeling however you want here:
        int i = Math.round(((Number) obj).floatValue());
        return toAppendTo.append(i + " thingies");
    }
            
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        // unused
        return null;
    }
});
```
 
If you are working on a dual scale implementation, you're likely also going to need to normalize
your series data in order to get it to display properly.  To help simplify this step, Androidplot provides 
[NormedXYSeries](advanced_xy_plot.md#normedxyseries) which can be used to wrap any XYSeries to provide the 
normalized representation of it's data.

There's a full [reference implementation](../demoapp/src/main/java/com/androidplot/demos/DualScaleActivity.java) 
of a dual scale plot using a custom Formatter and NormedXYSeries in the DemoApp.


### Line Label Interval
Androidplot allows you to configure the interval at which labels are rendered for domain and range lines:

* `XYPlot.getGraph().setLinesPerDomainLabel(int interval)`
* `XYPlot.getGraph().setLinesPerRangeLabel(int interval)`

### LineLabelStyle
The styling of the line labels drawn on each edge of the graph is controlled by it's associated style.
this style contains params that control:

* Paint used to draw labels (determines, color, font size, etc.)
* Format used to draw text (can be NumberFormat, etc.)
* Rotation of the label text

To get the style used to draw the left edge (range) labels:

```java
plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT);
```

### LineLabelRenderer
If you need to implement special rendering behavior for your line labels, such as drawing graphics, symbols, etc.
you can create a custom renderer by extending LineLabelRenderer and injecting it into the graph:

```java
plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.LEFT, customLineLabelRenderer);
```

[f(x) plot example source](../demoapp/src/main/java/com/androidplot/demos/FXPlotExampleActivity.java) 
provides an example of this kind of customization.

# Pan & Zoom
You can enable pan/zoom behavior on any instance of XYPlot using the PanZoom class like this:

```java
PanZoom.attach(plot);
```

The default behavior is to enable horizontal and vertical panning an to zoom using uniform scaling.
If you want to override this behavior use the three argument form of `PanZoom.attach(Plot)`.  For example,
to enable pan and zoom (stretch) on the horizontal axis only:

```java
PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
```

Pan and zoom operations abide by your plot's defined outer limits limits.  If no such limits have been set
then the plot will pan and zoom on both axes infinitely.  To set the plot's outer limits:

```java
// cap pan/zoom limits for panning and zooming to a 100x100 space:
plot.getOuterLimits().set(0, 100, 0, 100);
```

For a more detailed look at pan & zoom behavior, check out the [Touch Zoom Example source code](../demoapp/src/main/java/com/androidplot/demos/TouchZoomExampleActivity.java).

# Series Renderers
There are several renderers available for XYPlots:

* LineAndPointRenderer
* BarRenderer
* CandlestickRenderer

When you add a new series to your plot, you tell Androidplot how to render it by passing
in a subclass of XYSeriesFormatter that corresponds to the desired renderer.  For example, to use
LineAndPointRenderer, you'd register your series with an instance of LineAndPointFormatter:

```java
LineAndPointFormatter format = new LineAndPointFormatter();
plot.addSeries(series, format);
```

If you need special behavior not provided by an existing renderer you can create
your own by either extending XYSeriesRenderer or one of the above implementations.  You'll also need
to create a matching implementation of XYSeriesFormatter that returns your XYSeriesRenderer's class 
from it's `getRendererClass()` method.

## LineAndPointRenderer
TODO

Androidplot includes two additional variations of LineAndPointRenderer: 

* **FastLineAndPointRenderer** - intended for use in apps displaying large amounts of dynamic data where
fast refresh rates are important.
* **AdvancedLineAndPointRenderer** - provides capabilities for dynamically coloring individual line
segments, etc. See the ECG  source in the demo app for a full source example.
within a

### Labeling Points
TODO

## BarRenderer
See the [barcharts documentation](barchart.md).

## CandlestickRenderer
See the [candlestick documentation](candlestick.md)

# Drawing Smooth Lines
Smooth lines can be created by applying the 
[Catmull-Rom interpolator](http://androidplot.com/smooth-curves-and-androidplot/) to your series' Format.

# <a name="legend"></a> The Legend
By default, Androidplot will automatically produce a legend for your Plot.  You however choose to hide the legend
or you can customize it to suit your needs.

# Hiding Legend Items
As mentioned above, Androidplot automatically produces a legend for your Plot.  This "auto legend" includes
items for each series added to the plot.  If you wish to omit a series from the legend:

```java
formatter.setLegendIconEnabled(false);
```

## The TableModel
The TableModel controls how and where each item in the legend is drawn.  Androidplot provides two
default implementations; DynamicTableModel and FixedTableModel (detailed below).  All TableModel implementations
organize elements into a grid.  This grid is populated with items based on the order which it's corresponding
series was added to the plot.  This ordering can be further controlled by setting the TableModel's
TableOrder param to either [ROW_MAJOR](https://en.wikipedia.org/wiki/Row-major_order) (items are added left-to-right, top-down) or COLUMN_MAJOR (items are added top-down, left-to-right).

### DynamicTableModel
The DynamicTableModel takes a desired of numbered rows and columns and evenly subdivides the LegendWidget's
visible space into cells.  For example, A 2x2 legend using ROW_MAJOR ordering:

```java
plot.getLegend().setTableModel(new DynamicTableModel(2, 2, TableOrder.ROW_MAJOR));
```

### FixedTableModel
The FixedTableModel takes a desired size of each cell in pixels and adds cells using the specified TableOrder.
It automatically wraps to the next row or column (based on TableOrder) when the cell being added
exceeds the legend's available space on a given axis.  For example, A FixedTableModel using 300w*100h cells and
a TableOrder of COLUMN_MAJOR:

```java
plot.getLegend().setTableModel(new FixedTableModel(PixelUtils.dpToPix(300), 
    PixelUtils.dpToPix(100), TableOrder.COLUMN_MAJOR));
```

# Graph Rotation
Androidplot provides the Widget.setRotation(Widget.Rotation) method for controlling the orientation
of Widgets.  For example, if you wanted to create a bar graph where the bars extended across the screen 
from left to right:

xml
```xml
ap:graphRotation="ninety_degrees"
```

java
```java
plot.getGraph().setRotation(Widget.Rotation.NINETY_DEGREES);
```

# Optimization Tips
Here are a few suggestions to improve performance when plotting dynamic data:

* Create your own implementation of XYSeries to work with your data in it's rawest form.
* Use FastLineAndPointRenderer instead of LineAndPointRenderer:

```java
plot.addSeries(azimuthHistorySeries,
    new FastLineAndPointRenderer.Formatter(
        Color.rgb(100, 100, 200), null, null, null));
``` 
                       
* Consider averaging or subsampling very large datasets before rendering.  If you have the time and
inclination, [the LTTB algorithm](http://skemman.is/stream/get/1946/15343/37285/3/SS_MSthesis.pdf) is particularly well suited for downsampling XY Series data.
* If possible, avoid rendering vertices (points).
* Disable anti-aliasing on your Formatter's paint values:

```java
LineAndPointFormatter format = new LineAndPointFormatter(...);
format.getLinePaint().setAntiAlias(false);
```
