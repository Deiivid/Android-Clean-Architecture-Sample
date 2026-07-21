@file:Suppress("LongMethod")

package es.davidnavarro.androidcleanarchitecture.feature.characters

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun CharactersRoute(
    onCharacterClick: (Character) -> Unit,
    scrollToTopRequest: Int = 0,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CharactersScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadNextPage,
        onCharacterClick = onCharacterClick,
        scrollToTopRequest = scrollToTopRequest
    )
}

@Composable
fun CharactersScreen(
    uiState: CharactersUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onCharacterClick: (Character) -> Unit,
    scrollToTopRequest: Int = 0
) {
    Box(Modifier.fillMaxSize()) {
        CharacterSpaceBackdrop()
        Scaffold(containerColor = Color.Transparent) { padding ->
            when (uiState) {
                CharactersUiState.Loading -> LoadingContent(
                    padding = padding,
                    label = stringResource(R.string.characters_loading)
                )
                CharactersUiState.Empty -> MessageContent(
                    padding = padding,
                    message = stringResource(R.string.characters_empty)
                )
                is CharactersUiState.Error -> ErrorContent(
                    padding = padding,
                    message = characterErrorMessage(uiState.error),
                    retryLabel = stringResource(R.string.characters_retry),
                    onRetry = onRetry
                )
                is CharactersUiState.Content -> CharacterList(
                    state = uiState,
                    contentPadding = padding,
                    onLoadMore = onLoadMore,
                    onCharacterClick = onCharacterClick,
                    scrollToTopRequest = scrollToTopRequest
                )
            }
        }
    }
}

@Composable
private fun CharacterList(
    state: CharactersUiState.Content,
    contentPadding: PaddingValues,
    onLoadMore: () -> Unit,
    onCharacterClick: (Character) -> Unit,
    scrollToTopRequest: Int
) {
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    LaunchedEffect(listState, state.canLoadMore, state.isLoadingMore, state.loadMoreFailed) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            listState.layoutInfo.totalItemsCount > 0 &&
                lastVisible >= listState.layoutInfo.totalItemsCount - 4
        }
            .distinctUntilChanged()
            .filter { it && state.canLoadMore && !state.isLoadingMore && !state.loadMoreFailed }
            .collect { onLoadMore() }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = CatalogDimens.dp16,
            top = contentPadding.calculateTopPadding() + CatalogDimens.dp14,
            end = CatalogDimens.dp16,
            bottom = CatalogDimens.dp24
        ),
        verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp8)
    ) {
        item(key = "header") {
            CatalogHeader(
                title = stringResource(R.string.characters_title),
                subtitle = stringResource(R.string.characters_subtitle),
                count = pluralStringResource(
                    R.plurals.characters_loaded,
                    state.characters.size,
                    state.characters.size,
                    state.totalCharacters
                )
            )
        }
        items(items = state.characters, key = Character::id) { character ->
            CharacterCard(
                character = character,
                onClick = { onCharacterClick(character) }
            )
        }
        if (state.isLoadingMore || state.loadMoreFailed) {
            item(key = "pagination") {
                PaginationContent(state = state, onLoadMore = onLoadMore)
            }
        }
    }
}

@Composable
private fun CatalogHeader(title: String, subtitle: String, count: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CatalogDimens.dp156)
    ) {
        CharacterNetworkScanner(
            size = CatalogDimens.dp190,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            CharacterBackground,
                            CharacterBackground.copy(alpha = .92f),
                            Color.Transparent
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = CatalogDimens.dp8),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(.72f)) {
                Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = CharacterText
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharacterTextMuted
                )
            }
            Surface(
                shape = CircleShape,
                color = CharacterGreen.copy(alpha = .12f),
                border = BorderStroke(CatalogDimens.dp1, CharacterGreen.copy(alpha = .5f))
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = CatalogDimens.dp14,
                        vertical = CatalogDimens.dp8
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        modifier = Modifier.size(CatalogDimens.dp22),
                        tint = CharacterGreen
                    )
                    Spacer(Modifier.width(CatalogDimens.dp8))
                    Text(
                        text = count,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharacterGreen
                    )
                }
            }
        }
    }
}
