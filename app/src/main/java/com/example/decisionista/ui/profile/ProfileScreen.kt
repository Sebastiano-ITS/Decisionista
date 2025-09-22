package com.example.decisionista.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.SavedDecision // Keep for future use if needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    decisions: List<SavedDecision>, // Kept for API consistency, not used in current layout
    onNavigateToResult: (SavedDecision) -> Unit, // Kept for API consistency
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    // Local states for switches - actual theme/sound logic is external
    var isDarkMode by remember { mutableStateOf(false) } // Example state
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👤 Profilo Utente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Updated
                    titleContentColor = MaterialTheme.colorScheme.primary, // Updated
                    navigationIconContentColor = MaterialTheme.colorScheme.primary // Updated
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
                .verticalScroll(rememberScrollState()) // Added scroll for potentially long settings
        ) {
            // User Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧙", // Wizard emoji
                        fontSize = 40.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            text = "Benvenuto, Nobile Decisore!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (userEmail.isNotBlank()) {
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "⚙️ Impostazioni Incantate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp, top = 16.dp)
            )

            SettingsItem(
                icon = "🎨",
                title = "Modalità Notte Stellata",
                subtitle = "Abilita il tema scuro cosmico",
                isSwitch = true,
                switchValue = isDarkMode,
                onSwitchChange = { isDarkMode = it } // Note: This only changes local UI state
            )

            SettingsItem(
                icon = "🔊",
                title = "Sussurri Ancestrali",
                subtitle = "Attiva gli effetti sonori",
                isSwitch = true,
                switchValue = soundEnabled,
                onSwitchChange = { soundEnabled = it }
            )

            SettingsItem(
                icon = "📳",
                title = "Aure Mistiche",
                subtitle = "Feedback con vibrazione",
                isSwitch = true,
                switchValue = vibrationsEnabled,
                onSwitchChange = { vibrationsEnabled = it }
            )

            SettingsItem(
                icon = "👤",
                title = "Personalizza Avatar Magico",
                subtitle = "Modifica l'aspetto del tuo alter ego",
                onClick = { /* TODO: Implement avatar customization screen */ }
            )
            
            SettingsItem(
                icon = "📜",
                title = "Termini dell'Arcano",
                subtitle = "Leggi i patti e le condizioni",
                onClick = { /* TODO: Navigate to Terms & Conditions screen */ }
            )

            SettingsItem(
                icon = "🔒",
                title = "Privacy Mistica",
                subtitle = "Gestisci le tue preferenze",
                onClick = { /* TODO: Navigate to Privacy Policy screen */ }
            )

            Spacer(modifier = Modifier.height(24.dp)) // Spacer before logout button

            FilledTonalButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = "Logout Icon")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Logout", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp)) // Bottom spacer
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Conferma Fuga", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Sei certo di voler abbandonare queste terre incantate? Le tue gesta rimarranno leggenda.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onLogout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sì, Fuggi")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Resta")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: String, // Emoji or could be ImageVector
    title: String,
    subtitle: String,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: (() -> Unit)? = null // Made onClick nullable
) {
    Card(
        onClick = if (isSwitch || onClick == null) { {} } else { onClick }, // Clickable only if not switch and onClick is provided
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    text = icon,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(end = 16.dp),
                    color = MaterialTheme.colorScheme.secondary // Thematic color for icon
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isSwitch) {
                Switch(
                    checked = switchValue,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        checkedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            } else if (onClick != null) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Vai a $title",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
