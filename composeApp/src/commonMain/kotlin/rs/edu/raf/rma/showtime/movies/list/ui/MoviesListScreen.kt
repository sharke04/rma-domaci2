package rs.edu.raf.rma.showtime.movies.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.showtime.movies.list.MoviesListContract
import rs.edu.raf.rma.showtime.movies.list.MoviesListViewModel

@Composable
fun MoviesListScreen(
    viewModel: MoviesListViewModel,
    onMovieClick: (movieId: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    MoviesListScreen(
        viewModel = viewModel,
        state = state,
        onMovieClick = onMovieClick,
        eventPublisher = viewModel::setEvent,
    )
}

@Composable
private fun MoviesListScreen(
    viewModel: MoviesListViewModel,
    state: MoviesListContract.UiState,
    onMovieClick: (movieId: String) -> Unit,
    eventPublisher: (MoviesListContract.UiEvent) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            if (state.isFilterOpen)
                FilterTopBar(eventPublisher)
            else
                DefaultTopBar(eventPublisher, state)
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (state.isFilterOpen) {
                    FilterView(
                        state = state,
                        onUpdateSearch = { query -> eventPublisher(MoviesListContract.UiEvent.UpdateSearch(query)) },
                        onSelectGenre = { genre -> eventPublisher(MoviesListContract.UiEvent.SelectGenre(genre)) },
                        onUpdateYearRange = { from, to -> eventPublisher(MoviesListContract.UiEvent.UpdateYearRange(from, to)) },
                        onUpdateRating = { rating -> eventPublisher(MoviesListContract.UiEvent.UpdateRating(rating)) },
                        onApplyFilters = { eventPublisher(MoviesListContract.UiEvent.ApplyFilters) },
                        viewModel = viewModel,
                    )
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = scrollState)
                            .background(color = Color.Black)
                    ) {
                        if (state.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (state.error != null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Error: ${state.error.message}")
                            }
                        } else if (state.movies.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .padding(vertical = 15.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No movies found",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try adjusting your filters",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            state.movies.forEach { movie ->
                                MovieListItem(
                                    movie,
                                    onClick = onMovieClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterTopBar(
    eventPublisher: (MoviesListContract.UiEvent) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Filter Movies",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { eventPublisher(MoviesListContract.UiEvent.ToggleFilter) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0F101C),
            titleContentColor = Color.White,
        ),
        actions = {
            TextButton(
                onClick = { eventPublisher(MoviesListContract.UiEvent.ClearAllFilters) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = "Clear All",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DefaultTopBar(
    eventPublisher: (MoviesListContract.UiEvent) -> Unit,
    state: MoviesListContract.UiState,
) {
    Column(modifier = Modifier.background(color = Color.Black)) {
        TopAppBar(
            title = {
                Text(
                    text = "Premiere",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                Box(modifier = Modifier.padding(start = 16.dp, end = 4.dp)) {
                    Text(
                        text = "🎬",
                        fontSize = 24.sp 
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF0F101C), 
                titleContentColor = Color.White,
            ),

            actions = {
                val activeFilterCount = listOf(
                    state.activeSearchTitle.isNotEmpty(),
                    state.activeSelectedGenre != null,
                    state.activeMinRating > 0f,
                    state.activeYearFrom != "1920" || state.activeYearTo != "2025"
                ).count { it }

                Box(modifier = Modifier.padding(end = 16.dp)) {
                    Button(
                        onClick = { eventPublisher(MoviesListContract.UiEvent.ToggleFilter) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Filter", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (activeFilterCount > 0) {
                        Surface(
                            color = Color(0xFFFFD700), 
                            shape = CircleShape,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = activeFilterCount.toString(),
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        )
        SortingHeader(
            currentSort = state.sortBy,
            movieCount = state.movies.size,
            onSortSelected = { newSort ->
                eventPublisher(MoviesListContract.UiEvent.OnSortChanged(newSort))
            },
            isAscending = state.isAscending,
            onDirectionToggle = { eventPublisher(MoviesListContract.UiEvent.ToggleSortDirection) }
        )
    }
}
