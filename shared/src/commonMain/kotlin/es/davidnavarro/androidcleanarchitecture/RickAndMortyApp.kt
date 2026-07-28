package es.davidnavarro.androidcleanarchitecture

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.feature.characters.CharacterDetailScreen
import es.davidnavarro.androidcleanarchitecture.feature.characters.CharactersRoute
import es.davidnavarro.androidcleanarchitecture.feature.characters.CharactersViewModel
import es.davidnavarro.androidcleanarchitecture.feature.episodes.EpisodeDetailScreen
import es.davidnavarro.androidcleanarchitecture.feature.episodes.EpisodesRoute
import es.davidnavarro.androidcleanarchitecture.feature.episodes.EpisodesViewModel
import es.davidnavarro.androidcleanarchitecture.feature.locations.LocationDetailScreen
import es.davidnavarro.androidcleanarchitecture.feature.locations.LocationsRoute
import es.davidnavarro.androidcleanarchitecture.feature.locations.LocationsViewModel
import es.davidnavarro.androidcleanarchitecture.ui.theme.AndroidCleanArchitectureTheme

@Composable
fun RickAndMortyApp(
    component: CatalogAppComponent = remember { CatalogAppComponent() },
    navController: NavHostController = rememberNavController()
) {
    DisposableEffect(component) {
        onDispose(component::close)
    }
    AndroidCleanArchitectureTheme {
        var selectedCharacter by rememberSaveable(saver = selectedCharacterSaver) {
            mutableStateOf<Character?>(null)
        }
        var selectedLocation by rememberSaveable(saver = selectedLocationSaver) {
            mutableStateOf<Location?>(null)
        }
        var selectedEpisode by rememberSaveable(saver = selectedEpisodeSaver) {
            mutableStateOf<Episode?>(null)
        }
        CatalogScaffold(
            component = component,
            navController = navController,
            selection = CatalogSelection(
                character = selectedCharacter,
                location = selectedLocation,
                episode = selectedEpisode,
                onCharacterSelected = { selectedCharacter = it },
                onLocationSelected = { selectedLocation = it },
                onEpisodeSelected = { selectedEpisode = it }
            )
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun CatalogScaffold(
    component: CatalogAppComponent,
    navController: NavHostController,
    selection: CatalogSelection
) {
    val characterScrollToTopRequest = rememberSaveable { mutableStateOf(0) }
    val locationScrollToTopRequest = rememberSaveable { mutableStateOf(0) }
    val episodeScrollToTopRequest = rememberSaveable { mutableStateOf(0) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentDestinationRoutes = currentDestination
        ?.hierarchy
        ?.mapNotNull { it.route }
        ?.toSet()
        .orEmpty()
    val showBottomBar = currentDestination?.route !in detailRoutes
    val premiumCatalog = currentDestination?.hierarchy?.any {
        it.route == CatalogDestination.Characters.route ||
            it.route == CatalogDestination.Locations.route ||
            it.route == CatalogDestination.Episodes.route
    } == true

    Scaffold(
        containerColor = if (premiumCatalog) PremiumBackground else MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                CatalogNavigationBar(
                    currentDestinationRoutes = currentDestinationRoutes,
                    onDestinationClick = { destination ->
                        if (destination.route in currentDestinationRoutes) {
                            when (destination) {
                                CatalogDestination.Characters -> characterScrollToTopRequest.value++
                                CatalogDestination.Locations -> locationScrollToTopRequest.value++
                                CatalogDestination.Episodes -> episodeScrollToTopRequest.value++
                            }
                        } else {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        CatalogNavHost(
            component = component,
            navController = navController,
            selection = selection,
            scrollRequests = CatalogScrollRequests(
                characters = characterScrollToTopRequest,
                locations = locationScrollToTopRequest,
                episodes = episodeScrollToTopRequest
            ),
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}

private const val PREMIUM_BACKGROUND_ARGB = 0xFF030A12
private val PremiumBackground = Color(PREMIUM_BACKGROUND_ARGB)
private val detailRoutes = setOf(
    CHARACTER_DETAIL_ROUTE,
    LOCATION_DETAIL_ROUTE,
    EPISODE_DETAIL_ROUTE
)

@Composable
private fun CatalogNavHost(
    component: CatalogAppComponent,
    navController: NavHostController,
    selection: CatalogSelection,
    scrollRequests: CatalogScrollRequests,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = CatalogDestination.Characters.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        charactersGraph(
            component = component,
            navController = navController,
            selectedCharacter = selection.character,
            onCharacterSelected = selection.onCharacterSelected,
            scrollToTopRequest = scrollRequests.characters
        )
        locationsGraph(
            component = component,
            navController = navController,
            selectedLocation = selection.location,
            onLocationSelected = selection.onLocationSelected,
            scrollToTopRequest = scrollRequests.locations
        )
        episodesGraph(
            component = component,
            navController = navController,
            selectedEpisode = selection.episode,
            onEpisodeSelected = selection.onEpisodeSelected,
            scrollToTopRequest = scrollRequests.episodes
        )
    }
}

private data class CatalogSelection(
    val character: Character?,
    val location: Location?,
    val episode: Episode?,
    val onCharacterSelected: (Character) -> Unit,
    val onLocationSelected: (Location) -> Unit,
    val onEpisodeSelected: (Episode) -> Unit
)

private data class CatalogScrollRequests(
    val characters: MutableState<Int>,
    val locations: MutableState<Int>,
    val episodes: MutableState<Int>
)

private fun NavGraphBuilder.locationsGraph(
    component: CatalogAppComponent,
    navController: NavHostController,
    selectedLocation: Location?,
    onLocationSelected: (Location) -> Unit,
    scrollToTopRequest: MutableState<Int>
) {
    navigation(route = CatalogDestination.Locations.route, startDestination = LOCATIONS_LIST_ROUTE) {
        composable(LOCATIONS_LIST_ROUTE) {
            val locationsViewModel = viewModel {
                LocationsViewModel(component.getLocations)
            }
            LocationsRoute(
                viewModel = locationsViewModel,
                onLocationClick = { location ->
                    onLocationSelected(location)
                    navController.navigate(LOCATION_DETAIL_ROUTE) {
                        launchSingleTop = true
                    }
                },
                scrollToTopRequest = scrollToTopRequest.value
            )
        }
        composable(LOCATION_DETAIL_ROUTE) {
            if (selectedLocation != null) {
                LocationDetailScreen(selectedLocation, navController::popBackStack)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}

private fun NavGraphBuilder.episodesGraph(
    component: CatalogAppComponent,
    navController: NavHostController,
    selectedEpisode: Episode?,
    onEpisodeSelected: (Episode) -> Unit,
    scrollToTopRequest: MutableState<Int>
) {
    navigation(route = CatalogDestination.Episodes.route, startDestination = EPISODES_LIST_ROUTE) {
        composable(EPISODES_LIST_ROUTE) {
            val episodesViewModel = viewModel {
                EpisodesViewModel(component.getEpisodes)
            }
            EpisodesRoute(
                viewModel = episodesViewModel,
                onEpisodeClick = { episode ->
                    onEpisodeSelected(episode)
                    navController.navigate(EPISODE_DETAIL_ROUTE) {
                        launchSingleTop = true
                    }
                },
                scrollToTopRequest = scrollToTopRequest.value
            )
        }
        composable(EPISODE_DETAIL_ROUTE) {
            if (selectedEpisode != null) {
                EpisodeDetailScreen(selectedEpisode, navController::popBackStack)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}

private fun NavGraphBuilder.charactersGraph(
    component: CatalogAppComponent,
    navController: NavHostController,
    selectedCharacter: Character?,
    onCharacterSelected: (Character) -> Unit,
    scrollToTopRequest: MutableState<Int>
) {
    navigation(
        route = CatalogDestination.Characters.route,
        startDestination = CHARACTERS_LIST_ROUTE
    ) {
        composable(CHARACTERS_LIST_ROUTE) {
            val charactersViewModel = viewModel {
                CharactersViewModel(component.getCharacters)
            }
            CharactersRoute(
                viewModel = charactersViewModel,
                onCharacterClick = { character ->
                    onCharacterSelected(character)
                    navController.navigate(CHARACTER_DETAIL_ROUTE) {
                        launchSingleTop = true
                    }
                },
                scrollToTopRequest = scrollToTopRequest.value
            )
        }
        composable(CHARACTER_DETAIL_ROUTE) {
            if (selectedCharacter != null) {
                CharacterDetailScreen(
                    character = selectedCharacter,
                    onBack = navController::popBackStack
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}
