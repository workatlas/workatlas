package com.example.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.example.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading: Boolean = false
    private var applyClickCount: Int = 0

    /**
     * Preloads an interstitial ad using the environment-specific Interstitial Ad Unit ID.
     */
    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isAdLoading) return

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context.applicationContext,
            BuildConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d(TAG, "AdMob Interstitial Ad loaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.w(TAG, "AdMob Interstitial Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Shows the Interstitial Ad immediately if loaded, calling [onAdClosed] when dismissed or if display fails.
     * If the ad is not currently ready, it safely calls [onAdClosed] immediately so the user workflow is never blocked.
     */
    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdClosed()
                }
            }
            ad.show(activity)
        } else {
            loadInterstitialAd(activity)
            onAdClosed()
        }
    }

    /**
     * Handles clicks on 'Apply on Official Site':
     * Triggers the Interstitial Ad on every 2nd click, executing [onProceed] right after the ad closes
     * (or immediately on odd clicks, or if the ad is not ready).
     */
    fun handleApplyClick(activity: Activity, onProceed: () -> Unit) {
        applyClickCount++
        if (applyClickCount % 2 == 0) {
            showInterstitialAd(activity, onProceed)
        } else {
            onProceed()
        }
    }

    /**
     * Helper for testing click counter behavior.
     */
    fun getApplyClickCount(): Int = applyClickCount

    fun resetApplyClickCounter() {
        applyClickCount = 0
    }
}

/**
 * Traverses context wrappers to find the enclosing Activity.
 */
fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}
