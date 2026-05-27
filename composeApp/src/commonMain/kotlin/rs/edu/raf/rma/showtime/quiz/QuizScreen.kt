package rs.edu.raf.rma.showtime.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

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
            .background(Color.Black),
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

@Composable
private fun ActiveQuizView(
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

@Composable
private fun QuestionPage(
    question: QuizQuestion,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit,
) {
    val answered = selectedAnswerIndex != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = question.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(260.dp),
        )

        val title = when (question) {
            is QuizQuestion.GuessTheYear -> question.title
            is QuizQuestion.GuessTheActor -> question.title
            is QuizQuestion.GuessTheMovie -> null
        }
        if (title != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = when (question) {
                is QuizQuestion.GuessTheMovie -> "Which movie is this?"
                is QuizQuestion.GuessTheYear -> "When was this movie released?"
                is QuizQuestion.GuessTheActor -> "Who is the lead actor?"
            },
            color = Color.LightGray,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            question.answers.forEachIndexed { index, answer ->
                val containerColor = when {
                    !answered -> Color(0xFF2A2A2A)
                    index == question.correctAnswerIndex -> Color(0xFF2E7D32)
                    index == selectedAnswerIndex -> Color(0xFFB71C1C)
                    else -> Color(0xFF2A2A2A)
                }
                Button(
                    onClick = { onAnswerSelected(index) },
                    enabled = !answered,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        disabledContainerColor = containerColor,
                        disabledContentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = answer,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultView(
    state: QuizContract.UiState,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Quiz Complete!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "${state.score}",
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp,
        )
        Text(text = "out of 100", color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        ResultRow(label = "Correct", value = "${state.correctCount}", valueColor = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(8.dp))
        ResultRow(label = "Incorrect", value = "${state.incorrectCount}", valueColor = Color(0xFFE53935))
        Spacer(modifier = Modifier.height(8.dp))
        ResultRow(label = "Time used", value = "${state.timeUsed}s", valueColor = Color.LightGray)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp)
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
