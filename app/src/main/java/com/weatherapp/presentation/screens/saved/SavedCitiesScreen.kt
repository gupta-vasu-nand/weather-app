package com.weatherapp.presentation.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.domain.model.City
import com.weatherapp.presentation.components.CityItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCitiesScreen(
    viewModel: SavedCitiesViewModel = hiltViewModel(),
    onCitySelected: (City) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        when {
            state.isLoading -> ModernLoadingState()
            state.cities.isEmpty() -> ModernEmptyState()
            else -> CitiesList(
                cities = state.cities,
                onCitySelected = onCitySelected,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ModernLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(strokeWidth = 4.dp)
        Spacer(Modifier.height(18.dp))
        Text(
            "Loading saved cities…",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ModernEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "No saved cities yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Search and save cities to quickly access weather updates.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CitiesList(
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    viewModel: SavedCitiesViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = cities,
            key = { it.id }
        ) { city ->
            CityItem(
                city = city,
                isSelected = city.isDefault,
                onCityClick = {
                    viewModel.setDefaultCity(city)
                    onCitySelected(city)
                },
                onFavoriteClick = { viewModel.toggleFavorite(city) },
                onDeleteClick = { viewModel.deleteCity(city) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedCitiesScreenPreview() {
    MaterialTheme {
        SavedCitiesScreen(
            onCitySelected = {},
            onBackClick = {}
        )
    }
}