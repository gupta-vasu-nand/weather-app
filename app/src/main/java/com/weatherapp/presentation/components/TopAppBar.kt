package com.weatherapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weatherapp.R
import com.weatherapp.presentation.navigation.TopBarConfig
import com.weatherapp.presentation.theme.WeatherAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    config: TopBarConfig,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSavedCitiesClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    if (config.isLarge) {
        LargeTopAppBar(
            title = {
                Text(
                    text = config.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                if (config.showBack) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            },
            actions = {
                TopAppBarActions(
                    config = config,
                    onSearchClick = onSearchClick,
                    onSettingsClick = onSettingsClick,
                    onSavedCitiesClick = onSavedCitiesClick
                )
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = modifier
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = config.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                if (config.showBack) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            },
            actions = {
                TopAppBarActions(
                    config = config,
                    onSearchClick = onSearchClick,
                    onSettingsClick = onSettingsClick,
                    onSavedCitiesClick = onSavedCitiesClick
                )
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = modifier
        )
    }
}

@Composable
private fun TopAppBarActions(
    config: TopBarConfig,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSavedCitiesClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (config.showSavedCities) {
            IconButton(onClick = onSavedCitiesClick) {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = "Saved Cities",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (config.showSearch) {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (config.showSettings) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.cd_settings),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopAppBarPreview() {
    WeatherAppTheme {
        TopAppBar(
            config = TopBarConfig(
                title = "Weather",
                showSearch = true,
                showSettings = true,
                showSavedCities = true,
                isLarge = true
            ),
            onBackClick = {},
            onSearchClick = {},
            onSettingsClick = {},
            onSavedCitiesClick = {}
        )
    }
}