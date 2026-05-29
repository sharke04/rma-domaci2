package rs.edu.raf.rma.showtime.movies.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import rs.edu.raf.rma.showtime.domain.Actor
import rs.edu.raf.rma.showtime.domain.Image
import rs.edu.raf.rma.showtime.domain.Movie
import rs.edu.raf.rma.showtime.domain.Video

@Composable
fun MovieDetailsContent(
    movie: Movie,
    images: List<Image>,
    actors: List<Actor>,
    video: Video?,
    isFavourite: Boolean?,
    isWatchlisted: Boolean?,
    onBack: () -> Unit,
    onToggleFavourite: () -> Unit,
    onToggleWatchlist: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val uriHandler = LocalUriHandler.current

        Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original${movie.backdropPath}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 300f
                        )
                    )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(0.4f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }

            IconButton(
                onClick = {
                    val url = "https://www.youtube.com/watch?v=${video?.key}"
                    uriHandler.openUri(url)
                },
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.Red,
                    modifier = Modifier.fillMaxSize()
                )
            }

            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 16.dp)
                    .size(width = 100.dp, height = 150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.BottomStart)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 132.dp, bottom = 24.dp)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = Bold
                )
                Text(
                    text = "${movie.year} • ${movie.runtime} min",
                    color = Color.LightGray
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isFavourite != null) {
                Button(
                    onClick = onToggleFavourite,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFavourite) Color.Red else Color.DarkGray,
                    ),
                    shape = RoundedCornerShape(50),
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFavourite) "In Favourites" else "Add to Favourites",
                        fontWeight = Bold,
                    )
                }
            }
            if (isWatchlisted != null) {
                Button(
                    onClick = onToggleWatchlist,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWatchlisted) Color(0xFF1565C0) else Color.DarkGray,
                    ),
                    shape = RoundedCornerShape(50),
                ) {
                    Icon(
                        imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isWatchlisted) "In Watchlist" else "Add to Watchlist",
                        fontWeight = Bold,
                    )
                }
            }
        }

        RatingInfoRow(movie)
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            movie.genres.forEach { genre ->
                SuggestionChip(
                    onClick = { /* No-op */ },
                    label = { Text(genre.name, color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color.Red
                    ),
                    border = null,
                    shape = RoundedCornerShape(50)
                )
            }
        }

        SectionHeader("OVERVIEW")
        Text(
            text = movie.overview ?: "No overview available.",
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SectionHeader("INFO")
        InfoGrid(movie)

        SectionHeader("IMAGES")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images) { image ->
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w780${image.filePath}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 200.dp, height = 120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        SectionHeader("ACTORS")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            actors.forEach { actor ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w185${actor.profilePath}",
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = actor.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = Color.DarkGray.copy(alpha = 0.5f)
                    )
                }
            }
        }

    }
}