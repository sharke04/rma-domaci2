package rs.edu.raf.rma.showtime.accounts.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.networking.ShowtimeApi
import rs.edu.raf.rma.showtime.favourites.FavouritesRepository
import rs.edu.raf.rma.showtime.quiz.QuizResultsRepository
import rs.edu.raf.rma.showtime.watchlist.WatchlistRepository

class AccountDetailsViewModel(
    private val showtimeApi: ShowtimeApi,
    private val authStore: AuthStore,
    private val favouritesRepository: FavouritesRepository,
    private val watchlistRepository: WatchlistRepository,
    private val quizResultsRepository: QuizResultsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountDetailsContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: AccountDetailsContract.UiState.() -> AccountDetailsContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val _effects = MutableSharedFlow<AccountDetailsContract.UiEffect>()
    val effects = _effects.asSharedFlow()

    private val events = MutableSharedFlow<AccountDetailsContract.UiEvent>()

    fun setEvent(event: AccountDetailsContract.UiEvent) {
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
        loadProfile()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    AccountDetailsContract.UiEvent.Logout -> logout()
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val profile = showtimeApi.getProfile()
                setState { copy(fullName = profile.fullName, username = profile.username) }
                observeFavouritesNumber(profile.id)
                observeWatchlistNumber(profile.id)
                observeBestQuizResult(profile.id)
            } catch (_: Exception) {
                setState { copy(error = "Failed to load profile.") }
            }
            setState { copy(isLoading = false) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authStore.clearAuthData()
            _effects.emit(AccountDetailsContract.UiEffect.LogoutSuccess)
        }
    }

    private fun observeFavouritesNumber(userId: Int) {
        viewModelScope.launch {
            favouritesRepository.observeNumberOfFavourites(userId).collect { count ->
                setState { copy(favourites = count) }
            }
        }
    }

    private fun observeWatchlistNumber(userId: Int) {
        viewModelScope.launch {
            watchlistRepository.observeNumberOfWatchlistedMovies(userId).collect { count ->
                setState { copy(watchlistSize = count) }
            }
        }
    }

    private fun observeBestQuizResult(userId: Int) {
        viewModelScope.launch {
            quizResultsRepository.observeBestResult(userId).collect { result ->
                setState { copy(bestQuizResult = result) }
            }
        }
    }
}
