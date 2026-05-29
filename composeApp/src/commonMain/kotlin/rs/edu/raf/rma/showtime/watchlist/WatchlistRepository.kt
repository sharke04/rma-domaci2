package rs.edu.raf.rma.showtime.watchlist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.ShowtimeApi
import rs.edu.raf.rma.showtime.data.toDomain
import rs.edu.raf.rma.showtime.data.toEntity
import rs.edu.raf.rma.showtime.data.toMovieEntity
import rs.edu.raf.rma.showtime.data.toMovieGenreCrossRefs
import rs.edu.raf.rma.showtime.db.UserWatchlistCrossRef
import rs.edu.raf.rma.showtime.domain.Movie

interface WatchlistRepository {
    suspend fun sync(): Int
    fun observeWatchlist(userId: Int): Flow<List<Movie>>
    suspend fun isWatchlisted(movieId: String): Boolean
    suspend fun addToWatchlist(movieId: String)
    suspend fun removeFromWatchlist(movieId: String)
}

class WatchlistRepositoryImpl(
    private val db: AppDatabase,
    private val showtimeApi: ShowtimeApi,
) : WatchlistRepository {

    override suspend fun sync(): Int {
        val user = showtimeApi.getProfile()
        db.showtimeDao().upsertUser(user.toEntity())

        val watchlist = showtimeApi.getWatchlist()
        val movies = watchlist.map { it.toMovieEntity() }
        val genres = watchlist.flatMap { it.genres }.distinctBy { it.id }.map { it.toEntity() }
        val movieGenreCrossRefs = watchlist.flatMap { it.toMovieGenreCrossRefs() }
        val userWatchlistCrossRefs = watchlist.map { UserWatchlistCrossRef(user.id, it.imdbId) }

        val dao = db.showtimeDao()
        dao.deleteWatchlistForUser(user.id)
        dao.upsertMovies(movies)
        dao.upsertGenres(genres)
        dao.upsertMovieGenreCrossRefs(movieGenreCrossRefs)
        dao.upsertUserWatchlist(userWatchlistCrossRefs)

        return user.id
    }

    override fun observeWatchlist(userId: Int): Flow<List<Movie>> =
        db.showtimeDao()
            .observeWatchlistMovies(userId)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun isWatchlisted(movieId: String): Boolean {
        val userId = showtimeApi.getProfile().id
        return db.showtimeDao().isWatchlisted(userId, movieId)
    }

    override suspend fun addToWatchlist(movieId: String) {
        val userId = showtimeApi.getProfile().id
        showtimeApi.addToWatchlist(movieId)
        db.showtimeDao().insertUserWatchlist(UserWatchlistCrossRef(userId, movieId))
    }

    override suspend fun removeFromWatchlist(movieId: String) {
        val userId = showtimeApi.getProfile().id
        showtimeApi.removeFromWatchlist(movieId)
        db.showtimeDao().deleteUserWatchlist(userId, movieId)
    }
}
