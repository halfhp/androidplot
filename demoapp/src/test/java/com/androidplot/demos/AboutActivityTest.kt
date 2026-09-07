// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos

import android.content.Intent
import android.widget.TextView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class AboutActivityTest {

    private lateinit var controller: ActivityController<AboutActivity>
    private lateinit var activity: AboutActivity

    private val links = mapOf(
        R.id.aboutWebsite to "https://androidplot.com",
        R.id.aboutSource to "https://github.com/halfhp/androidplot",
        R.id.aboutDocs to "https://github.com/halfhp/androidplot/blob/master/docs/index.md",
        R.id.aboutIssues to "https://github.com/halfhp/androidplot/issues",
        R.id.aboutLicense to "https://www.apache.org/licenses/LICENSE-2.0",
    )

    @Before
    fun setUp() {
        controller = DemoAppTest.launch(AboutActivity::class.java)
        activity = controller.get()
    }

    @After
    fun tearDown() {
        DemoAppTest.finish(controller)
    }

    @Test
    fun showsTheVersion() {
        val versionName = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
        assertFalse(versionName.isNullOrBlank())
        val text = activity.findViewById<TextView>(R.id.aboutVersion).text.toString()
        assertEquals(activity.getString(R.string.about_version, versionName), text)
        assertTrue(text, text.contains(versionName!!))
    }

    @Test
    fun linkButtonsOpenTheirUrls() {
        assertNull(shadowOf(activity).nextStartedActivity)
        links.forEach { (id, url) ->
            activity.findViewById<TextView>(id).performClick()
            DemoAppTest.idle()
            val intent = shadowOf(activity).nextStartedActivity
            val name = activity.resources.getResourceEntryName(id)
            assertEquals(name, Intent.ACTION_VIEW, intent?.action)
            assertEquals(name, url, intent?.data?.toString())
        }
        assertNull(shadowOf(activity).nextStartedActivity)
    }
}
