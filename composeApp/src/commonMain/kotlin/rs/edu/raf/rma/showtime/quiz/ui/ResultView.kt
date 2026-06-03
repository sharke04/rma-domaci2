package rs.edu.raf.rma.showtime.quiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.showtime.quiz.QuizContract

@Composable
fun ResultView(
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

        ResultRow(label = "Correct", value = "${state.correctCount}", valueColor = Color.Green)
        Spacer(modifier = Modifier.height(8.dp))
        ResultRow(label = "Incorrect", value = "${state.incorrectCount}", valueColor = Color.Red)
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
