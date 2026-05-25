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
import rs.edu.raf.rma.showtime.accounts.AccountsViewModel
import rs.edu.raf.rma.showtime.accounts.ui.LoginScreen
import rs.edu.raf.rma.showtime.accounts.ui.RegisterScreen
import rs.edu.raf.rma.showtime.movies.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.movies.details.ui.MovieDetailsScreen
import rs.edu.raf.rma.showtime.movies.list.MoviesListViewModel
import rs.edu.raf.rma.showtime.movies.list.ui.MoviesListScreen
import rs.edu.raf.rma.showtime.welcome.ShowtimeWelcomeScreen

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
            ShowtimeWelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") },
                onMoviesClick = { navController.navigate("movies") },
            )
        }

        composable(route = "login") {
            val viewModel = koinViewModel<AccountsViewModel>()
            LoginScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onLoginSuccess = {
                    navController.navigate("movies") {
                        popUpTo("welcome") { inclusive = false }
                    }
                },
            )
        }

        composable(route = "register") {
            val viewModel = koinViewModel<AccountsViewModel>()
            RegisterScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onRegisterSuccess = { navController.navigate("welcome") },
            )
        }

        composable(route = "movies") {
            val viewModel = koinViewModel<MoviesListViewModel>()
            MoviesListScreen(
                viewModel = viewModel,
                onMovieClick = { navController.navigateToMovieDetails(movieId = it) },
                onBack = { navController.navigateUp() },
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
