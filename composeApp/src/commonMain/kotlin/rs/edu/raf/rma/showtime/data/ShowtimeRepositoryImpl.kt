package rs.edu.raf.rma.showtime.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.MoviesApi
import rs.edu.raf.rma.showtime.db.MovieGenreCrossRef
import rs.edu.raf.rma.showtime.domain.Genre
import rs.edu.raf.rma.showtime.domain.Image
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

    override fun observeMovie(id: String): Flow<Movie?> =
        appDatabase.showtimeDao()
            .observeMovie(id)
            .map { it?.toDomain() }

    override fun observeGenres(): Flow<List<Genre>> =
        appDatabase.showtimeDao()
            .observeGenres()
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeMovieImages(movieId: String): Flow<List<Image>> =
        appDatabase.showtimeDao()
            .observeMovieImages(movieId)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun refreshMovieDetails(movieId: String) {
        val details = moviesApi.getMovieDetails(movieId)
        appDatabase.showtimeDao().upsertMovies(listOf(details.toMovieEntity()))
    }

    override suspend fun refreshMovieImages(movieId: String) {
        val images = moviesApi.getMovieImages(movieId)
        appDatabase.showtimeDao().upsertImages(
            images.backdrops.take(6).map { it.toEntity(movieId) }
        )
    }

    override suspend fun refreshMovies() {
        val allItems = buildList {
            var page = 1
            do {
                val response = moviesApi.getMovies(page = page, pageSize = 100)
                addAll(response.items)
                page++
            } while (page <= response.totalPages)
        }

        val genres = moviesApi.getGenres()

        val movieEntities = allItems.map { it.toMovieEntity() }
        val genreEntities = genres.map { it.toEntity() }
        val crossRefs = allItems.flatMap { movie ->
            movie.genres.map { genre -> MovieGenreCrossRef(movieId = movie.imdbId, genreId = genre.id) }
        }

        appDatabase.showtimeDao().refreshListTransaction(movieEntities, genreEntities, crossRefs)
    }
}
