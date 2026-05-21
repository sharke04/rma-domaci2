package rs.edu.raf.rma.showtime.db

import androidx.room.Embedded
import androidx.room.Relation

data class MovieWithImages(
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "imageId",
    )
    val images: List<ImageEntity>,
)