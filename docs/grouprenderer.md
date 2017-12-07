# GroupRenderer
`GroupRenderer` is a special implementation of `XYSeriesRenderer` that combines multiple instances
of `XYSeries` into a single virtual series of s higher dimension.  Examples of `GroupRenderer` are:

* BarRenderer
* CandlestickRenderer

Let's take a quick look at `CandlestickRenderer`.  A candlestick chart is basically a two two dimensional 
representation of a complex value; for every value of x a single candlestick is drawn, but there are 
four values (dimensions) that make up each candlestick:

* open
* close
* high
* low

When a series is added to the plot with a formatter associated with a renderer that extends `GroupRenderer`,
all series added with formatters of the same type are automatically "grouped" together during rendering.