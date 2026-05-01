package com.example.pollafutbolera_android.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pollafutbolera_android.data.model.BetEntry
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.example.pollafutbolera_android.ui.viewmodel.BetRegistrationViewModel

// ---------- Modelos ----------

data class Match(
    val id: Int,
    val teamA: String,
    val teamB: String
)

data class Group(
    val name: String,
    val teams: List<String>,
    val matches: List<Match>
)

// ---------- Datos ----------

val tournamentGroups = listOf(
    Group(
        name = "Grupo A",
        teams = listOf("México", "Sudáfrica", "Corea del Sur", "República Checa"),
        matches = listOf(
            Match(1, "México", "Sudáfrica"),
            Match(2, "Corea del Sur", "República Checa")
        )
    ),
    Group(
        name = "Grupo B",
        teams = listOf("Canadá", "Bosnia y Herzegovina", "Catar", "Suiza"),
        matches = listOf(
            Match(3,"Canadá","Bosnia y Herzegovina" ),
            Match(4,"Catar","Suiza")
        )
    ),
    Group(
        name = "Grupo C",
        teams = listOf("Brasil", "Marruecos", "Haití", "Escocia"),
        matches = listOf(
            Match(5,"Brasil","Marruecos"),
            Match(6,"Haití","Escocia")
        )
    ),
    Group(
        name = "Grupo D",
        teams = listOf("Estados Unidos", "Paraguay", "Australia", "Turquía"),
        matches = listOf(
            Match(7,"Estados Unidos","Paraguay"),
            Match(8,"Australia","Turquía")
        )
    ),
    Group(
        name = "Grupo E",
        teams = listOf("Alemania", "Curazao", "Costa de Marfil", "Ecuador"),
        matches = listOf(
            Match(9,"Alemania","Curazao"),
            Match(10,"Costa de Marfil","Ecuador")
        )
    ),
    Group(
        name = "Grupo F",
        teams = listOf("Países Bajos", "Japón", "Suecia", "Túnez"),
        matches = listOf(
            Match(11,"Países Bajos","Japón"),
            Match(12,"Suecia","Túnez")
        )
    ),
    Group(
        name = "Grupo G",
        teams = listOf("Bélgica", "Egipto", "Irán", "Nueva Zelanda"),
        matches = listOf(
            Match(13,"Bélgica","Egipto"),
            Match(14,"Irán","Nueva Zelanda")
        )
    ),
    Group(
        name = "Grupo H",
        teams = listOf("España", "Cabo Verde", "Arabia Saudita", "Uruguay"),
        matches = listOf(
            Match(15,"España","Cabo Verde"),
            Match(16,"Arabia Saudita","Uruguay")
        )
    ),
    Group(
        name = "Grupo I",
        teams = listOf("Francia", "Senegal", "Irak", "Noruega"),
        matches = listOf(
            Match(17,"Francia","Senegal"),
            Match(18,"Irak","Noruega")
        )
    ),
    Group(
        name = "Grupo J",
        teams = listOf("Argentina", "Argelia", "Austria", "Jordania"),
        matches = listOf(
            Match(19,"Argentina","Argelia"),
            Match(20,"Austria","Jordania")
        )
    ),
    Group(
        name = "Grupo K",
        teams = listOf("Portugal","RD Congo","Uzbekistán","Colombia"),
        matches = listOf(
            Match(21,"Portugal","RD Congo"),
            Match(22,"Uzbekistán","Colombia")
        )
    ),
    Group(
        name = "Grupo L",
        teams = listOf("Inglaterra","Croacia","Ghana","Panamá"),
        matches = listOf(
            Match(23,"Inglaterra","Croacia"),
            Match(24,"Ghana","Panamá")
        )
    )
)

// ---------- Pantalla principal ----------

