package rs.edu.raf.rma.showtime.accounts.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.auth.model.AuthData
import rs.edu.raf.rma.networking.AccountsApi
import rs.edu.raf.rma.networking.model.ErrorResponse
import rs.edu.raf.rma.networking.model.LoginBody
import rs.edu.raf.rma.networking.model.RegisterBody

class AccountRegistrationViewModel(
    private val accountsApi: AccountsApi,
    private val authStore: AuthStore,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountRegistrationContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: AccountRegistrationContract.UiState.() -> AccountRegistrationContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<AccountRegistrationContract.UiEvent>()

    fun setEvent(event: AccountRegistrationContract.UiEvent) {
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is AccountRegistrationContract.UiEvent.Register -> register(
                        fullName = event.fullName,
                        username = event.username,
                        password = event.password,
                        repeatPassword = event.repeatPassword,
                    )
                    is AccountRegistrationContract.UiEvent.Login -> login(
                        username = event.username,
                        password = event.password,
                    )
                }
            }
        }
    }

    private fun register(
        fullName: String,
        username: String,
        password: String,
        repeatPassword: String,
    ) {
        if (fullName.isBlank() || username.isBlank() || password.isBlank()) {
            setState { copy(error = "All fields are required.") }
            return
        }
        if (password != repeatPassword) {
            setState { copy(error = "Passwords do not match.") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            var responseException: ResponseException? = null

            try {
                val response = accountsApi.signUp(
                    RegisterBody(
                        fullName = fullName,
                        username = username,
                        password = password
                    )
                )
                authStore.setAuthData(
                    AuthData(
                        accessToken = response.accessToken,
                        username = response.user.username,
                        userId = response.user.id,
                    )
                )
                setState { copy(registrationSuccessful = true) }
            } catch (e: ResponseException) {
                responseException = e
            } catch (_: Exception) {
                setState { copy(error = "Registration failed.") }
            }

            if (responseException != null) {
                val message = runCatching {
                    responseException.response.body<ErrorResponse>().message
                }.getOrElse { "Registration failed." }

                setState { copy(error = message) }
            }

            setState { copy(isLoading = false) }
        }
    }

    private fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            setState { copy(error = "All fields are required.") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            var responseException: ResponseException? = null

            try {
                val response = accountsApi.login(
                    LoginBody(
                        username = username,
                        password = password
                    )
                )
                authStore.setAuthData(
                    AuthData(
                        accessToken = response.accessToken,
                        username = response.user.username,
                        userId = response.user.id,
                    )
                )
                setState { copy(loginSuccessful = true) }
            } catch (e: ResponseException) {
                responseException = e
            } catch (_: Exception) {
                setState { copy(error = "Login failed.") }
            }

            if (responseException != null) {
                val message = runCatching {
                    responseException.response.body<ErrorResponse>().message
                }.getOrElse { "Login failed." }

                setState { copy(error = message) }
            }

            setState { copy(isLoading = false) }
        }
    }
}