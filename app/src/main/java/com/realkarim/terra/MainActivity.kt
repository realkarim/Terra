package com.realkarim.terra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation3.runtime.rememberNavBackStack
import com.realkarim.navigation.Navigator
import com.realkarim.terra.navigation.HandleNavigation
import com.realkarim.terra.navigation.TerraNavHost
import com.realkarim.terra.theme.TerraTheme
import com.realkarim.welcome.presentation.WelcomeRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val backStack = rememberNavBackStack(WelcomeRoute)
            TerraTheme {
                HandleNavigation(
                    navigator = navigator,
                    backStack = backStack,
                )
                TerraNavHost(backStack = backStack)
            }
        }
    }
}
