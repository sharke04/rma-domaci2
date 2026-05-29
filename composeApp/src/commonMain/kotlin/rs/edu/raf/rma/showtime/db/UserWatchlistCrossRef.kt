package rs.edu.raf.rma.showtime.db

import androidx.room.Entity

@Entity(
    tableName = "user_watchlist_cross_ref",
    primaryKeys = ["userId", "movieImdbId"],
)
data class UserWatchlistCrossRef(
    val userId: Int,
    val movieImdbId: String,
)
