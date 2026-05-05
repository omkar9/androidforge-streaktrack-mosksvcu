package com.androidforge.streaktrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.androidforge.streaktrack.presentation.common.AdInterstitialManager
import com.androidforge.streaktrack.presentation.navigation.AppNavHost
import com.androidforge.streaktrack.presentation.ui.theme.StreakTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adInterstitialManager: AdInterstitialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StreakTrackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Provide the activity context for interstitial ads
        adInterstitialManager.showInterstitialAd(this)
    }
}