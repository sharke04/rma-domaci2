package rs.edu.raf.rma.showtime.movies.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.showtime.movies.details.MovieDetailsContract
import rs.edu.raf.rma.showtime.movies.details.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    viewModel: MovieDetailsViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    MovieDetailsScreen(
        state = state,
        eventPublisher = { viewModel.setEvent(it) },
        onClose = onClose,
    )
}

@Composable
private fun MovieDetailsScreen(
    state: MovieDetailsContract.UiState,
    eventPublisher: (MovieDetailsContract.UiEvent) -> Unit,
    onClose: () -> Unit,
) {
    Scaffold(containerColor = Color.Black) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Red)
            } else if (state.error != null) {
                ErrorView(
                    onRetry = { eventPublisher(MovieDetailsContract.UiEvent.Retry) },
                    onBack = onClose,
                    message = state.error.message,
                )
            } else {
                state.movie?.let { movie ->
                    MovieDetailsContent(
                        movie = movie,
                        images = state.images,
                        actors = state.actors,
                        video = state.video,
                        isFavourite = state.isFavourite,
                        onBack = onClose,
                        onToggleFavourite = { eventPublisher(MovieDetailsContract.UiEvent.ToggleFavourite) },
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorView(
    onRetry: () -> Unit,
    onBack: () -> Unit,
    message: String?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White.copy(0.1f), CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message?: "Oops! Something went wrong.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We couldn't load the movie details. Please check your connection and try again.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "RETRY", color = Color.White, fontWeight = Bold)
            }
        }
    }
}