@Composable
fun BetRegistrationScreen(
    onBack: () -> Unit = {},
    groups: List<Group> = tournamentGroups,
    viewModel: BetRegistrationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var nameReadOnly by remember { mutableStateOf(false) }
    val scores = remember { mutableStateMapOf<Int, Pair<String, String>>() }
    val expandedGroups = remember { mutableStateMapOf<String, Boolean>() }
    var idFieldFocused by remember { mutableStateOf(false) }

    val userDataComplete = fullName.isNotBlank() && idNumber.isNotBlank()

    val allMatches = groups.flatMap { it.matches }
    val allScoresFilled = allMatches.isNotEmpty() && allMatches.all { match ->
        val s = scores[match.id]
        s != null && s.first.isNotBlank() && s.second.isNotBlank()
    }

    val isLoading = uiState is BetRegistrationViewModel.UiState.Loading

    // Reaccionar cuando se encuentran apuestas existentes
    LaunchedEffect(uiState) {
        if (uiState is BetRegistrationViewModel.UiState.ExistingBetsFound) {
            val state = uiState as BetRegistrationViewModel.UiState.ExistingBetsFound
            fullName = state.playerName
            nameReadOnly = true
            val betsByKey = state.bets.associateBy { Triple(it.grupo, it.equipoA, it.equipoB) }
            groups.forEach { group ->
                group.matches.forEach { match ->
                    val key = Triple(group.name, match.teamA, match.teamB)
                    val existing = betsByKey[key]
                    if (existing != null) {
                        scores[match.id] = Pair(existing.marcadorA, existing.marcadorB)
                    }
                }
            }
        }
    }

    // Diálogo de éxito
    if (uiState is BetRegistrationViewModel.UiState.Success) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text("¡Apuesta registrada!") },
            text = { Text("Tu apuesta fue guardada exitosamente.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetState()
                    onBack()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de error
    if (uiState is BetRegistrationViewModel.UiState.Error) {
        val errorMsg = (uiState as BetRegistrationViewModel.UiState.Error).message
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text("Error al registrar") },
            text = { Text(errorMsg) },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetState()
                    onBack()
                }) {
                    Text("Reintentar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Encabezado
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Registro de Apuesta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Ingresa tus datos y predice los marcadores",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Datos personales
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Datos del participante",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre completo") },
                    placeholder = { Text("Ej: Juan Pérez García") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !nameReadOnly,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = {
                        idNumber = it.filter { c -> c.isDigit() }
                        if (nameReadOnly) {
                            nameReadOnly = false
                            scores.clear()
                        }
                    },
                    label = { Text("Número de identificación") },
                    placeholder = { Text("Ej: 1234567890") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            val wasFocused = idFieldFocused
                            idFieldFocused = focusState.isFocused
                            if (wasFocused && !focusState.isFocused && idNumber.length >= 6) {
                                viewModel.searchExistingBets(idNumber)
                            }
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Banner informativo cuando se cargan apuestas existentes
                if (uiState is BetRegistrationViewModel.UiState.ExistingBetsFound) {
                    val playerName = (uiState as BetRegistrationViewModel.UiState.ExistingBetsFound).playerName
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apuesta existente cargada para $playerName. Puedes modificarla y re-enviar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }

        // Grupos (visibles al completar datos personales)
        AnimatedVisibility(
            visible = userDataComplete,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Grupos del torneo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Toca un grupo para ingresar los marcadores",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                groups.forEach { group ->
                    val isExpanded = expandedGroups[group.name] == true
                val groupComplete = group.matches.isEmpty() || group.matches.all { match ->
                    val s = scores[match.id]
                    s != null && s.first.isNotBlank() && s.second.isNotBlank()
                }
                    GroupCard(
                        group = group,
                        isExpanded = isExpanded,
                        isComplete = groupComplete,
                        scores = scores,
                        onToggle = {
                            expandedGroups[group.name] = !isExpanded
                        },
                        onScoreChange = { matchId, scoreA, scoreB ->
                            scores[matchId] = Pair(scoreA, scoreB)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val bets = groups.flatMap { group ->
                            group.matches.mapNotNull { match ->
                                val s = scores[match.id] ?: return@mapNotNull null
                                BetEntry(
                                    grupo = group.name,
                                    equipoA = match.teamA,
                                    equipoB = match.teamB,
                                    marcadorA = s.first.toIntOrNull() ?: 0,
                                    marcadorB = s.second.toIntOrNull() ?: 0
                                )
                            }
                        }
                        viewModel.submit(fullName, idNumber, bets)
                    },
                    enabled = allScoresFilled && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Registrar Apuesta",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        } // cierre Column

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } // cierre Box
}

// ---------- Card de grupo expandible ----------

@Composable
private fun GroupCard(
    group: Group,
    isExpanded: Boolean,
    isComplete: Boolean,
    scores: Map<Int, Pair<String, String>>,
    onToggle: () -> Unit,
    onScoreChange: (Int, String, String) -> Unit
) {
    val arrowAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Cabecera del grupo (toca para expandir)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Indicador completado / pendiente
                    Icon(
                        imageVector = if (isComplete) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isComplete) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
            val nonEmptyTeams = group.teams.filter { it.isNotBlank() }
            val subtitle = when {
                nonEmptyTeams.isNotEmpty() && group.matches.isNotEmpty() -> "${group.matches.size} partidos · ${nonEmptyTeams.size} equipos"
                nonEmptyTeams.isNotEmpty() -> "${nonEmptyTeams.size} equipos"
                group.matches.isNotEmpty() -> "${group.matches.size} partidos"
                else -> "Equipos por confirmar"
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
                    }
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    modifier = Modifier.rotate(arrowAngle),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        // Equipos del grupo (visibles solo si hay equipos definidos)
        val nonEmptyTeams = group.teams.filter { it.isNotBlank() }
        if (nonEmptyTeams.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "Equipos del grupo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        nonEmptyTeams.forEach { team ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SportsSoccer,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = team,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

            // Partidos con campos de marcador (solo al expandir)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider()
                    Text(
                        text = "Marcadores",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    group.matches.forEach { match ->
                        val current = scores[match.id] ?: Pair("", "")
                        MatchScoreRow(
                            match = match,
                            scoreA = current.first,
                            scoreB = current.second,
                            onScoreAChange = { v -> onScoreChange(match.id, v, current.second) },
                            onScoreBChange = { v -> onScoreChange(match.id, current.first, v) }
                        )
                    }
                }
            }
        }
    }
}

// ---------- Fila de marcador de un partido ----------

@Composable
private fun MatchScoreRow(
    match: Match,
    scoreA: String,
    scoreB: String,
    onScoreAChange: (String) -> Unit,
    onScoreBChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Equipo A
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = match.teamA,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = scoreA,
                    onValueChange = { v -> onScoreAChange(v.filter { it.isDigit() }.take(2)) },
                    modifier = Modifier.width(70.dp),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "0",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            // VS
            Text(
                text = "VS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                textAlign = TextAlign.Center
            )

            // Equipo B
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = match.teamB,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = scoreB,
                    onValueChange = { v -> onScoreBChange(v.filter { it.isDigit() }.take(2)) },
                    modifier = Modifier.width(70.dp),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "0",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}

// ---------- Preview ----------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BetRegistrationScreenPreview() {
    PollaFutbolera_AndroidTheme {
        BetRegistrationScreen()
    }
}
