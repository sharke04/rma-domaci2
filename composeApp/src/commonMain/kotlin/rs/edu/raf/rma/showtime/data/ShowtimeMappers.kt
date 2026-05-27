package rs.edu.raf.rma.showtime.data

import rs.edu.raf.rma.networking.model.GenreApiModel
import rs.edu.raf.rma.networking.model.ImageApiModel
import rs.edu.raf.rma.networking.model.MovieDetailsApiModel
import rs.edu.raf.rma.networking.model.MovieListItemApiModel
import rs.edu.raf.rma.networking.model.PersonSummaryApiModel
import rs.edu.raf.rma.networking.model.UserApiModel
import rs.edu.raf.rma.networking.model.VideoApiModel
import rs.edu.raf.rma.showtime.db.ActorEntity
import rs.edu.raf.rma.showtime.db.GenreEntity
import rs.edu.raf.rma.showtime.db.ImageEntity
import rs.edu.raf.rma.showtime.db.MovieEntity
import rs.edu.raf.rma.showtime.db.MovieGenreCrossRef
import rs.edu.raf.rma.showtime.db.MovieWithGenres
import rs.edu.raf.rma.showtime.db.UserEntity
import rs.edu.raf.rma.showtime.db.VideoEntity
import rs.edu.raf.rma.showtime.domain.Actor
import rs.edu.raf.rma.showtime.domain.Genre
import rs.edu.raf.rma.showtime.domain.Image
import rs.edu.raf.rma.showtime.domain.Movie
import rs.edu.raf.rma.showtime.domain.Video

fun MovieListItemApiModel.toMovieEntity() = MovieEntity(
    id = imdbId,
    tmdbId = null,
    title = title,
    originalTitle = null,
    overview = null,
    tagline = null,
    releaseDate = null,
    year = year,
    runtime = null,
    budget = null,
    revenue = null,
    languageCode = null,
    popularity = null,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = null,
    tmdbVotes = null,
    posterPath = posterPath,
    backdropPath = null,
    homepage = null,
)

fun MovieDetailsApiModel.toMovieEntity() = MovieEntity(
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
)

fun GenreApiModel.toEntity() = GenreEntity(id = id, name = name)

fun ImageApiModel.toEntity(movieId: String) = ImageEntity(
    movieId = movieId,
    filePath = filePath,
    width = width,
    height = height,
    voteAverage = voteAverage,
    language = language,
)

fun ImageEntity.toDomain() = Image(
    filePath = filePath,
    width = width,
    height = height,
    voteAverage = voteAverage,
    language = language,
)

fun VideoApiModel.toEntity(movieId: String) = VideoEntity(
    key = key,
    movieId = movieId,
    site = site,
    name = name,
    type = type,
    official = official,
    publishedAt = publishedAt,
)

fun VideoEntity.toDomain() = Video(
    key = key,
    site = site,
    name = name,
    type = type,
    official = official,
    publishedAt = publishedAt,
)

fun PersonSummaryApiModel.toEntity(movieId: String) = ActorEntity(
    movieId = movieId,
    imdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath,
)

fun ActorEntity.toDomain() = Actor(
    imdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath,
)

fun GenreEntity.toDomain() = Genre(id = id, name = name)

fun UserApiModel.toEntity() = UserEntity(id = id, username = username, fullName = fullName)

fun MovieListItemApiModel.toMovieGenreCrossRefs(): List<MovieGenreCrossRef> =
    genres.map { MovieGenreCrossRef(movieId = imdbId, genreId = it.id) }

fun MovieWithGenres.toDomain() = Movie(
    id = movie.id,
    tmdbId = movie.tmdbId,
    title = movie.title,
    originalTitle = movie.originalTitle,
    overview = movie.overview,
    tagline = movie.tagline,
    releaseDate = movie.releaseDate,
    year = movie.year,
    runtime = movie.runtime,
    budget = movie.budget,
    revenue = movie.revenue,
    languageCode = movie.languageCode,
    popularity = movie.popularity,
    imdbRating = movie.imdbRating,
    imdbVotes = movie.imdbVotes,
    tmdbRating = movie.tmdbRating,
    tmdbVotes = movie.tmdbVotes,
    posterPath = movie.posterPath,
    backdropPath = movie.backdropPath,
    homepage = movie.homepage,
    genres = genres.map { it.toDomain() },
)
