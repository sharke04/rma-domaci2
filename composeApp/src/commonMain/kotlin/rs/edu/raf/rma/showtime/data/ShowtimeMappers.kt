package rs.edu.raf.rma.showtime.data

import rs.edu.raf.rma.networking.model.MovieDetailsApiModel
import rs.edu.raf.rma.showtime.db.MovieEntity
import rs.edu.raf.rma.showtime.domain.Movie

fun MovieDetailsApiModel.toMovieEntity() : MovieEntity = MovieEntity(
    id = imdbId,
    tmdbId = tmdbId,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    tagline = tagline,
    releaseDate = releaseDate,
    year = year,
    runtime = runtime,
    budget = budget,
    revenue = revenue,
    languageCode = languageCode,
    popularity = popularity,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = tmdbRating,
    tmdbVotes = tmdbVotes,
    posterPath = posterPath,
    backdropPath = backdropPath,
    homepage = homepage,
    genres = genres,
    collection = collection,
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    tmdbId = tmdbId,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    tagline = tagline,
    releaseDate = releaseDate,
    year = year,
    runtime = runtime,
    budget = budget,
    revenue = revenue,
    languageCode = languageCode,
    popularity = popularity,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = tmdbRating,
    tmdbVotes = tmdbVotes,
    posterPath = posterPath,
    backdropPath = backdropPath,
    homepage = homepage,
    genres = genres,
    collection = collection,
)