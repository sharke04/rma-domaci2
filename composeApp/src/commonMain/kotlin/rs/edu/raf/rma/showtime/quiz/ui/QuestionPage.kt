package rs.edu.raf.rma.showtime.quiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import rs.edu.raf.rma.showtime.quiz.QuizQuestion

@Composable
fun QuestionPage(
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
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(300.dp),
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