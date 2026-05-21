package rs.edu.raf.rma.networking

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import rs.edu.raf.rma.networking.model.GenreApiModel
import rs.edu.raf.rma.networking.model.MovieDetailsApiModel
import rs.edu.raf.rma.networking.model.MovieImagesApiModel
import rs.edu.raf.rma.networking.model.MovieListItemApiModel
import rs.edu.raf.rma.networking.model.PaginatedResponse
import rs.edu.raf.rma.networking.model.PersonSummaryApiModel
import rs.edu.raf.rma.networking.model.VideoApiModel

interface MoviesApi {
    @GET("movies")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("query") query: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("min_year") minYear: Int? = null,
        @Query("max_year") maxYear: Int? = null,
        @Query("min_rating") minRating: Float? = null,
        @Query("sort_by") sortBy: String? = "imdb_votes",
        @Query("sort_order") sortOrder: String? = "desc",
    ): PaginatedResponse<MovieListItemApiModel>

    @GET("genres")
    suspend fun getGenres(): List<GenreApiModel>

    @GET("movies/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: String
    ): MovieDetailsApiModel

    @GET("movies/{id}/cast")
    suspend fun getMovieCast(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): PaginatedResponse<PersonSummaryApiModel>

    @GET("movies/{id}/images")
    suspend fun getMovieImages(
        @Path("id") id: String,
        @Query("type") type: String? = null
    ): MovieImagesApiModel

    @GET("movies/{id}/videos")
    suspend fun getMovieVideos(
        @Path("id") id: String,
        @Query("type") type: String? = null
    ): List<VideoApiModel>
}