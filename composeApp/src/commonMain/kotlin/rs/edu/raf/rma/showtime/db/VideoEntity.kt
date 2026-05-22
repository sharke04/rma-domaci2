package rs.edu.raf.rma.showtime.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val key: String,
    val movieId: String,
    val site: String,
    val name: String? = null,
    val type: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
)
