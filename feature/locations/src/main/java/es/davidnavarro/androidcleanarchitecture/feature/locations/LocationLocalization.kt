package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun localizedLocationType(type: String): String = when (type.lowercase()) {
    "planet" -> stringResource(R.string.location_type_planet)
    "cluster" -> stringResource(R.string.location_type_cluster)
    "space station" -> stringResource(R.string.location_type_space_station)
    "microverse" -> stringResource(R.string.location_type_microverse)
    "dimension" -> stringResource(R.string.location_type_dimension)
    "dream" -> stringResource(R.string.location_type_dream)
    "unknown", "" -> stringResource(R.string.location_value_unknown)
    else -> type
}

@Composable
internal fun localizedDimensionValue(dimension: String): String = when {
    dimension.equals("unknown", ignoreCase = true) || dimension.isBlank() ->
        stringResource(R.string.location_value_unknown_feminine)
    dimension.equals("Replacement Dimension", ignoreCase = true) ->
        stringResource(R.string.location_dimension_replacement)
    dimension.startsWith("Dimension ", ignoreCase = true) ->
        dimension.substringAfter(' ')
    else -> dimension
}
