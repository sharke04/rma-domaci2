package rs.edu.raf.rma.showtime.db

import androidx.room.Embedded
import androidx.room.Relation

data class GenreWithMovies(
    @Embedded val genre: GenreEntity,
    @Relation(
        parentColumn = "genreId",
        entityColumn = "movieId",
    )
    val movies: List<MovieEntity>,
)