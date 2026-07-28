package es.davidnavarro.androidcleanarchitecture.core.domain.character

import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Page

interface CharacterRepository {
    suspend fun getCharacters(page: Int): CatalogResult<Page<Character>, CatalogError>
}
