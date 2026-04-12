package com.example.pollafutbolera_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.pollafutbolera_android.ui.screen.AdminLoginScreen
import com.example.pollafutbolera_android.ui.screen.BetRegistrationScreen
import com.example.pollafutbolera_android.ui.screen.HomeScreen
import com.example.pollafutbolera_android.ui.screen.SheetScreen
import com.example.pollafutbolera_android.ui.screen.StandingsScreen
import com.example.pollafutbolera_android.ui.screen.ViewBetsScreen
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PollaFutbolera_AndroidTheme {
                var showBetRegistration by rememberSaveable { mutableStateOf(false) }
                var showViewBets by rememberSaveable { mutableStateOf(false) }
                var showStandings by rememberSaveable { mutableStateOf(false) }
                var showAdminLogin by rememberSaveable { mutableStateOf(false) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        showBetRegistration -> BetRegistrationScreen(
                            onBack = { showBetRegistration = false }
                        )
                        showViewBets -> ViewBetsScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { showViewBets = false }
                        )
                        showStandings -> StandingsScreen(
                            onBack = { showStandings = false }
                        )
                        showAdminLogin -> AdminLoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { showAdminLogin = false }
                        )
                        else -> HomeScreen(
                            onRegisterBetsClick = { showBetRegistration = true },
                            onViewBetsClick = { showViewBets = true },
                            onStandingsClick = { showStandings = true },
                            onAdminLoginClick = { showAdminLogin = true }
                        )
                    }
                }
            }
        }
    }
}
