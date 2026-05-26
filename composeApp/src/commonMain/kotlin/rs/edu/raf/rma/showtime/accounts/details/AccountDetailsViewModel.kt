package rs.edu.raf.rma.showtime.accounts.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.networking.ShowtimeApi

class AccountDetailsViewModel(
    private val showtimeApi: ShowtimeApi,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountDetailsContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: AccountDetailsContract.UiState.() -> AccountDetailsContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val profile = showtimeApi.getProfile()
                setState { copy(fullName = profile.fullName, username = profile.username) }
            } catch (_: Exception) {
                setState { copy(error = "Failed to load profile.") }
            }
            setState { copy(isLoading = false) }
        }
    }
}
