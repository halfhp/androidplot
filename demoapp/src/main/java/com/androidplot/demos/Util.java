package com.androidplot.demos;

import android.app.*;
import android.content.*;

/**
 * Created by halfhp on 10/29/16.
 */
public class Util {



    public ProgressDialog showLoadingDialog(Context context) {
        return ProgressDialog.show(context, "Loading", "Please wait...", true);
    }

}
