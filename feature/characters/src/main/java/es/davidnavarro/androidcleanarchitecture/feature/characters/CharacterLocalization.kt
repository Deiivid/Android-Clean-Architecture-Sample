package es.davidnavarro.androidcleanarchitecture.feature.characters

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun localizedStatus(status: String): String = when (status.lowercase()) {
    "alive" -> stringResource(R.string.character_status_alive)
    "dead" -> stringResource(R.string.character_status_dead)
    else -> stringResource(R.string.character_value_unknown)
}

@Composable
internal fun localizedSpecies(species: String): String = when (species.lowercase()) {
    "human" -> stringResource(R.string.character_species_human)
    "alien" -> stringResource(R.string.character_species_alien)
    "humanoid" -> stringResource(R.string.character_species_humanoid)
    "animal" -> stringResource(R.string.character_species_animal)
    "robot" -> stringResource(R.string.character_species_robot)
    "mythological creature" -> stringResource(R.string.character_species_mythological)
    "unknown", "" -> stringResource(R.string.character_value_unknown)
    else -> species
}

@Composable
internal fun localizedGender(gender: String): String = when (gender.lowercase()) {
    "male" -> stringResource(R.string.character_gender_male)
    "female" -> stringResource(R.string.character_gender_female)
    "genderless" -> stringResource(R.string.character_gender_genderless)
    else -> stringResource(R.string.character_value_unknown)
}

@Composable
internal fun localizedBackendValue(value: String): String = when (value.lowercase()) {
    "unknown", "" -> stringResource(R.string.character_value_unknown)
    else -> value
}
