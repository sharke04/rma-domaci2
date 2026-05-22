package rs.edu.raf.rma.showtime.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.networking.MoviesApi
import rs.edu.raf.rma.networking.model.ApiErrorResponse
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository
import rs.edu.raf.rma.showtime.movieIdOrThrow

class MovieDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val showtimeRepository: ShowtimeRepository,
    private val moviesApi: MoviesApi,
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailsContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: MovieDetailsContract.UiState.() -> MovieDetailsContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val movieId: String = savedStateHandle.movieIdOrThrow

    init {
        observeMovie()
        observeMovieImages()
        observeMovieVideo()
        loadMovieDetails()
    }

    private fun observeMovie() {
        viewModelScope.launch {
            showtimeRepository.observeMovie(movieId).collect { movie ->
                setState { copy(movie = movie) }
            }
        }
    }

    private fun observeMovieImages() {
        viewModelScope.launch {
            showtimeRepository.observeMovieImages(movieId).collect { images ->
                setState { copy(images = images) }
            }
        }
    }

    private fun observeMovieVideo() {
        viewModelScope.launch {
            showtimeRepository.observeMovieVideo(movieId).collect { video ->
                setState { copy(video = video) }
            }
        }
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                coroutineScope {
                    val detailsDeferred = async { showtimeRepository.refreshMovieDetails(movieId) }
                    val imagesDeferred = async { showtimeRepository.refreshMovieImages(movieId) }
                    val videosDeferred = async { showtimeRepository.refreshMovieVideos(movieId) }
                    val actorsDeferred = async { moviesApi.getMovieCast(id = movieId, pageSize = 10) }

                    detailsDeferred.await()
                    imagesDeferred.await()
                    videosDeferred.await()
                    val actors = actorsDeferred.await()

                    setState {
                        copy(
                            isLoading = false,
                            actors = actors.items,
                        )
                    }
                }
            } catch (e: Exception) {
                val userMessage = when (e) {
                    is ResponseException -> {
                        try {
                            val apiError = e.response.body<ApiErrorResponse>()
                            apiError.message
                        } catch (_: Exception) {
                            "Service unavailable. Please try again later."
                        }
                    }
                    else -> "Check your internet connection."
                }

                setState {
                    copy(
                        isLoading = false,
                        error = Exception(userMessage)
                    )
                }
            }
        }
    }
}