package rs.edu.raf.rma.showtime.accounts

interface AccountsContract {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val registrationSuccessful: Boolean = false,
    )

    sealed class UiEvent {
        data class Register(
            val fullName: String,
            val username: String,
            val password: String,
            val repeatPassword: String,
        ) : UiEvent()
    }
}
