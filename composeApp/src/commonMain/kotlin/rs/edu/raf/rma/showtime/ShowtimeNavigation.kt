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
import rs.edu.raf.rma.showtime.accounts.registration.AccountRegistrationViewModel
import rs.edu.raf.rma.showtime.accounts.registration.ui.LoginScreen
import rs.edu.raf.rma.showtime.accounts.registration.ui.RegisterScreen
import rs.edu.raf.rma.showtime.movies.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.movies.details.ui.MovieDetailsScreen
import rs.edu.raf.rma.showtime.movies.list.MoviesListViewModel
import rs.edu.raf.rma.showtime.movies.list.ui.MoviesListScreen
import rs.edu.raf.rma.showtime.accounts.details.AccountDetailsScreen
import rs.edu.raf.rma.showtime.accounts.details.AccountDetailsViewModel
import rs.edu.raf.rma.showtime.welcome.ShowtimeWelcomeScreen
import rs.edu.raf.rma.showtime.welcome.WelcomeViewModel

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
            val viewModel = koinViewModel<WelcomeViewModel>()
            ShowtimeWelcomeScreen(
                viewModel = viewModel,
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") },
                onMoviesClick = { navController.navigate("movies") },
                onAccountClick = { navController.navigate("account_details") },
            )
        }

        composable(route = "account_details") {
            val viewModel = koinViewModel<AccountDetailsViewModel>()
            AccountDetailsScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onLogout = { navController.navigateToWelcome() },
            )
        }

        composable(route = "login") {
            val viewModel = koinViewModel<AccountRegistrationViewModel>()
            LoginScreen(
                viewModel = viewModel,
                onBack = { navController.navigateUp() },
                onLoginSuccess = { navController.navigate("welcome") },
            )
        }

        composable(route = "register") {
            val viewModel = koinViewModel<AccountRegistrationViewModel>()
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

private fun NavController.navigateToWelcome() {
    navigate("welcome") { popUpTo("welcome") { inclusive = false } }
}

private fun NavController.navigateToMovieDetails(movieId: String) {
    navigate("movies/$movieId")
}

const val MOVIE_ID = "movieId"
inline val SavedStateHandle.movieIdOrThrow: String get() = get(MOVIE_ID)
    ?: throw IllegalStateException("$MOVIE_ID is mandatory and can not be null")
