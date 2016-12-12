import com.androidplot.Plot;
import com.androidplot.Series;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by halfhp on 12/11/16.
 */
public abstract class SeriesAnimator<PlotType extends Plot, SeriesType extends Series> {

    private final Redrawer redrawer;
    private final List<SeriesType> seriesList;
    private final PlotType plot;

    protected SeriesAnimator(PlotType plot) {
        this.plot = plot;
        this.seriesList = new ArrayList<>();
        this.redrawer = new Redrawer(plot, 0, false);
    }

    public SeriesAnimator createAnimation(PlotType plot) {
        throw new UnsupportedOperationException("Not yet implemented.");
    };

    public SeriesAnimator addSeries(SeriesType series) {
        throw new UnsupportedOperationException("Not yet implemented.");
    };

    public SeriesAnimator setDuration(long durationMs) {
        throw new UnsupportedOperationException("Not yet implemented.");
    };

    public void start() {
        redrawer.start();
    }
}
