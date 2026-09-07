// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.util.TreeSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * Guards {@link DemoActivities#ALL} against drifting from the manifest.
 */
@RunWith(RobolectricTestRunner.class)
public class DemoActivitiesTest {

    @Test
    public void listMatchesManifest() throws Exception {
        Context ctx = RuntimeEnvironment.getApplication();
        PackageInfo info = ctx.getPackageManager()
                .getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

        // the debug manifest also merges in activities from dependencies (eg. LeakCanary);
        // only the demoapp's own activities are of interest here.
        TreeSet<String> manifest = new TreeSet<>();
        for (ActivityInfo activity : info.activities) {
            if (activity.name.startsWith(ctx.getPackageName() + ".")) {
                manifest.add(activity.name);
            }
        }

        TreeSet<String> listed = new TreeSet<>();
        for (Class<? extends Activity> cls : DemoActivities.ALL) {
            listed.add(cls.getName());
        }

        assertEquals("DemoActivities.ALL must list exactly the activities in AndroidManifest.xml",
                manifest, listed);
        assertEquals("DemoActivities.ALL contains a duplicate", DemoActivities.ALL.size(), listed.size());
    }
}
