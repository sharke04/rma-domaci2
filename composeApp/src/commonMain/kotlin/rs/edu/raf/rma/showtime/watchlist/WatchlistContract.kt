package rs.edu.raf.rma.showtime.watchlist

import rs.edu.raf.rma.showtime.domain.Movie

interface WatchlistContract {
    data class UiState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    sealed interface UiEvent
}
