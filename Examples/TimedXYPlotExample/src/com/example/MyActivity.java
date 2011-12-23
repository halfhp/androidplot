package com.example;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.util.FontUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.FillDirection;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class MyActivity extends Activity {

    // redraws a plot whenever an update is received:
    private class MyPlotUpdater implements Observer {
        Plot mPlot;

        // Timing info.
        int mFrameNumber;
        long mStartTime;

        public MyPlotUpdater(Plot plot) {
            this.mPlot = plot;
            Reset();
        }
        public void Reset() {
            mFrameNumber = 0;
            mStartTime = 0L;
            mFps = 0.0f;
            mAverageTime = 0;
            // Log.d(TAG, "Reset()");
        }

        @Override
        public void update(Observable o, Object arg) {
            try {
                // Log.d(TAG, "update");
                mPlot.postRedraw();
                if (mStartTime == 0) {
                    mStartTime = SystemClock.elapsedRealtime();
                } else {
                    long NowTime = SystemClock.elapsedRealtime();
                    mFrameNumber++;
                    long TotalTime = (NowTime - mStartTime);
                    mFps = ((float) mFrameNumber * 1000) / ((float) (TotalTime));
                    mAverageTime = (int) (TotalTime / mFrameNumber);

                    if ((mFrameNumber & 7) == 0) {
                        MyActivity.this.runOnUiThread(ui_updater);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace(); // To change body of catch statement use
                                     // File | Settings | File Templates.
            }
        }
    }

    public static final String TAG = "AndroidPlot";
    public static final int MAX_SERIES = 100;
    private static final float FONT_SIZE = 12.0f;
    // UI
    private Button mSettings;
    private TextView mFpsText;
    private TextView mTimeText;

    private float mFps;
    private int mAverageTime;

    // Plot
    private XYPlot mDynamicPlot;
    private MyPlotUpdater mPlotUpdater;
    private SampleDynamicXYDatasource mDynamicData;
    private Thread mMyThread;
    SampleDynamicSeries mMySeries[];
    LineAndPointFormatter mMyFormater[];

    // things we can configure
    private int mSeriesCount;
    private int mPointCount;
    private boolean mSinOn;
    private boolean mKeyOn;
    private boolean mRangeLabelOn;
    private boolean mRangeAxisOn;
    private boolean mDomainLabelOn;
    private boolean mDomainAxisOn;
    private boolean mGridOn;
    private boolean mBoarderOn;
    private boolean mFillOn;
    private boolean mVertexOn;
    private boolean mLineOn;
    private boolean mTitleOn;
    private boolean mBackgroundOn;
    
    // axis control
    private boolean mRangeAxisLeft;
    private boolean mRangeAxisOverlay;
    private boolean mDomainAxisBottom;
    private boolean mDomainAxisOverlay;

    private boolean mDonePrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // android boilerplate stuff
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);

        mDonePrefs = false;

        // UI
        mSettings = (Button) findViewById(R.id.prefs);
        mFpsText = (TextView) findViewById(R.id.text_fps);
        mTimeText = (TextView) findViewById(R.id.text_time);

        mSettings.setOnClickListener(m_on_click_settings_listener);

        // get handles to our View defined in layout.xml:
        mDynamicPlot = (XYPlot) findViewById(R.id.dynamicPlot);

        mPlotUpdater = new MyPlotUpdater(mDynamicPlot);
        // getInstance and position datasets:
        mDynamicData = new SampleDynamicXYDatasource();

        mMySeries = new SampleDynamicSeries[MAX_SERIES];
        mMyFormater = new LineAndPointFormatter[MAX_SERIES];
    }

    public void addSeries() {
        Random r = new Random();
        for (int i = 0; i < MAX_SERIES; i++) {
            // remove old
            if (mMySeries[i] != null) {
                mDynamicPlot.removeSeries(mMySeries[i]);
                mMySeries[i] = null;
                Log.v(TAG, "RemoveSeries " + i);
            }
            // add new
            if (i < mSeriesCount) {

                mMySeries[i] = new SampleDynamicSeries(mDynamicData, i, "Series "
                        + i);
                mMyFormater[i] = new LineAndPointFormatter(Color.rgb(
                        r.nextInt(256), r.nextInt(256), r.nextInt(256)),
                        Color.rgb(r.nextInt(256), r.nextInt(256),
                                r.nextInt(256)), Color.argb(128,
                                r.nextInt(256), r.nextInt(256), r.nextInt(256)));

                mMyFormater[i].setFillDirection(FillDirection.RANGE_ORIGIN);
                if (!mFillOn)
                    mMyFormater[i].setFillPaint(null);
                if (!mVertexOn)
                    mMyFormater[i].setVertexPaint(null);
                if (!mLineOn)
                    mMyFormater[i].setLinePaint(null);

                mDynamicPlot.addSeries(mMySeries[i], mMyFormater[i]);
                Log.v(TAG, "AddSeries " + i);
            }
        }
    }

    public void setupPlot() {
        // only display whole numbers in domain labels
        mDynamicPlot.getGraphWidget().setDomainValueFormat(
                new DecimalFormat("#"));

        mDynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        mDynamicPlot.setDomainStepValue(20);

        mDynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        mDynamicPlot.setRangeStepValue(20);

        mDynamicPlot.disableAllMarkup();

        // freeze the range boundaries:
        mDynamicPlot.setUserRangeOrigin(0);
        mDynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);

        if (!mBackgroundOn) {
            // remove the background stuff.
            mDynamicPlot.setBackgroundPaint(null);
            mDynamicPlot.getGraphWidget().setBackgroundPaint(null);
            mDynamicPlot.getGraphWidget().setGridBackgroundPaint(null);
        }

        if (!mKeyOn)
            mDynamicPlot.getLayoutManager()
                    .remove(mDynamicPlot.getLegendWidget());
        if (!mDomainLabelOn)
            mDynamicPlot.getLayoutManager().remove(
                    mDynamicPlot.getDomainLabelWidget());
        if (!mDomainAxisOn) {
            mDynamicPlot.getGraphWidget().setDomainLabelPaint(null);
            mDynamicPlot.getGraphWidget().setDomainOriginLabelPaint(null);
        }
        if (!mBoarderOn)
            mDynamicPlot.setDrawBorderEnabled(false);
        if (!mRangeLabelOn)
            mDynamicPlot.getLayoutManager().remove(
                    mDynamicPlot.getRangeLabelWidget());
        if (!mRangeAxisOn) {
            mDynamicPlot.getGraphWidget().setRangeLabelPaint(null);
            mDynamicPlot.getGraphWidget().setRangeOriginLabelPaint(null);
        }
        if (!mGridOn) {
            mDynamicPlot.getGraphWidget().setGridLinePaint(null);
            mDynamicPlot.getGraphWidget().setDomainOriginLinePaint(null);
            mDynamicPlot.getGraphWidget().setRangeOriginLinePaint(null);
        }
        if (!mTitleOn) 
            mDynamicPlot.getLayoutManager().remove(mDynamicPlot.getTitleWidget());

        // mDynamicPlot.setPlotMargins(-20, 0, 0, 0);
        mDynamicPlot.setPlotPadding(0, 0, 0, 0);
        mDynamicPlot.getGraphWidget().setPadding(0, 0, 0, 0);
        mDynamicPlot.getGraphWidget().setMargins(0, 0, 0, 0);

    }

    public void setupAxis() {
        mDynamicPlot.getGraphWidget()
                .setRangeValueFormat(new DecimalFormat("#"));
        
        mDynamicPlot.getGraphWidget().setRangeAxisPosition(mRangeAxisLeft, mRangeAxisOverlay, 5, "-200"); 
        mDynamicPlot.getGraphWidget().setDomainAxisPosition(mDomainAxisBottom, mDomainAxisOverlay, 5, "200"); 
    }
    public void getPrefs() {
        SharedPreferences appPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mPointCount = (Integer.valueOf(appPreferences.getString(
                getString(R.string.key_points), "200")));
        mSeriesCount = (Integer.valueOf(appPreferences.getString(
                getString(R.string.key_series), "2")));
        mSinOn = appPreferences.getBoolean(getString(R.string.key_sin_on), true);
        mKeyOn = appPreferences.getBoolean(getString(R.string.key_key_on), true);
        mRangeLabelOn = appPreferences.getBoolean(
                getString(R.string.key_range_label_on), true);
        mRangeAxisOn = appPreferences.getBoolean(
                getString(R.string.key_range_axis_on), true);
        mDomainLabelOn = appPreferences.getBoolean(
                getString(R.string.key_domain_label_on), true);
        mDomainAxisOn = appPreferences.getBoolean(
                getString(R.string.key_domain_axis_on), true);
        mGridOn = appPreferences.getBoolean(getString(R.string.key_grid_on),
                true);
        mBoarderOn = appPreferences.getBoolean(
                getString(R.string.key_boarder_on), true);
        mFillOn = appPreferences.getBoolean(getString(R.string.key_fill_on),
                true);
        mVertexOn = appPreferences.getBoolean(getString(R.string.key_vertex_on),
                true);
        mLineOn = appPreferences.getBoolean(getString(R.string.key_line_on),
                true);
        mTitleOn = appPreferences.getBoolean(getString(R.string.key_title_on),
                true);
        mBackgroundOn = appPreferences.getBoolean(
                getString(R.string.key_background_on), true);
        

        mRangeAxisLeft = appPreferences.getBoolean(getString(R.string.key_range_axis_left),
                true); 
        mRangeAxisOverlay = appPreferences.getBoolean(getString(R.string.key_range_axis_overlay),
                false);
        mDomainAxisBottom = appPreferences.getBoolean(getString(R.string.key_domain_axis_bottom),
                true);
        mDomainAxisOverlay = appPreferences.getBoolean(getString(R.string.key_domain_axis_overlay),
                false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        // hook up the plotUpdater to the data model:
        mDynamicData.removeObserver(mPlotUpdater);
        mDynamicData.setExit();
        mMyThread = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Ifor this is rather hacky.
        // I can not work out how to switch everything back on. So if the
        // preferences may have changed I force a restart.
        if (mDonePrefs) {
            Log.d(TAG, "Restarting");
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(
                            getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else {
            getPrefs();
            mDynamicData.setPoints(mPointCount);
            // Log.d(TAG, "SinOn :" + SinOn);

            mDynamicData.setSinOn(mSinOn);
            addSeries();
            setupPlot();
            setupAxis();
            // hook up the plotUpdater to the data model:
            mDynamicData.addObserver(mPlotUpdater);
            mPlotUpdater.Reset();
            mMyThread = new Thread(mDynamicData);
            mMyThread.start();
        }
    }

    // OnClickListener implementation for bringing buttons to front and back.
    private OnClickListener m_on_click_settings_listener = new OnClickListener() {
        public void onClick(View v) {
            mDonePrefs = true;
            Intent launchPreferencesIntent = new Intent().setClass(
                    MyActivity.this, PreferencesFromXml.class);
            MyActivity.this.startActivity(launchPreferencesIntent);
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mDonePrefs = true;
        Intent launchPreferencesIntent = new Intent().setClass(MyActivity.this,
                PreferencesFromXml.class);
        MyActivity.this.startActivity(launchPreferencesIntent);
        return true;
    }

    public Runnable ui_updater = new Runnable() {
        public void run() {
            mFpsText.setText(String.format("%3.1f", mFps));
            mTimeText.setText("" + mAverageTime);
        }
    };
}
