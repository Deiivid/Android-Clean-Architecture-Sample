package es.davidnavarro.androidcleanarchitecture

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CatalogSelectionSaverTest {
    @Test
    fun `character selection survives save and restore`() {
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = "Earth (C-137)",
            location = "Citadel of Ricks",
            imageUrl = "https://example.com/rick.png",
            episodeCount = 51
        )

        assertEquals(character, roundTrip(selectedCharacterSaver, character))
    }

    @Test
    fun `location selection survives save and restore`() {
        val location = Location(
            id = 1,
            name = "Earth (C-137)",
            type = "Planet",
            dimension = "Dimension C-137",
            residentCount = 27
        )

        assertEquals(location, roundTrip(selectedLocationSaver, location))
    }

    @Test
    fun `episode selection survives save and restore`() {
        val episode = Episode(
            id = 1,
            name = "Pilot",
            airDate = "December 2, 2013",
            code = "S01E01",
            characterCount = 19
        )

        assertEquals(episode, roundTrip(selectedEpisodeSaver, episode))
    }

    @Test
    fun `empty selections survive save and restore`() {
        assertNull(roundTrip<Character>(selectedCharacterSaver, null))
        assertNull(roundTrip<Location>(selectedLocationSaver, null))
        assertNull(roundTrip<Episode>(selectedEpisodeSaver, null))
    }

    private fun <T> roundTrip(saver: Saver<MutableState<T?>, Any>, value: T?): T? {
        val saved = with(saver) { SavingScope.save(mutableStateOf(value)) }
        return saver.restore(requireNotNull(saved))?.value
    }

    private companion object {
        val SavingScope = SaverScope { true }
    }
}
