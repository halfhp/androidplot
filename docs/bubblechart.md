# Bubble Charts
A bubble chart is essentially a two dimensional representation of three dimensional data
on an xy plot, where the xy values are drawn as a circle whose radius represents the z value.

![image](images/screens/bubble_horiz.png)

# Basic Usage
The first step in creating a bubble chart is to define the data to be plotted.  We'll start
by creating an instance of BubbleSeries:

Using the implicit i-val for x:

```java
BubbleSeries series1 = new BubbleSeries(
    Arrays.asList(new Number[]{3, 5, 2, 3, 6}),
    Arrays.asList(new Number[]{1, 5, 2, 2, 3}), "s1");
```

Or it's equivalent four argument counterpart:

```java
BubbleSeries bubbleSeries = new BubbleSeries(
    Arrays.asList(new Number[]{0, 1, 2, 3, 4}),
    Arrays.asList(new Number[]{3, 5, 2, 3, 6}),
    Arrays.asList(new Number[]{1, 5, 2, 2, 3}), "s1");
```

Next, create a Formatter defining the fill and outline colors of the bubbles:


```java
// draw bubbles with a green fill and white outline:
BubbleFormatter formatter = new BubbleFormatter(Color.GREEN, Color.WHITE)
```

Finally, add the bubble series to our plot as you would any other xyseries instance:

```java
plot.addSeries(bubbleSeries, formatter);
```

# BubbleScaleMode
By default, BubbleRenderer scales each rendered bubble radius using the square root  of it's corresponding
z-val, preventing apparent size differences in each bubble radius from being visually misleading.  See: https://en.wikipedia.org/wiki/Bubble_chart#Choosing_bubble_sizes_correctly

If you'd prefer to use a linear scale:

```java
plot.getRenderer(BubbleRenderer.class).setBubbleScaleMode(BubbleRenderer.BubbleScaleMode.LINEAR);
```
# Example
Check out the [bubble chart example source](../demoapp/src/main/java/com/androidplot/demos/BubbleChartActivity.java) for a full source example of a bubble chart.
