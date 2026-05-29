package rs.edu.raf.rma.showtime.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val repository: WatchlistRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: WatchlistContract.UiState.() -> WatchlistContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    init {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val userId = repository.sync()
                repository.observeWatchlist(userId)
                    .onEach { setState { copy(movies = it, isLoading = false) } }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}
