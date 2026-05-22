package rs.edu.raf.rma.showtime.movies.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.showtime.movies.list.MoviesListContract
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterView(
    state: MoviesListContract.UiState,
    onUpdateSearch: (String) -> Unit,
    onSelectGenre: (String) -> Unit,
    onUpdateYearRange: (String, String) -> Unit,
    onUpdateRating: (Float) -> Unit,
    onApplyFilters: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F101C))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("SEARCH", color = Color.White, fontWeight = FontWeight.Bold)
        TextField(
            value = state.searchTitle,
            onValueChange = onUpdateSearch,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            placeholder = { Text("Search by movie title...", color = Color.Gray) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("GENRE", color = Color.White, fontWeight = FontWeight.Bold)
        FlowRow(modifier = Modifier.padding(vertical = 8.dp)) {
            state.genres.forEach { genre ->
                FilterChip(
                    modifier = Modifier.padding(end = 8.dp),
                    selected = state.selectedGenre == genre.name,
                    onClick = { onSelectGenre(genre.name) },
                    label = { Text(genre.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Red,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("YEAR RANGE", color = Color.White, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("From", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                TextField(
                    value = state.yearFrom,
                    onValueChange = { newYear -> onUpdateYearRange(newYear, state.yearTo) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(" — ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("To", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                TextField(
                    value = state.yearTo,
                    onValueChange = { newYear -> onUpdateYearRange(state.yearFrom, newYear) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("MINIMUM RATING", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(Color(0xFF252537), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("⭐ ${roundToHalf(state.minRating)}", color = Color.White)
            }
        }
        Slider(
            value = state.minRating,
            onValueChange = onUpdateRating,
            valueRange = 0f..10f,
            colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onApplyFilters,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
    }
}

private fun roundToHalf(rating: Float): Float {
    return (rating * 2).roundToInt() / 2f
}