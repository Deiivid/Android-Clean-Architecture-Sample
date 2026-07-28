package es.davidnavarro.androidcleanarchitecture.core.data.repository

import es.davidnavarro.androidcleanarchitecture.core.data.character.CharacterDto
import es.davidnavarro.androidcleanarchitecture.core.data.character.NetworkCharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.data.character.PlaceDto
import es.davidnavarro.androidcleanarchitecture.core.data.episode.EpisodeDto
import es.davidnavarro.androidcleanarchitecture.core.data.episode.NetworkEpisodeRepository
import es.davidnavarro.androidcleanarchitecture.core.data.location.LocationDto
import es.davidnavarro.androidcleanarchitecture.core.data.location.NetworkLocationRepository
import es.davidnavarro.androidcleanarchitecture.core.data.network.KtorRickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.data.network.RickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PageInfoDto
import es.davidnavarro.androidcleanarchitecture.core.data.network.model.PagedResponseDto
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Episode
import es.davidnavarro.androidcleanarchitecture.core.model.Location
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class NetworkCatalogRepositoriesTest {
    @Test
    fun `character repository maps API response to domain page`() = runTest {
        val repository = NetworkCharacterRepository(
            FakeRickAndMortyApi(characters = { characterPage() })
        )

        val result = repository.getCharacters(page = 2)

        assertEquals(
            CatalogResult.Success(
                Page(
                    items = listOf(
                        Character(
                            id = 1,
                            name = "Rick Sanchez",
                            status = "Alive",
                            species = "Human",
                            type = "",
                            gender = "Male",
                            origin = "Earth (C-137)",
                            location = "Citadel of Ricks",
                            imageUrl = "https://example.com/rick.png",
                            episodeCount = 2
                        )
                    ),
                    number = 2,
                    totalPages = 42,
                    totalItems = 826
                )
            ),
            result
        )
    }

    @Test
    fun `episode repository maps API response to domain page`() = runTest {
        val repository = NetworkEpisodeRepository(
            FakeRickAndMortyApi(episodes = { episodePage() })
        )

        val result = repository.getEpisodes(page = 3)

        assertEquals(
            CatalogResult.Success(
                Page(
                    items = listOf(
                        Episode(
                            id = 1,
                            name = "Pilot",
                            airDate = "December 2, 2013",
                            code = "S01E01",
                            characterCount = 2
                        )
                    ),
                    number = 3,
                    totalPages = 3,
                    totalItems = 51
                )
            ),
            result
        )
    }

    @Test
    fun `location repository maps API response to domain page`() = runTest {
        val repository = NetworkLocationRepository(
            FakeRickAndMortyApi(locations = { locationPage() })
        )

        val result = repository.getLocations(page = 4)

        assertEquals(
            CatalogResult.Success(
                Page(
                    items = listOf(
                        Location(
                            id = 1,
                            name = "Earth (C-137)",
                            type = "Planet",
                            dimension = "Dimension C-137",
                            residentCount = 2
                        )
                    ),
                    number = 4,
                    totalPages = 7,
                    totalItems = 126
                )
            ),
            result
        )
    }

    @Test
    fun `maps connectivity failures`() = runTest {
        val result = characterResultThrowing(IOException("offline"))

        assertEquals(CatalogResult.Failure(CatalogError.Connectivity), result)
    }

    @Test
    fun `maps HTTP failures with their status code`() = runTest {
        val result = characterResultFromHttp(
            body = "{}",
            status = HttpStatusCode.ServiceUnavailable
        )

        assertEquals(CatalogResult.Failure(CatalogError.Http(statusCode = 503)), result)
    }

    @Test
    fun `maps serialization failures`() = runTest {
        val result = characterResultThrowing(SerializationException("Malformed payload"))

        assertEquals(CatalogResult.Failure(CatalogError.Serialization), result)
    }

    @Test
    fun `maps malformed Ktor payload as serialization failure`() = runTest {
        val result = characterResultFromHttp("{not-valid-json}")

        assertEquals(CatalogResult.Failure(CatalogError.Serialization), result)
    }

    @Test
    fun `maps truncated Ktor payload as serialization failure`() = runTest {
        val result = characterResultFromHttp("{\"info\":")

        assertEquals(CatalogResult.Failure(CatalogError.Serialization), result)
    }

    @Test
    fun `maps unexpected failures without exposing their cause`() = runTest {
        val result = characterResultThrowing(IllegalStateException("Internal detail"))

        assertEquals(CatalogResult.Failure(CatalogError.Unexpected), result)
    }

    @Test
    fun `propagates coroutine cancellation`() = runTest {
        val cancellation = CancellationException("Cancelled by caller")
        val repository = NetworkCharacterRepository(
            FakeRickAndMortyApi(characters = { throw cancellation })
        )

        try {
            repository.getCharacters(page = 1)
            fail("CancellationException was converted into a result")
        } catch (thrown: CancellationException) {
            assertSame(cancellation, thrown)
        }
    }

    private suspend fun characterResultThrowing(exception: Exception): CatalogResult<Page<Character>, CatalogError> =
        NetworkCharacterRepository(
            FakeRickAndMortyApi(characters = { throw exception })
        ).getCharacters(page = 1)

    private suspend fun characterResultFromHttp(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): CatalogResult<Page<Character>, CatalogError> {
        val client = HttpClient(
            MockEngine {
                respond(
                    content = body,
                    status = status,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        ) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return try {
            NetworkCharacterRepository(
                KtorRickAndMortyApi(client, "https://example.test/api/")
            ).getCharacters(page = 1)
        } finally {
            client.close()
        }
    }

    private class FakeRickAndMortyApi(
        private val characters: suspend (Int) -> PagedResponseDto<CharacterDto> = unusedCall(),
        private val locations: suspend (Int) -> PagedResponseDto<LocationDto> = unusedCall(),
        private val episodes: suspend (Int) -> PagedResponseDto<EpisodeDto> = unusedCall()
    ) : RickAndMortyApi {
        override suspend fun getCharacters(page: Int): PagedResponseDto<CharacterDto> = characters(page)

        override suspend fun getLocations(page: Int): PagedResponseDto<LocationDto> = locations(page)

        override suspend fun getEpisodes(page: Int): PagedResponseDto<EpisodeDto> = episodes(page)
    }

    private companion object {
        fun characterPage() = PagedResponseDto(
            info = PageInfoDto(count = 826, pages = 42),
            results = listOf(
                CharacterDto(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    type = "",
                    gender = "Male",
                    origin = PlaceDto("Earth (C-137)"),
                    location = PlaceDto("Citadel of Ricks"),
                    imageUrl = "https://example.com/rick.png",
                    episodes = listOf("episode/1", "episode/2")
                )
            )
        )

        fun episodePage() = PagedResponseDto(
            info = PageInfoDto(count = 51, pages = 3),
            results = listOf(
                EpisodeDto(
                    id = 1,
                    name = "Pilot",
                    airDate = "December 2, 2013",
                    code = "S01E01",
                    characters = listOf("character/1", "character/2")
                )
            )
        )

        fun locationPage() = PagedResponseDto(
            info = PageInfoDto(count = 126, pages = 7),
            results = listOf(
                LocationDto(
                    id = 1,
                    name = "Earth (C-137)",
                    type = "Planet",
                    dimension = "Dimension C-137",
                    residents = listOf("character/38", "character/45")
                )
            )
        )

        fun <T> unusedCall(): suspend (Int) -> T = {
            error("Unexpected API call")
        }
    }
}
