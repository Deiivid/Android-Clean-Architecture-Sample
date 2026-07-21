package es.davidnavarro.androidcleanarchitecture.core.data.character

import es.davidnavarro.androidcleanarchitecture.core.data.mapper.toDomainPage
import es.davidnavarro.androidcleanarchitecture.core.data.network.RickAndMortyApi
import es.davidnavarro.androidcleanarchitecture.core.data.repository.executeCatalogRequest
import es.davidnavarro.androidcleanarchitecture.core.domain.character.CharacterRepository
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Page
import javax.inject.Inject

internal class NetworkCharacterRepository @Inject constructor(private val api: RickAndMortyApi) : CharacterRepository {
    override suspend fun getCharacters(page: Int): CatalogResult<Page<Character>, CatalogError> =
        executeCatalogRequest {
            api.getCharacters(page).toDomainPage(page) { it.toDomain() }
        }
}
