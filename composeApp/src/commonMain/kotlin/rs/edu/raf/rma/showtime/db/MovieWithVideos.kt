package rs.edu.raf.rma.showtime.db

import androidx.room.Embedded
import androidx.room.Relation

data class MovieWithVideos(
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "videoId",
    )
    val videos: List<VideoEntity>,
)