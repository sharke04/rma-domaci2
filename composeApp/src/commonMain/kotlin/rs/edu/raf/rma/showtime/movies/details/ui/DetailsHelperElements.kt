package rs.edu.raf.rma.premiere.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.edu.raf.rma.networking.model.MovieDetailsApiModel
import kotlin.math.roundToInt

@Composable
fun RatingInfoRow(movie: MovieDetailsApiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = movie.imdbRating?.toString() ?: "0.0",
            color = Color(0xFFFFC107),
            fontSize = 20.sp,
            fontWeight = Bold
        )
        Text(
            text = "/10",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = formatVotes(movie.imdbVotes),
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "TMDB ",
            color = Color.LightGray,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = (roundToOneDecimal(movie.tmdbRating)).toString(),
            color = Color(0xFF01B4E4), 
            fontWeight = Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun InfoGrid(movie: MovieDetailsApiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoItem(label = "Budget", value = formatCurrency(movie.budget), modifier = Modifier.weight(1f))
        InfoItem(label = "Revenue", value = formatCurrency(movie.revenue), modifier = Modifier.weight(1f))
        InfoItem(label = "Language", value = movie.languageCode?.uppercase() ?: "EN", modifier = Modifier.weight(1f))
        InfoItem(label = "Popularity", value = movie.popularity?.toInt().toString(), modifier = Modifier.weight(1f))
    }
}

@Composable
fun InfoItem(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1C1C27))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = Bold, fontSize = 14.sp)
    }
}

@Composable
fun SectionHeader(
    header: String
) {
    Text(
        text = header,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = Bold,
        fontFamily = FontFamily.SansSerif,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

private fun formatCurrency(amount: Long?): String {
    if (amount == null || amount == 0L) return "N/A"
    return when {
        amount >= 1_000_000_000 -> "$${amount / 1_000_000_000}B"
        amount >= 1_000_000 -> "$${amount / 1_000_000}M"
        amount >= 1_000 -> "$${amount / 1_000}K"
        else -> "$$amount"
    }
}

private fun roundToOneDecimal(rating: Float?): Float {
    if (rating == null)
        return 0f
    return (rating * 10).roundToInt() / 10f
}

private fun formatVotes(votes: Int?): String {
    if (votes == null)
        return "0 votes"
    if (votes >= 1_000_000)
        return (votes / 1_000_000).toString() + "M votes"
    if (votes >= 1_000)
        return (votes / 1_000).toString() + "K votes"
    return "$votes votes"
}