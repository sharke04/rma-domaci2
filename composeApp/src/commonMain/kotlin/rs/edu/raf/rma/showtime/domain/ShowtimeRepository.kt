package rs.edu.raf.rma.showtime.domain

import kotlinx.coroutines.flow.Flow

interface ShowtimeRepository {
    fun observeMovies(): Flow<List<Movie>>
    suspend fun refreshMovies()
}