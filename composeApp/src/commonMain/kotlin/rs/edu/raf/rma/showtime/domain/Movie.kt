package rs.edu.raf.rma.showtime.domain

import rs.edu.raf.rma.networking.model.CollectionApiModel
import rs.edu.raf.rma.networking.model.GenreApiModel

data class Movie(
    val id: String,
    val tmdbId: Int?,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val tagline: String?,
    val releaseDate: String?,
    val year: Int?,
    val runtime: Int?,
    val budget: Long?,
    val revenue: Long?,
    val languageCode: String?,
    val popularity: Float?,
    val imdbRating: Float?,
    val imdbVotes: Int?,
    val tmdbRating: Float?,
    val tmdbVotes: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val homepage: String?,
    val genres: List<GenreApiModel>,
    val collection: CollectionApiModel?,
)