package rs.edu.raf.rma.showtime.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowtimeDao {
    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Transaction
    @Query("SELECT * FROM movies")
    fun observeMovies(): Flow<List<MovieEntity>>

    @Transaction
    suspend fun refreshListTransaction(movies: List<MovieEntity>) {
        upsertMovies(movies)
    }
}