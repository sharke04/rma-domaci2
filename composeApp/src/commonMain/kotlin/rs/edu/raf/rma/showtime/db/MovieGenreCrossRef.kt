package rs.edu.raf.rma.showtime.db

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movie_genre_cross_refs",
    primaryKeys = ["movieId", "genreId"],
    indices = [Index("genreId")],
)
data class MovieGenreCrossRef(
    val movieId: String,
    val genreId: Int,
)
