package rs.edu.raf.rma.showtime.quiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.showtime.quiz.QuizContract

@Composable
fun ActiveQuizView(
    state: QuizContract.UiState,
    eventPublisher: (QuizContract.UiEvent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { state.questions.size.coerceAtLeast(1) })

    LaunchedEffect(state.currentIndex) {
        if (state.questions.isNotEmpty()) {
            pagerState.animateScrollToPage(state.currentIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val progress = state.timeRemaining / 60f
        val timerColor = if (state.timeRemaining <= 10) Color.Red else Color(0xFFE53935)

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Question ${state.currentIndex + 1} / ${state.questions.size}",
                    color = Color.Gray,
                    fontSize = 13.sp,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${state.timeRemaining}s",
                        color = timerColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                    TextButton(onClick = { eventPublisher(QuizContract.UiEvent.BackPressed) }) {
                        Text(text = "Quit", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = timerColor,
                trackColor = Color.DarkGray,
            )
        }

        if (state.questions.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                QuestionPage(
                    question = state.questions[page],
                    selectedAnswerIndex = state.answers.getOrNull(page),
                    onAnswerSelected = { eventPublisher(QuizContract.UiEvent.AnswerSelected(it)) },
                )
            }
        }
    }

    if (state.showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { eventPublisher(QuizContract.UiEvent.AbandonDismissed) },
            title = { Text("Abandon quiz?", color = Color.White) },
            text = { Text("Your progress will be lost.", color = Color.Gray) },
            confirmButton = {
                TextButton(onClick = { eventPublisher(QuizContract.UiEvent.AbandonConfirmed) }) {
                    Text("Abandon", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventPublisher(QuizContract.UiEvent.AbandonDismissed) }) {
                    Text("Continue", color = Color.White)
                }
            },
            containerColor = Color(0xFF1C1C1C),
        )
    }
}