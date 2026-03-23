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
import com.example.pollafutbolera_android.ui.screen.SheetScreen
import com.example.pollafutbolera_android.ui.screen.SignInScreen
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PollaFutbolera_AndroidTheme {
                var isSignedIn by rememberSaveable {
                    mutableStateOf(GoogleSignIn.getLastSignedInAccount(this) != null)
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isSignedIn) {
                        SheetScreen(modifier = Modifier.padding(innerPadding))
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
