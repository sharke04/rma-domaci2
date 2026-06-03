package rs.edu.raf.rma.showtime.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.auth.model.AuthState
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository
import rs.edu.raf.rma.showtime.favourites.FavouritesRepository
import rs.edu.raf.rma.showtime.watchlist.WatchlistRepository

class WelcomeViewModel(
    private val authStore: AuthStore,
    private val showtimeRepository: ShowtimeRepository,
    private val favouritesRepository: FavouritesRepository,
    private val watchlistRepository: WatchlistRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WelcomeContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: WelcomeContract.UiState.() -> WelcomeContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    init {
        observeAuthState()
        refreshMovies()
        syncCollections()
    }

    private fun refreshMovies() {
        viewModelScope.launch {
            showtimeRepository.refreshMovies()
        }
    }

    private fun syncCollections() {
        viewModelScope.launch {
            favouritesRepository.sync()
        }
        viewModelScope.launch {
            watchlistRepository.sync()
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authStore.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> setState { copy(username = authState.data.username) }
                    AuthState.Unauthenticated -> setState { copy(username = null) }
                }
            }
        }
    }
}
