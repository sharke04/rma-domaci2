package rs.edu.raf.rma.showtime.accounts.details

interface AccountDetailsContract {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val fullName: String? = null,
        val username: String? = null,
    )
}
