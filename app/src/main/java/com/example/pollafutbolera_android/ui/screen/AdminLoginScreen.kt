package com.example.pollafutbolera_android.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pollafutbolera_android.ui.theme.BackgroundLight
import com.example.pollafutbolera_android.ui.theme.LimeGreen
import com.example.pollafutbolera_android.ui.theme.NavyBlue
import com.example.pollafutbolera_android.ui.theme.PollaFutbolera_AndroidTheme
import com.example.pollafutbolera_android.ui.theme.TextPrimary
import com.example.pollafutbolera_android.ui.theme.TextSecondary
import com.example.pollafutbolera_android.ui.viewmodel.AdminViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    adminViewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val rankingState by adminViewModel.rankingState.collectAsStateWithLifecycle()
    val empatesState by adminViewModel.empatesState.collectAsStateWithLifecycle()
    val allowedAdmins by adminViewModel.allowedAdmins.collectAsStateWithLifecycle()

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/spreadsheets"))
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    var isSignedIn by remember {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        mutableStateOf(account != null && adminViewModel.isEmailAllowed(account.email ?: ""))
    }
    var signedInEmail by remember {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        mutableStateOf(account?.email ?: "")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAdminRegistration by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email ?: ""
            if (adminViewModel.isEmailAllowed(email)) {
                isSignedIn = true
                signedInEmail = email
                errorMessage = null
            } else {
                googleSignInClient.signOut()
                errorMessage = "El correo $email no tiene permisos de administrador."
            }
        } catch (e: ApiException) {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null && adminViewModel.isEmailAllowed(account.email ?: "")) {
                isSignedIn = true
                signedInEmail = account.email ?: ""
            } else {
                errorMessage = "Error al iniciar sesión (código ${e.statusCode})"
            }
        }
    }

    if (showAdminRegistration) {
        AdminRegistrationDialog(
            admins = allowedAdmins,
            defaultAdmins = adminViewModel.defaultAdmins,
            onAdd = { adminViewModel.addAdmin(it) },
            onRemove = { adminViewModel.removeAdmin(it) },
            onDismiss = { showAdminRegistration = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Administración",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isSignedIn) Arrangement.Top else Arrangement.Center
        ) {
            if (isSignedIn) {
                // — Panel de administrador —
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = LimeGreen,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sesión activa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = signedInEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // Botón Calcular Ranking
                Button(
                    onClick = { adminViewModel.calcularRanking() },
                    enabled = rankingState !is AdminViewModel.RankingState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeGreen,
                        contentColor = NavyBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Calcular Ranking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Registrar Administradores
                OutlinedButton(
                    onClick = { showAdminRegistration = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Registrar Administradores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Buscar Empates
                OutlinedButton(
                    onClick = { adminViewModel.buscarEmpates() },
                    enabled = empatesState !is AdminViewModel.EmpatesState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Buscar Empates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Resolver Empates
                OutlinedButton(
                    onClick = { adminViewModel.resolverEmpates() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Resolver Empates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estado de búsqueda de empates
                when (val state = empatesState) {
                    is AdminViewModel.EmpatesState.Loading -> {
                        CircularProgressIndicator(color = LimeGreen)
                    }
                    is AdminViewModel.EmpatesState.Success -> {
                        Text(
                            text = if (state.totalGrupos == 0)
                                "No se encontraron empates en el ranking"
                            else
                                "Se encontraron ${state.totalGrupos} grupo(s) de empate con ${state.totalJugadores} jugador(es). Copiados a la hoja \"Empates\".",
                            color = NavyBlue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    is AdminViewModel.EmpatesState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estado del cálculo de ranking
                when (val state = rankingState) {
                    is AdminViewModel.RankingState.Loading -> {
                        CircularProgressIndicator(color = LimeGreen)
                    }
                    is AdminViewModel.RankingState.Success -> {
                        Text(
                            text = "Ranking calculado para ${state.totalJugadores} jugador(es)",
                            color = NavyBlue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    is AdminViewModel.RankingState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.weight(1f))

                // Cerrar sesión al fondo
                OutlinedButton(
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            isSignedIn = false
                            signedInEmail = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cerrar sesión",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // — Pantalla de login —
                Icon(
                    imageVector = Icons.Filled.AdminPanelSettings,
                    contentDescription = null,
                    tint = NavyBlue,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Login de administración",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inicia sesión con tu cuenta de Google para acceder al panel de administración",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { launcher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeGreen,
                        contentColor = NavyBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Iniciar sesión con Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminRegistrationDialog(
    admins: List<String>,
    defaultAdmins: Set<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newEmail by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundLight,
        title = {
            Text(
                text = "Administradores permitidos",
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        },
        text = {
            Column {
                Text(
                    text = "Correos con acceso al panel de administración:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(admins) { email ->
                        val isDefault = defaultAdmins.contains(email.lowercase())
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NavyBlue,
                                modifier = Modifier.weight(1f)
                            )
                            if (!isDefault) {
                                IconButton(
                                    onClick = { onRemove(email) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(32.dp))
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = {
                        newEmail = it
                        emailError = null
                    },
                    label = { Text("Nuevo correo") },
                    placeholder = { Text("ejemplo@gmail.com") },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyBlue,
                        focusedLabelColor = NavyBlue,
                        cursorColor = NavyBlue
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val trimmed = newEmail.trim()
                        if (trimmed.isBlank()) {
                            emailError = "Ingresa un correo válido"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                            emailError = "Formato de correo inválido"
                        } else if (admins.any { it.equals(trimmed, ignoreCase = true) }) {
                            emailError = "Este correo ya está registrado"
                        } else {
                            onAdd(trimmed)
                            newEmail = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeGreen,
                        contentColor = NavyBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Agregar", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = NavyBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminLoginScreenPreview() {
    PollaFutbolera_AndroidTheme {
        AdminLoginScreen(onBack = {})
    }
}
