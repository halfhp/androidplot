# XML Configuration

Androidplot lets you style formatters and plots from XML by mapping attribute names onto the
setter methods of the object being configured.  This is the mechanism behind formatter config files
such as `R.xml.line_point_formatter` and the `androidPlot.` prefixed attributes usable in layouts.

# Formatter Config Files
Place a config file in your `/res/xml` directory and pass its id to a formatter's constructor:

**/res/xml/line_point_formatter.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<config
    linePaint.strokeWidth="5dp"
    linePaint.color="@color/plot_line"
    vertexPaint.color="#007700"
    fillPaint.color="#00000000"/>
```

```java
LineAndPointFormatter formatter = new LineAndPointFormatter(this, R.xml.line_point_formatter);
```

Because they are ordinary resources, config files can be qualified by screen size, density,
orientation and so on using the standard resource directory conventions.

# Property Paths
Each attribute name is a path of JavaBean properties, separated by dots, ending in the property
to set.  Every segment except the last must have a getter; the last must have a setter.  Matching
is case insensitive.

```xml
linePaint.strokeWidth="5dp"
```

is equivalent to:

```java
formatter.getLinePaint().setStrokeWidth(...);
```

Any property reachable this way can be configured, not just the ones Androidplot documents.

# Values
Values are converted according to the type of the setter's parameter.

| Setter parameter | Accepted values |
|---|---|
| `int` | A plain integer such as `3` or `-1`; a color such as `#00AA00`, `#FF00AA00` or `red`; or a resource reference such as `@color/plot_line`, `@dimen/marker_size` or `@integer/max_points`. Color references yield the color value, dimension references the size in pixels. |
| `float` | A dimension with units such as `5dp`, `12sp`, `2mm`; a `@dimen` reference; or a plain float. Prefer `dp` and `sp` so sizes scale consistently across devices. |
| `boolean` | `true` or `false`. |
| `String` | A literal, or a `@string` reference. |
| enum | The constant's name, case insensitive, for example `fillDirection="bottom"`. |

Setters taking more than one parameter are given the values separated by `|`, in order:

```xml
ap:lineLabels="left|bottom"
```

# Configuring Plots From Layout XML
The `Plot` styleable attributes listed in the [XML attributes reference](attrs.md) cover the most
commonly used properties.  For anything not yet available as a dedicated attribute, the same
property-path syntax can be used directly in the layout by prefixing it with `androidPlot.`:

```xml
<com.androidplot.xy.XYPlot
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    androidPlot.title="My Plot"
    androidPlot.graph.domainGridLinePaint.color="#33FFFFFF"/>
```

# Obfuscation
Configuration relies on reflection over method names, so classes configured this way must keep
their members intact through ProGuard or R8.  Androidplot's own classes are covered by the rule
from the [quickstart](quickstart.md):

```
-keep class com.androidplot.** { *; }
```

If you configure your own classes through this mechanism, add an equivalent rule for them.
