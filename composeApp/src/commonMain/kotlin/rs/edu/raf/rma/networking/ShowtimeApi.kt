package rs.edu.raf.rma.networking

import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import rs.edu.raf.rma.networking.model.MovieListItemApiModel
import rs.edu.raf.rma.networking.model.UserApiModel

interface ShowtimeApi {
    @GET("me")
    suspend fun getProfile(): UserApiModel

    @GET("me/favorites")
    suspend fun getFavorites(): List<MovieListItemApiModel>

    @POST("me/favorites/{movie_id}")
    suspend fun addFavorite(@Path("movie_id") movieId: String)

    @DELETE("me/favorites/{movie_id}")
    suspend fun removeFavorite(@Path("movie_id") movieId: String)
}