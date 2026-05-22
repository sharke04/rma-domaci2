package rs.edu.raf.rma.showtime.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.MoviesApi
import rs.edu.raf.rma.showtime.db.MovieGenreCrossRef
import rs.edu.raf.rma.showtime.domain.Genre
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

    override fun observeGenres(): Flow<List<Genre>> =
        appDatabase.showtimeDao()
            .observeGenres()
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun refreshMovies() {
        val response = moviesApi.getMovies()
        val details = response.items.map { moviesApi.getMovieDetails(it.imdbId) }
        val genres = moviesApi.getGenres()

        val movieEntities = details.map { it.toMovieEntity() }
        val genreEntities = genres.map { it.toEntity() }
        val crossRefs = details.flatMap { movie ->
            movie.genres.map { genre -> MovieGenreCrossRef(movieId = movie.imdbId, genreId = genre.id) }
        }

        appDatabase.showtimeDao().refreshListTransaction(movieEntities, genreEntities, crossRefs)
    }
}
