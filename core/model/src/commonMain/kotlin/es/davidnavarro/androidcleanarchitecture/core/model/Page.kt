package es.davidnavarro.androidcleanarchitecture.core.model

data class Page<T>(val items: List<T>, val number: Int, val totalPages: Int, val totalItems: Int) {
    val hasNextPage: Boolean get() = number < totalPages
}
