package rs.edu.raf.rma.showtime.movies.details

import rs.edu.raf.rma.networking.model.PersonSummaryApiModel
import rs.edu.raf.rma.networking.model.VideoApiModel
import rs.edu.raf.rma.showtime.domain.Image
import rs.edu.raf.rma.showtime.domain.Movie

interface MovieDetailsContract {
    data class UiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
        val images: List<Image> = emptyList(),
        val actors: List<PersonSummaryApiModel> = emptyList(),
        val video: VideoApiModel? = null,
        val error: Throwable? = null
    )
}