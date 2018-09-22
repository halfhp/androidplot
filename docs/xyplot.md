# XYPlot
An `XYPlot` renders one or more XYSeries onto an instance of XYGraphWidget.  It also includes two instances
of `TextLabelWidget` used to display an optional title for the domain and range axis, and an instance
of [XYLegendWidget](#legend) which by default will automatically display legend items for each `XYSeries` added to the plot.

![image](images/plot_anatomy.png)

# XYSeries
`XYSeries` is the interface Androidplot uses to retrieve your numeric data.  You may either create your own
implementation of `XYSeries` or use `SimpleXYSeries` if you don't have tight performance requirements or
if your numeric data is easily accessed as an array or list of values.

## SimpleXYSeries
As a convenience, Androidplot provides an all-purpose implementation of the `XYSeries` interface called 
`SimpleXYSeries`.  `SimpleXYSeries` is used to wrap your raw data with an implementation of the `XYSeries` interface.  

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

Keep in mind that `SimpleXYSeries` is designed to be easy to use for a broad number of applications; it's not 
optimized for any specific scenario; if you are dynamically displaying data that needs to be refreshed more than several
times a second, consider building your own implementation of `XYSeries` designed for your app's
specific needs.

# The Graph
`XYGraphWidget` encapsulates XYPlot's graphing functionality.  Given an instance of `XYPlot`, a reference
to `XYGraphWidget` can be retrieve via `XYPlot.getGraph()`.

## Domain & Range Boundaries
By default, Androidplot will analyze all `XYSeries` instances registered with the `XYPlot`, determine the
min/max values for domain and range and adjust the `XYPlot` boundaries to match those values.  If your
plot contains dynamic data, especially if your plot can periodically contain either no series data
or data with no resolution on one or both axis (all identical values for either x or y) then you may
want to manually set your `XYPlot`'s domain and range boundaries.

To set your plot's boundaries use:

* `XYPlot.setDomainBoundaries(Number value, BoundaryMode mode)`
* `XYPlot.setRangeBoundaries(Number value, BoundaryMode mode)`

Note that the value argument is only used when setting `BoundaryMode.FIXED`.  For all other
modes, pass in null.

### BoundaryMode
Androidplot provides four boundary modes:

#### FIXED
The plot's boundaries on the specified axis are fixed to user defined values.

#### AUTO (default)
The plot's boundaries auto adjust to the min/max values for the defined axis.

#### GROW
The plot's boundaries automatically increase to the max value encountered by the plot.  The initial
determines the starting boundaries from which the `BoundaryMode.GROW` behavior will be based.

#### SHRINK
The plot's boundaries automatically shrink to the min value encountered by the plot.  The initial
determines the starting boundaries from which the shrink behavior will be based.

## Domain & Range Lines
These are the horizontal lines drawn on a graph.  These lines are configured via:

* `XYPlot.setDomainStep(StepMode mode, Number value)`
* `XYPlot.setRangeStep(StepMode, Number value)`

Androidplot provides these step modes:

### Subdivide
When using `StepMode.SUBDIVIDE`, the graph is subdivided into the specified number of sections.

### IncrementByValue
`StepMode.INCREMENT_BY_VALUE` instructs Androidplot draw grid lines at the specified interval.  This
is the most commonly used modes as is produces an easy to read result.

### IncrementByPixels
`StepMode.INCREMENT_BY_PIXELS` behaves identically to `StepMode.INCREMENT_BY_VALUE` except that 
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
 
You're likely also going to need to normalize your series data in order to get it to display properly.   To help simplify 
this step, Androidplot provides [NormedXYSeries](advanced_xy_plot.md#normedxyseries) 
which can be used to wrap any `XYSeries` to provide the normalized representation of it's data.

There's a full [reference implementation](../demoapp/src/main/java/com/androidplot/demos/DualScaleActivity.java) 
of a dual scale plot using a custom `Formatter` and `NormedXYSeries` in the DemoApp.


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

# Saving & Restoring PanZoom State
The `PanZoom` class provides convenience methods for saving and restoring state from your `Activity`:

```java
// save the current pan/zoom state
@Override
public void onSaveInstanceState(Bundle bundle) {
     bundle.putSerializable("pan-zoom-state", panZoom.getState());
}

// restore the previously saved pan/zoom state
@Override
public void onRestoreInstanceState(Bundle bundle) {
    PanZoom.State state = (PanZoom.State) bundle.getSerializable("pan-zoom-state");
    panZoom.setState(state);
    plot.redraw();
}
```

For a more detailed look at pan & zoom behavior, check out the [Touch Zoom Example source code](../demoapp/src/main/java/com/androidplot/demos/TouchZoomExampleActivity.java).

# Series Renderers
There are several renderers available for XYPlots:

* LineAndPointRenderer
* BarRenderer
* CandlestickRenderer

When you add a new series to your plot, you tell Androidplot how to render it by passing
in a subclass of `XYSeriesFormatter` that corresponds to the desired renderer.  For example, to use
`LineAndPointRenderer`, you'd register your series with an instance of `LineAndPointFormatter`:

```java
LineAndPointFormatter format = new LineAndPointFormatter();
plot.addSeries(series, format);
```

If you need special behavior not provided by an existing renderer you can create
your own by either extending `XYSeriesRenderer` or one of the above implementations.  You'll also need
to create a matching implementation of `XYSeriesFormatter` that returns your renderer's class 
from it's `getRendererClass()` method.

## LineAndPointRenderer
`LineAndPointRenderer` is the go-to renderer when it comes to `XYSeries`.  It provides the most robust
feature set of any `XYSeriesRenderer` and has been the most carefully optimized for performance.  Having
said that, `LineAndPointRenderer` isn't always the best tool for the job.  Androidplot includes two 
additional variations of `LineAndPointRenderer`: 

* **FastLineAndPointRenderer** - intended for use in apps displaying large amounts of dynamic data where
fast refresh rates are important.
* **AdvancedLineAndPointRenderer** - provides capabilities for dynamically coloring individual line
segments, etc. See the [ECGExample source](../demoapp/src/main/java/com/androidplot/demos/ECGExample.java)
in the demo app for a complete example.

### Labeling Points
Most implementations of `XYSeriesRenderer` support labeling rendered points with text.  This functionality
is activated by setting the `PointLabelFormatter` property in the associated `XYSeriesFormatter`.  For
example, to enable point labels for a `LineAndPointFormatter`:

// create a new `PointLabelFormatter` with a text size of 12sp and a color of red:

```java
PointLabelFormatter plf = new PointLabelFormatter();
plf.getTextPaint().setTextSize(PixelUtils.spToPix(12));
plf.getTextPaint().setColor(Color.RED);
lineAndPointFormatter.setPointLabelFormatter(plf);
```

By default this will enable labels for all points using a string representation of the yVal of each
point.  This behavior can be customized by setting a custom instance of `PointLabeler` on the `XYSeriesFormatter`:

```java
formatter.setPointLabeler(new PointLabeler() {
    @Override
    public String getLabel(XYSeries series, int index) {
        // draw labels on even indexes only:
        if(index % 2 == 0) {
            return "Y=" + series.getY(index).doubleValue();
        }
        return null;
    }
});
```

## BarRenderer
See the [barcharts documentation](barchart.md).

## CandlestickRenderer
See the [candlestick documentation](candlestick.md)

# Drawing Smooth Lines
Smooth lines can be created by applying the 
[Catmull-Rom interpolator](http://androidplot.com/smooth-curves-and-androidplot/) to your series' Format.

# The Legend <a name="legend"></a>
By default, Androidplot will automatically produce a legend for your `XYPlot`.  See [the legend](legend.md) doc
for usage details.

# Graph Rotation
Androidplot provides the `Widget.setRotation(Widget.Rotation)` method for controlling the orientation
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

* Create your own implementation of `XYSeries` to work with your data in it's rawest form.
* Use `FastLineAndPointRenderer` instead of `LineAndPointRenderer`:

```java
plot.addSeries(azimuthHistorySeries,
    new FastLineAndPointRenderer.Formatter(
        Color.rgb(100, 100, 200), null, null, null));
``` 
                       
* Consider averaging or subsampling very large datasets before rendering.  If you have the time and
inclination, [the LTTB algorithm](http://skemman.is/stream/get/1946/15343/37285/3/SS_MSthesis.pdf) is 
particularly well suited for downsampling `XYSeries` data.
* If possible, avoid rendering vertices (points).
* Disable anti-aliasing on your `XYSeriesFormatter`'s paint values:

```java
LineAndPointFormatter format = new LineAndPointFormatter(...);
format.getLinePaint().setAntiAlias(false);
```
# Converting Values
Because the coordinate system used by your `XYSeries` data is almost always different than the screen
coordinate system upon which the data is rendered, you'll often need to convert from one system to
the other.  `XYPlot` provides convenience methods for this purpose:

## Screen to Series Conversion
```java
// x
float screenX = ...
Number x = plot.screenToSeriesX(screenX);

// y
float screenY = ...
Number y = plot.screenToSeriesY(screenY);

// x and y
PointF screenCoords = ...
XYCoords xy = plot.screenToSeries(screenCoords)
```

## Series to Screen Conversion
```java
// x
Number x = ...
float screenX = plot.seriesToScreenX(x);

// y
Number y = ...
float screenY = plot.seriesToScreenY(y);

// x and y
XYCoords xy = ...
PointF screenCoords = plot.series.Screen(xy);
```

# Whats Next?
Explore [Advanced XYPlot Topics](advanced_xy_plot.md)
