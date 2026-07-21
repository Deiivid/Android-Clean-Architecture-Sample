package es.davidnavarro.androidcleanarchitecture

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.feature.characters.CharacterDetailScreen
import es.davidnavarro.androidcleanarchitecture.feature.characters.CharactersRoute
import es.davidnavarro.androidcleanarchitecture.feature.episodes.EpisodeDetailScreen
import es.davidnavarro.androidcleanarchitecture.feature.episodes.EpisodesRoute
import es.davidnavarro.androidcleanarchitecture.feature.locations.LocationDetailScreen
import es.davidnavarro.androidcleanarchitecture.feature.locations.LocationsRoute

@Composable
fun RickAndMortyApp(navController: NavHostController = rememberNavController()) {
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

@Composable
@Suppress("LongMethod")
private fun CatalogScaffold(navController: NavHostController, selection: CatalogSelection) {
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
    val showBottomBar = currentDestination?.route !in setOf(
        CHARACTER_DETAIL_ROUTE,
        LOCATION_DETAIL_ROUTE,
        EPISODE_DETAIL_ROUTE
    )
    val premiumCatalog = currentDestination?.hierarchy?.any {
        it.route == CatalogDestination.Characters.route ||
            it.route == CatalogDestination.Locations.route ||
            it.route == CatalogDestination.Episodes.route
    } == true
    val darkSystemBars = premiumCatalog || isSystemInDarkTheme()
    val activity = LocalActivity.current as? ComponentActivity
    LaunchedEffect(activity, darkSystemBars) {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (darkSystemBars) {
                SystemBarStyle.dark(Color.Transparent.toArgb())
            } else {
                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
            },
            navigationBarStyle = if (darkSystemBars) {
                SystemBarStyle.dark(Color.Transparent.toArgb())
            } else {
                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
            }
        )
    }

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

@Composable
private fun CatalogNavHost(
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
            navController = navController,
            selectedCharacter = selection.character,
            onCharacterSelected = selection.onCharacterSelected,
            scrollToTopRequest = scrollRequests.characters
        )
        locationsGraph(
            navController,
            selection.location,
            selection.onLocationSelected,
            scrollRequests.locations
        )
        episodesGraph(
            navController,
            selection.episode,
            selection.onEpisodeSelected,
            scrollRequests.episodes
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
    navController: NavHostController,
    selectedLocation: Location?,
    onLocationSelected: (Location) -> Unit,
    scrollToTopRequest: MutableState<Int>
) {
    navigation(route = CatalogDestination.Locations.route, startDestination = LOCATIONS_LIST_ROUTE) {
        composable(LOCATIONS_LIST_ROUTE) {
            LocationsRoute(
                onLocationClick = { location ->
                    onLocationSelected(location)
                    navController.navigate(locationDetailRoute(location.id)) { launchSingleTop = true }
                },
                scrollToTopRequest = scrollToTopRequest.value
            )
        }
        composable(
            route = LOCATION_DETAIL_ROUTE,
            arguments = listOf(navArgument(LOCATION_ID_ARGUMENT) { type = NavType.IntType })
        ) { entry ->
            val location = selectedLocation?.takeIf { it.id == entry.arguments?.getInt(LOCATION_ID_ARGUMENT) }
            if (location != null) {
                LocationDetailScreen(location, navController::popBackStack)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}

private fun NavGraphBuilder.episodesGraph(
    navController: NavHostController,
    selectedEpisode: Episode?,
    onEpisodeSelected: (Episode) -> Unit,
    scrollToTopRequest: MutableState<Int>
) {
    navigation(route = CatalogDestination.Episodes.route, startDestination = EPISODES_LIST_ROUTE) {
        composable(EPISODES_LIST_ROUTE) {
            EpisodesRoute(
                onEpisodeClick = { episode ->
                    onEpisodeSelected(episode)
                    navController.navigate(episodeDetailRoute(episode.id)) { launchSingleTop = true }
                },
                scrollToTopRequest = scrollToTopRequest.value
            )
        }
        composable(
            route = EPISODE_DETAIL_ROUTE,
            arguments = listOf(navArgument(EPISODE_ID_ARGUMENT) { type = NavType.IntType })
        ) { entry ->
            val episode = selectedEpisode?.takeIf { it.id == entry.arguments?.getInt(EPISODE_ID_ARGUMENT) }
            if (episode != null) {
                EpisodeDetailScreen(episode, navController::popBackStack)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}

private fun NavGraphBuilder.charactersGraph(
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
            CharactersRoute(
                onCharacterClick = { character ->
                    onCharacterSelected(character)
                    navController.navigate(characterDetailRoute(character.id)) {
                        launchSingleTop = true
                    }
                },
                scrollToTopRequest = scrollToTopRequest.value
            )
        }
        composable(
            route = CHARACTER_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(CHARACTER_ID_ARGUMENT) { type = NavType.IntType }
            )
        ) { entry ->
            val characterId = entry.arguments?.getInt(CHARACTER_ID_ARGUMENT)
            val character = selectedCharacter?.takeIf { it.id == characterId }
            if (character != null) {
                CharacterDetailScreen(
                    character = character,
                    onBack = navController::popBackStack
                )
            } else {
                LaunchedEffect(characterId) {
                    navController.popBackStack()
                }
            }
        }
    }
}
