package com.weatherapp.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.weatherapp.presentation.components.BottomNavigationBar
import com.weatherapp.presentation.components.TopAppBar
import com.weatherapp.presentation.screens.home.HomeScreen
import com.weatherapp.presentation.screens.home.HomeViewModel
import com.weatherapp.presentation.screens.saved.SavedCitiesScreen
import com.weatherapp.presentation.screens.search.SearchScreen
import com.weatherapp.presentation.screens.settings.SettingsScreen
import com.weatherapp.utils.NetworkMonitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    networkMonitor: NetworkMonitor,
    startDestination: String
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topBarConfig = getTopBarConfig(currentRoute)
    val scrollBehavior = key(currentRoute) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                config = topBarConfig,
                onBackClick = { navController.navigateUp() },
                onSearchClick = { navController.navigate(NavRoutes.Search.route) },
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) },
                onSavedCitiesClick = { navController.navigate(NavRoutes.SavedCities.route) },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (currentRoute in listOf(
                    NavRoutes.Home.route,
                    NavRoutes.SavedCities.route,
                    NavRoutes.Settings.route
                )
            ) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(NavRoutes.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(NavRoutes.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onSearchClick = { navController.navigate(NavRoutes.Search.route) }
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
                    onCitySelected = {
                        navController.popBackStack()
                    },
                    onBackClick = { navController.navigateUp() },
                    onAddClick = { navController.navigate(NavRoutes.Search.route) }
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