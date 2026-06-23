package com.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.presentation.navigation.NavGraph
import com.weatherapp.presentation.theme.WeatherAppTheme
import com.weatherapp.update.VVAppUpdateManager
import com.weatherapp.utils.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode != RESULT_OK) {
            Log.d(
                "MainActivity",
                "Update flow failed! Result code: ${it.resultCode}"
            )
        }
    }

    private val inVVAppUpdateManager by lazy {
        VVAppUpdateManager(this, updateLauncher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        inVVAppUpdateManager.checkForUpdates()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()
            
            val useDarkTheme = when(themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            WeatherAppTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp(networkMonitor = networkMonitor)
                }
            }
        }
    }
}

@Composable
fun WeatherApp(networkMonitor: NetworkMonitor) {
    val navController = rememberNavController()

    NavGraph(
        navController = navController,
        networkMonitor = networkMonitor,
        startDestination = "home"
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    WeatherAppTheme {
        WeatherApp(networkMonitor = NetworkMonitor(context = null))
    }
}
