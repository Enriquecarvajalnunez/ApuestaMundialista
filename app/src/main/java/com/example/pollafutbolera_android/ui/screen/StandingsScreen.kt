package com.example.pollafutbolera_android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pollafutbolera_android.data.model.RankingEntry
import com.example.pollafutbolera_android.ui.theme.BackgroundLight
import com.example.pollafutbolera_android.ui.theme.LimeGreen
import com.example.pollafutbolera_android.ui.theme.NavyBlue
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.example.pollafutbolera_android.ui.theme.SurfaceWhite
import com.example.pollafutbolera_android.ui.theme.TextPrimary
import com.example.pollafutbolera_android.ui.theme.TextSecondary
import com.example.pollafutbolera_android.ui.viewmodel.StandingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StandingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadRanking()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tabla de posiciones",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadRanking() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar",
                            tint = LimeGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is StandingsViewModel.UiState.Idle -> Unit

                is StandingsViewModel.UiState.Loading -> {
                    CircularProgressIndicator(
                        color = LimeGreen,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is StandingsViewModel.UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadRanking() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LimeGreen,
                                contentColor = NavyBlue
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reintentar", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                is StandingsViewModel.UiState.Success -> {
                    if (state.ranking.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aún no hay puntajes disponibles",
                                color = NavyBlue,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.ranking) { entry ->
                                RankingCard(entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingCard(entry: RankingEntry) {
    val posicionColor: Color = when (entry.posicion) {
        1 -> LimeGreen
        2 -> NavyBlue
        3 -> TextSecondary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posición
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = posicionColor.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${entry.posicion}",
                    fontWeight = FontWeight.Bold,
                    color = posicionColor,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Nombre
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.nombre,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Puntaje
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.puntos}",
                    fontWeight = FontWeight.Bold,
                    color = LimeGreen,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "pts",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StandingsScreenPreview() {
    PollaFutbolera_AndroidTheme {
        StandingsScreen()
    }
}
