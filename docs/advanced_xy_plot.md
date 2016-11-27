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

FastXYSeries allows the developer to avoid the high overhead of this iteration by calculating these
min/max values more efficiently.  For example, if you have data whose YVals are strictly ascending,
the first and last YVal of the series will always be the min/max YVals respectively.

XYSeries implementation supports a min/max algorithm implementation that is more efficient than doing 
a comparison on each point of the series via iteration. It's a good idea to implement this interface
if your series will contain more than about 500 points

## OrderedXYSeries
If the YVals of your series are in ascending order, implementing this interface provides a hint to
the series renderer that allows it to avoid iterating over points that are outside the screen's
visible domain.  For larger data sets, implementing this interface can mean the difference between
smooth animations and freezing.

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

# Sampling
Series size is generally the biggest factor when it comes to rendering performance.  If you're plotting 
very large datasets, its generally a good idea to sample the data to improve performance.
For example, if you have an XYSeries that consists of 10,000 points, we can sample that data into 
the 200 points that most accurately represent the profile of the original series:

```java
// An instance of any implementation of XYSeries.  Assume it's size is 10,000
XYSeries origalSeries = ...;

// Sampled series of size 200:
EditableXYSeries sampledSeries = new FixedSizeEditableXYSeries(
    origalSeries.getTitle(), 200);

// an instance of an implementation of Sampler:
Sampler sampler = ...

// do the actual sampling:
new LTTBSampler().run(origalSeries, sampledSeries);
```

Currently LTTBSampler is the only available implementation.

# Storing series data in onSaveInstanceState
TODO