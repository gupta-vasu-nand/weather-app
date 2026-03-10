package com.weatherapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.weatherapp.presentation.components.TopAppBar
import com.weatherapp.presentation.screens.home.HomeScreen
import com.weatherapp.presentation.screens.home.HomeViewModel
import com.weatherapp.presentation.screens.saved.SavedCitiesScreen
import com.weatherapp.presentation.screens.search.SearchScreen
import com.weatherapp.presentation.screens.settings.SettingsScreen
import com.weatherapp.utils.NetworkMonitor

@Composable
fun NavGraph(
    navController: NavHostController,
    networkMonitor: NetworkMonitor,
    startDestination: String
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topBarConfig = getTopBarConfig(currentRoute)

    Scaffold(
        topBar = {
            TopAppBar(
                config = topBarConfig,
                onBackClick = { navController.navigateUp() },
                onSearchClick = { navController.navigate(NavRoutes.Search.route) },
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavRoutes.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onSearchClick = { navController.navigate(NavRoutes.Search.route) },
                    onSettingsClick = { navController.navigate(NavRoutes.Settings.route) },
                    onCitySelected = { cityName ->
                        // Handle city selection
                    }
                )
            }

            composable(NavRoutes.Search.route) {
                SearchScreen(
                    onCitySelected = { city ->
                        navController.popBackStack()
                        // Navigate to home with selected city
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(NavRoutes.SavedCities.route) {
                SavedCitiesScreen(
                    onCitySelected = { city ->
                        navController.popBackStack()
                        // Navigate to home with selected city
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(NavRoutes.Settings.route) {
                SettingsScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}