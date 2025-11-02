package com.volleylord.pexels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volleylord.core.ui.theme.PexelsTheme
import com.volleylord.pexels.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

/**
 * Entry point of the application.
 *
 * Hosts the composable UI and observes [MainUiState] to determine the initial navigation.
 * Uses Hilt for dependency injection and applies the app's theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      PexelsTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          RenderUiState(uiState)
        }
      }
    }
  }

  /**
   * Renders the UI based on the current [MainUiState].
   *
   * @param uiState The state of the main screen (loading or ready).
   */
  @Composable
  private fun RenderUiState(uiState: MainUiState) {
    when (uiState) {
      is MainUiState.Loading -> Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }

      is MainUiState.Ready -> AppNavigation(startDestination = uiState.startDestination)
    }
  }
}
