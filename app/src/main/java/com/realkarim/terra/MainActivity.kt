package com.realkarim.terra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.rememberNavBackStack
import com.realkarim.terra.navigation.TerraNavHost
import com.realkarim.terra.theme.TerraTheme
import com.realkarim.welcome.presentation.WelcomeRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = rememberNavBackStack(WelcomeRoute)
            TerraTheme(dynamicColor = false) {
                TerraNavHost(backStack = backStack)
            }
        }
    }
}
