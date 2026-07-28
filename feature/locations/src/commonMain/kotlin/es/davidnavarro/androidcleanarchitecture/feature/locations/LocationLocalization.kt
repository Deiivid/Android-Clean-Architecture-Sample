package es.davidnavarro.androidcleanarchitecture.feature.locations

import androidx.compose.runtime.Composable
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_dimension_replacement
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_cluster
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_dimension
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_dream
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_microverse
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_planet
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_type_space_station
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_value_unknown
import es.davidnavarro.androidcleanarchitecture.feature.locations.generated.resources.location_value_unknown_feminine
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun localizedLocationType(type: String): String = when (type.lowercase()) {
    "planet" -> stringResource(Res.string.location_type_planet)
    "cluster" -> stringResource(Res.string.location_type_cluster)
    "space station" -> stringResource(Res.string.location_type_space_station)
    "microverse" -> stringResource(Res.string.location_type_microverse)
    "dimension" -> stringResource(Res.string.location_type_dimension)
    "dream" -> stringResource(Res.string.location_type_dream)
    "unknown", "" -> stringResource(Res.string.location_value_unknown)
    else -> type
}

@Composable
internal fun localizedDimensionValue(dimension: String): String = when {
    dimension.equals("unknown", ignoreCase = true) || dimension.isBlank() ->
        stringResource(Res.string.location_value_unknown_feminine)
    dimension.equals("Replacement Dimension", ignoreCase = true) ->
        stringResource(Res.string.location_dimension_replacement)
    dimension.startsWith("Dimension ", ignoreCase = true) ->
        dimension.substringAfter(' ')
    else -> dimension
}
