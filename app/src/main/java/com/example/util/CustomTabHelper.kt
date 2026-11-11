package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.R

object CustomTabHelper {
    /**
     * Opens a URL inside an in-app Chrome Custom Tab overlay with an easy close button,
     * maintaining app context and bringing the user right back to the directory when closed.
     */
    fun openCustomTab(context: Context, url: String) {
        try {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(android.graphics.Color.parseColor("#0B132B"))
                .setNavigationBarColor(android.graphics.Color.parseColor("#0B132B"))
                .build()

            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(false)
                .build()

            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            // Fallback to normal VIEW intent if Chrome / Custom Tabs is unavailable
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Unable to open link: $url", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
