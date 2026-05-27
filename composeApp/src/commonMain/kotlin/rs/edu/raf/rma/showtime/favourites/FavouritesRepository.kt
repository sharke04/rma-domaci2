package rs.edu.raf.rma.showtime.favourites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.ShowtimeApi
import rs.edu.raf.rma.showtime.data.toEntity
import rs.edu.raf.rma.showtime.data.toDomain
import rs.edu.raf.rma.showtime.data.toMovieEntity
import rs.edu.raf.rma.showtime.data.toMovieGenreCrossRefs
import rs.edu.raf.rma.showtime.db.UserFavouriteCrossRef
import rs.edu.raf.rma.showtime.domain.Movie

interface FavouritesRepository {
    suspend fun sync(): Int
    fun observeFavourites(userId: Int): Flow<List<Movie>>
    suspend fun isFavourite(movieId: String): Boolean
    suspend fun addFavourite(movieId: String)
    suspend fun removeFavourite(movieId: String)
}

class FavouritesRepositoryImpl(
    private val db: AppDatabase,
    private val showtimeApi: ShowtimeApi,
) : FavouritesRepository {

    override suspend fun sync(): Int {
        val user = showtimeApi.getProfile()
        db.showtimeDao().upsertUser(user.toEntity())

        val favourites = showtimeApi.getFavorites()
        val movies = favourites.map { it.toMovieEntity() }
        val genres = favourites.flatMap { it.genres }.distinctBy { it.id }.map { it.toEntity() }
        val movieGenreCrossRefs = favourites.flatMap { it.toMovieGenreCrossRefs() }
        val userFavouriteCrossRefs = favourites.map { UserFavouriteCrossRef(user.id, it.imdbId) }

        val dao = db.showtimeDao()
        dao.deleteFavouritesForUser(user.id)
        dao.upsertMovies(movies)
        dao.upsertGenres(genres)
        dao.upsertMovieGenreCrossRefs(movieGenreCrossRefs)
        dao.upsertUserFavourites(userFavouriteCrossRefs)

        return user.id
    }

    override fun observeFavourites(userId: Int): Flow<List<Movie>> =
        db.showtimeDao()
            .observeFavouriteMovies(userId)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun isFavourite(movieId: String): Boolean {
        val userId = showtimeApi.getProfile().id
        return db.showtimeDao().isFavourite(userId, movieId)
    }

    override suspend fun addFavourite(movieId: String) {
        val userId = showtimeApi.getProfile().id
        showtimeApi.addFavorite(movieId)
        db.showtimeDao().insertUserFavourite(UserFavouriteCrossRef(userId, movieId))
    }

    override suspend fun removeFavourite(movieId: String) {
        val userId = showtimeApi.getProfile().id
        showtimeApi.removeFavorite(movieId)
        db.showtimeDao().deleteUserFavourite(userId, movieId)
    }
}
