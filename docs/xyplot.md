# XYPlot
An XYPlot renders one or more XYSeries onto an instance of XYGraphWidget.  It also includes two instances
of TextLabelWidget used to display an optional title for the domain and range axis, and an instance
of [XYLegendWidget](#legend) which by default will automatically display legend items for each XYSeries added to the plot.

![image](images/plot_anatomy.png)

# XYSeries
XYSeries is the interface Androidplot uses to retrieve your numeric data.  You may either create your own
implementation of XYSeries or use SimpleXYSeries if you don't have tight performance requirements or
if your numeric data is easily accessed as an array or list of values.  As a convenience, Androidplot provides
an all-purpose implementation of the XYSeries interface called SimpleXYSeries.

## SimpleXYSeries
SimpleXYSeries is used to wrap your raw data with an implementation of the XYSeries interface.  You can supply
your data in several ways:

As a list of y-vals; the x-val is implicitly set to array index of each supplied y-val:
```java
Number[] yVals = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
XYSeries series1 = new SimpleXYSeries(
    Arrays.asList(yVals), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "my series");
```               

An interleaved list of x/y value pairs:
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

NOTE: SimpleXYSeries is designed to be easy to use for a broad number of applications,; it is not 
designed for speed; if you are dynamically displaying data that needs to be refreshed more than several
times a second, you'll probably want to write an implementation of XYSeries designed for your app's
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

### Dual Axis Labels
Sometimes it is desirable to display additional labels for a single axis, each using it's own scale.

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

# Renderers
There are several renderers available for XYPlots:

* LineAndPointRenderer
* BarRenderer
* CandlestickRenderer

# Drawing Smooth Lines
Smooth lines can be created by applying the 
[Catmull-Rom interpolator](http://androidplot.com/smooth-curves-and-androidplot/) to your series' Format.

## <a name="legend"></a> The Legend
By default, Androidplot will automatically produce a legend for your Plot.  You however choose to hide the legend
or you can customize it to suit your needs.

In addition to the scaling and positioning operations common to all widgets, number and behavior of legend
rows and columns can be configured.

### The TableModel
The TableModel controls how and where each item in the legend is drawn.  Androidplot provides two
default implementations; DynamicTableModel and FixedTableModel (detailed below).  All TableModel implementations
organize elements into a grid.  This grid is populated with items based on the order which it's corresponding
series was added to the plot.  This ordering can be further controlled by setting the TableModel's
TableOrder param to either ROW_MAJOR (items are added left-to-right, top-down) or COLUMN_MAJOR (items are added top-down, left-to-right).

#### DynamicTableModel
The DynamicTableModel takes a desired of numbered rows and columns and evenly subdivides the LegendWidget's
visible space into cells.  For example, A 2x2 legend using ROW_MAJOR ordering:

```java
plot.getLegend().setTableModel(new DynamicTableModel(2, 2, TableOrder.ROW_MAJOR));
```

#### FixedTableModel
The FixedTableModel takes a desired size of each cell in pixels and adds cells using the specified TableOrder.
It automatically wraps to the next row or column (based on TableOrder) when the cell being added
exceeds the legend's available space on a given axis.  For example, A FixedTableModel using 300w*100h cells and
a TableOrder of COLUMN_MAJOR:

```java
plot.getLegend().setTableModel(new FixedTableModel(PixelUtils.dpToPix(300), 
    PixelUtils.dpToPix(100), TableOrder.COLUMN_MAJOR));
```                