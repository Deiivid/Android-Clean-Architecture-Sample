package com.example.rickymortydn.feature.episodes

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
import com.example.rickymortydn.core.model.Episode

@Composable
fun EpisodesRoute(
    viewModel: EpisodesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EpisodesScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadNextPage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodesScreen(
    uiState: EpisodesUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.episodes_title),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.episodes_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (uiState) {
            EpisodesUiState.Loading -> CenteredContent(padding) {
                CircularProgressIndicator()
            }
            EpisodesUiState.Empty -> CenteredContent(padding) {
                Text(stringResource(R.string.episodes_empty))
            }
            is EpisodesUiState.Error -> CenteredContent(padding) {
                ErrorContent(onRetry)
            }
            is EpisodesUiState.Content -> EpisodeList(uiState, padding, onLoadMore)
        }
    }
}

@Composable
private fun EpisodeList(
    state: EpisodesUiState.Content,
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
                    R.plurals.episodes_loaded,
                    state.episodes.size,
                    state.episodes.size,
                    state.totalEpisodes,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(state.episodes, key = Episode::id) { episode ->
            EpisodeCard(episode)
        }
        if (state.canLoadMore || state.isLoadingMore || state.loadMoreFailed) {
            item(key = "pagination") {
                PaginationContent(state, onLoadMore)
            }
        }
    }
}

@Composable
private fun EpisodeCard(episode: Episode) {
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = episode.code,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Text(
                    text = pluralStringResource(
                        R.plurals.episode_characters,
                        episode.characterCount,
                        episode.characterCount,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = episode.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.episode_air_date, episode.airDate),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PaginationContent(
    state: EpisodesUiState.Content,
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
                    text = stringResource(R.string.episodes_load_more_error),
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onLoadMore) {
                    Text(stringResource(R.string.episodes_retry))
                }
            }
            else -> Button(onClick = onLoadMore) {
                Text(stringResource(R.string.episodes_load_more))
            }
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.episodes_error))
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.episodes_retry))
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
private fun EpisodesScreenPreview() {
    MaterialTheme {
        EpisodesScreen(
            uiState = EpisodesUiState.Content(
                episodes = listOf(
                    Episode(
                        id = 1,
                        name = "Pilot",
                        airDate = "December 2, 2013",
                        code = "S01E01",
                        characterCount = 19,
                    ),
                ),
                page = 1,
                totalPages = 3,
                totalEpisodes = 51,
            ),
            onRetry = {},
            onLoadMore = {},
        )
    }
}
