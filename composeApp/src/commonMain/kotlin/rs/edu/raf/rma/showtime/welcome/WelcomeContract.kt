package rs.edu.raf.rma.showtime.welcome

interface WelcomeContract {
    data class UiState(
        val username: String? = null,
    )

    sealed interface UiEvent {
        data object Logout : UiEvent
    }
}
