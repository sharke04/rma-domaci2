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

    @Upsert
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Upsert
    suspend fun upsertMovieGenreCrossRefs(crossRefs: List<MovieGenreCrossRef>)

    @Upsert
    suspend fun upsertImages(images: List<ImageEntity>)

    @Query("SELECT * FROM images WHERE movieId = :movieId")
    fun observeMovieImages(movieId: String): Flow<List<ImageEntity>>

    @Upsert
    suspend fun upsertVideos(videos: List<VideoEntity>)

    @Query("SELECT * FROM videos WHERE movieId = :movieId LIMIT 1")
    fun observeMovieVideo(movieId: String): Flow<VideoEntity?>

    @Upsert
    suspend fun upsertActors(actors: List<ActorEntity>)

    @Query("SELECT * FROM actors WHERE movieId = :movieId")
    fun observeMovieActors(movieId: String): Flow<List<ActorEntity>>

    @Transaction
    @Query("SELECT * FROM movies WHERE id = :id")
    fun observeMovie(id: String): Flow<MovieWithGenres?>

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun observeGenres(): Flow<List<GenreEntity>>

    @Transaction
    @Query("""
        SELECT DISTINCT movies.*
        FROM movies
        LEFT JOIN movie_genre_cross_refs ON movies.id = movie_genre_cross_refs.movieId
        WHERE (:name = '' OR movies.title LIKE '%' || :name || '%')
        AND (:genreId = 0 OR movie_genre_cross_refs.genreId = :genreId)
        AND (movies.year IS NULL OR movies.year >= :minYear)
        AND (movies.year IS NULL OR movies.year <= :maxYear)
        AND (:minRating <= 0 OR movies.imdbRating >= :minRating)
        ORDER BY
            CASE WHEN LOWER(:sortBy) = 'title' AND LOWER(:sortOrder) = 'asc' THEN movies.title END ASC,
            CASE WHEN LOWER(:sortBy) = 'title' AND LOWER(:sortOrder) = 'desc' THEN movies.title END DESC,
            CASE WHEN LOWER(:sortBy) = 'year' AND LOWER(:sortOrder) = 'asc' THEN movies.year END ASC,
            CASE WHEN LOWER(:sortBy) = 'year' AND LOWER(:sortOrder) = 'desc' THEN movies.year END DESC,
            CASE WHEN LOWER(:sortBy) = 'popularity' AND LOWER(:sortOrder) = 'asc' THEN movies.popularity END ASC,
            CASE WHEN LOWER(:sortBy) = 'popularity' AND LOWER(:sortOrder) = 'desc' THEN movies.popularity END DESC,
            CASE WHEN LOWER(:sortBy) = 'rating' AND LOWER(:sortOrder) = 'asc' THEN movies.imdbRating END ASC,
            CASE WHEN LOWER(:sortBy) = 'rating' AND LOWER(:sortOrder) = 'desc' THEN movies.imdbRating END DESC
        """)
    fun observeMovies(
        name: String,
        genreId: Int,
        minYear: Int,
        maxYear: Int,
        minRating: Float,
        sortBy: String,
        sortOrder: String
    ): Flow<List<MovieWithGenres>>

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Upsert
    suspend fun upsertUserFavourites(crossRefs: List<UserFavouriteCrossRef>)

    @Query("DELETE FROM user_favourite_cross_ref WHERE userId = :userId")
    suspend fun deleteFavouritesForUser(userId: Int)

    @Transaction
    @Query("""
        SELECT movies.* FROM movies
        INNER JOIN user_favourite_cross_ref ON movies.id = user_favourite_cross_ref.movieImdbId
        WHERE user_favourite_cross_ref.userId = :userId
    """)
    fun observeFavouriteMovies(userId: Int): Flow<List<MovieWithGenres>>

    @Transaction
    suspend fun refreshListTransaction(
        movies: List<MovieEntity>,
        genres: List<GenreEntity>,
        crossRefs: List<MovieGenreCrossRef>,
    ) {
        upsertMovies(movies)
        upsertGenres(genres)
        upsertMovieGenreCrossRefs(crossRefs)
    }
}