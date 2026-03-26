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
import com.example.pollafutbolera_android.ui.screen.BetRegistrationScreen
import com.example.pollafutbolera_android.ui.screen.SheetScreen
import com.example.pollafutbolera_android.ui.screen.SignInScreen
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isSignedIn) {
                        BetRegistrationScreen()
                    } else {
                        SignInScreen(
                            modifier = Modifier.padding(innerPadding),
                            onSignedIn = { isSignedIn = true }
                        )
                    }
                }
            }
        }
    }
}
