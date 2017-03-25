# Advanced XY Series Types
Androidplot offers several specialized implementations of XYSeries providing various performance
or usability enhancements.

## EditableXYSeries
Enhances the standard XYSeries interface with edit methods.

## FixedSizeEditableXYSeries
An implementation of EditableXYSeries that supports modifying x/y values and has been optimized 
for speed. FixedSizeEditableXYSeries is optimized for data whose samples may frequently change but
whose absolute size doesn't change, such as an ECG (ring buffer) or an animated plot.

## FastXYSeries
By default, Androidplot iterates over every element in each series every render cycle to
determine it's current min/max values.  This is necessary in order to support dynamic plots where
data can change at any moment and invalidate the previously calculated min/max vals.

FastXYSeries allows the developer to provide a more efficient algorithm to obtain min/max XVals 
and avoid the high overhead of this iteration.  For example, if you know your XVals will always 
be in strict ascending order then the first and last XVal of the series will always contain the 
min/max XVals respectively.

XYSeries implementation supports a min/max algorithm implementation that is more efficient than doing 
a comparison on each point of the series via iteration. It's a good idea to implement this interface
if your series will contain more than about 500 points

## OrderedXYSeries
If the XVals of your series are in ascending order, implementing this interface provides a hint to
the series renderer that allows it to avoid iterating over points that are outside the screen's
visible domain.  For larger data sets, implementing this interface can mean the difference between
smooth animations and freezing.

## ScalingXYSeries
Wraps any other instance of XYSeries and provides a simple interface for dynamically 
scaling x and/or y values.  A popular use case for dynamic scaling is to create an animated intro
for your XYSeries where yVals increase (or decrease in the case of negative values) from 0 towards
their original value. The [AnimatedXYPlotActivity](../demoapp/src/main/java/com/androidplot/demos/AnimatedXYPlotActivity.java)
in the DemoApp is one example on how this can be accomplished.

## SampledXYSeries
SampledXYSeries is meant for use with extremely large datasets.  Given a series, multiple sampled 
series instances at stepped resolutions are generated for faster rendering.

Basic usage:

```java
XYSeries series = ... // instantiate an XYSeries however you want here

// wrap our series in a SampledXYSeries with a threshold of 1000:
SampledXYSeries sampledSeries =
    new SampledXYSeries(series, OrderedXYSeries.XOrder.ASCENDING, 2,100);

// add the SampledXYSeries instance to the plot:
plot.addSeries(sampledSeries, formatter);
```

SampledXYSeries is meant to be used in conjunction with ZoomEstimator, which enables a plot to 
automatically render using the resolution appropriate for the current screen boundaries, allowing 
pan / zoom operations to perform with little or no degradation as series size increases.  

To enable ZoomEstimator:

```java
// enable autoselect of sampling level based on visible boundaries:
plot.getRegistry().setEstimator(new ZoomEstimator());
```

[The Touch Zoom Example source code](../demoapp/src/main/java/com/androidplot/demos/TouchZoomExampleActivity.java) provides a functional reference implementation.

If you want to take advantage of the performance benefits of sampling but don't need pan/zoom support
check out the Sampling section below.

## NormedXYSeries
A convenience wrapper to simplify normalizing XYSeries data in the range of 0 to 1.  Usage is straightforward:

```java
XYSeries normedSeries = new NormedXYSeries(series);
```

which is equivalent to:

```java
XYSeries normedSeries = new NormedXYSeries(series, null, new Norm(null, 0, false));
```

The first null argument pertains to the normalization being applied to the x axis values.  While normalization
can certainly be applied on this axis, it's typically unused, which is why the single argument constructor
defaults to a null value here.

Both examples auto normalize the passed in series, maximizing the resolution of the
output result.  Sometimes however, it's desirable to control the output resolution for the purpose
of visually shifting the result up or down in the graph.  This can be done by using the `NormedXYSeries.Norm`
constructor's second and third arguments: `offset` and `useOffsetCompression`.

##### offset
This value is added to the original normalized value to effectively shift the series data up or down
within the normalized range.  If the offset is large enough and offset compression is not used, this 
can cause normalized values to exceed the norm range of 0 to 1.

##### useOffsetCompression
If you want to shift a normalized series up or down in the graph but do not want the shift to potentially
move values offscreen, you can set `useOffsetComression` to true.  This tells `NormedXYSeries` to shrink
the scale of the associated series relative to the offset being used to ensure that normed values
stay within the range of 0 to 1.  If set to true and you supply an offset <0 or >1, an 
`IllegalArgumentException` will be thrown.

We've not talked about the first argument to the `NormedXYSeries.Norm` constructor: `minMax`.  This is an
optional optimization value that reflects min/max values in the series data being passed in.  If they
are known, you can pass these in to speed up normalization, otherwise just pass in null and they will
be auto calculated.

IMPORTANT: If your series data is dynamic and it's min/max values change at runtime, you'll need to invoke
`NormedXYSeries.normalize(Norm, Norm)` immediately following each change in order to maintain accuracy.

There's a [reference implementation](../demoapp/src/main/java/com/androidplot/demos/DualScaleActivity.java) 
of a dual scale plot that demonstrates NormedXYSeries usage in the DemoApp.

# Sampling
Series size is generally the biggest factor when it comes to rendering performance.  If you're plotting 
very large datasets, its generally a good idea to sample the data to improve performance.
For example, if you have an XYSeries that consists of 10,000 points, we can sample that data into 
the 200 points that most accurately represent the profile of the original series:

```java
// An instance of any implementation of XYSeries.  Assume it's size is 10,000
XYSeries originalSeries = ...;

// Sampled series of size 200:
EditableXYSeries sampledSeries = new FixedSizeEditableXYSeries(
    originalSeries.getTitle(), 200);

// an instance of an implementation of Sampler:
Sampler sampler = ...

// do the actual sampling:
new LTTBSampler().run(originalSeries, sampledSeries);
```

Currently LTTBSampler is the only available implementation.

# Storing series data in onSaveInstanceState
If your series data requires a non trivial amount of preprocessing (subsampling etc.) or your data comes
from a dynamic source, you'll likely want to persist your series data when your Activity saves its
instance state.  There are a few caveats to be aware of:

* You can only persist about 1mb worth of data at a time so if your series data is much larger than that
you'll need to find a creative solution to the problem
* Due to [quirks in the way Android persists data](http://stackoverflow.com/questions/12300886/linkedlist-put-into-intent-extra-gets-recast-to-arraylist-when-retrieving-in-nex)
`XYSeries` implementations such as `SimpleXYSeries` that use `LinkedList` instances to store data cannot be serialized directly.
* Formatters generally cannot be persisted as they typically contain instances of `Paint` that cannot be serialized directly..

Due to these limitations we suggest storing `XYSeries` data into an array or `ArrayList` and using that to 
instantiate your `XYSeries`.  The DemoApp's [Time Series Example](../demoapp/src/main/java/com/androidplot/demos/TimeSeriesActivity.java) 
contains a full source example.