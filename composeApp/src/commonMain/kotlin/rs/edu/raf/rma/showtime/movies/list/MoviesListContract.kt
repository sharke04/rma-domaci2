package rs.edu.raf.rma.showtime.movies.list

import rs.edu.raf.rma.showtime.domain.Genre
import rs.edu.raf.rma.showtime.domain.Movie

interface MoviesListContract {
    data class UiState(
        val movies: List<Movie> = emptyList(),
        val genres: List<Genre> = emptyList(),
        val isLoading: Boolean = true,
        val error: Throwable? = null,
        val sortBy: String = "Rating",
        val isAscending: Boolean = false,
        val isFilterOpen: Boolean = false,

        val searchTitle: String = "",
        val selectedGenre: String? = null,
        val yearFrom: String = "1920",
        val yearTo: String = "2025",
        val minRating: Float = 0f,

        val activeSearchTitle: String = "",
        val activeSelectedGenre: String? = null,
        val activeYearFrom: String = "1920",
        val activeYearTo: String = "2025",
        val activeMinRating: Float = 0f,
    )
    sealed class UiEvent {
        data class OnSortChanged(val sortField: String) : UiEvent()
        data object ToggleSortDirection : UiEvent()
        data object ToggleFilter : UiEvent()
        data class UpdateSearch(val query: String) : UiEvent()
        data class SelectGenre(val genre: String) : UiEvent()
        data class UpdateYearRange(val from: String, val to: String) : UiEvent()
        data class UpdateRating(val rating: Float) : UiEvent()
        data object ApplyFilters : UiEvent()
        data object ClearAllFilters : UiEvent()
    }
}