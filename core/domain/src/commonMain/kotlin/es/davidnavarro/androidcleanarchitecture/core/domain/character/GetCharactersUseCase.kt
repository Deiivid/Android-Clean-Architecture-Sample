package es.davidnavarro.androidcleanarchitecture.core.domain.character
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogError
import es.davidnavarro.androidcleanarchitecture.core.domain.result.CatalogResult
import es.davidnavarro.androidcleanarchitecture.core.model.Character
import es.davidnavarro.androidcleanarchitecture.core.model.Page

class GetCharactersUseCase(private val repository: CharacterRepository) {
    suspend operator fun invoke(page: Int = 1): CatalogResult<Page<Character>, CatalogError> =
        repository.getCharacters(page)
}
