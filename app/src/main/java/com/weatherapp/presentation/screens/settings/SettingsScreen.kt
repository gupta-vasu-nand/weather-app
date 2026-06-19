package com.weatherapp.presentation.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.model.WindSpeedUnit
import com.weatherapp.presentation.theme.DarkTealPrimary
import com.weatherapp.presentation.theme.TealPrimary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showClearCacheDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val formattedCacheSize = remember(state.cacheSize) {
        formatFileSize(state.cacheSize)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.toggleNotifications(isGranted)
    }

    state.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
        ) {
            // Units Section
            item(key = "units") {
                SettingsSection(title = "Units") {
                    UnitSelector(
                        temperatureUnit = state.temperatureUnit,
                        windSpeedUnit = state.windSpeedUnit,
                        onTemperatureUnitChange = viewModel::updateTemperatureUnit,
                        onWindSpeedUnitChange = viewModel::updateWindSpeedUnit
                    )
                }
            }

            // Appearance Section
            item(key = "appearance") {
                SettingsSection(title = "Appearance") {
                    ThemeSelector(
                        selectedMode = state.themeMode, onModeSelected = viewModel::updateThemeMode
                    )
                }
            }

            // Notifications Section
            item(key = "notifications") {
                SettingsSection(title = "Notifications") {
                    NotificationRow(
                        enabled = state.notificationsEnabled, onToggle = { checked ->
                            if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        viewModel.toggleNotifications(true)
                                    }

                                    else -> {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                viewModel.toggleNotifications(checked)
                            }
                        })
                }
            }

            // Storage Section
            item(key = "storage") {
                SettingsSection(title = "Storage") {
                    StorageRow(
                        cacheSize = formattedCacheSize,
                        isLoading = state.isClearingCache,
                        onClearCache = { showClearCacheDialog = true })
                }
            }

            // About Section
            item(key = "about") {
                SettingsSection(title = "About") {
                    AboutCard()
                }
            }
        }
    }

    // Clear Cache Dialog
    if (showClearCacheDialog) {
        ClearCacheDialog(onDismiss = { showClearCacheDialog = false }, onConfirm = {
            viewModel.clearCache()
            showClearCacheDialog = false
        })
    }
}

@Composable
fun SettingsSection(
    title: String, content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TealPrimary,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            border = BorderStroke(
                1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun UnitSelector(
    temperatureUnit: TemperatureUnit,
    windSpeedUnit: WindSpeedUnit,
    onTemperatureUnitChange: (TemperatureUnit) -> Unit,
    onWindSpeedUnitChange: (WindSpeedUnit) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Temperature Unit
        Column {
            Text(
                text = "Temperature", style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ), modifier = Modifier.padding(bottom = 8.dp)
            )
            UnitToggleRow(
                options = listOf("Celsius", "Fahrenheit"),
                selectedIndex = if (temperatureUnit == TemperatureUnit.CELSIUS) 0 else 1,
                onOptionSelected = { index ->
                    onTemperatureUnitChange(
                        if (index == 0) TemperatureUnit.CELSIUS else TemperatureUnit.FAHRENHEIT
                    )
                })
        }

        // Wind Speed Unit
        Column {
            Text(
                text = "Wind Speed", style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ), modifier = Modifier.padding(bottom = 8.dp)
            )
            UnitToggleRow(
                options = listOf("km/h", "mph"),
                selectedIndex = if (windSpeedUnit == WindSpeedUnit.KPH) 0 else 1,
                onOptionSelected = { index ->
                    onWindSpeedUnitChange(
                        if (index == 0) WindSpeedUnit.KPH else WindSpeedUnit.MPH
                    )
                })
        }
    }
}

@Composable
private fun UnitToggleRow(
    options: List<String>, selectedIndex: Int, onOptionSelected: (Int) -> Unit
) {
    val gradient = remember { Brush.horizontalGradient(listOf(TealPrimary, DarkTealPrimary)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
            .border(
                1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape
            )
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .then(if (isSelected) Modifier.background(gradient) else Modifier)
                    .clickable { onOptionSelected(index) }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedMode: ThemeMode, onModeSelected: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ThemeOption(
            icon = Icons.Default.LightMode,
            label = "Light",
            isSelected = selectedMode == ThemeMode.LIGHT,
            modifier = Modifier.weight(1f)
        ) { onModeSelected(ThemeMode.LIGHT) }

        ThemeOption(
            icon = Icons.Default.DarkMode,
            label = "Dark",
            isSelected = selectedMode == ThemeMode.DARK,
            modifier = Modifier.weight(1f)
        ) { onModeSelected(ThemeMode.DARK) }

        ThemeOption(
            icon = Icons.Default.SettingsSuggest,
            label = "System",
            isSelected = selectedMode == ThemeMode.SYSTEM,
            modifier = Modifier.weight(1f)
        ) { onModeSelected(ThemeMode.SYSTEM) }
    }
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val gradient = remember { Brush.horizontalGradient(listOf(TealPrimary, DarkTealPrimary)) }

    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(if (isSelected) Modifier.background(gradient) else Modifier)
            .then(
                if (!isSelected) {
                    Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            BorderStroke(
                                1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                            ), RoundedCornerShape(20.dp)
                        )
                } else Modifier
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, tint = contentColor, modifier = Modifier.size(24.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
        )
    }
}

@Composable
private fun NotificationRow(
    enabled: Boolean, onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggle(!enabled) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = TealPrimary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "Daily Weather Alerts",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (enabled) "Receive morning weather updates" else "Notifications are disabled",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status Chip
        AssistChip(
            onClick = {},
            label = {
                Text(
                    text = if (enabled) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            modifier = Modifier.padding(end = 4.dp),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (enabled) {
                    TealPrimary.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                }, labelColor = if (enabled) {
                    TealPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            border = BorderStroke(
                0.5.dp, if (enabled) {
                    TealPrimary.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                }
            )
        )

        Switch(
            checked = enabled, onCheckedChange = onToggle, colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun StorageRow(
    cacheSize: String, isLoading: Boolean, onClearCache: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !isLoading) { onClearCache() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = "Clear Cached Data",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
            )
            Text(
                text = if (isLoading) "Clearing cache..." else "Free up storage space",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Cache Size Badge
        if (!isLoading) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = cacheSize, style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AboutCard() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Icon
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(24.dp),
            color = TealPrimary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.CloudQueue,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // App Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Weather App", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black
                )
            )
            Text(
                text = "Version 1.0.0", style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold
                )
            )
        }

        // Description
        Text(
            text = "A premium weather experience providing hyper-local forecasts and global city management with a modern interface.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        // Feature Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FeatureChip(
                    label = "Hyper-local Forecasts"
                )
            }
            item {
                FeatureChip(
                    label = "Global City Management"
                )
            }
            item {
                FeatureChip(
                    label = "Modern Interface"
                )
            }
            item {
                FeatureChip(
                    label = "No Ads"
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Powered By
        Surface(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://www.weatherapi.com/".toUri()
                }
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Powered by WeatherAPI.com",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FeatureChip(
    modifier: Modifier = Modifier, label: String
) {
    AssistChip(
        onClick = {}, label = {
            Text(
                text = label, style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp
                ), maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }, modifier = modifier, colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = MaterialTheme.colorScheme.onBackground
        ), border = BorderStroke(
            0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClearCacheDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Clear all data?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "This will permanently delete your search history, saved cities, and offline weather cache.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Clear Everything", style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Maybe later", style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        })
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(
        Locale.US, "%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups]
    )
}