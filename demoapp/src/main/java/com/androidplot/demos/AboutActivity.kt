// SPDX-License-Identifier: Apache-2.0
package com.androidplot.demos

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import com.androidplot.demos.databinding.AboutBinding

/**
 * Shows the app and library version, a short description, useful links and the license.
 */
class AboutActivity : Activity() {

    private lateinit var binding: AboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.about_title)

        binding.aboutVersion.text = getString(R.string.about_version, versionName())

        binding.aboutWebsite.setOnClickListener { openUrl(WEBSITE_URL) }
        binding.aboutSource.setOnClickListener { openUrl(SOURCE_URL) }
        binding.aboutDocs.setOnClickListener { openUrl(DOCS_URL) }
        binding.aboutIssues.setOnClickListener { openUrl(ISSUES_URL) }
        binding.aboutLicense.setOnClickListener { openUrl(LICENSE_URL) }
    }

    /** The app's version name, which carries the Androidplot library version it was built with. */
    private fun versionName(): String {
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        return info.versionName ?: "?"
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val WEBSITE_URL = "https://androidplot.com"
        private const val SOURCE_URL = "https://github.com/halfhp/androidplot"
        private const val DOCS_URL = "https://github.com/halfhp/androidplot/blob/master/docs/index.md"
        private const val ISSUES_URL = "https://github.com/halfhp/androidplot/issues"
        private const val LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0"
    }
}
