package com.realkarim.terra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.realkarim.navigation.Navigator
import com.realkarim.terra.navigation.HandleNavigation
import com.realkarim.terra.navigation.TerraNavHost
import com.realkarim.terra.theme.TerraTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            TerraTheme {
                HandleNavigation(
                    navigator = navigator,
                    navController = navController,
                )
                TerraNavHost(navController = navController)
            }
        }
    }
}