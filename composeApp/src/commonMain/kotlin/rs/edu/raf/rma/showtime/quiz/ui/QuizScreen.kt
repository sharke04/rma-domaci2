package rs.edu.raf.rma.showtime.quiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rs.edu.raf.rma.showtime.quiz.QuizContract
import rs.edu.raf.rma.showtime.quiz.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    QuizScreen(
        state = state,
        eventPublisher = viewModel::setEvent,
        onClose = onClose,
    )
}

@Composable
private fun QuizScreen(
    state: QuizContract.UiState,
    eventPublisher: (QuizContract.UiEvent) -> Unit,
    onClose: () -> Unit,
) {
    if (state.navigateUp) {
        LaunchedEffect(Unit) { onClose() }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(bottom = 20.dp),
    ) {
        when (state.phase) {
            QuizContract.Phase.Loading -> LoadingView(state.error)
            QuizContract.Phase.Active -> ActiveQuizView(state, eventPublisher)
            QuizContract.Phase.Result -> ResultView(state, onClose)
        }
    }
}

@Composable
private fun LoadingView(error: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (error != null) {
            Text(
                text = error,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp),
            )
        } else {
            CircularProgressIndicator(color = Color.Red)
        }
    }
}