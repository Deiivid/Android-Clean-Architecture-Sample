package com.example.rickymortydn.feature.characters

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rickymortydn.core.model.Character

@Composable
fun CharactersRoute(
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CharactersScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadNextPage,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CharactersScreen(
    uiState: CharactersUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.characters_title),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.characters_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (uiState) {
            CharactersUiState.Loading -> CenteredContent(padding) {
                CircularProgressIndicator()
            }
            CharactersUiState.Empty -> CenteredContent(padding) {
                Text(stringResource(R.string.characters_empty))
            }
            is CharactersUiState.Error -> CenteredContent(padding) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.characters_error))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.characters_retry))
                    }
                }
            }
            is CharactersUiState.Content -> CharacterList(
                state = uiState,
                contentPadding = padding,
                onLoadMore = onLoadMore,
            )
        }
    }
}

@Composable
private fun CharacterList(
    state: CharactersUiState.Content,
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
                    R.plurals.characters_loaded,
                    state.characters.size,
                    state.characters.size,
                    state.totalCharacters,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(items = state.characters, key = Character::id) { character ->
            CharacterCard(character)
        }
        if (state.canLoadMore || state.isLoadingMore || state.loadMoreFailed) {
            item(key = "pagination") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when {
                        state.isLoadingMore -> CircularProgressIndicator()
                        state.loadMoreFailed -> {
                            Text(
                                text = stringResource(R.string.characters_load_more_error),
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = onLoadMore) {
                                Text(stringResource(R.string.characters_retry))
                            }
                        }
                        else -> Button(onClick = onLoadMore) {
                            Text(stringResource(R.string.characters_load_more))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterCard(character: Character) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = stringResource(
                    R.string.character_image_description,
                    character.name,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor(character.status)),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${character.status} · ${character.species}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.character_location, character.location),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.character_origin, character.origin),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.character_episodes,
                        character.episodeCount,
                        character.episodeCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun statusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> Color(0xFF2E7D32)
    "dead" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.outline
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
private fun CharactersScreenPreview() {
    MaterialTheme {
        CharactersScreen(
            uiState = CharactersUiState.Content(
                listOf(
                    Character(
                        id = 1,
                        name = "Rick Sanchez",
                        status = "Alive",
                        species = "Human",
                        type = "",
                        gender = "Male",
                        origin = "Earth (C-137)",
                        location = "Citadel of Ricks",
                        imageUrl = "",
                        episodeCount = 51,
                    ),
                ),
                page = 1,
                totalPages = 42,
                totalCharacters = 826,
            ),
            onRetry = {},
            onLoadMore = {},
        )
    }
}
