package rs.edu.raf.rma.showtime.domain

import kotlinx.coroutines.flow.Flow

interface ShowtimeRepository {
    fun observeMovies(
        name: String,
        genreId: Int,
        minYear: Int,
        maxYear: Int,
        minRating: Float,
        sortBy: String,
        sortOrder: String,
    ): Flow<List<Movie>>
    suspend fun refreshMovies()
}