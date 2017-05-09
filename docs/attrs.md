# Androidplot XML Attributes
Attributes are broken down by element followed by either their type or list of accepted values.
<br/>
<br/>
_This documentation is auto generated from `attrs.xml` and should not be edited directly._

## Plot
TODO

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
__dimension__

### rangeStepMode
* subdivide
* increment_by_val
* increment_by_pixels

### rangeStep
__dimension__

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
__dimension__

### domainTitleWidth
__dimension__

### domainTitleHorizontalPositioning
* absolute_from_left
* absolute_from_right
* absolute_from_center
* relative_from_left
* relative_from_right
* relative_from_center

### domainTitleVerticalPositioning
* absolute_from_top
* absolute_from_bottom
* absolute_from_center
* relative_from_top
* relative_from_bottom
* relative_from_center

### domainTitleHorizontalPosition
__dimension__

### domainTitleVerticalPosition
__dimension__

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

### rangeTitleTextSize
__dimension__

### rangeTitleHeight
__dimension__

### rangeTitleWidth
__dimension__

### rangeTitleHorizontalPosition
__dimension__

### rangeTitleVerticalPosition
__dimension__

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

### graphHeightMode
* absolute
* relative
* fill

### graphWidthMode
* absolute
* relative
* fill

### graphHeight
__dimension__

### graphWidth
__dimension__

### graphHorizontalPosition
__dimension__

### graphVerticalPosition
__dimension__

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
__floatn__
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

## PieChart
TODO

