package rs.edu.raf.rma.networking.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieListItemApiModel(
    val imdbId: String,
    val title: String,
    val year: Int? = null,
    val imdbRating: Float? = null,
    val imdbVotes: Int? = null,
    val posterPath: String? = null,
    val genres: List<GenreApiModel> = emptyList()
)

@Serializable
data class GenreApiModel(
    val id: Int,
    val name: String
)

@Serializable
data class MovieDetailsApiModel(
    val imdbId: String,
    val tmdbId: Int? = null,
    val title: String,
    val originalTitle: String? = null,
    val overview: String? = null,
    val tagline: String? = null,
    val releaseDate: String? = null,
    val year: Int? = null,
    val runtime: Int? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val languageCode: String? = null,
    val popularity: Float? = null,
    val imdbRating: Float? = null,
    val imdbVotes: Int? = null,
    val tmdbRating: Float? = null,
    val tmdbVotes: Int? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val homepage: String? = null,
    val genres: List<GenreApiModel> = emptyList(),
    val collection: CollectionApiModel? = null
)

@Serializable
data class CollectionApiModel(
    val id: Int,
    val name: String,
    val posterPath: String? = null,
    val backdropPath: String? = null
)

@Serializable
data class PersonSummaryApiModel(
    val imdbId: String,
    val name: String,
    val professions: String? = null,
    val department: String? = null,
    val profilePath: String? = null
)

@Serializable
data class MovieImagesApiModel(
    val posters: List<ImageApiModel> = emptyList(),
    val backdrops: List<ImageApiModel> = emptyList(),
    val logos: List<ImageApiModel> = emptyList()
)

@Serializable
data class ImageApiModel(
    val filePath: String,
    val width: Int? = null,
    val height: Int? = null,
    val voteAverage: Float? = null,
    val language: String? = null
)

@Serializable
data class VideoApiModel(
    val key: String,
    val site: String,
    val name: String? = null,
    val type: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null
)

@Serializable
data class ApiErrorResponse(
    val error: String,
    val httpCode: Int,
    val message: String,
    val description: String? = null,
    val suggestion: String? = null
)