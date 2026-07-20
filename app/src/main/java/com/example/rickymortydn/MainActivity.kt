package com.example.rickymortydn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import com.example.rickymortydn.feature.characters.CharactersRoute
import com.example.rickymortydn.feature.episodes.EpisodesRoute
import com.example.rickymortydn.feature.locations.LocationsRoute
import com.example.rickymortydn.ui.theme.RickyMortyDNTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickyMortyDNTheme {
                var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
                val stateHolder = rememberSaveableStateHolder()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            destinations.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = null,
                                        )
                                    },
                                    label = { Text(stringResource(destination.label)) },
                                )
                            }
                        }
                    },
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        val destination = destinations[selectedIndex]
                        stateHolder.SaveableStateProvider(destination.name) {
                            when (destination) {
                                CatalogDestination.Characters -> CharactersRoute()
                                CatalogDestination.Locations -> LocationsRoute()
                                CatalogDestination.Episodes -> EpisodesRoute()
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class CatalogDestination(
    @param:StringRes val label: Int,
    val icon: ImageVector,
) {
    Characters(R.string.navigation_characters, Icons.Default.Person),
    Locations(R.string.navigation_locations, Icons.Default.Place),
    Episodes(R.string.navigation_episodes, Icons.Default.PlayArrow),
}

private val destinations = CatalogDestination.entries
