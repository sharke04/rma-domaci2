package rs.edu.raf.rma.showtime.favourites

import rs.edu.raf.rma.showtime.domain.Movie

interface FavouritesContract {
    data class UiState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    sealed interface UiEvent
}
