package com.example.pollafutbolera_android.ui.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pollafutbolera_android.data.model.BetResult
import com.example.pollafutbolera_android.ui.theme.GoldBright
import com.example.pollafutbolera_android.ui.theme.GreenDark
import com.example.pollafutbolera_android.ui.theme.GreenLight
import com.example.pollafutbolera_android.ui.theme.GreenMid
import com.example.pollafutbolera_android.ui.theme.MintGreen
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.example.pollafutbolera_android.ui.viewmodel.ViewBetsViewModel

@Composable
fun ViewBetsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ViewBetsViewModel = viewModel()
) {
    var idNumber by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Ver apuestas realizadas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input + botón buscar
        OutlinedTextField(
            value = idNumber,
            onValueChange = { idNumber = it },
            label = { Text("Número de identificación") },
            placeholder = { Text("Ej: 12345678") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                keyboard?.hide()
                viewModel.searchBets(idNumber)
            }),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                keyboard?.hide()
                viewModel.searchBets(idNumber)
            },
            enabled = idNumber.isNotBlank() && uiState !is ViewBetsViewModel.UiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MintGreen,
                contentColor = GreenDark
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Consultar apuestas", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Contenido según estado
        when (val state = uiState) {
            is ViewBetsViewModel.UiState.Idle -> Unit

            is ViewBetsViewModel.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoldBright, modifier = Modifier.padding(32.dp))
                }
            }

            is ViewBetsViewModel.UiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            is ViewBetsViewModel.UiState.Success -> {
                if (state.bets.isEmpty()) {
                    Text(
                        text = "No se encontraron apuestas registradas para este jugador.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                } else {
                    Text(
                        text = "Jugador: ${state.playerName}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MintGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${state.bets.size} partido(s) registrado(s)",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldBright,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.bets) { bet ->
                            BetResultCard(bet)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BetResultCard(bet: BetResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenMid),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Grupo
            Text(
                text = bet.grupo,
                style = MaterialTheme.typography.labelMedium,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = GreenLight, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))
            // Marcador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bet.equipoA,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${bet.marcadorA}  -  ${bet.marcadorB}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MintGreen,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Text(
                    text = bet.equipoB,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewBetsScreenPreview() {
    PollaFutbolera_AndroidTheme {
        ViewBetsScreen()
    }
}
