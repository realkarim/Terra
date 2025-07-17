package com.realkarim.terra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.realkarim.terra.navigation.HandleNavigation
import com.realkarim.terra.navigation.Navigator
import com.realkarim.terra.navigation.TerraNavHost
import com.realkarim.terra.theme.TerraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            TerraTheme {
                HandleNavigation(
                    navigator = Navigator(),
                    navController = navController,
                )
                TerraNavHost(navController = navController)
            }
        }
    }
}