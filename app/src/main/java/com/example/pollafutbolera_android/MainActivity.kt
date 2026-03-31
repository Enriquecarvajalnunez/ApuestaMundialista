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
import com.example.pollafutbolera_android.ui.screen.SignInScreen
import com.example.pollafutbolera_android.ui.screen.StandingsScreen
import com.example.pollafutbolera_android.ui.screen.ViewBetsScreen
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.Scope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PollaFutbolera_AndroidTheme {
                val sheetsScope = Scope("https://www.googleapis.com/auth/spreadsheets.readonly")
                var isSignedIn by rememberSaveable {
                    val account = GoogleSignIn.getLastSignedInAccount(this)
                    mutableStateOf(
                        account != null && GoogleSignIn.hasPermissions(account, sheetsScope)
                    )
                }
                var showBetRegistration by rememberSaveable { mutableStateOf(false) }
                var showViewBets by rememberSaveable { mutableStateOf(false) }
                var showStandings by rememberSaveable { mutableStateOf(false) }
                var showAdminLogin by rememberSaveable { mutableStateOf(false) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        !isSignedIn -> SignInScreen(
                            modifier = Modifier.padding(innerPadding),
                            onSignedIn = { isSignedIn = true }
                        )
                        showBetRegistration -> BetRegistrationScreen(
                            onBack = { showBetRegistration = false }
                        )
                        showViewBets -> ViewBetsScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { showViewBets = false }
                        )
                        showStandings -> StandingsScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                        showAdminLogin -> AdminLoginScreen(
                            modifier = Modifier.padding(innerPadding)
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
