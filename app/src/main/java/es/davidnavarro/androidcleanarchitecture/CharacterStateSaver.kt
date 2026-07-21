package es.davidnavarro.androidcleanarchitecture

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Location

internal val selectedCharacterSaver: Saver<MutableState<Character?>, Any> = listSaver(
    save = { state -> state.value.toSavedValues() },
    restore = { values -> mutableStateOf(values.toCharacter()) }
)

private fun Character?.toSavedValues(): List<Any> = if (this == null) {
    listOf(false)
} else {
    listOf(
        true,
        id,
        name,
        status,
        species,
        type,
        gender,
        origin,
        location,
        imageUrl,
        episodeCount
    )
}

private fun List<Any>.toCharacter(): Character? {
    if (!(first() as Boolean)) return null
    return Character(
        id = this[1] as Int,
        name = this[2] as String,
        status = this[3] as String,
        species = this[4] as String,
        type = this[5] as String,
        gender = this[6] as String,
        origin = this[7] as String,
        location = this[8] as String,
        imageUrl = this[9] as String,
        episodeCount = this[10] as Int
    )
}

internal val selectedLocationSaver: Saver<MutableState<Location?>, Any> = listSaver(
    save = { state ->
        state.value?.let { listOf(true, it.id, it.name, it.type, it.dimension, it.residentCount) }
            ?: listOf(false)
    },
    restore = { values ->
        mutableStateOf(
            if (values.first() as Boolean) {
                Location(
                    id = values[1] as Int,
                    name = values[2] as String,
                    type = values[3] as String,
                    dimension = values[4] as String,
                    residentCount = values[5] as Int
                )
            } else {
                null
            }
        )
    }
)

internal val selectedEpisodeSaver: Saver<MutableState<Episode?>, Any> = listSaver(
    save = { state ->
        state.value?.let { listOf(true, it.id, it.name, it.airDate, it.code, it.characterCount) }
            ?: listOf(false)
    },
    restore = { values ->
        mutableStateOf(
            if (values.first() as Boolean) {
                Episode(
                    id = values[1] as Int,
                    name = values[2] as String,
                    airDate = values[3] as String,
                    code = values[4] as String,
                    characterCount = values[5] as Int
                )
            } else {
                null
            }
        )
    }
)
