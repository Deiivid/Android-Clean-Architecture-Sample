package com.example.rickymortydn.feature.locations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickymortydn.core.model.Location

@Composable
fun LocationsRoute(
    viewModel: LocationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LocationsScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadNextPage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    uiState: LocationsUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.locations_title),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.locations_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (uiState) {
            LocationsUiState.Loading -> CenteredContent(padding) {
                CircularProgressIndicator()
            }
            LocationsUiState.Empty -> CenteredContent(padding) {
                Text(stringResource(R.string.locations_empty))
            }
            is LocationsUiState.Error -> CenteredContent(padding) {
                ErrorContent(onRetry)
            }
            is LocationsUiState.Content -> LocationList(uiState, padding, onLoadMore)
        }
    }
}

@Composable
private fun LocationList(
    state: LocationsUiState.Content,
    contentPadding: PaddingValues,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "count") {
            Text(
                text = pluralStringResource(
                    R.plurals.locations_loaded,
                    state.locations.size,
                    state.locations.size,
                    state.totalLocations,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(state.locations, key = Location::id) { location ->
            LocationCard(location)
        }
        if (state.canLoadMore || state.isLoadingMore || state.loadMoreFailed) {
            item(key = "pagination") {
                PaginationContent(state, onLoadMore)
            }
        }
    }
}

@Composable
private fun LocationCard(location: Location) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = location.type,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Text(
                    text = pluralStringResource(
                        R.plurals.location_residents,
                        location.residentCount,
                        location.residentCount,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.location_dimension, location.dimension),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PaginationContent(
    state: LocationsUiState.Content,
    onLoadMore: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            state.isLoadingMore -> CircularProgressIndicator()
            state.loadMoreFailed -> {
                Text(
                    text = stringResource(R.string.locations_load_more_error),
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onLoadMore) {
                    Text(stringResource(R.string.locations_retry))
                }
            }
            else -> Button(onClick = onLoadMore) {
                Text(stringResource(R.string.locations_load_more))
            }
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.locations_error))
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.locations_retry))
        }
    }
}

@Composable
private fun CenteredContent(
    padding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationsScreenPreview() {
    MaterialTheme {
        LocationsScreen(
            uiState = LocationsUiState.Content(
                locations = listOf(
                    Location(
                        id = 1,
                        name = "Earth (C-137)",
                        type = "Planet",
                        dimension = "Dimension C-137",
                        residentCount = 27,
                    ),
                ),
                page = 1,
                totalPages = 7,
                totalLocations = 126,
            ),
            onRetry = {},
            onLoadMore = {},
        )
    }
}
