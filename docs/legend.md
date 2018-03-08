# <a name="legend"></a> The Legend
For `Plot` types that support it, the legend displays a list of elements in the plot along with
a color coded icon.  The color coded icon is automatically generated using the colors and line styles
used to render the associated item.  In the case of a `Series`, this is the `Formatter` you associated
with the `Series` when you added it to your `Plot`.

# Showing / Hiding the Legend
Depending on the `Plot` type(s) you are using, the legend may or may not be visible by default.  To
can enable / disable the legend:

```java
plot.getLegend().setVisible(true|false);
```

# Hiding Series Items
You can tell Androidplot not to generate a legend item for a `Series` by configuring it's associated
`Formatter`:

```java
formatter.setLegendIconEnabled(false);
```

## The TableModel
The `TableModel` controls how and where each item in the legend is drawn.  Androidplot provides two
default implementations; `DynamicTableModel` and `FixedTableModel` (detailed below).  All `TableModel` implementations
organize elements into a grid.  This grid is populated with items based on the order which it's corresponding
series was added to the plot.  This ordering can be further controlled by setting the `TableModel`'s
`TableOrder` param to either [ROW_MAJOR](https://en.wikipedia.org/wiki/Row-major_order) (items are added left-to-right, top-down) 
or `COLUMN_MAJOR` (items are added top-down, left-to-right).

### DynamicTableModel
The `DynamicTableModel` takes a desired of numbered rows and columns and evenly subdivides the `LegendWidget`'s
visible space into cells.  For example, A 2x2 legend using `ROW_MAJOR` ordering:

```java
plot.getLegend().setTableModel(new DynamicTableModel(2, 2, TableOrder.ROW_MAJOR));
```

### FixedTableModel
The `FixedTableModel` takes a desired size of each cell in pixels and adds cells using the specified `TableOrder`.
It automatically wraps to the next row or column (based on `TableOrder`) when the cell being added
exceeds the legend's available space on a given axis.  For example, A `FixedTableModel` using 300w*100h cells and
a TableOrder of `COLUMN_MAJOR`:

```java
plot.getLegend().setTableModel(new FixedTableModel(PixelUtils.dpToPix(300), 
    PixelUtils.dpToPix(100), TableOrder.COLUMN_MAJOR));
```

# Sorting Legend Entries
You can control the order of Legend entries by setting a custom `Comparator` on the legend:

```java
Comparator<...> myComparator = ...
plot.getLegend().setLegendItemComparator(myComparator);
```

Using a custom `Comparator` in conjunction with `ROW_MAJOR` and `COLUMN_MAJOR` properties on the `TableModel`
(show above) gives you full control over the display ordering of your legend entries.

# Positioning and Resizing
The legend is just an implementation of a Widget and is positioned and resized in the same ways
that all Widget instances are positioned.  See the [Plot Composition](plot_composition.md) doc for details.