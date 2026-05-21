package rs.edu.raf.rma.premiere.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.edu.raf.rma.networking.MoviesApi
import rs.edu.raf.rma.networking.model.ApiErrorResponse
import kotlin.math.roundToInt

class MoviesListViewModel(
    private val moviesApi: MoviesApi
) : ViewModel() {
    private val _state = MutableStateFlow(MoviesListContract.UiState())
    val state = _state.asStateFlow()
    val genreIds = HashMap<String, Int>()

    private fun setState(reducer: MoviesListContract.UiState.() -> MoviesListContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<MoviesListContract.UiEvent>()

    fun setEvent(event: MoviesListContract.UiEvent) {
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
        loadData()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is MoviesListContract.UiEvent.OnSortChanged -> {
                        setState { copy(sortBy = event.sortField) }
                        loadData()
                    }
                    is MoviesListContract.UiEvent.ToggleSortDirection -> {
                        setState { copy(isAscending = !isAscending) }
                        loadData()
                    }
                    is MoviesListContract.UiEvent.ToggleFilter -> {
                        if (!state.value.isFilterOpen) {
                            setState {
                                copy(
                                    isFilterOpen = true,
                                    searchTitle = activeSearchTitle,
                                    selectedGenre = activeSelectedGenre,
                                    yearFrom = activeYearFrom,
                                    yearTo = activeYearTo,
                                    minRating = activeMinRating
                                )
                            }
                        } else {
                            setState { copy(isFilterOpen = false) }
                        }
                    }
                    is MoviesListContract.UiEvent.UpdateSearch -> {
                        setState { copy(searchTitle = event.query) }
                    }
                    is MoviesListContract.UiEvent.SelectGenre -> setState {
                        copy(selectedGenre = if (selectedGenre == event.genre) null else event.genre)
                    }
                    is MoviesListContract.UiEvent.UpdateYearRange -> setState {
                        copy(yearFrom = event.from, yearTo = event.to)
                    }
                    is MoviesListContract.UiEvent.UpdateRating -> setState {
                        copy(minRating = event.rating)
                    }
                    is MoviesListContract.UiEvent.ApplyFilters -> {
                        setState {
                            copy(
                                isFilterOpen = false,
                                activeSearchTitle = searchTitle,
                                activeSelectedGenre = selectedGenre,
                                activeYearFrom = yearFrom,
                                activeYearTo = yearTo,
                                activeMinRating = minRating
                            )
                        }
                        loadData()
                    }
                    is MoviesListContract.UiEvent.ClearAllFilters -> {
                        setState {
                            copy(
                                searchTitle = "",
                                selectedGenre = null,
                                minRating = 0f,
                                yearFrom = "1920",
                                yearTo = "2025",
                                activeSearchTitle = "",
                                activeSelectedGenre = null,
                                activeMinRating = 0f,
                                activeYearFrom = "1920",
                                activeYearTo = "2025"
                            )
                        }
                        loadData()
                    }
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val genres = moviesApi.getGenres()
            genres.forEach {
                genreIds[it.name] = it.id
            }

            val currentState = _state.value
            val sortBy = getSortingColumnName(currentState.sortBy)
            val sortingOrder = if (currentState.isAscending) "asc" else "desc"
            val title = currentState.activeSearchTitle.takeIf { it.isNotBlank() }
            val genre = currentState.activeSelectedGenre
            val yearFrom = currentState.activeYearFrom.toIntOrNull()
            val yearTo = currentState.activeYearTo.toIntOrNull()
            val minRating = roundToHalf(currentState.activeMinRating)

            val apiResult = withContext(Dispatchers.IO) {
                runCatching {
                    val response = moviesApi.getMovies(
                        sortBy = sortBy,
                        sortOrder = sortingOrder,
                        query = title,
                        genreId = genreIds[genre],
                        minYear = yearFrom,
                        maxYear = yearTo,
                        minRating = minRating
                    )
                    response.copy(
                        items = response.items
                    )
                }
            }

            apiResult.fold(
                onSuccess = {
                    setState {
                        copy(
                            isLoading = false,
                            movies = it.items
                        )
                    }
                },
                onFailure = { throwable ->
                    val userMessage = when (throwable) {
                        is ResponseException -> {
                            try {
                                val apiError = throwable.response.body<ApiErrorResponse>()
                                apiError.message
                            } catch (_: Exception) {
                                "Error ${throwable.response.status.value}: Unable to load movies."
                            }
                        }
                        else -> throwable.message ?: "Network error occurred."
                    }

                    setState {
                        copy(
                            isLoading = false,
                            error = Exception(userMessage)
                        )
                    }
                }
            )
        }
    }

    fun getGenreList(): List<String> {
        return genreIds.keys.toList()
    }

    private fun roundToHalf(rating: Float): Float? {
        if (rating > 0)
            return (rating * 2).roundToInt() / 2f
        return null
    }

    private fun getSortingColumnName(sortingOption: String): String {
        return if (sortingOption == "Rating")
            "imdb_rating"
        else
            sortingOption.lowercase()
    }
}