# Androidplot Documentation

When working with Androidplot, your best resource
is the [DemoApp's example source code](../demoapp) as it's kept up to date for every release.  

If you can't find an answer feel free to ask a question on Stack Overflow using the 
[Androidplot tag](http://stackoverflow.com/questions/tagged/androidplot).

# Tutorials
These tutorials are roughly in the order that they should be read.  Quickstart will get your app 
up and running with a basic xy plot.  Plot Composition explains the common anatomy of all plots
in the Androidplot library and lays the foundation for future topics.  The tutorials discuss 
specific plot types explaining styling and other advanced topics.

* [Quickstart](quickstart.md) :star:
* [Quickstart (YouTube Video)](https://www.youtube.com/watch?v=wEFkzQY_wWI) :movie_camera:
* [Plot Composition](plot_composition.md)
* [The Legend](legend.md)
* [XY Plots](xyplot.md)
* [Bar Charts](barchart.md)
* [Candlestick Charts](candlestick.md)
* [Pie Charts](piechart.md)
* [Bubble Charts](bubblechart.md)
* [Dynamic Plots](dynamicdata.md)
* [Advanced XY Plot Topics](advanced_xy_plot.md)
* [Custom Renderers](custom_renderer.md)

# Examples
Source code examples of the various plot types.

* [An XYPlot](../demoapp/src/main/java/com/androidplot/demos/SimpleXYPlotActivity.java)
* [A dynamic XYPlot](../demoapp/src/main/java/com/androidplot/demos/DynamicXYPlotActivity.java)
* [Panning & Zooming](../demoapp/src/main/java/com/androidplot/demos/TouchZoomExampleActivity.java)
* [Step Chart](../demoapp/src/main/java/com/androidplot/demos/StepChartExampleActivity.java)
* [Candlestick Chart](../demoapp/src/main/java/com/androidplot/demos/CandlestickChartActivity.java)
* [Scatter Plot](../demoapp/src/main/java/com/androidplot/demos/ScatterPlotActivity.java)
* [Bar Chart](../demoapp/src/main/java/com/androidplot/demos/BarPlotExampleActivity.java)
* [Pie Chart](../demoapp/src/main/java/com/androidplot/demos/SimplePieChartActivity.java)
* [An ECG Example](../demoapp/src/main/java/com/androidplot/demos/ECGExample.java)
* [f(x) Plot](../demoapp/src/main/java/com/androidplot/demos/FXPlotExampleActivity.java)

# XML Attributes
A complete list of XML attributes is [available here](attrs.md).  Formatters and plots can also be
styled through property paths, described in [XML Configuration](xml_configuration.md).
# Javadoc
Javadocs for versions through 1.5.7 are [available here](https://javadoc.io/doc/com.androidplot/androidplot-core).  Due to a build issue
producing Javadocs for the latest releases has been paused.  There have been very
few changes to the API since 1.5.7 so the Javadocs should still be useful in the mean time.

# Release Notes
Full release notes are [available here](release_notes.md)

# Contributing
_If you see something that isn't right or want to contribute, please [make a pull-request](https://help.github.com/articles/creating-a-pull-request/) - these docs 
live the main repo in the top-level `/docs` directory.  For more info, see the [Contributing Source Code](docs/contributing.md) doc.