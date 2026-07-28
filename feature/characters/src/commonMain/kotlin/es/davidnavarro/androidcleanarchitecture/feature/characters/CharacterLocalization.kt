package es.davidnavarro.androidcleanarchitecture.feature.characters

import androidx.compose.runtime.Composable
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.Res
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_gender_female
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_gender_genderless
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_gender_male
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_alien
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_animal
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_human
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_humanoid
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_mythological
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_species_robot
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_status_alive
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_status_dead
import es.davidnavarro.androidcleanarchitecture.feature.characters.generated.resources.character_value_unknown
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun localizedStatus(status: String): String = when (status.lowercase()) {
    "alive" -> stringResource(Res.string.character_status_alive)
    "dead" -> stringResource(Res.string.character_status_dead)
    else -> stringResource(Res.string.character_value_unknown)
}

@Composable
internal fun localizedSpecies(species: String): String = when (species.lowercase()) {
    "human" -> stringResource(Res.string.character_species_human)
    "alien" -> stringResource(Res.string.character_species_alien)
    "humanoid" -> stringResource(Res.string.character_species_humanoid)
    "animal" -> stringResource(Res.string.character_species_animal)
    "robot" -> stringResource(Res.string.character_species_robot)
    "mythological creature" -> stringResource(Res.string.character_species_mythological)
    "unknown", "" -> stringResource(Res.string.character_value_unknown)
    else -> species
}

@Composable
internal fun localizedGender(gender: String): String = when (gender.lowercase()) {
    "male" -> stringResource(Res.string.character_gender_male)
    "female" -> stringResource(Res.string.character_gender_female)
    "genderless" -> stringResource(Res.string.character_gender_genderless)
    else -> stringResource(Res.string.character_value_unknown)
}

@Composable
internal fun localizedBackendValue(value: String): String = when (value.lowercase()) {
    "unknown", "" -> stringResource(Res.string.character_value_unknown)
    else -> value
}
