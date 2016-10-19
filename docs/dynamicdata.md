# Plotting Dynamic Data
One of Androidplot's core focuses is on plotting dynamically changing data in real-time.  Special care has
been put into the design of the library to provide solutions for various scenarios and synchronization concerns.

# Background Rendering
By default, Androidplot does all of it's rendering on the UI thread.  While this is fine for most applications,
it can cause performance issues when rendering larger datasets or when continuously redrawing dynamic data.
To resolve this issue, Androidplot provides a background rendering mode that can be enabled programmatically in
your Java source or via XML:

**Programmatically:**
```java
plot.setRenderMode(Plot.RenderMode.USE_BACKGROUND_THREAD);
```

**XML:**
```xml
ap:renderMode="use_background_thread"
```

In general, if your plot is continuously redrawing the plot, you should use background rendering.

#Rendering Dynamic Data
There are two general approaches to dynamically rendering data: event driven and render loops.  Each has
pros and cons and often times, the application might force you to use one approach over the other, however
90% of the time using a render loop is the better approach.

In both cases the redraw is triggered by invoking `Plot.redraw().`
 
## Render Loops
A render loop is basically a continuous loop of calls to a render routine.  This loop might
include logic for updating the data being plotted, or it might simply focus on maintaining  a stable refresh rate.
Androidplot provides a convenience utility, Redrawer which provides a basic implementation of a render loop
running on a fixed frequency.  Check out the [ECG demo source](../demoapp/src/main/java/com/androidplot/demos/ECGExample.java)  for a working example.
 
## Event Driven Redraws
Sometimes it's more efficient to only redraw the plot as a result of an event such as a GPS update, button click, etc.
Event driven updates are as simple as invoking `Plot.redraw()` from the callback handling the event of interest,
after the data being plotted has been updated.  While it's possible for the event triggering the redraw
to fire at a faster rate than the plot is capable of redrawing, no special care needs to be taken as Androidplot
ignores subsequent invocations of redraw() when a previous invocation is already active.

# Synchronization
A major challenge of plotting dynamic data is the need to render an accurate representation of the data
as it existed at a specific point in time.  This is not a trivial problem and the right solution depends
greatly on the specific details of the project.  Presented here are a list of items to keep in mind 
along with general best practices and approaches to solving more common problems.

The most common scenario is when your data model is being dynamically updated by a background thread.
Regardless of whether or not you use background rendering here, invoking plot.redraw() is asynchronous
and your background thread can easily loop around and start altering values while your plot is half way through
rendering the previous set of values.  The result of a race condition is indeterminate; if the size of your data
changed the app could crash from an IndexOutOfBoundsException, or a bogus representation of your data might
be rendered to the display.

To prevent the race condition, changes to the data model must be synchronized with Androidplot's 
rendering loop.  A PlotListener can be used to detect when a Plot begins and ends a redraw and a read-write 
can be used to protect the model (typically any series data being drawn):

```java
plot.addListener(new PlotListener() {
    @Override
    public void onBeforeDraw(Plot source, Canvas canvas) {
        // write-lock each active series for writes 
    }

    @Override
    public void onAfterDraw(Plot source, Canvas canvas) {
        // unlock any locked series
    }
});
```   
Note that if your custom series implements the PlotListener interface, it's onBeforeDraw and onAfterDraw
methods will be automatically called when the series is rendered, without the need for adding it
as a listener to the plot.  SimpleXYSeries provides a reference implementation of this synchronization
technique.

In-depth usage of thread synchronization and read-write locks is beyond scope for this doc but you can 
check out the source code of the SimpleXYSeries class for a functional example of ReentrantReadWriteLock
being used to synchronize an XYSeries implementation.

# Examples
Androidplot provides a few full-source examples of how to dynamically plot data as demonstrated
in the [demo app](https://play.google.com/store/apps/details?id=com.androidplot.demos&hl=en).

* [Plotting Realtime Orientation Sensor Data](../demoapp/src/main/java/com/androidplot/demos/OrientationSensorExampleActivity.java)
* [An ECG Example](../demoapp/src/main/java/com/androidplot/demos/ECGExample.java)
* [A simple dynamic XYPlot](../demoapp/src/main/java/com/androidplot/demos/DynamicXYPlotActivity.java)