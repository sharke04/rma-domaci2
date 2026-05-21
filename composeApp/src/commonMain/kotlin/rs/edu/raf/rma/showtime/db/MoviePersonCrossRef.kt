package rs.edu.raf.rma.showtime.db

import androidx.room.Entity

@Entity(primaryKeys = ["movieId", "personId"])
data class MoviePersonCrossRef(
    val movieId: String,
    val personId: String,
)