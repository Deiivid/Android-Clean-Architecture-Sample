package es.davidnavarro.androidcleanarchitecture.core.data.mapper

import es.davidnavarro.androidcleanarchitecture.core.data.character.CharacterDto
import es.davidnavarro.androidcleanarchitecture.core.data.character.PlaceDto
import es.davidnavarro.androidcleanarchitecture.core.data.character.toDomain
import es.davidnavarro.androidcleanarchitecture.core.data.episode.EpisodeDto
import es.davidnavarro.androidcleanarchitecture.core.data.episode.toDomain
import es.davidnavarro.androidcleanarchitecture.core.data.location.LocationDto
import es.davidnavarro.androidcleanarchitecture.core.data.location.toDomain
import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PageInfoDto
import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PagedResponseDto
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import kotlin.test.Test
import kotlin.test.assertEquals

class CatalogMapperTest {
    @Test
    fun `maps every character field used by domain`() {
        val dto = CharacterDto(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "Scientist",
            gender = "Male",
            origin = PlaceDto(name = "Earth (C-137)"),
            location = PlaceDto(name = "Citadel of Ricks"),
            imageUrl = "https://example.com/rick.png",
            episodes = listOf("episode/1", "episode/2")
        )

        assertEquals(
            Character(
                id = 1,
                name = "Rick Sanchez",
                status = "Alive",
                species = "Human",
                type = "Scientist",
                gender = "Male",
                origin = "Earth (C-137)",
                location = "Citadel of Ricks",
                imageUrl = "https://example.com/rick.png",
                episodeCount = 2
            ),
            dto.toDomain()
        )
    }

    @Test
    fun `maps every episode field used by domain`() {
        val dto = EpisodeDto(
            id = 28,
            name = "The Ricklantis Mixup",
            airDate = "September 10, 2017",
            code = "S03E07",
            characters = listOf("character/1", "character/2", "character/3")
        )

        assertEquals(
            Episode(
                id = 28,
                name = "The Ricklantis Mixup",
                airDate = "September 10, 2017",
                code = "S03E07",
                characterCount = 3
            ),
            dto.toDomain()
        )
    }

    @Test
    fun `maps every location field used by domain`() {
        val dto = LocationDto(
            id = 3,
            name = "Citadel of Ricks",
            type = "Space station",
            dimension = "unknown",
            residents = listOf("character/8", "character/14")
        )

        assertEquals(
            Location(
                id = 3,
                name = "Citadel of Ricks",
                type = "Space station",
                dimension = "unknown",
                residentCount = 2
            ),
            dto.toDomain()
        )
    }

    @Test
    fun `maps page metadata and transformed results`() {
        val dto = PagedResponseDto(
            info = PageInfoDto(count = 826, pages = 42),
            results = listOf("Rick", "Morty")
        )

        assertEquals(
            Page(
                items = listOf(4, 5),
                number = 7,
                totalPages = 42,
                totalItems = 826
            ),
            dto.toDomainPage(pageNumber = 7, transform = String::length)
        )
    }
}
