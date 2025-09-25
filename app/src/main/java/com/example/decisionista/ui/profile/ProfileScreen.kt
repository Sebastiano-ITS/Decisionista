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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep // NUOVA ICONA
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.GUEST_USER_IDENTIFIER
import com.example.decisionista.model.SavedDecision // Keep for future use if needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String,
    magicName: String,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    decisions: List<SavedDecision>, // Kept for API consistency
    onNavigateToResult: (SavedDecision) -> Unit, // Kept for API consistency
    isDarkModeActual: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    showLogoutDialog: Boolean,
    onShowLogoutDialogChange: (Boolean) -> Unit,
    showTermsDialog: Boolean,
    onShowTermsDialogChange: (Boolean) -> Unit,
    showResetDialog: Boolean,
    onShowResetDialogChange: (Boolean) -> Unit,
    onResetDataConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilo Utente") },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                    Column {
                        Text(
                            text = if (magicName.isNotBlank() && magicName != "Ospite") "Benvenuto/a, Nobile $magicName!" else "Benvenuto/a, Nobile Decisore!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) { // Non mostrare email per ospite
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
                title = "Tema",
                subtitle = "Abilita il tema scuro cosmico",
                isSwitch = true,
                switchValue = isDarkModeActual,
                onSwitchChange = onDarkModeChange
            )
            
            SettingsItem(
                icon = "📜",
                title = "Termini dell'Arcano",
                subtitle = "Leggi i patti e le condizioni",
                onClick = { onShowTermsDialogChange(true) }
            )

            // NUOVO: SettingsItem per Azzera Dati Utente
            if (userEmail.isNotBlank() && userEmail != GUEST_USER_IDENTIFIER) {
                SettingsItem(
                    icon = "🗑️", // Puoi usare anche un Icon(Icons.Filled.DeleteSweep, ...) se preferisci
                    title = "Azzera Dati Utente",
                    subtitle = "Resetta decisioni e conteggio Oracolo",
                    onClick = { onShowResetDialogChange(true) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilledTonalButton(
                onClick = { onShowLogoutDialogChange(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout Icon")
                Spacer(modifier = Modifier.width(12.dp))
                Text("Logout", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { onShowLogoutDialogChange(false) },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Conferma Fuga", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Sei certo di voler abbandonare queste terre incantate? Le tue gesta rimarranno leggenda.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onLogout()
                        onShowLogoutDialogChange(false)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sì, Fuggi")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onShowLogoutDialogChange(false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Resta")
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { onShowTermsDialogChange(false) },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Termini dell'Arcano", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall) },
            text = {
                Text(
                    "Benvenuto in Decisionista!\n\n" +
                    "Utilizzando questa applicazione, accetti di affidare le tue decisioni al Fato, " +
                    "all'Oracolo e ad altre entità mistiche. Non ci assumiamo responsabilità per " +
                    "scelte di vita che portano a ricchezza indicibile, fama improvvisa o incontri " +
                    "con unicorni.\n\n" +
                    "Ricorda: ogni decisione è un'avventura!\n\n" +
                    "- Il Team Decisionista",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onShowTermsDialogChange(false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ho Capito")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { onShowResetDialogChange(false) },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Azzerare Dati?", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Sei sicuro di voler azzerare la cronologia delle decisioni e il conteggio delle consultazioni dell'Oracolo? Questa azione è irreversibile.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetDataConfirm()
                        onShowResetDialogChange(false)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Azzera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onShowResetDialogChange(false) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Annulla")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: String, // Cambiato da Icon a String per flessibilità, puoi usare Emoji o un Composable Icon qui
    title: String,
    subtitle: String,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = if (isSwitch || onClick == null) { {} } else { onClick },
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
                // Se icon è un Emoji String
                Text(
                    text = icon, 
                    fontSize = 26.sp, 
                    modifier = Modifier.padding(end = 16.dp), 
                    color = MaterialTheme.colorScheme.secondary
                )
                // Se volessi usare un Icon Composable, dovresti modificare il parametro icon 
                // e la logica qui per accettare un ImageVector o un @Composable () -> Unit.
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
