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

    @Query("""
        SELECT *
        FROM movies
        WHERE (:name = '' OR title LIKE '%' || :name || '%')
        AND (:genreId = 0 OR genres LIKE '%"id":' || :genreId || '%')
        AND (year IS NULL OR year >= :minYear)
        AND (year IS NULL OR year <= :maxYear)
        AND (:minRating <= 0 OR imdbRating >= :minRating)
        ORDER BY
            CASE WHEN LOWER(:sortBy) = 'title' AND LOWER(:sortOrder) = 'asc' THEN title END ASC,
            CASE WHEN LOWER(:sortBy) = 'title' AND LOWER(:sortOrder) = 'desc' THEN title END DESC,
            CASE WHEN LOWER(:sortBy) = 'year' AND LOWER(:sortOrder) = 'asc' THEN year END ASC,
            CASE WHEN LOWER(:sortBy) = 'year' AND LOWER(:sortOrder) = 'desc' THEN year END DESC,
            CASE WHEN LOWER(:sortBy) = 'popularity' AND LOWER(:sortOrder) = 'asc' THEN popularity END ASC,
            CASE WHEN LOWER(:sortBy) = 'popularity' AND LOWER(:sortOrder) = 'desc' THEN popularity END DESC,
            CASE WHEN LOWER(:sortBy) = 'rating' AND LOWER(:sortOrder) = 'asc' THEN imdbRating END ASC,
            CASE WHEN LOWER(:sortBy) = 'rating' AND LOWER(:sortOrder) = 'desc' THEN imdbRating END DESC
        """)
    fun observeMovies(
        name: String,
        genreId: Int,
        minYear: Int,
        maxYear: Int,
        minRating: Float,
        sortBy: String,
        sortOrder: String
    ): Flow<List<MovieEntity>>

    @Transaction
    suspend fun refreshListTransaction(movies: List<MovieEntity>) {
        upsertMovies(movies)
    }
}