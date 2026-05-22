package rs.edu.raf.rma.showtime.db

import androidx.room.Entity

@Entity(
    tableName = "movie_genre_cross_refs",
    primaryKeys = ["movieId", "genreId"],
)
data class MovieGenreCrossRef(
    val movieId: String,
    val genreId: Int,
)
