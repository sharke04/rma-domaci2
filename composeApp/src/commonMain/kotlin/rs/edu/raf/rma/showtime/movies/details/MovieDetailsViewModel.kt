package rs.edu.raf.rma.showtime.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.auth.model.AuthState
import rs.edu.raf.rma.networking.model.ApiErrorResponse
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository
import rs.edu.raf.rma.showtime.favourites.FavouritesRepository
import rs.edu.raf.rma.showtime.movieIdOrThrow
import rs.edu.raf.rma.showtime.watchlist.WatchlistRepository

class MovieDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val showtimeRepository: ShowtimeRepository,
    private val favouritesRepository: FavouritesRepository,
    private val watchlistRepository: WatchlistRepository,
    private val authStore: AuthStore,
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailsContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: MovieDetailsContract.UiState.() -> MovieDetailsContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val movieId: String = savedStateHandle.movieIdOrThrow

    private val events = MutableSharedFlow<MovieDetailsContract.UiEvent>()

    fun setEvent(event: MovieDetailsContract.UiEvent) {
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
        observeMovie()
        observeMovieImages()
        observeMovieVideo()
        observeMovieActors()
        loadMovieDetails()
        observeAuthState()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    MovieDetailsContract.UiEvent.ToggleFavourite -> toggleFavourite()
                    MovieDetailsContract.UiEvent.ToggleWatchlist -> toggleWatchlist()
                    MovieDetailsContract.UiEvent.Retry -> loadMovieDetails()
                }
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authStore.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        loadIsFavourite()
                        loadIsWatchlisted()
                    }
                    AuthState.Unauthenticated -> setState { copy(isFavourite = null, isWatchlisted = null) }
                }
            }
        }
    }

    private fun loadIsFavourite() {
        viewModelScope.launch {
            val favourite = favouritesRepository.isFavourite(movieId)
            setState { copy(isFavourite = favourite) }
        }
    }

    private fun loadIsWatchlisted() {
        viewModelScope.launch {
            val watchlisted = watchlistRepository.isWatchlisted(movieId)
            setState { copy(isWatchlisted = watchlisted) }
        }
    }

    private fun toggleFavourite() {
        val current = state.value.isFavourite ?: return
        setState { copy(isFavourite = !current) }
        viewModelScope.launch {
            try {
                if (!current) favouritesRepository.addFavourite(movieId)
                else favouritesRepository.removeFavourite(movieId)
            } catch (_: Exception) {
                setState { copy(isFavourite = current) }
            }
        }
    }

    private fun toggleWatchlist() {
        val current = state.value.isWatchlisted ?: return
        setState { copy(isWatchlisted = !current) }
        viewModelScope.launch {
            try {
                if (!current) watchlistRepository.addToWatchlist(movieId)
                else watchlistRepository.removeFromWatchlist(movieId)
            } catch (_: Exception) {
                setState { copy(isWatchlisted = current) }
            }
        }
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

    private fun observeMovieActors() {
        viewModelScope.launch {
            showtimeRepository.observeMovieActors(movieId).collect { actors ->
                setState { copy(actors = actors) }
            }
        }
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                coroutineScope {
                    val detailsDeferred = async { showtimeRepository.refreshMovieDetails(movieId) }
                    val imagesDeferred = async { showtimeRepository.refreshMovieImages(movieId) }
                    val videosDeferred = async { showtimeRepository.refreshMovieVideos(movieId) }
                    val actorsDeferred = async { showtimeRepository.refreshMovieActors(movieId) }

                    detailsDeferred.await()
                    imagesDeferred.await()
                    videosDeferred.await()
                    actorsDeferred.await()

                    setState { copy(isLoading = false) }
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