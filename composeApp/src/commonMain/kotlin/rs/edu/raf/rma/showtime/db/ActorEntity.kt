package rs.edu.raf.rma.showtime.db

import androidx.room.Entity

@Entity(tableName = "actors", primaryKeys = ["movieId", "imdbId"])
data class ActorEntity(
    val movieId: String,
    val imdbId: String,
    val name: String,
    val professions: String? = null,
    val department: String? = null,
    val profilePath: String? = null,
)
