package rs.edu.raf.rma.showtime.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.MoviesApi
import rs.edu.raf.rma.showtime.db.MovieEntity
import rs.edu.raf.rma.showtime.domain.Movie
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository

class ShowtimeRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val moviesApi: MoviesApi,
) : ShowtimeRepository {
    override fun observeMovies(
        name: String,
        genreId: Int,
        minYear: Int,
        maxYear: Int,
        minRating: Float,
        sortBy: String,
        sortOrder: String
    ): Flow<List<Movie>> =
        appDatabase.showtimeDao()
            .observeMovies(
                name = name,
                genreId = genreId,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
                sortBy = sortBy,
                sortOrder = sortOrder,
            )
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun refreshMovies() {
        val response = moviesApi.getMovies()
        val entities = response.items
            .map { moviesApi.getMovieDetails(it.imdbId) }
            .map { it.toMovieEntity() }
        appDatabase.showtimeDao().refreshListTransaction(entities)
    }
}