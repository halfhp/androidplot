_This documentation is auto generated from [attrs.xml](../androidplot-core/src/main/res/values/attrs.xml)._

# Androidplot XML Attributes
Attributes are broken down by element followed by either their type or list of accepted values.
Supported Elements:

* [Plot](#Plot)
* [XYPlot](#XYPlot)
* [PieChart](#PieChart)

## Plot
Plot's attrs are available in all Plot types.

### markupEnabled
__boolean__

### renderMode
* use_background_thread
* use_main_thread

### marginTop
__dimension__

### marginBottom
__dimension__

### marginLeft
__dimension__

### marginRight
__dimension__

### paddingTop
__dimension__

### paddingBottom
__dimension__

### paddingLeft
__dimension__

### paddingRight
__dimension__

### title
__dimension__

### titleTextSize
__string__

### titleTextColor
__dimension__

### backgroundColor
__color__

### borderColor
__color__

### borderThickness
__color__

## XYPlot
XML attributes for the [XYPlot](xyplot.md) class.

### previewMode
TODO

### domainStepMode
* subdivide
* increment_by_val
* increment_by_pixels

### domainStep
__dimension|float|integer__

### rangeStepMode
* subdivide
* increment_by_val
* increment_by_pixels

### rangeStep
__dimension|float|integer__

### domainTitle
__string__

### domainTitleTextColor
__color__

### domainTitleTextSize
__dimension__

### domainTitleHeightMode
* absolute
* relative
* fill

### domainTitleWidthMode
* absolute
* relative
* fill

### domainTitleHeight
__dimension|float|integer__

### domainTitleWidth
__dimension|float|integer__

### domainTitleHorizontalPositioning
* absolute_from_left
* absolute_from_right
* absolute_from_center
* relative_from_left
* relative_from_right
* relative_from_center

`HorizontalPositioning` component of the `HorizontalPosition` of the `TextLabelWidget`
that displays the domain title.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### domainTitleVerticalPositioning
* absolute_from_top
* absolute_from_bottom
* absolute_from_center
* relative_from_top
* relative_from_bottom
* relative_from_center

`VerticalPositioning` component of the `VerticalPosition` of the `TextLabelWidget`
that displays the domain title.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### domainTitleHorizontalPosition
__dimension|float|integer__

`float` value component of the `HorizontalPosition` of the `TextLabelWidget`
that displays the domain title.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### domainTitleVerticalPosition
__dimension|float|integer__

`float` value component of the `VerticalPosition` of the `TextLabelWidget`
that displays the domain title.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### domainTitleAnchor
* top_middle
* left_top
* left_middle
* left_bottom
* right_top
* right_middle
* right_bottom
* bottom_middle
* center

### domainTitleVisible
__boolean__

### rangeTitle
__string__

### rangeTitleTextColor
__color__

### rangeTitleTextSize
__dimension__

### rangeTitleHeightMode
* absolute
* relative
* fill

### rangeTitleWidthMode
* absolute
* relative
* fill

### rangeTitleHeight
__dimension|float|integer__

### rangeTitleWidth
__dimension|float|integer__

### rangeTitleHorizontalPositioning
* absolute_from_left
* absolute_from_right
* absolute_from_center
* relative_from_left
* relative_from_right
* relative_from_center

### rangeTitleVerticalPositioning
* absolute_from_top
* absolute_from_bottom
* absolute_from_center
* relative_from_top
* relative_from_bottom
* relative_from_center

### rangeTitleHorizontalPosition
__dimension|float|integer__

### rangeTitleVerticalPosition
__dimension|float|integer__

### rangeTitleAnchor
* top_middle
* left_top
* left_middle
* left_bottom
* right_top
* right_middle
* right_bottom
* bottom_middle
* center

### rangeTitleVisible
__boolean__

### drawGridOnTop
__boolean__
<br/>
(default is false) When set to true, grid lines are drawn on top of rendered series data
instead of underneath.

### graphHeightMode
* absolute
* relative
* fill

### graphWidthMode
* absolute
* relative
* fill

### graphHeight
__dimension|float|integer__

### graphWidth
__dimension|float|integer__

### graphRotation
* none
* ninety_degrees
* negative_ninety_degrees
* one_hundred_eighty_degrees

### graphHorizontalPositioning
* absolute_from_left
* absolute_from_right
* absolute_from_center
* relative_from_left
* relative_from_right
* relative_from_center

`HorizontalPositioning` component of the `HorizontalPosition` of the `XYGraphWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### graphVerticalPositioning
* absolute_from_top
* absolute_from_bottom
* absolute_from_center
* relative_from_top
* relative_from_bottom
* relative_from_center

`VerticalPositioning` component of the `VerticalPosition` of the `XYGraphWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### graphHorizontalPosition
__dimension|float|integer__

`float` value component of the `HorizontalPosition` of the `XYGraphWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### graphVerticalPosition
__dimension|float|integer__

`float` value component of the `VerticalPosition` of the `XYGraphWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### graphAnchor
* top_middle
* left_top
* left_middle
* left_bottom
* right_top
* right_middle
* right_bottom
* bottom_middle
* center

### graphVisible
__boolean__

### graphMarginTop
__dimension__

### graphMarginBottom
__dimension__

### graphMarginLeft
__dimension__

### graphMarginRight
__dimension__

### graphPaddingTop
__dimension__

### graphPaddingBottom
__dimension__

### graphPaddingLeft
__dimension__

### graphPaddinRight
__dimension__

### gridClippingEnabled
__boolean__

### gridInsetTop
__dimension__

### gridInsetBottom
__dimension__

### gridInsetLeft
__dimension__

### gridInsetRight
__dimension__

### lineLabelInsetTop
__dimension__
<br/>
Top edge of a rectangle relative to the XYGraphWidget
border upon which line labels will be anchored.

### lineLabelInsetBottom
__dimension__
<br/>
Bottom edge of a rectangle relative to the XYGraphWidget
border upon which line labels will be anchored.

### lineLabelInsetLeft
__dimension__
<br/>
Left edge of a rectangle relative to the XYGraphWidget
border upon which line labels will be anchored.

### lineLabelInsetRight
__dimension__
<br/>
Right edge of a rectangle relative to the XYGraphWidget
border upon which line labels will be anchored.

### lineLabels
* top
* bottom
* left
* right

Enable line labels on one or more edge of the graph.  For example, to enable labels on the left
and bottom edges:
```
ap:lineLabels="left|bottom"
```

### lineLabelAlignTop
* left
* center
* right

Text alignment of line labels drawn on top edge of the plot.  This alignment is applied relative
to the line label insets defined by `lineLabelInsetTop`.

### lineLabelAlignBottom
* left
* center
* right

Text alignment of line labels drawn on bottom edge of the plot.  This alignment is applied relative
to the line label insets defined by `lineLabelInsetBottom`.

### lineLabelAlignLeft
* left
* center
* right

Text alignment of line labels drawn on left edge of the plot.  This alignment is applied relative
to the line label insets defined by `lineLabelInsetLeft`.

### lineLabelAlignRight
* left
* center
* right

Text alignment of line labels drawn on right edge of the plot.  This alignment is applied relative
to the line label insets defined by `lineLabelInsetRight`.

### lineLabelRotationTop
__float__
<br/>
Angle at which line labels on the plot's top edge are drawn.

### lineLabelRotationBottom
__float__
<br/>
Angle at which line labels on the plot's bottom edge are drawn.

### lineLabelRotationLet
__float__
<br/>
Angle at which line labels on the plot's left edge are drawn.

### lineLabelRotationRight
__float__
<br/>
Angle at which line labels on the plot's right edge are drawn.

### domainLineThickness
__dimension__

### rangeLineThickness
__dimension__

### domainLineColor
__color__

### rangeLineColor
__color__

### domainOriginLineThickness
__dimension__

### rangeOriginLineThickness
__dimension__

### domainOriginLineColor
__color__

### rangeOriginLineColor
__color__

### lineLabelTextSizeTop
__dimension__

### lineLabelTextSizeBottom
__dimension__

### lineLabelTextSizeLeft
__dimension__

### lineLabelTextSizeRight
__dimension__

### lineLabelTextColorTop
__color__

### lineLabelTextColorBottom
__color__

### lineLabelTextColorLeft
__color__

### lineLabelTextColorRight
__color__

### lineExtensionTop
__dimension__

### lineExtensionBottom
__dimension__

### lineExtensionLeft
__dimension__

### lineExtensionRight
__dimension__

### gridBackgroundColor
__color__
<br/>
background color of the grid portion of the XYGraphWidget

### graphBackgroundColor
__color__
<br/>
background color of the XYGraphWidget

### legendHeightMode
* absolute
* relative
* fill

### legendWidthMode
* absolute
* relative
* fill

### legendHeight
__dimension|float|integer__

### legendWidth
__dimension|float|integer__

### legendHorizontalPositioning
`HorizontalPositioning` component of the `HorizontalPosition` of the `XYLegendWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.
* absolute_from_left
* absolute_from_right
* absolute_from_center
* relative_from_left
* relative_from_right
* relative_from_center

### legendVerticalPositioning
`VerticalPositioning` component of the `VerticalPosition` of the `XYLegendWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.
* absolute_from_top
* absolute_from_bottom
* absolute_from_center
* relative_from_top
* relative_from_bottom
* relative_from_center

### legendHorizontalPosition
__dimension|float|integer__

`float` value component of the `HorizontalPosition` of the `XYLegendWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### legendVerticalPosition
__dimension|float|integer__

`float` value component of the `VerticalPosition` of the `XYLegendWidget`.
See [Positioning Widgets](plot_composition.md#positioning-widgets) documentation.

### legendAnchor
* top_middle
* left_top
* left_middle
* left_bottom
* right_top
* right_middle
* right_bottom
* bottom_middle
* center

### legendTextSize
__dimension__

### legendTextColor
__color__

### legendIconHeightMode
* absolute
* relative
* fill

### legendIconWidthMode
* absolute
* relative
* fill

### legendIconHeight
__dimension|float|integer__

### legendIconWidth
__dimension|float|integer__

### legendVisible
__boolean__

### pieBorderThickness
__dimension__
<br/>
Determines how far beyond the graph's edge each domain grid line will extend.

### pieBorderThickness
__dimension__
<br/>
Determines how far beyond the graph's edge each range grid line will extend.

## PieChart
TODO

### pieBorderColor
__color__

### pieBorderThickness
__dimension__

