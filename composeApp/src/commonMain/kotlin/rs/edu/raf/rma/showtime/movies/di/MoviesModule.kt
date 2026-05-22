package rs.edu.raf.rma.showtime.movies.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.movies.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.movies.list.MoviesListViewModel
import rs.edu.raf.rma.showtime.data.ShowtimeRepositoryImpl
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository

val moviesModule = module {
    // TODO: Mozda treba napraviti Showtime module umesto ovog
    single { ShowtimeRepositoryImpl(appDatabase = get(), moviesApi = get()) } bind ShowtimeRepository::class
    viewModelOf(::MoviesListViewModel)
    viewModelOf(::MovieDetailsViewModel)
}