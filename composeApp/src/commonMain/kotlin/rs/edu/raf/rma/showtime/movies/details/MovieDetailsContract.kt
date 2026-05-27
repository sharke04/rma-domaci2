package rs.edu.raf.rma.showtime.movies.details

import rs.edu.raf.rma.showtime.domain.Actor
import rs.edu.raf.rma.showtime.domain.Image
import rs.edu.raf.rma.showtime.domain.Movie
import rs.edu.raf.rma.showtime.domain.Video

interface MovieDetailsContract {
    data class UiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
        val images: List<Image> = emptyList(),
        val actors: List<Actor> = emptyList(),
        val video: Video? = null,
        val error: Throwable? = null,
        val isFavourite: Boolean? = null,
    )

    sealed interface UiEvent {
        data object ToggleFavourite : UiEvent
        data object Retry : UiEvent
    }
}