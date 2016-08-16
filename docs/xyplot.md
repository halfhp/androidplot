# XYPlot
XYPlot renders XYSeries data onto a graph.

![image](images/plot_anatomy.png)

# XYSeries
XYSeries is the interface Androidplot uses to retrieve your numeric data.  You may either create your own
implementation of XYSeries or use SimpleXYSeries if you don't have tight performance requirements or
if your numeric data is easily accessed as an array or list of values.

## SimpleXYSeries
TODO - For now, use the DemoApp's source as a reference as nearly all samples rely on SimpleXYSeries
to populate the graph with series data.

# The Graph
XYGraphWidget encapsulates XYPlot's graphing functionality.  Given an instance of XYPlot, a reference
to XYGraphWidget can be retrieve via `Plot.getGraph()`.

## Domain & Range Boundaries
By default, Androidplot will analyze all XYSeries instances registered with the Plot, determine the
min/max values for domain and range and adjust the Plot's boundaries to match those values.  If your
plot contains dynamic data, especially if your plot can periodically contain either no series data
or data with no resolution on one or both axis (all identical values for either x or y) then you may
want to manually set your XYPlot's domain and range boundaries.

### BoundaryMode
Androidplot provides four BoundaryMode options

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
TODO

## Domain & Range Labels
Androidplot supports labeling domain values on either or both the top and bottom  graph edges 
and range values on either or both the left and right graph edges.  Most default styles show labels
only on the left and bottom edges.

### Dual Axis Labels
Sometimes it is desirable to display additional labels for a single axis, each using it's own scale.

TODO - [f(x) plot example source](../demoapp/src/main/java/com/androidplot/demos/FXPlotExampleActivity.java) 
provides a reference implementation.

### StepMode
TODO

# Renderers
There are several renderers available for XYPlots:

* LineAndPointRenderer
* BarRenderer
* CandlestickRenderer

# Drawing Smooth Lines
Smooth lines can be created by applying the 
[Catmull-Rom interpolator](http://androidplot.com/smooth-curves-and-androidplot/) to your series' Format.

## The Legend
By default, Androidplot will automatically produce a legend for your Plot.  You however choose to hide the legend
or you can customize it to suit your needs.

TODO