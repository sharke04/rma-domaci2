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
    fun observeMovie(id: String): Flow<Movie?>
    fun observeGenres(): Flow<List<Genre>>
    fun observeMovieImages(movieId: String): Flow<List<Image>>
    suspend fun refreshMovies()
    suspend fun refreshMovieDetails(movieId: String)
    suspend fun refreshMovieImages(movieId: String)
}