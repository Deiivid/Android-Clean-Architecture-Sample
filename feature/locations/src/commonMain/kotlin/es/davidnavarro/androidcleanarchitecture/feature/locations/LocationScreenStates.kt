package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import es.davidnavarro.androidcleanarchitecture.core.designsystem.CatalogDimens
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_error
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_error_connectivity
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_error_format
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_error_service
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_load_more_error
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_loading_more
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.locations_retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PaginationContent(state: LocationsUiState.Content, onLoadMore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = CatalogDimens.dp8),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            state.isLoadingMore -> LoadingIndicator(
                label = stringResource(Res.string.locations_loading_more)
            )
            state.loadMoreFailed -> LoadMoreError(onLoadMore)
        }
    }
}

@Composable
private fun LoadMoreError(onRetry: () -> Unit) {
    Text(
        text = stringResource(Res.string.locations_load_more_error),
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        color = MaterialTheme.colorScheme.error
    )
    Spacer(Modifier.height(CatalogDimens.dp8))
    Button(onClick = onRetry) {
        Text(stringResource(Res.string.locations_retry))
    }
}

@Composable
internal fun LoadingContent(padding: PaddingValues, label: String) {
    MessageContent(padding = padding, message = label) { LoadingIndicator(label) }
}

@Composable
private fun LoadingIndicator(label: String) {
    Column(
        modifier = Modifier.semantics {
            liveRegion = LiveRegionMode.Polite
            stateDescription = label
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CatalogDimens.dp12)
    ) {
        CircularProgressIndicator()
        Text(label)
    }
}

@Composable
internal fun ErrorContent(padding: PaddingValues, message: String, onRetry: () -> Unit) {
    MessageContent(padding = padding, message = message) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Spacer(Modifier.height(CatalogDimens.dp16))
            Button(onClick = onRetry) {
                Text(stringResource(Res.string.locations_retry))
            }
        }
    }
}

@Composable
internal fun MessageContent(padding: PaddingValues, message: String, content: @Composable (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(CatalogDimens.dp24),
        contentAlignment = Alignment.Center
    ) {
        content?.invoke() ?: Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
internal fun locationErrorMessage(error: CatalogError): String = when (error) {
    CatalogError.Connectivity -> stringResource(Res.string.locations_error_connectivity)
    is CatalogError.Http -> stringResource(Res.string.locations_error_service)
    CatalogError.Serialization -> stringResource(Res.string.locations_error_format)
    CatalogError.Unexpected -> stringResource(Res.string.locations_error)
}
