package rs.edu.raf.rma.showtime

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.compose.viewmodel.koinViewModel
import rs.edu.raf.rma.premiere.details.MovieDetailsViewModel
import rs.edu.raf.rma.premiere.details.ui.MovieDetailsScreen
import rs.edu.raf.rma.premiere.list.MoviesListViewModel
import rs.edu.raf.rma.premiere.list.ui.MoviesListScreen

@Composable
fun ShowtimeNavigation(
    startDestination: String,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(route = "welcome") {

        }

        composable(route = "movies") {
            val viewModel = koinViewModel<MoviesListViewModel>()
            MoviesListScreen(
                viewModel = viewModel,
                onMovieClick = { navController.navigateToMovieDetails(movieId = it) },
            )
        }

        composable(
            route = "movies/{$MOVIE_ID}",
            arguments = listOf(
                navArgument(MOVIE_ID) {
                    type = NavType.StringType
                    nullable = false
                },
            ),
        ) {
            val viewModel = koinViewModel<MovieDetailsViewModel>()
            MovieDetailsScreen(
                viewModel = viewModel,
                onClose = { navController.navigateUp() },
            )
        }
    }
}

private fun NavController.navigateToMovieDetails(movieId: String) {
    navigate("movies/$movieId")
}

const val MOVIE_ID = "movieId"
inline val SavedStateHandle.movieIdOrThrow: String get() = get(MOVIE_ID)
    ?: throw IllegalStateException("$MOVIE_ID is mandatory and can not be null")
