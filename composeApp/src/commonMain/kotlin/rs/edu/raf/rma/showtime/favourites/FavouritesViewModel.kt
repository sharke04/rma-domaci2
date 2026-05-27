package rs.edu.raf.rma.showtime.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val repository: FavouritesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FavouritesContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: FavouritesContract.UiState.() -> FavouritesContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    init {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val userId = repository.sync()
                repository.observeFavourites(userId)
                    .onEach { setState { copy(movies = it, isLoading = false) } }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}
