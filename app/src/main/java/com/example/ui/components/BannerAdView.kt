package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig
import com.example.ui.theme.AtlasNavy
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val adWidth = configuration.screenWidthDp.coerceAtLeast(320)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(AtlasNavy)
            .testTag("sticky_banner_ad_container"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                            context,
                            adWidth
                        )
                    )
                    adUnitId = BuildConfig.BANNER_AD_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
