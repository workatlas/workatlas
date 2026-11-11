package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppNavTab
import com.example.reminder.DailyJobReminderWorker
import com.example.ui.MainWorkAtlasScreen
import com.example.ui.WorkAtlasViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AdMobManager
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    private val workAtlasViewModel: WorkAtlasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()

        // Initialize Google Mobile Ads SDK and preload Interstitial Ad
        MobileAds.initialize(this) {
            AdMobManager.loadInterstitialAd(this)
        }

        setContent {
            MyApplicationTheme {
                MainWorkAtlasScreen(viewModel = workAtlasViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val targetTab = intent?.getStringExtra(DailyJobReminderWorker.EXTRA_TARGET_TAB)
        if (targetTab == AppNavTab.JOBS.name || targetTab == "JOBS") {
            workAtlasViewModel.selectTab(AppNavTab.JOBS)
        }
    }
}
