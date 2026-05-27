package rs.edu.raf.rma.showtime.db

import androidx.room.Entity

@Entity(
    tableName = "user_favourite_cross_ref",
    primaryKeys = ["userId", "movieImdbId"],
)
data class UserFavouriteCrossRef(
    val userId: Int,
    val movieImdbId: String,
)
