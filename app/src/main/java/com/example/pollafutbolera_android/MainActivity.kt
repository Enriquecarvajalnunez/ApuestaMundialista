package com.example.pollafutbolera_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.pollafutbolera_android.ui.screen.AdminLoginScreen
import com.example.pollafutbolera_android.ui.screen.BetRegistrationScreen
import com.example.pollafutbolera_android.ui.screen.HomeScreen
import com.example.pollafutbolera_android.ui.screen.StandingsScreen
import com.example.pollafutbolera_android.ui.screen.ViewBetsScreen
import com.example.pollafutbolera_android.ui.theme.BackgroundLight
import com.example.pollafutbolera_android.ui.theme.LimeGreen
import com.example.pollafutbolera_android.ui.theme.NavyBlue
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.example.pollafutbolera_android.ui.theme.TextPrimary
import com.example.pollafutbolera_android.ui.theme.TextSecondary

enum class AppScreen { Home, Standings, AdminLogin }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PollaFutbolera_AndroidTheme {
                var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Home) }
                var showBetRegistration by rememberSaveable { mutableStateOf(false) }
                var showViewBets by rememberSaveable { mutableStateOf(false) }

                val inSubScreen = showBetRegistration || showViewBets

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!inSubScreen) {
                            NavigationBar(containerColor = NavyBlue) {
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.Home,
                                    onClick = { currentScreen = AppScreen.Home },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Home,
                                            contentDescription = "Inicio"
                                        )
                                    },
                                    label = {
                                        Text("Inicio", fontWeight = FontWeight.SemiBold)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyBlue,
                                        selectedTextColor = LimeGreen,
                                        indicatorColor = LimeGreen,
                                        unselectedIconColor = TextPrimary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.Standings,
                                    onClick = { currentScreen = AppScreen.Standings },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.EmojiEvents,
                                            contentDescription = "Tabla de posiciones"
                                        )
                                    },
                                    label = {
                                        Text("Posiciones", fontWeight = FontWeight.SemiBold)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyBlue,
                                        selectedTextColor = LimeGreen,
                                        indicatorColor = LimeGreen,
                                        unselectedIconColor = TextPrimary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.AdminLogin,
                                    onClick = { currentScreen = AppScreen.AdminLogin },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.AdminPanelSettings,
                                            contentDescription = "Administración"
                                        )
                                    },
                                    label = {
                                        Text("Admin", fontWeight = FontWeight.SemiBold)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyBlue,
                                        selectedTextColor = LimeGreen,
                                        indicatorColor = LimeGreen,
                                        unselectedIconColor = TextPrimary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when {
                        showBetRegistration -> BetRegistrationScreen(
                            onBack = { showBetRegistration = false }
                        )
                        showViewBets -> ViewBetsScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { showViewBets = false }
                        )
                        else -> when (currentScreen) {
                            AppScreen.Home -> HomeScreen(
                                onRegisterBetsClick = { showBetRegistration = true },
                                onViewBetsClick = { showViewBets = true },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.Standings -> StandingsScreen(
                                onBack = { currentScreen = AppScreen.Home }
                            )
                            AppScreen.AdminLogin -> AdminLoginScreen(
                                onBack = { currentScreen = AppScreen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
