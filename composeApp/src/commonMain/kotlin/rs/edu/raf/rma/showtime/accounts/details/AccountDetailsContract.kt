package rs.edu.raf.rma.showtime.accounts.details

interface AccountDetailsContract {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val fullName: String? = null,
        val username: String? = null,
        val favourites: Int = 0,
    )

    sealed interface UiEvent {
        data object Logout : UiEvent
    }

    sealed interface UiEffect {
        data object LogoutSuccess : UiEffect
    }
}
