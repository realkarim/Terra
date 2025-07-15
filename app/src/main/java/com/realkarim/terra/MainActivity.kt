package com.realkarim.terra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.realkarim.terra.theme.TerraTheme
import com.realkarim.welcome.presentation.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerraTheme {
                WelcomeScreen {}
            }
        }
    }
}