package rs.edu.raf.rma.showtime.movies.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository

class MoviesListViewModel(
    private val showtimeRepository: ShowtimeRepository
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
        observeMovies()
        fetchData()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is MoviesListContract.UiEvent.OnSortChanged -> {
                        setState { copy(sortBy = event.sortField) }
                    }
                    is MoviesListContract.UiEvent.ToggleSortDirection -> {
                        setState { copy(isAscending = !isAscending) }
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
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMovies() {
        viewModelScope.launch {
            state
                .distinctUntilChanged { old, new ->
                    old.activeSearchTitle == new.activeSearchTitle &&
                    old.activeSelectedGenre == new.activeSelectedGenre &&
                    old.activeYearFrom == new.activeYearFrom &&
                    old.activeYearTo == new.activeYearTo &&
                    old.activeMinRating == new.activeMinRating &&
                    old.sortBy == new.sortBy &&
                    old.isAscending == new.isAscending
                }
                .flatMapLatest { s ->
                    showtimeRepository.observeMovies(
                        name = s.activeSearchTitle,
                        genreId = getGenreId(s.activeSelectedGenre),
                        minYear = s.activeYearFrom.toIntOrNull() ?: 1920,
                        maxYear = s.activeYearTo.toIntOrNull() ?: 2025,
                        minRating = s.activeMinRating,
                        sortBy = s.sortBy,
                        sortOrder = getSortOrder(s.isAscending),
                    )
                }
                .collect { movies ->
                    setState { copy(movies = movies) }
                }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            runCatching { showtimeRepository.refreshMovies() }
                .onFailure { setState { copy(error = it) } }

            setState { copy(isLoading = false) }
        }
    }

    fun getGenreList(): List<String> {
        return genreIds.keys.toList()
    }

    private fun getGenreId(genreName: String?): Int {
        if (genreName == null) return 0
        return genreIds[genreName] ?: 0
    }

    private fun getSortOrder(isAscending: Boolean): String {
        return if (isAscending) "asc" else "desc"
    }
}